package com.zygon.rl.data.buildings;

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
 * @author zygon
 */
public final class BuildingData extends Building {

    private static final Map<String, BuildingData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<BuildingData>>() {
    }.getType();

    private static final String HOUSE_PATH = "/data/buildings/house.json";

    private static void load(String path) throws FileNotFoundException, IOException {

        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                BuildingData.class.getResourceAsStream(HOUSE_PATH)))) {
            List<BuildingData> data = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(data.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(HOUSE_PATH);

        // TODO: more
    }

    public static BuildingData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
