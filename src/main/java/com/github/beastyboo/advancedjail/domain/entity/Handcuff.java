package com.github.beastyboo.advancedjail.domain.entity;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Handcuff {

    private final String name;
    private final String displayName;
    private final String permission;
    private final ItemStack itemStack;
    private final boolean hasKey;
    private final Key key;

    public Handcuff(String name, String displayName, String permission, ItemStack itemStack, boolean hasKey, Key key) {
        this.name = name;
        this.displayName = displayName;
        this.permission = permission;
        this.itemStack = itemStack;
        this.hasKey = hasKey;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isHasKey() {
        return hasKey;
    }

    public Key getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Handcuff that = (Handcuff) o;

        if (isHasKey() != that.isHasKey()) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getDisplayName().equals(that.getDisplayName())) return false;
        if (!getPermission().equals(that.getPermission())) return false;
        if (!getItemStack().equals(that.getItemStack())) return false;
        return getKey().equals(that.getKey());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDisplayName().hashCode();
        result = 31 * result + getPermission().hashCode();
        result = 31 * result + getItemStack().hashCode();
        result = 31 * result + (isHasKey() ? 1 : 0);
        result = 31 * result + getKey().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HandcuffModel{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", permission='" + permission + '\'' +
                ", itemStack=" + itemStack +
                ", hasKey=" + hasKey +
                ", key=" + key.getName() +
                '}';
    }

}
