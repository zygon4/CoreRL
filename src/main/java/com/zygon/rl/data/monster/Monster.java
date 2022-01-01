package com.zygon.rl.data.monster;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.Creature;
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
public class Monster extends Creature {

    public static final String MONSTER_TYPE = "MONSTER";

    private static final Map<String, Monster> NPC_BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Monster>>() {
    }.getType();

    private static final String RESOURCE_PATH = "/data/monsters/monsters.json";

    public static void load() throws FileNotFoundException, IOException {

        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Monster.class.getResourceAsStream(RESOURCE_PATH)))) {
            List<Monster> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);

            NPC_BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static Monster get(String id) {
        return NPC_BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return NPC_BY_ID.keySet();
    }
}
