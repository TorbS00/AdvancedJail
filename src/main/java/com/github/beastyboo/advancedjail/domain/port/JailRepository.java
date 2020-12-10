package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 10.12.2020.
 */
public interface JailRepository {

    void load();

    void close();

    void releaseTask();

    boolean createJail(String name, Player player, CuboidRegion region);

    boolean deleteJail(String name, Player player);

    boolean createReleasePoint(String name, Player player);

    boolean sendJailList(Player player);

    boolean sendCellsList(String name, Player player);

    boolean sendInmateList(String name, Player player);

    Optional<Jail> getJailByName(String name);

    Optional<Jail> getJailByInmate(UUID uuid);

    Set<Jail> getAllJails();

    Set<Cell> getJailCells(String name);

    Set<Inmate> getJailInmates(String name);

}
