package com.github.beastyboo.advancedjail.domain.entity;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Inmate {

    private final UUID uuid;
    private final Set<Crime> crimes;
    private final ItemStack[] armorContent;
    private final ItemStack[] inventoryContent;
    private int penalty;

    public Inmate(UUID uuid, Set<Crime> crimes, ItemStack[] armorContent, ItemStack[] inventoryContent, int penalty) {
        this.uuid = uuid;
        this.crimes = crimes;
        this.armorContent = armorContent;
        this.inventoryContent = inventoryContent;
        this.penalty = penalty;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<Crime> getCrimes() {
        return crimes;
    }

    public ItemStack[] getArmorContent() {
        return armorContent;
    }

    public ItemStack[] getInventoryContent() {
        return inventoryContent;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public double getBill() {
        double bill = 0;
        for(Crime crime : crimes) {
            bill += crime.getBill();
        }
        return bill;
    }

    public String crimesToString() {
        StringBuilder sb = new StringBuilder("");
        for(Crime crime : crimes) {
            sb.append("- " + crime.getName() + "\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inmate inmate = (Inmate) o;

        if (getPenalty() != inmate.getPenalty()) return false;
        if (!getUuid().equals(inmate.getUuid())) return false;
        if (!getCrimes().equals(inmate.getCrimes())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getArmorContent(), inmate.getArmorContent())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getInventoryContent(), inmate.getInventoryContent());
    }

    @Override
    public int hashCode() {
        int result = getUuid().hashCode();
        result = 31 * result + getCrimes().hashCode();
        result = 31 * result + Arrays.hashCode(getArmorContent());
        result = 31 * result + Arrays.hashCode(getInventoryContent());
        result = 31 * result + getPenalty();
        return result;
    }

    @Override
    public String toString() {
        return "Inmate{" +
                "uuid=" + uuid +
                ", crimes=" + crimes +
                ", armorContent=" + Arrays.toString(armorContent) +
                ", inventoryContent=" + Arrays.toString(inventoryContent) +
                ", penalty=" + penalty +
                '}';
    }
}
