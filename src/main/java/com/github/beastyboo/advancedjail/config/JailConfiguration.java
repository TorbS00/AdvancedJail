package com.github.beastyboo.advancedjail.config;

import com.github.beastyboo.advancedjail.adapter.cached.*;
import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.*;
import com.github.beastyboo.advancedjail.domain.port.*;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
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
    private final HandcuffRepository handcuffRepository;
    private final CrimeRepository crimeRepository;
    private final InmateRepository inmateRepository;

    public JailConfiguration(AJail core) {
        this.core = core;
        jailRepository = new JailMemory(core);
        cellRepository = new CellMemory(core);
        handcuffRepository = new HandcuffMemory(core);
        crimeRepository = new CrimeMemory(core);
        inmateRepository = new InmateMemory(core);
    }

    public void load() {
        handcuffRepository.load();
        crimeRepository.load();
        inmateRepository.load();
        cellRepository.load();
        jailRepository.load();
    }

    public void close() {
        jailRepository.close();
        cellRepository.close();
        inmateRepository.close();
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

    //Handcuffs
    public boolean addHandcuffTarget(Player player, Player target){
        return handcuffRepository.addHandcuffTarget(player, target);
    }

    public boolean removeHandcuffTarget(Player player, Player target){
        return handcuffRepository.removeHandcuffTarget(player, target);
    }

    public boolean giveHandcuff(Player player,  String name){
        return handcuffRepository.giveHandcuff(player, name);
    }

    public boolean giveKey(Player player, String name){
        return handcuffRepository.giveKey(player, name);
    }

    public Optional<Key> getKeyByName(String name){
        return handcuffRepository.getKeyByName(name);
    }

    public Optional<Key> getKeyByItemStack(ItemStack itemStack){
        return handcuffRepository.getKeyByItemStack(itemStack);
    }

    public Optional<Handcuff> getHandcuffByName(String name){
        return handcuffRepository.getHandcuffByName(name);
    }

    public Optional<Handcuff> getHandcuffByItemStack(ItemStack itemStack){
        return handcuffRepository.getHandcuffByItemStack(itemStack);
    }

    public Set<Key> getAllKeys(){
        return handcuffRepository.getAllKeys();
    }

    public Set<Handcuff> getAllHandcuffs(){
        return handcuffRepository.getAllHandcuffs();
    }

    public Map<UUID, Handcuff> getAllHandcuffedPlayers(){
        return handcuffRepository.getAllHandcuffedPlayers();
    }

    //Crime
    public boolean openArrestInventory(Player player, Player target, String jailName, String cellName) {
        return crimeRepository.openArrestInventory(player, target, jailName, cellName);
    }

    public Set<Crime> getAllCrimes() { return crimeRepository.getAllCrimes(); }

    public Optional<Crime> getCrimeByName(String name) {
        return crimeRepository.getCrimeByName(name);
    }

    //Inmate
    public boolean clickBillItem(Player player) {
        return inmateRepository.clickBillItem(player);
    }

    public boolean clickBroadcastItem(Player player) {
        return inmateRepository.clickBroadcastItem(player);
    }

    public boolean arrestPlayer(Player player, Player target, String jailName, String cellName, Set<Crime> selectedCrimes) {
        return inmateRepository.arrestPlayer(player, target, jailName, cellName, selectedCrimes);
    }

    public boolean releasePlayer(Optional<CommandSender> sender, Player target, boolean escaped) {
        return inmateRepository.releasePlayer(sender, target, escaped);
    }

    public Optional<ItemStack> getBillItem(UUID uuid) {
        return inmateRepository.getBillItem(uuid);
    }

    public Optional<ItemStack> getBroadcastItem(UUID uuid) {
        return inmateRepository.getBroadcastItem(uuid);
    }

    public Optional<Inmate> getInmateByUUID(UUID uuid) {
        return inmateRepository.getInmateByUUID(uuid);
    }

    public Set<Inmate> getAllInmates() {
        return inmateRepository.getAllInmates();
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
