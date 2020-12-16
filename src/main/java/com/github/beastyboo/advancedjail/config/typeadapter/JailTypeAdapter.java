package com.github.beastyboo.advancedjail.config.typeadapter;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.domain.entity.Jail;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

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



        out.endObject();
    }

    @Override
    public Jail read(JsonReader in) throws IOException {
        return null;
    }
}
