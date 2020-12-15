package com.github.beastyboo.advancedjail.adapter.event;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.holder.ArrestInventoryHolder;
import com.github.beastyboo.advancedjail.util.BasicUtil;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Torbie on 15.12.2020.
 */
public class InmateEvents implements Listener {

    private final AJail core;
    private final JailConfiguration api;

    public InmateEvents(AJail core) {
        this.core = core;
        api = core.getAPI();
    }

    @EventHandler
    public void onEscape(RegionLeftEvent event) {
        Player p = event.getPlayer();

        Optional<Inmate> inmate = api.getInmateByUUID(p.getUniqueId());
        if(!inmate.isPresent()) {
            return;
        }

        Optional<Jail> jail = api.getJailByInmate(p.getUniqueId());
        if(!jail.isPresent()) {
            return;
        }

        if(!event.getRegionName().equalsIgnoreCase(jail.get().getName())) {
            return;
        }

        api.releasePlayer(Optional.empty(), p, true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();

        Optional<Inmate> inmate = api.getInmateByUUID(p.getUniqueId());
        if(!inmate.isPresent()) {
            return;
        }

        p.sendMessage(ChatColor.DARK_RED + "Time: " + BasicUtil.formatTime(inmate.get().getPenalty()));
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractWithInmateItems(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if(!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        api.clickBillItem(event.getPlayer());
        api.clickBroadcastItem(event.getPlayer());
    }

    @EventHandler
    public void onArrest(InventoryClickEvent event) {
        if(event.getInventory().getHolder() == null || !(event.getInventory().getHolder() instanceof ArrestInventoryHolder)) {
            return;
        }

        if(!(event.getWhoClicked() instanceof  Player)) {
            return;
        }

        Player p = (Player) event.getWhoClicked();

        if(event.getCurrentItem()==null || event.getCurrentItem().getType()== Material.AIR||!event.getCurrentItem().hasItemMeta()){
            return;
        }

        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();
        final ArrestInventoryHolder inv = (ArrestInventoryHolder) event.getInventory().getHolder();

        switch(clickedItem.getType()) {
            case ENCHANTED_BOOK:
                Optional<Crime> crime = api.getCrimeByName((ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).toLowerCase()));
                if(!crime.isPresent()) {
                    break;
                }

                inv.getSelectedCrimes().add(crime.get());
                event.getInventory().removeItem(clickedItem);

                List<String> lore = new ArrayList<>();
                lore.add("§6" + "Selected crimes: ");
                double bill = 0;
                int penalty = 0;

                for(Crime crimes : inv.getSelectedCrimes()) {
                    lore.add("§c" + "  - " + crimes.getName());
                    bill += crimes.getBill();
                    penalty += crimes.getPenalty();
                }

                lore.add("§6" + "Total bill: " + "§c€ " + String.valueOf(bill));
                lore.add("§6" + "Total penalty: " + "§c Time: " + BasicUtil.formatTime(penalty));

                event.getInventory().setItem(53, createItem(Material.GREEN_WOOL, "§a" + "CONFIRM!", lore));

                p.updateInventory();

                break;
            case RED_WOOL:
                inv.getSelectedCrimes().clear();

                defaultInv(event.getInventory());
                p.updateInventory();
                break;
            case YELLOW_WOOL:
                inv.getSelectedCrimes().clear();
                p.closeInventory();
                break;
            case GREEN_WOOL:
                if(inv.getSelectedCrimes().size() <= 0) {
                    break;
                }

                Player target = inv.getTarget();
                Jail jail = inv.getJail();
                Cell cell = inv.getCell();

                api.arrestPlayer(p, target, jail.getName(), cell.getName(), inv.getSelectedCrimes());
                p.closeInventory();
                break;
            default:
                break;
        }

    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void defaultInv(Inventory inventory) {
        inventory.clear();

        for (Crime crime : api.getAllCrimes()){

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

}
