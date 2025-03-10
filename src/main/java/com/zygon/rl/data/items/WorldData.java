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
public class WorldData extends ItemClass {

    private static final Map<String, WorldData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<WorldData>>() {
    }.getType();

    private static final String WORLD_ITEMS = "/data/items/world.json";

    public static enum WorldItems {

        STONE("item_stone"),
        ROCK("item_rock"),
        YELLOW_FLOWER("item_yellow_flower"),
        RED_FLOWER("item_red_flower");

        private final String id;

        private WorldItems(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                WorldData.class.getResourceAsStream(path)))) {
            List<WorldData> items = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(items.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(WORLD_ITEMS);
        // TODO: more
    }

    public static WorldData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
