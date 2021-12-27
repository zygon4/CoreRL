package com.zygon.rl.data.items;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.ItemClass;
import com.zygon.rl.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class Building extends ItemClass {

    public static enum TypeNames {
        DOOR
    }

    private static final Map<String, Building> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Building>>() {
    }.getType();

    private static final String PATH = "/data/items/building.json";

    public static void load() throws FileNotFoundException, IOException {

        List<Building> element = null;
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Building.class.getResourceAsStream(PATH)))) {
            element = StringUtil.JSON.fromJson(jsonReader, TYPE);
        }

        BY_ID.putAll(element.stream()
                .collect(Collectors.toMap(m -> m.getId(), m -> m)));
    }

    public static Building get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
