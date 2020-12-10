package com.github.beastyboo.advancedjail.config;

import com.github.beastyboo.advancedjail.adapter.CellMemory;
import com.github.beastyboo.advancedjail.adapter.JailMemory;
import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.github.beastyboo.advancedjail.domain.port.CellRepository;
import com.github.beastyboo.advancedjail.domain.port.JailRepository;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 08.12.2020.
 */
public class JailConfiguration {

    private final AJail core;
    private final JailRepository jailRepository;
    private final CellRepository cellRepository;

    public JailConfiguration(AJail core) {
        this.core = core;
        jailRepository = new JailMemory(core);
        cellRepository = new CellMemory(core);
    }

    public void load() {
        cellRepository.load();
        jailRepository.load();
    }

    public void close() {
        jailRepository.close();
        cellRepository.close();
    }

    //Jail
    public boolean createJail(String name, Player player, CuboidRegion region) {
        return jailRepository.createJail(name, player, region);
    }

    public boolean deleteJail(String name, Player player) {
        return jailRepository.deleteJail(name, player);
    }

    public boolean createReleasePoint(String name, Player player) {
        return jailRepository.createReleasePoint(name, player);
    }

    public boolean sendJailList(Player player) {
        return jailRepository.sendJailList(player);
    }

    public boolean sendCellsList(String name, Player player) {
        return jailRepository.sendCellsList(name, player);
    }

    public boolean sendInmateList(String name, Player player) {
        return jailRepository.sendInmateList(name, player);
    }

    public Optional<Jail> getJailByName(String name) {
        return jailRepository.getJailByName(name);
    }

    public Optional<Jail> getJailByInmate(UUID uuid) {
        return jailRepository.getJailByInmate(uuid);
    }

    public Set<Jail> getAllJails() {
        return jailRepository.getAllJails();
    }

    public Set<Cell> getJailCells(String name) {
        return jailRepository.getJailCells(name);
    }

    public Set<Inmate> getJailInmates(String name) {
        return jailRepository.getJailInmates(name);
    }

    //cell
    public boolean createCell(String jailName, String name, Player player, int size) {
        return cellRepository.createCell(jailName, name, player, size);
    }

    public boolean deleteCell(String jailName, String name, Player player) {
        return cellRepository.deleteCell(jailName, name, player);
    }

    public Optional<Cell> getCellByUUID(UUID uuid) {
        return cellRepository.getCellByUUID(uuid);
    }

    public Optional<Cell> getCellByJailAndName(String jailName, String name) {
        return cellRepository.getCellByJailAndName(jailName, name);
    }

    public Optional<Cell> getCellByInmate(UUID uuid) {
        return cellRepository.getCellByInmate(uuid);
    }

    public Set<Cell> getAllCells() {
        return cellRepository.getAllCells();
    }

    /**
     * Don't use this method unless you really understand how this plugin operates.
     * This is only added so I can access a repeatingTask...
     * @return the jailRepository currently in use.
     */
    public JailRepository getJailRepository() {
        return jailRepository;
    }
}
