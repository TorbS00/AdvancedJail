package com.github.beastyboo.advancedjail.domain.output;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Torbie on 22.12.2020.
 */
public class ItemStackValueSerialiser implements ValueSerialiser<ItemStack>{

    @Override
    public Class<ItemStack> getTargetClass() {
        return ItemStack.class;
    }

    @Override
    public ItemStack deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = new HashMap<>();

        for(Map.Entry<FlexibleType, FlexibleType> mapEntry : flexibleType.getMap().entrySet()) {
            map.put(mapEntry.getKey().getString(), mapEntry.getValue());
        }

        FlexibleType name = map.get("item-name");
        FlexibleType material = map.get("material");
        FlexibleType modelData = map.get("model-data");
        FlexibleType lore = map.get("lore");

        if (name == null || material == null) {
            throw flexibleType.badValueExceptionBuilder().message("Incorrect input to generate a key with.").build();
        }

        ItemStack itemStack = new ItemStack(Material.valueOf(material.getString()), 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name.getString()));
        if(lore != null) {
            List<String> newList = new ArrayList<>();
            for(FlexibleType list : lore.getList()) {
                newList.add(ChatColor.translateAlternateColorCodes('&', list.getString()));
            }

            meta.setLore(newList);
        }
        meta.setCustomModelData(modelData.getInteger());
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public Object serialise(ItemStack value, Decomposer decomposer) {
        Map<String, Object> map = new HashMap<>();
        map.put("item-name", value.getItemMeta().getDisplayName());
        map.put("material", value.getType().toString());
        ItemMeta meta = value.getItemMeta();
        map.put("model-data", meta.getCustomModelData());
        if(meta.getLore() == null) {
            map.put("lore", new ArrayList<String>());
        } else {
            map.put("lore", value.getItemMeta().getLore());
        }
        return map;
    }

}
