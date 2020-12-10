package com.github.beastyboo.advancedjail.domain.entity;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Key {

    private final String name;
    private final String displayName;
    private final String permission;
    private final boolean pincer;
    private final ItemStack itemStack;

    public Key(String name, String displayName, String permission, boolean pincer, ItemStack itemStack) {
        this.name = name;
        this.displayName = displayName;
        this.permission = permission;
        this.pincer = pincer;
        this.itemStack = itemStack;
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

    public boolean isPincer() {
        return pincer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key keyModel = (Key) o;

        if (isPincer() != keyModel.isPincer()) return false;
        if (!getName().equals(keyModel.getName())) return false;
        if (!getDisplayName().equals(keyModel.getDisplayName())) return false;
        if (!getPermission().equals(keyModel.getPermission())) return false;
        return getItemStack().equals(keyModel.getItemStack());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDisplayName().hashCode();
        result = 31 * result + getPermission().hashCode();
        result = 31 * result + (isPincer() ? 1 : 0);
        result = 31 * result + getItemStack().hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "KeyModel{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", permission='" + permission + '\'' +
                ", pincer=" + pincer +
                ", itemStack=" + itemStack.getType().name() +
                '}';
    }

}
