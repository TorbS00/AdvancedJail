package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Cell;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 10.12.2020.
 */
public interface CellRepository {

    void load();

    void close();

    boolean createCell(String jailName, String name, Player player, int size);

    boolean deleteCell(String jailName, String name, Player player);

    Optional<Cell> getCellByUUID(UUID uuid);

    Optional<Cell> getCellByJailAndName(String jailName, String name);

    Optional<Cell> getCellByInmate(UUID uuid);

    Set<Cell> getAllCells();

}
