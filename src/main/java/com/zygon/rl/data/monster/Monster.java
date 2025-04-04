package com.zygon.rl.data.monster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.Creature;
import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class Monster extends Creature {

    private static final System.Logger logger = System.getLogger(Monster.class.getCanonicalName());

    public static final String MONSTER_TYPE = "MONSTER";

    private static final Map<String, Monster> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Monster>>() {
    }.getType();

    private static final String HUMANOID_PATH = "/data/monsters/humanoid.json";
    private static final String RESOURCE_PATH = "/data/monsters/monsters.json";

    public Monster() {
        super();
    }

    private static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                Monster.class.getResourceAsStream(path)))) {
            List<Monster> monsters = StringUtil.JSON.fromJson(jsonReader, TYPE);

            logger.log(Level.INFO, "Loading : {0}", path);
            monsters.forEach(m -> {
                logger.log(Level.INFO, "Loading : {0}", m);
            });

            BY_ID.putAll(monsters.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(HUMANOID_PATH);
        load(RESOURCE_PATH);
    }

    public static Monster get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
