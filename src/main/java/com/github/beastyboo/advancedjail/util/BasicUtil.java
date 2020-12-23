package com.github.beastyboo.advancedjail.util;

import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Handcuff;
import com.github.beastyboo.advancedjail.domain.entity.Key;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Torbie on 10.12.2020.
 */
public class BasicUtil {

    private BasicUtil() {
        throw new AssertionError();
    }

    public static String formatTime(int total) {
        int h = (total/3600);
        int m = (total-(3600*h))/60;
        int s = (total -(3600*h)-(m*60));
        if(h > 0) {
            return String.valueOf(h) + "h " + String.valueOf(m) + "m e " + String.valueOf(s) + "s.";
        }
        if(m > 0) {
            return String.valueOf(m) + "m e " + String.valueOf(s) + "s.";
        }
        return String.valueOf(s) + "s.";
    }

    public static boolean isPlayerInJail(World world, String regionID, Location loc) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));

        if(regions == null) {
            return false;
        }

        ProtectedRegion region = regions.getRegion(regionID);
        if(region == null) {
            return false;
        }
        if(!(region instanceof ProtectedCuboidRegion)) {
            return false;
        }

        if(region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
            return true;
        }

        return false;
    }

    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }

    }

    public static ItemStack billItem() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cPay your bill!");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + String.valueOf("Test"));
        meta.setLore(lore);
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack callCopsItem() {
        ItemStack item = new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cCall the cops");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + String.valueOf("Test"));
        meta.setLore(lore);
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }

    public static Map<String, Crime> defaultCrimes() {
        Map<String, Crime> crimes = new HashMap<>();
        Crime crime = new Crime("murder", 1000000.99, 600);
        crimes.put("murder", crime);
        return crimes;
    }

    public static Map<String, Key> defaultKeys() {
        Map<String, Key> keys = new HashMap<>();
        Key key = defaultKey();
        keys.put("key", key);
        return keys;
    }

    public static Key defaultKey() {
        ItemStack is = new ItemStack(Material.BELL, 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("In-game key");
        List<String> lore = new ArrayList<>();
        lore.add("test!!!!");
        lore.add("yay test again.");
        meta.setLore(lore);
        meta.setCustomModelData(1);
        is.setItemMeta(meta);
        return new Key("key", "In-game key", "jail.key.key", false, is);
    }

    public static Map<String, Handcuff> defaultHandcuffs() {
        ItemStack is = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("In-game handcuff");
        List<String> lore = new ArrayList<>();
        lore.add("test!!!!");
        lore.add("yay test again.");
        meta.setLore(lore);
        meta.setCustomModelData(1);
        is.setItemMeta(meta);

        Map<String, Handcuff> handcuffs = new HashMap<>();
        Handcuff handcuff = new Handcuff("handcuff", "In-game handcuff", "jail.handcuff.handcuff", is, true, defaultKey());
        handcuffs.put("handcuff", handcuff);
        return handcuffs;
    }


}
