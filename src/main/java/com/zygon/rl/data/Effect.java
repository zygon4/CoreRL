package com.zygon.rl.data;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.context.Data;
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
 * @author zygon
 */
public final class Effect extends Element {

    public static enum EffectNames {
        HOSTILE(Data.get("effect_hostile")),
        PET(Data.get("effect_pet"));

        private final Element effect;

        public String getId() {
            return effect.getId();
        }

        private EffectNames(Element effect) {
            this.effect = effect;
        }
    }

    private static final Map<String, Effect> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Effect>>() {
    }.getType();

    private static final String PATH = "/data/effects.json";

    public static void load() throws FileNotFoundException, IOException {

        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(
                Effect.class.getResourceAsStream(PATH)))) {
            List<Effect> data = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(data.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static Effect get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    private int maxDuration;

    public int getMaxDuration() {
        return maxDuration;
    }
}
