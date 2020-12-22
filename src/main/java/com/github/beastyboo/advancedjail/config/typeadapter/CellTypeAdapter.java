package com.github.beastyboo.advancedjail.config.typeadapter;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Cell;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
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
public class CellTypeAdapter extends TypeAdapter<Cell>{

    private final AJail core;

    public CellTypeAdapter(AJail core) {
        this.core = core;
    }

    @Override
    public void write(JsonWriter out, Cell cell) throws IOException {
        out.beginObject();

        out.name("id").value(cell.getId().toString());

        out.name("name").value(cell.getName());

        out.name("world").value(cell.getLocation().getWorld().getName());
        out.name("x").value(cell.getLocation().getX());
        out.name("y").value(cell.getLocation().getY());
        out.name("z").value(cell.getLocation().getZ());
        out.name("yaw").value(cell.getLocation().getYaw());
        out.name("pitch").value(cell.getLocation().getPitch());

        out.name("limit").value(cell.getLimit());

        out.name("inmates").beginArray();
        for(Inmate inmate : cell.getPlayers().values()) {
            out.beginObject();
            out.name("uuid").value(inmate.getUuid().toString());
            out.endObject();
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public Cell read(JsonReader in) throws IOException {
        in.beginObject();

        UUID id = null;
        String name = "";

        World world = null;
        double x = 0;
        double y = 0;
        double z = 0;
        double yaw = 0;
        double pitch = 0;

        int limit = 0;

        Map<UUID, Inmate> players = new HashMap<>();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    id = UUID.fromString(in.nextString());
                    break;
                case "name":
                    name = in.nextString();
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
                case "limit":
                    limit = in.nextInt();
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
            }
        }
        Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
        Cell cell = new Cell(id, name, location, limit, players);
        in.endObject();
        return cell;
    }
}
