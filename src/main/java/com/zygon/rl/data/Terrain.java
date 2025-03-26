package com.zygon.rl.data;

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
import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class Terrain extends WorldElement {

    // A lot of these should be items
    public static enum Ids {

        DIRT("t_dirt"),
        GRASS("t_grass"),
        TALL_GRASS("t_tall_grass"),
        DEEP_WATER("t_deep_water"),
        PUDDLE("t_puddle"),
        SAND("t_sand"),
        TREE("t_tree"),
        WALL("t_rock_wall");

        private final String id;

        private Ids(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public Terrain get() {
            return Terrain.get(id);
        }
    }

    private static final Map<String, Terrain> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Terrain>>() {
    }.getType();

    private static final String PATH = "/data/terrain.json";

    public static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                Terrain.class.getResourceAsStream(PATH)))) {
            List<Terrain> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(PATH);
    }

    public static Terrain get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
