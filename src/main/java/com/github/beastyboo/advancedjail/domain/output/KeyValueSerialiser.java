package com.github.beastyboo.advancedjail.domain.output;

import com.github.beastyboo.advancedjail.domain.entity.Key;
import org.bukkit.inventory.ItemStack;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Torbie on 21.12.2020.
 */
public class KeyValueSerialiser implements ValueSerialiser<Key>{

    @Override
    public Class<Key> getTargetClass() {
        return Key.class;
    }

    @Override
    public Key deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = new HashMap<>();

        for(Map.Entry<FlexibleType, FlexibleType> mapEntry : flexibleType.getMap().entrySet()) {
            map.put(mapEntry.getKey().getString(), mapEntry.getValue());
        }

        FlexibleType name = map.get("name");
        FlexibleType displayName = map.get("display-name");
        FlexibleType permission = map.get("permission");
        FlexibleType isPincer = map.get("is-pincer");
        FlexibleType itemStack = map.get("itemstack");

        if (name == null || displayName == null || permission == null || isPincer == null || itemStack == null) {
            throw flexibleType.badValueExceptionBuilder().message("Incorrect input to generate a key with.").build();
        }

        return new Key(name.getString(), displayName.getString(), permission.getString(), isPincer.getBoolean(), itemStack.getObject(ItemStack.class));
    }

    @Override
    public Object serialise(Key value, Decomposer decomposer) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", value.getName());
        map.put("display-name", value.getDisplayName());
        map.put("permission", value.getPermission());
        map.put("is-pincer", value.isPincer());
        map.put("itemstack", decomposer.decompose(ItemStack.class, value.getItemStack()));
        return map;
    }
}
