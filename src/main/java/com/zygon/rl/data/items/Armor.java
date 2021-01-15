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
public class Armor extends ItemClass {

    private static final Map<String, Armor> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Armor>>() {
    }.getType();

    private static final String ARMOR_BOOTS_PATH = "/data/items/armor_boots.json";

    public static void load(String path) throws FileNotFoundException, IOException {
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Armor.class.getResourceAsStream(ARMOR_BOOTS_PATH)))) {
            List<Armor> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(ARMOR_BOOTS_PATH);
        // TODO: more
    }

    public static Armor get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    private List<String> slots;

    public List<String> getSlots() {
        return slots;
    }
}
