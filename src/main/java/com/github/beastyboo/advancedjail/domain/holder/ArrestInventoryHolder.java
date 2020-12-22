package com.github.beastyboo.advancedjail.domain.holder;

import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Torbie on 12.12.2020.
 */
public class ArrestInventoryHolder implements InventoryHolder {

    private final Player target;
    private final Jail jail;
    private final Cell cell;
    private final Set<Crime> selectedCrimes;

    public ArrestInventoryHolder(Player target, Jail jail, Cell cell) {
        this.target = target;
        this.jail = jail;
        this.cell = cell;
        selectedCrimes = new HashSet<>();
    }

    public Player getTarget() {
        return target;
    }

    public Jail getJail() {
        return jail;
    }

    public Cell getCell() {
        return cell;
    }

    public Set<Crime> getSelectedCrimes() {
        return selectedCrimes;
    }

    @Override
    public Inventory getInventory() {
        return this.getInventory();
    }
}
