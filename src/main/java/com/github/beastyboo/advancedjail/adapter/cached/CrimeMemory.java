package com.github.beastyboo.advancedjail.adapter.cached;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.holder.ArrestInventoryHolder;
import com.github.beastyboo.advancedjail.domain.port.CrimeRepository;
import com.github.beastyboo.advancedjail.util.BasicUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by Torbie on 12.12.2020.
 */
public class CrimeMemory implements CrimeRepository{

    private final AJail core;
    private final Map<String, Crime> loadedCrimes;

    public CrimeMemory(AJail core) {
        this.core = core;
        loadedCrimes = new HashMap<>();
    }

    @Override
    public void load() {

    }

    @Override
    public boolean openArrestInventory(Player player, Player target, String jailName, String cellName) {
        Optional<Jail> jail = core.getAPI().getJailByName(jailName);
        Optional<Cell> cell = core.getAPI().getCellByJailAndName(jailName, cellName);

        if(!jail.isPresent()) {
            //Could not find jail
            return false;
        }

        if(!cell.isPresent()) {
            //Could not find cell
            return false;
        }

        if(cell.get().getPlayers().size() >= cell.get().getLimit()) {
            //Cell is full
            return false;
        }

        if(core.getAPI().getJailByInmate(target.getUniqueId()).isPresent()) {
            //Player already belongs to a jail
            return false;
        }

        Inventory inventory = Bukkit.createInventory(new ArrestInventoryHolder(target, jail.get(), cell.get()), 54,
                ChatColor.RED + "Select crimes for: " + ChatColor.DARK_RED + target.getName());

        defaultInv(inventory);

        player.openInventory(inventory);
        return true;
    }

    @Override
    public Optional<Crime> getCrimeByName(String name) {
        return Optional.ofNullable(loadedCrimes.get(name.toLowerCase()));
    }

    private void defaultInv(Inventory inventory) {

        inventory.clear();

        for (Crime crime : loadedCrimes.values()){

            List<String> lore = new ArrayList<>();
            lore.add("§6Bill: §c€ " + String.valueOf(crime.getBill()));
            lore.add("§6Penalty: §c" + "Time: " + BasicUtil.formatTime(crime.getPenalty()));

            inventory.addItem(createItem(Material.ENCHANTED_BOOK, "§6" + crime.getName(), lore));
        }

        List<String> lore = new ArrayList<>();
        lore.add("§6" + "Selected crimes: ");
        inventory.setItem(53, createItem(Material.GREEN_WOOL, "§a" + "CONFIRM!", lore));

        lore.clear();
        lore.add("§4" + "Click here to clear selections. ");
        inventory.setItem(45, createItem(Material.RED_WOOL, "§4" + "CLEAR!", lore));

        lore.clear();
        lore.add("§e" + "Click here to cancel the arrest. ");
        inventory.setItem(49, createItem(Material.YELLOW_WOOL, "§e" + "CANCEL!", lore));
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}
