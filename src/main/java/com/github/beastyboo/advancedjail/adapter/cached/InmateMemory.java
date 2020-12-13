package com.github.beastyboo.advancedjail.adapter.cached;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.port.InmateRepository;
import com.github.beastyboo.advancedjail.util.BasicUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Torbie on 13.12.2020.
 */
public class InmateMemory implements InmateRepository {

    private final AJail core;
    private final Map<UUID, Inmate> inmates;
    private final Cache<UUID, Long> cachedCooldown;

    //Make configurable.
    private final long current = 5;

    public InmateMemory(AJail core) {
        this.core = core;
        inmates = new HashMap<>();
        cachedCooldown = CacheBuilder.newBuilder().expireAfterWrite(current, TimeUnit.MINUTES).build();
    }

    @Override
    public void load() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean clickBillItem(Player player) {
        Optional<Inmate> inmate = this.getInmateByUUID(player.getUniqueId());
        if(!inmate.isPresent()) {
            //not an inmate...
            return false;
        }

        Optional<ItemStack> billItem = this.getBillItem(player.getUniqueId());
        if(!billItem.isPresent()) {
            //Could not load billItem item for player.
            return false;
        }

        if(!player.getInventory().getItemInMainHand().isSimilar(billItem.get())) {
            //Did not click billItem item.
            return false;
        }

        Economy econ = core.getEcon();
        double bill = inmate.get().getBill();
        if(econ.getBalance(player) < bill) {
            //Not enough money
            return false;
        }

        econ.withdrawPlayer(player, bill);
        this.releasePlayer(Optional.empty(), player);
        return true;
    }

    @Override
    public boolean clickBroadcastItem(Player player) {
        Optional<Inmate> inmate = this.getInmateByUUID(player.getUniqueId());
        if(!inmate.isPresent()) {
            //not an inmate...
            return false;
        }

        Optional<ItemStack> broadcastItem = this.getBroadcastItem(player.getUniqueId());
        if(!broadcastItem.isPresent()) {
            //Could not load broadcast item for player.
            return false;
        }

        if(!player.getInventory().getItemInMainHand().isSimilar(broadcastItem.get())) {
            //Did not click broadcast item.
            return false;
        }

        if(cachedCooldown.getIfPresent(player.getUniqueId()) != null) {
            //Still in cooldown.

            return false;
        }

        cachedCooldown.put(player.getUniqueId(), current);

        //TODO: Optimize permission...
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.hasPermission("jail.broadcast")) {
                //Broadcast a message to everyyone
            }
        }

        //broadcast sent...
        return true;
    }

    @Override
    public boolean arrestPlayer(Player player, Player target, String jailName, String cellName, Set<Crime> selectedCrimes) {
        UUID uuid = target.getUniqueId();
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(inmate.isPresent()) {
            //Inmate already exist
            return false;
        }

        JailConfiguration api = core.getAPI();

        Optional<Jail> jail = api.getJailByName(jailName);
        if(!jail.isPresent()) {
            //Jail dont exist
            return false;
        }

        Optional<Cell> cell = api.getCellByJailAndName(jailName, cellName);
        if(!cell.isPresent()) {
            //Cell dont exist
            return false;
        }

        PlayerInventory inv = target.getInventory();
        inv.clear();

        Optional<ItemStack> billItem = this.getBillItem(uuid);
        Optional<ItemStack> broadcastItem = this.getBroadcastItem(uuid);
        if(billItem.isPresent()) {
            inv.addItem(billItem.get());
        }
        if(broadcastItem.isPresent()) {
            inv.addItem(broadcastItem.get());
        }

        Inmate newInmate = new Inmate(uuid, selectedCrimes, inv.getArmorContents(), inv.getContents(), getPenalty(selectedCrimes));
        inmates.put(uuid, newInmate);


        cell.get().getPlayers().put(uuid, newInmate);
        target.teleport(cell.get().getLocation());

        //Needs to be added with a delay to prevent from triggering escape feature.
        Bukkit.getScheduler().scheduleSyncDelayedTask(core.getPlugin(), () -> jail.get().getPlayers().put(uuid, newInmate), 30);

        if(core.getAPI().getAllHandcuffedPlayers().containsKey(uuid)) {
            core.getAPI().removeHandcuffTarget(player, target);
        }

        player.getInventory().addItem(this.playerArrestBook(newInmate, target, jail.get(), cell.get()));

        //Message to BOTH: PLAYER ARRESTED...
        return true;
    }

    @Override
    public boolean releasePlayer(Optional<CommandSender> sender, Player target) {

        //TODO:
        //  - delete file.

        return false;
    }

    @Override
    public Optional<ItemStack> getBillItem(UUID uuid) {
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(!inmate.isPresent()) {
            return Optional.empty();
        }



        return null;
    }

    @Override
    public Optional<ItemStack> getBroadcastItem(UUID uuid) {
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(!inmate.isPresent()) {
            return Optional.empty();
        }



        return null;
    }

    @Override
    public Optional<Inmate> getInmateByUUID(UUID uuid) {
        return Optional.ofNullable(inmates.get(uuid));
    }

    @Override
    public Set<Inmate> getAllInmates() {
        return new HashSet<>(inmates.values());
    }

    private int getPenalty(Set<Crime> crimes) {
        int i = 0;
        for(Crime crime : crimes) {
            i += crime.getPenalty();
        }
        return i;
    }

    private ItemStack playerArrestBook(Inmate model, Player target, Jail jailModel, Cell cellModel) {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        bookMeta.setTitle("Identikit di " + target.getName());
        bookMeta.setAuthor("RoyalCity");

        String split = "\n";
        List<String> pages = new ArrayList<>();

        pages.add("§7Nome: §c" + target.getName() + split +
                "§7Prigione: §c" + jailModel.getName() + split +
                "§7Cella: §c" + cellModel.getName() + split +
                "§7Cauzione: §c" + String.valueOf(model.getBill())  + "€" + split +
                "§7Pena: §c" + BasicUtil.formatTime(model.getPenalty()) + split +
                "§7Reati: §c" + split + model.crimesToString()
        );
        bookMeta.setPages(pages);
        item.setItemMeta(bookMeta);
        return item;
    }

}
