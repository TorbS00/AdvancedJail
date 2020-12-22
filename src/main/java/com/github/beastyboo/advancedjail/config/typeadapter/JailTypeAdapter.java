package com.github.beastyboo.advancedjail.config.typeadapter;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Torbie on 16.12.2020.
 */
public class JailTypeAdapter extends TypeAdapter<Jail>{

    private final AJail core;

    public JailTypeAdapter(AJail core) {
        this.core = core;
    }

    @Override
    public void write(JsonWriter out, Jail value) throws IOException {
        out.beginObject();

        out.name("name").value(value.getName());

        out.name("cells").beginArray();
        for(Cell cell : value.getCells().values()) {
            out.beginObject();
            out.name("id").value(cell.getId().toString());
            out.endObject();
        }
        out.endArray();

        out.name("inmates").beginArray();
        for(Inmate inmate : value.getPlayers().values()) {
            out.beginObject();
            out.name("uuid").value(inmate.getUuid().toString());
            out.endObject();
        }
        out.endArray();

        out.name("world").value(value.getReleasePoint().getWorld().getName());
        out.name("x").value(value.getReleasePoint().getX());
        out.name("y").value(value.getReleasePoint().getY());
        out.name("z").value(value.getReleasePoint().getZ());
        out.name("yaw").value(value.getReleasePoint().getYaw());
        out.name("pitch").value(value.getReleasePoint().getPitch());

        out.endObject();
    }

    @Override
    public Jail read(JsonReader in) throws IOException {
        in.beginObject();

        String name = "";
        Map<String, Cell> cells = new HashMap<>();
        Map<UUID, Inmate> players = new HashMap<>();

        World world = null;
        double x = 0;
        double y = 0;
        double z = 0;
        double yaw = 0;
        double pitch = 0;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name":
                    name = in.nextString();
                    break;
                case "cells":
                    in.beginArray();
                    while (in.hasNext()) {
                        in.beginObject();
                        while(in.hasNext()) {
                            switch (in.nextName()) {
                                case "id":
                                    Optional<Cell> cell = core.getAPI().getCellByUUID(UUID.fromString(in.nextString()));
                                    if(cell.isPresent()) {
                                        cells.put(cell.get().getName().toLowerCase(), cell.get());
                                    }
                            }
                        }
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "inmates":
                    in.beginArray();
                    while (in.hasNext()) {
                        in.beginObject();
                        while(in.hasNext()) {
                            switch (in.nextName()) {
                                case "uuid":
                                    Optional<Inmate> inmate = core.getAPI().getInmateByUUID(UUID.fromString(in.nextString()));
                                    if(inmate.isPresent()) {
                                        players.put(inmate.get().getUuid(), inmate.get());
                                    }
                                    break;
                            }
                        }
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "world":
                    world = Bukkit.getWorld(in.nextString());
                    break;
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "z":
                    z = in.nextDouble();
                    break;
                case "yaw":
                    yaw = in.nextDouble();
                    break;
                case "pitch":
                    pitch = in.nextDouble();
                    break;
            }
        }
        Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
        Jail jail = new Jail.Builder(name).cells(cells).players(players).releasePoint(location).build();
        in.endObject();
        return jail;
    }
}
