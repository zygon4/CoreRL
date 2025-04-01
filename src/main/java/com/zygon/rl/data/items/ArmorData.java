package com.zygon.rl.data.items;

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

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.ItemClass;
import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class ArmorData extends ItemClass {

    private static final Map<String, ArmorData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<ArmorData>>() {
    }.getType();

    private static final String ARMOR_ARMS_PATH = "/data/items/armor_arms.json";
    private static final String ARMOR_TORSO_PATH = "/data/items/armor_torso.json";
    private static final String ARMOR_LEGS_PATH = "/data/items/armor_legs.json";
    private static final String ARMOR_FEET_PATH = "/data/items/armor_feet.json";

    private static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                ArmorData.class.getResourceAsStream(path)))) {
            List<ArmorData> items = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(items.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(ARMOR_ARMS_PATH);
        load(ARMOR_TORSO_PATH);
        load(ARMOR_LEGS_PATH);
        load(ARMOR_FEET_PATH);
        // TODO: more
    }

    public static ArmorData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    private int av;
    private List<String> slots;

    public int getAv() {
        return av;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setAv(int av) {
        this.av = av;
    }

    public void setSlots(List<String> slots) {
        this.slots = slots;
    }
}
