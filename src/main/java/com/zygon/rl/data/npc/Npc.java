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

        List<Npc> melee = null;
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Npc.class.getResourceAsStream(RESOURCE_PATH)))) {
            melee = StringUtil.JSON.fromJson(jsonReader, TYPE);
        }

        NPC_BY_ID.putAll(melee.stream()
                .collect(Collectors.toMap(m -> m.getId(), m -> m)));
    }

    public static Npc get(String id) {
        return NPC_BY_ID.get(id);
    }

    private int aggression;

    public int getAggression() {
        return aggression;
    }

    @Override
    public String toString() {
        return StringUtil.JSON.toJson(this);
    }

    /**
     * [
     * {
     * "type": "GENERIC", "id": "sword_wood", "symbol": "!", "color": "brown",
     * "name": { "str": "2-by-sword" }, "description": "A two by four with a
     * cross guard and whittled down point; not much for slashing, but much
     * better than your bare hands.", "material": "wood", "volume": "1250 ml",
     * "weight": "600 g", "bashing": 12, "cutting": 1, "to_hit": 1, "flags": [
     * "SHEATH_SWORD" ], "techniques": [ "WBLOCK_1" ] },
     *
     */
    // TODO: remove main
    public static void main(String[] args) throws IOException {

        NPC_BY_ID.forEach((s, m) -> {
            System.out.println(s + " " + m);
        });
    }
}