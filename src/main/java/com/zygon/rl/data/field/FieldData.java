package com.zygon.rl.data.field;

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
public class FieldData extends Element {

    private static final String TYPE_NAME = "FIELD";

    private static final Map<String, FieldData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<FieldData>>() {
    }.getType();

    private static final String PATH = "/data/fields/fields.json";

    public static void load(String path) throws FileNotFoundException, IOException {
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
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

    private List<String> slots;

    public List<String> getSlots() {
        return slots;
    }

    public static boolean isFieldData(Element element) {
        return element.getType().equals(TYPE_NAME);
    }

    public static String getTypeName() {
        return TYPE_NAME;
    }
}
