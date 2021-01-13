package com.zygon.rl.data.npc;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.Element;
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
public class Npc extends Element {

    private static final Map<String, Npc> NPC_BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Npc>>() {
    }.getType();

    private static final String RESOURCE_PATH = "/data/npcs/npc.json";

    public static void load() throws FileNotFoundException, IOException {
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Npc.class.getResourceAsStream(RESOURCE_PATH)))) {
            List<Npc> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);
            NPC_BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static Npc get(String id) {
        return NPC_BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return NPC_BY_ID.keySet();
    }

    private int aggression;

    public int getAggression() {
        return aggression;
    }
}
