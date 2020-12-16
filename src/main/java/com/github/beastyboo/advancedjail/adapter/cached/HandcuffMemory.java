package com.github.beastyboo.advancedjail.adapter.cached;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.MessageType;
import com.github.beastyboo.advancedjail.domain.entity.Handcuff;
import com.github.beastyboo.advancedjail.domain.entity.Key;
import com.github.beastyboo.advancedjail.domain.port.HandcuffRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Torbie on 12.12.2020.
 */
public class HandcuffMemory implements HandcuffRepository{

    private final AJail core;
    private final Map<String, Handcuff> loadedHandcuffs;
    private final Map<String, Key> loadedKeys;
    private final Map<UUID, Handcuff> handcuffedPlayers;

    public HandcuffMemory(AJail core) {
        this.core = core;
        loadedHandcuffs = new HashMap<>();
        loadedKeys = new HashMap<>();
        handcuffedPlayers = new HashMap<>();
    }

    @Override
    public void load() {

    }

    @Override
    public boolean addHandcuffTarget(Player player, Player target) {
        PlayerInventory inv = player.getInventory();
        ItemStack itemInMainHand = inv.getItemInMainHand();

        Optional<Handcuff> handcuff = this.getHandcuffByItemStack(itemInMainHand);
        if(!handcuff.isPresent()) {
            return false;
        }

        if(handcuffedPlayers.containsKey(target.getUniqueId())) {
            core.message(player, MessageType.PLAYER_ALREADY_HANDCUFFED);
            return false;
        }

        //Added for safety (If removed cause bug with double clicks sometimes.)
        Bukkit.getScheduler().scheduleSyncDelayedTask(core.getPlugin(), () -> handcuffedPlayers.put(target.getUniqueId(), handcuff.get()), 10L);

        int amount = itemInMainHand.getAmount();
        if(amount > 1) {
            itemInMainHand.setAmount(amount - 1);
        } else {
            inv.remove(itemInMainHand);
        }

        if(handcuff.get().isHasKey() == true) {
            inv.addItem(handcuff.get().getKey().getItemStack());
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
        core.message(player, MessageType.PLAYER_HANDCUFFED_ADDED);
        core.message(target, MessageType.TARGET_HANDCUFF_ADDED);
        return true;
    }

    @Override
    public boolean removeHandcuffTarget(Player player, Player target) {
        PlayerInventory inv = player.getInventory();
        ItemStack itemInMainHand = inv.getItemInMainHand();

        Optional<Key> key = this.getKeyByItemStack(itemInMainHand);
        if(!key.isPresent()) {
            return false;
        }

        if(!handcuffedPlayers.containsKey(target.getUniqueId())) {
            core.message(player, MessageType.PLAYER_NOT_HANDCUFFED);
            return false;
        }

        Handcuff handcuff = handcuffedPlayers.get(target.getUniqueId());

        if(!handcuff.getKey().equals(key.get()) || !key.get().isPincer()) {
            core.message(player, MessageType.PLAYER_WRONG_KEY);
            return false;
        }

        int amount = itemInMainHand.getAmount();
        if(amount > 1) {
            itemInMainHand.setAmount(amount - 1);
        } else {
            inv.remove(itemInMainHand);
        }

        target.removePotionEffect(PotionEffectType.SLOW);

        inv.addItem(handcuff.getItemStack());

        Bukkit.getScheduler().scheduleSyncDelayedTask(core.getPlugin(), () -> handcuffedPlayers.remove(target.getUniqueId(), handcuff), 10L);

        core.message(player, MessageType.PLAYER_HANDCUFFED_REMOVED);
        core.message(target, MessageType.TARGET_HANDCUFF_REMOVED);
        return true;
    }

    @Override
    public boolean giveHandcuff(Player player, String name) {
        Optional<Handcuff> handcuff = this.getHandcuffByName(name);
        if(!handcuff.isPresent()) {
            core.message(player, MessageType.HANDCUFF_NOT_FOUND);
            return false;
        }

        player.getInventory().addItem(handcuff.get().getItemStack());
        core.message(player, MessageType.HANDCUFF_GIVEN);
        return true;
    }

    @Override
    public boolean giveKey(Player player, String name) {
        Optional<Key> key = this.getKeyByName(name);
        if(!key.isPresent()) {
            core.message(player, MessageType.KEY_NOT_FOUND);
            return false;
        }

        player.getInventory().addItem(key.get().getItemStack());
        core.message(player, MessageType.KEY_GIVEN);
        return true;
    }

    @Override
    public Optional<Key> getKeyByName(String name) {
        return Optional.ofNullable(loadedKeys.get(name.toLowerCase()));
    }

    @Override
    public Optional<Key> getKeyByItemStack(ItemStack itemStack) {
        for(Key key : loadedKeys.values()) {
            if(key.getItemStack().isSimilar(itemStack)) {
                return Optional.ofNullable(key);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Handcuff> getHandcuffByName(String name) {
        return Optional.ofNullable(loadedHandcuffs.get(name.toLowerCase()));
    }

    @Override
    public Optional<Handcuff> getHandcuffByItemStack(ItemStack itemStack) {
        for(Handcuff handcuff : loadedHandcuffs.values()) {
            if(handcuff.getItemStack().isSimilar(itemStack)) {
                return Optional.ofNullable(handcuff);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Key> getAllKeys() {
        return new HashSet<>(loadedKeys.values());
    }

    @Override
    public Set<Handcuff> getAllHandcuffs() {
        return new HashSet<>(loadedHandcuffs.values());
    }

    @Override
    public Map<UUID, Handcuff> getAllHandcuffedPlayers() {
        return handcuffedPlayers;
    }

    private List<String> parseLore(List<String> lore) {
        final List<String> parsed = new ArrayList<>();

        for(String s : lore) {
            parsed.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return parsed;
    }

    private ItemStack getItemStack(String displayName, Material material, int customModelData, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
