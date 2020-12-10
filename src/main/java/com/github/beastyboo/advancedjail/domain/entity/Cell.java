package com.github.beastyboo.advancedjail.domain.entity;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Cell {

    private final UUID id;
    private final String name;
    private final Location location;
    private final int limit;
    private final Map<UUID, Inmate> players;

    public Cell(UUID id, String name, Location location, int limit, Map<UUID, Inmate> players) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.limit = limit;
        this.players = players;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public int getLimit() {
        return limit;
    }

    public Map<UUID, Inmate> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (getLimit() != cell.getLimit()) return false;
        if (!getId().equals(cell.getId())) return false;
        if (!getName().equals(cell.getName())) return false;
        if (!getLocation().equals(cell.getLocation())) return false;
        return getPlayers().equals(cell.getPlayers());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getLocation().hashCode();
        result = 31 * result + getLimit();
        result = 31 * result + getPlayers().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", limit=" + limit +
                ", players=" + players +
                '}';
    }
}
