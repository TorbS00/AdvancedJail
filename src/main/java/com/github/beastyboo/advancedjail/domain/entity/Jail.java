package com.github.beastyboo.advancedjail.domain.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Torbie on 10.12.2020.
 */
public class Jail {

    private final String name;
    private final Map<String, Cell> cells;
    private final Map<UUID, Inmate> players;
    private Location releasePoint;

    public static class Builder {

        private final String name;
        private Map<String, Cell> cells = new HashMap<>();
        private Map<UUID, Inmate> players = new HashMap<>();
        private Location releasePoint = Bukkit.getWorlds().get(0).getSpawnLocation();

        public Builder(String name) {
            this.name = name;
        }

        public Builder cells(Map<String, Cell> value) {
            cells = value;
            return this;
        }

        public Builder players(Map<UUID, Inmate> value) {
            players = value;
            return this;
        }

        public Builder releasePoint(Location value) {
            releasePoint = value;
            return this;
        }

        public Jail build() {
            return new Jail(this);
        }
    }

    private Jail(Builder builder) {
        name = builder.name;
        cells = builder.cells;
        players = builder.players;
        releasePoint = builder.releasePoint;
    }

    public String getName() {
        return name;
    }

    public Map<String, Cell> getCells() {
        return cells;
    }

    public Map<UUID, Inmate> getPlayers() {
        return players;
    }

    public Location getReleasePoint() {
        return releasePoint;
    }

    public void setReleasePoint(Location releasePoint) {
        this.releasePoint = releasePoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Jail jail = (Jail) o;

        if (!getName().equals(jail.getName())) return false;
        if (!getCells().equals(jail.getCells())) return false;
        if (!getPlayers().equals(jail.getPlayers())) return false;
        return getReleasePoint().equals(jail.getReleasePoint());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getCells().hashCode();
        result = 31 * result + getPlayers().hashCode();
        result = 31 * result + getReleasePoint().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Jail{" +
                "name='" + name + '\'' +
                ", cells=" + cells +
                ", players=" + players +
                ", releasePoint=" + releasePoint +
                '}';
    }
}
