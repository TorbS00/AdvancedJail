package com.github.beastyboo.advancedjail.config.typeadapter;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import com.github.beastyboo.advancedjail.util.BasicUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 16.12.2020.
 */
public class InmateTypeAdapter extends TypeAdapter<Inmate> {

    private final AJail core;

    public InmateTypeAdapter(AJail core) {
        this.core = core;
    }


    @Override
    public void write(JsonWriter out, Inmate inmate) throws IOException {
        out.beginObject();

        out.name("uuid").value(inmate.getUuid().toString());

        out.name("crimes").beginArray();
        for(Crime crime : inmate.getCrimes()) {
            out.beginObject();
            out.name("name").value(crime.getName());
            out.endObject();
        }
        out.endArray();

        out.name("armor-content").value(BasicUtil.itemStackArrayToBase64(inmate.getArmorContent()));
        out.name("inventory").value(BasicUtil.itemStackArrayToBase64(inmate.getInventoryContent()));
        out.name("penalty").value(inmate.getPenalty());

        out.endObject();
    }

    @Override
    public Inmate read(JsonReader in) throws IOException {
        in.beginObject();

        UUID uuid = null;
        Set<Crime> crimes = new HashSet<>();
        ItemStack[] armorContent = null;
        ItemStack[] inventoryContent = null;
        int penalty = 0;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "uuid":
                    uuid = UUID.fromString(in.nextString());
                    break;
                case "crimes":
                    in.beginArray();
                    while (in.hasNext()) {
                        in.beginObject();
                        while(in.hasNext()) {
                            switch (in.nextName()) {
                                case "name":
                                    Optional<Crime> crime = core.getAPI().getCrimeByName(in.nextString());
                                    if(crime.isPresent()) {
                                        crimes.add(crime.get());
                                    }
                            }
                        }
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "armor-content":
                    armorContent = BasicUtil.itemStackArrayFromBase64(in.nextString());
                    break;
                case "inventory":
                    inventoryContent = BasicUtil.itemStackArrayFromBase64(in.nextString());
                    break;
                case "penalty":
                    penalty = in.nextInt();
                    break;
            }
        }
        Inmate inmate = new Inmate(uuid, crimes, armorContent, inventoryContent, penalty);
        in.endObject();
        return inmate;
    }
}
