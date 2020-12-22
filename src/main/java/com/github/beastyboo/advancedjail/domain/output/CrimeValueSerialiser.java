package com.github.beastyboo.advancedjail.domain.output;

import com.github.beastyboo.advancedjail.domain.entity.Crime;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Torbie on 22.12.2020.
 */
public class CrimeValueSerialiser implements ValueSerialiser<Crime>{

    @Override
    public Class<Crime> getTargetClass() {
        return Crime.class;
    }

    @Override
    public Crime deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, FlexibleType> map = new HashMap<>();

        for(Map.Entry<FlexibleType, FlexibleType> mapEntry : flexibleType.getMap().entrySet()) {
            map.put(mapEntry.getKey().toString(), mapEntry.getValue());
        }

        FlexibleType name = map.get("name");
        FlexibleType bill = map.get("bill");
        FlexibleType penalty = map.get("penalty");

        if(name == null || bill == null || penalty == null) {
            throw flexibleType.badValueExceptionBuilder().message("Incorrect input to generate a crime with.").build();
        }

        return new Crime(name.getString(), bill.getDouble(), penalty.getInteger());
    }

    @Override
    public Object serialise(Crime value, Decomposer decomposer) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", value.getName());
        map.put("bill", value.getBill());
        map.put("penalty", value.getPenalty());
        return map;
    }
}
