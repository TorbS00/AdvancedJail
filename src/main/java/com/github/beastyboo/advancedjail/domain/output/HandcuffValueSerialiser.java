package com.github.beastyboo.advancedjail.domain.output;

import com.github.beastyboo.advancedjail.domain.entity.Handcuff;
import com.github.beastyboo.advancedjail.domain.entity.Key;
import org.bukkit.inventory.ItemStack;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Torbie on 22.12.2020.
 */
public class HandcuffValueSerialiser implements ValueSerialiser<Handcuff>{


    @Override
    public Class<Handcuff> getTargetClass() {
        return Handcuff.class;
    }

    @Override
    public Handcuff deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = new HashMap<>();

        for(Map.Entry<FlexibleType, FlexibleType> mapEntry : flexibleType.getMap().entrySet()) {
            map.put(mapEntry.getKey().getString(), mapEntry.getValue());
        }

        FlexibleType name = map.get("name");
        FlexibleType displayName = map.get("display-name");
        FlexibleType permission = map.get("permission");
        FlexibleType itemStack = map.get("itemstack");
        FlexibleType hasKey = map.get("hasKey");
        FlexibleType key = map.get("key");

        if (name == null || displayName == null || permission == null || itemStack == null || hasKey == null || key == null) {
            throw flexibleType.badValueExceptionBuilder().message("Incorrect input to generate a handcuff with.").build();
        }

        return new Handcuff(name.getString(), displayName.getString(), permission.getString(), itemStack.getObject(ItemStack.class), hasKey.getBoolean(), key.getObject(Key.class));
    }

    @Override
    public Object serialise(Handcuff value, Decomposer decomposer) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", value.getName());
        map.put("display-name", value.getDisplayName());
        map.put("permission", value.getPermission());
        map.put("itemstack", decomposer.decompose(ItemStack.class, value.getItemStack()));
        map.put("hasKey", value.isHasKey());
        map.put("key", decomposer.decompose(Key.class, value.getKey()));
        return map;
    }
}
