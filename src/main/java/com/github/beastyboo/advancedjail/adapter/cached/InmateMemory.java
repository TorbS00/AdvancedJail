package com.github.beastyboo.advancedjail.adapter.cached;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.config.typeadapter.CellTypeAdapter;
import com.github.beastyboo.advancedjail.config.typeadapter.InmateTypeAdapter;
import com.github.beastyboo.advancedjail.domain.MessageType;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.port.InmateRepository;
import com.github.beastyboo.advancedjail.util.BasicUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Torbie on 13.12.2020.
 */
public class InmateMemory implements InmateRepository {

    private final AJail core;
    private final Map<UUID, Inmate> inmates;
    private final Cache<UUID, Long> cachedCooldown;
    private final long current;
    private final Gson gson;
    private final File folder;

    public InmateMemory(AJail core) {
        this.core = core;
        inmates = new HashMap<>();
        current = core.getConfig().useBroadcastItem();
        cachedCooldown = CacheBuilder.newBuilder().expireAfterWrite(current, TimeUnit.MINUTES).build();
        gson = this.getGson();
        folder = new File(core.getPlugin().getDataFolder(), "inmates");
    }

    @Override
    public void load() {
        if(!folder.exists()) {
            folder.mkdirs();
        }
        File[] directoryListing = folder.listFiles();
        if (directoryListing == null) {
            return;
        }
        for (File child : directoryListing) {
            String json = BasicUtil.loadContent(child);
            Inmate inmate = this.deserialize(json);
            inmates.put(inmate.getUuid(), inmate);
        }
    }

    @Override
    public void close() {
        for(Inmate inmate : inmates.values()) {
            File file = new File(folder, inmate.getUuid().toString() + ".json");
            if(!folder.exists()) {
                folder.mkdirs();
            }
            String json = this.serialize(inmate);
            BasicUtil.saveFile(file, json);
        }
    }

    @Override
    public boolean clickBillItem(Player player) {
        Optional<Inmate> inmate = this.getInmateByUUID(player.getUniqueId());
        if(!inmate.isPresent()) {
            core.message(player, MessageType.INMATE_NOT_FOUND);
            return false;
        }

        Optional<ItemStack> billItem = this.getBillItem(player.getUniqueId());
        if(!billItem.isPresent()) {
            return false;
        }

        if(!player.getInventory().getItemInMainHand().isSimilar(billItem.get())) {
            return false;
        }

        Economy econ = core.getEcon();
        double bill = inmate.get().getBill();
        if(econ.getBalance(player) < bill) {
            core.message(player, MessageType.INMATE_NOT_ENOUGH_MONEY);
            return false;
        }

        econ.withdrawPlayer(player, bill);
        this.releasePlayer(Optional.empty(), player, false);
        return true;
    }

    @Override
    public boolean clickBroadcastItem(Player player) {
        Optional<Inmate> inmate = this.getInmateByUUID(player.getUniqueId());
        if(!inmate.isPresent()) {
            core.message(player, MessageType.INMATE_NOT_FOUND);
            return false;
        }

        Optional<ItemStack> broadcastItem = this.getBroadcastItem(player.getUniqueId());
        if(!broadcastItem.isPresent()) {
            return false;
        }

        if(!player.getInventory().getItemInMainHand().isSimilar(broadcastItem.get())) {
            return false;
        }

        if(cachedCooldown.getIfPresent(player.getUniqueId()) != null) {
            core.message(player, MessageType.INMATE_STILL_IN_COOLDOWN);

            return false;
        }

        cachedCooldown.put(player.getUniqueId(), current);

        //TODO: Optimize permission...
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.hasPermission("jail.broadcast.inmate")) {
                core.message(all, MessageType.INMATE_BROADCAST_ITEM_USED);
            }
        }

        core.message(player, MessageType.INMATE_BROADCAST_SENT);
        return true;
    }

    @Override
    public boolean arrestPlayer(Player player, Player target, String jailName, String cellName, Set<Crime> selectedCrimes) {
        UUID uuid = target.getUniqueId();
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(inmate.isPresent()) {
            core.message(player, MessageType.INMATE_ALREADY_EXIST);
            return false;
        }

        JailConfiguration api = core.getAPI();

        Optional<Jail> jail = api.getJailByName(jailName);
        if(!jail.isPresent()) {
            core.message(player, MessageType.JAIL_NOT_FOUND);
            return false;
        }

        Optional<Cell> cell = api.getCellByJailAndName(jailName, cellName);
        if(!cell.isPresent()) {
            core.message(player, MessageType.CELL_NOT_FOUND);
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

        core.message(player, MessageType.PLAYER_ARREST);
        core.message(target, MessageType.TARGET_ARREST);
        return true;
    }

    @Override
    public boolean releasePlayer(Optional<CommandSender> sender, Player target, boolean escaped) {
        UUID uuid = target.getUniqueId();
        PlayerInventory inv = target.getInventory();
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(!inmate.isPresent()) {
            if(sender.isPresent()) {
                if(sender.get() instanceof Player) {
                    core.message((Player) sender.get(), MessageType.INMATE_NOT_FOUND);
                }
            }
            return false;
        }

        JailConfiguration api = core.getAPI();

        inv.clear();

        if(escaped == false || (escaped && core.getConfig().returnInventoryEscape() == true)) {
            inv.setArmorContents(inmate.get().getArmorContent());
            inv.setContents(inmate.get().getInventoryContent());
        }

        Optional<Jail> jail = api.getJailByInmate(uuid);
        Optional<Cell> cell = api.getCellByInmate(uuid);

        if(!jail.isPresent()) {
            return false;
        }

        if(!cell.isPresent()) {
            return false;
        }

        cell.get().getPlayers().remove(uuid, inmate.get());
        jail.get().getPlayers().remove(uuid, inmate.get());
        inmates.remove(uuid, inmate.get());

        if(target.isOnline()) {
            target.teleport(jail.get().getReleasePoint());
            core.message(target, MessageType.TARGET_RELEASE);
        }

        File sourceFile = new File(folder, inmate.get().getUuid().toString() + ".json");
        if(sourceFile.exists()) {
            sourceFile.delete();
        }

        if(sender.isPresent()) {
            if(sender.get() instanceof Player) {
                core.message((Player) sender.get(), MessageType.PLAYER_RELEASE);
            }

            else if(sender.get() instanceof ConsoleCommandSender) {
                core.broadcast(MessageType.INMATE_RELEASE_BROADCAST);
            }
        }

        if(escaped) {
            for(Player pl : Bukkit.getOnlinePlayers()) {
                if(pl.hasPermission("jail.broadcast.escape")) {
                    core.message(pl, MessageType.INMATE_ESCAPE_BROADCAST);
                }
            }
        }

        return false;
    }

    @Override
    public Optional<ItemStack> getBillItem(UUID uuid) {
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(!inmate.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(core.getConfig().billItem());
    }

    @Override
    public Optional<ItemStack> getBroadcastItem(UUID uuid) {
        Optional<Inmate> inmate = this.getInmateByUUID(uuid);
        if(!inmate.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(core.getConfig().broadcastItem());
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
        bookMeta.setAuthor(core.getConfig().signature());

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

    private Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(Inmate.class, new InmateTypeAdapter(core))
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    private String serialize(Inmate value) {
        return this.gson.toJson(value);
    }

    private Inmate deserialize(String json) {
        return this.gson.fromJson(json, Inmate.class);
    }

}
