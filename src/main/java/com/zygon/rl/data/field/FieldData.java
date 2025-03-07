package com.zygon.rl.data.field;

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
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.util.StringUtil;
import com.zygon.rl.world.CommonAttributes;

/**
 *
 */
public class FieldData extends WorldElement {

    private static final String TYPE_NAME = "FIELD";

    private static final Map<String, FieldData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<FieldData>>() {
    }.getType();

    private static final String PATH = "/data/fields/fields.json";

    public static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                FieldData.class.getResourceAsStream(PATH)))) {
            List<FieldData> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(PATH);
    }

    public static FieldData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    public static boolean isFieldData(WorldElement element) {
        return element.getType().equals(TYPE_NAME);
    }

    public static boolean isEnvironmental(String id) {
        Boolean magic = get(id).getFlag(CommonAttributes.MAGIC.name());
        return magic == null || !magic;
    }

    public static String getTypeName() {
        return TYPE_NAME;
    }
}
