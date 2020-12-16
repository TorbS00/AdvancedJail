package com.github.beastyboo.advancedjail.adapter.cached;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.MessageType;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.port.JailRepository;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Torbie on 10.12.2020.
 */
public class JailMemory implements JailRepository{

    private final AJail core;
    private final Map<String, Jail> jails;

    public JailMemory(AJail core) {
        this.core = core;
        jails = new HashMap<>();
    }

    @Override
    public void load() {

    }

    @Override
    public void close() {

    }

    @Override
    public void releaseTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(core.getPlugin(), () -> {
            for(Inmate inmate : core.getAPI().getAllInmates()) {
                if(inmate.getPenalty() != 0) {
                    inmate.setPenalty(inmate.getPenalty() - 1);
                    return;
                }

                core.getAPI().releasePlayer(Optional.of(core.getPlugin().getServer().getConsoleSender()), Bukkit.getPlayer(inmate.getUuid()), false);
            }
        }, 0L, 20L);
    }

    @Override
    public boolean createJail(String name, Player player, CuboidRegion region) {
        Optional<Jail> jail = this.getJailByName(name);
        if(jail.isPresent()) {
            core.message(player, MessageType.JAIL_ALREADY_EXIST);
            return false;
        }

        ProtectedCuboidRegion weRegion = new ProtectedCuboidRegion(name, region.getMinimumPoint(), region.getMaximumPoint());
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(region.getWorld());

        if(regions.getRegions().containsKey(name)) {
            core.message(player, MessageType.JAIL_REGIONNAME_ALREADY_EXIST);
            return false;
        }

        regions.addRegion(weRegion);

        Jail newJail = new Jail.Builder(name).releasePoint(player.getWorld().getSpawnLocation()).build();
        jails.put(newJail.getName().toLowerCase(), newJail);
        core.message(player, MessageType.JAIL_CREATED);
        return true;
    }

    @Override
    public boolean deleteJail(String name, Player player) {
        Optional<Jail> jail = this.getJailByName(name);
        if(!jail.isPresent()) {
            core.message(player, MessageType.JAIL_NOT_FOUND);
            return false;
        }

        //TODO:
        //  - delete file.

        for(Inmate inmate : jail.get().getPlayers().values()) {
            Player pInmate = Bukkit.getPlayer(inmate.getUuid());
            core.getAPI().releasePlayer(Optional.empty(), pInmate, false);
        }

        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        regions.removeRegion(jail.get().getName());

        jails.remove(jail.get().getName().toLowerCase(), jail.get());
        core.message(player, MessageType.JAIL_DELETED);
        return true;
    }

    @Override
    public boolean createReleasePoint(String name, Player player) {
        Optional<Jail> jail = this.getJailByName(name);
        if(!jail.isPresent()) {
            core.message(player, MessageType.JAIL_NOT_FOUND);
            return false;
        }

        jail.get().setReleasePoint(player.getLocation());
        core.message(player, MessageType.JAIL_RELEASE_POINT_CREATED);
        return true;
    }

    @Override
    public boolean sendJailList(Player player) {
        if(jails.size() <= 0) {
            core.message(player, MessageType.JAIL_LIST_EMPTY);
            return false;
        }
        player.sendMessage("§6Jails:");
        for(Jail jail : jails.values()) {
            player.sendMessage(" §c- " + jail.getName());
        }
        return true;
    }

    @Override
    public boolean sendCellsList(String name, Player player) {
        Optional<Jail> jail = this.getJailByName(name);
        if(!jail.isPresent()) {
            core.message(player, MessageType.JAIL_NOT_FOUND);
            return false;
        }

        if(jail.get().getCells().size() <= 0) {
            core.message(player, MessageType.JAIL_CELL_LIST_EMPTY);
            return false;
        }

        player.sendMessage("§6Cells in " + jail.get().getName() + ":");
        for(Cell cell : jail.get().getCells().values()) {
            if(cell.getPlayers().size() < cell.getLimit()) {
                player.sendMessage(" §a- " + cell.getName() + " || Available");
            } else if(cell.getPlayers().size() >= cell.getLimit()) {
                player.sendMessage(" §c- " + cell.getName() + " || Occupied");
            }
        }
        return true;
    }

    @Override
    public boolean sendInmateList(String name, Player player) {
        Optional<Jail> jail = this.getJailByName(name);
        if(!jail.isPresent()) {
            core.message(player, MessageType.JAIL_NOT_FOUND);
            return false;
        }

        if(jail.get().getPlayers().size() <= 0) {
            core.message(player, MessageType.JAIL_INMATE_LIST_EMPTY);
            return false;
        }

        player.sendMessage("§6Players in " + jail.get().getName() + ":");
        for(Inmate inmate : jail.get().getPlayers().values()) {
            player.sendMessage(" §a- " + Bukkit.getOfflinePlayer(inmate.getUuid()).getName());
        }
        return true;
    }

    @Override
    public Optional<Jail> getJailByName(String name) {
        return Optional.ofNullable(jails.get(name.toLowerCase()));
    }

    @Override
    public Optional<Jail> getJailByInmate(UUID uuid) {
        for(Jail jail : jails.values()) {
            if(jail.getPlayers().get(uuid) != null) {
                return Optional.ofNullable(jail);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Jail> getAllJails() {
        return new HashSet<>(jails.values());
    }

    @Override
    public Set<Cell> getJailCells(String name) {
        Optional<Jail> jail = this.getJailByName(name);

        if(!jail.isPresent()) {
            return null;
        }

        return new HashSet<>(jail.get().getCells().values());
    }

    @Override
    public Set<Inmate> getJailInmates(String name) {

        Optional<Jail> jail = this.getJailByName(name);

        if(!jail.isPresent()) {
            return null;
        }
        return new HashSet<>(jail.get().getPlayers().values());
    }
}
