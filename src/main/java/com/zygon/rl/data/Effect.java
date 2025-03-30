package com.zygon.rl.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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
public final class Effect extends Element {

    private static final Logger logger = System.getLogger(Effect.class.getCanonicalName());

    public static class StatMod {

        private String name;
        private int amount;

        public StatMod() {
        }

        public int getAmount() {
            return amount;
        }

        public String getName() {
            return name;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Not sure if this can keep up..
    public static enum EffectNames {
        BLEEDING_MAJOR(Effect.get("effect_bleeding_major")),
        CONFUSION(Effect.get("effect_confusion")),
        ENHANCED_SPEED(Effect.get("effect_enhanced_speed")),
        HOSTILE(Effect.get("effect_hostile")),
        PET(Effect.get("effect_pet")),
        TERRIFIED(Effect.get("effect_terror")),
        SENTRY(Effect.get("effect_sentry")),
        SUN_FEVER_MINOR(Effect.get("effect_sun_fever_minor")),
        HEALING_MINOR(Effect.get("effect_healing_minor"));

        private final Effect effect;

        public String getId() {
            return effect.getId();
        }

        public Effect getEffect() {
            return effect;
        }

        public static EffectNames getInstance(String effectId) {
            for (EffectNames effectName : EffectNames.values()) {
                if (effectName.getId().equals(effectId)) {
                    return effectName;
                }
            }
            return null;
        }

        private EffectNames(Effect effect) {
            this.effect = effect;
        }
    }

    private static final Map<String, Effect> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Effect>>() {
    }.getType();

    private static final String PATH = "/data/effects.json";

    public Effect() {
        super();
    }

    public static void load() throws FileNotFoundException, IOException {

        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                Effect.class.getResourceAsStream(PATH)))) {

            List<Effect> data = StringUtil.JSON.fromJson(jsonReader, TYPE);

            data.forEach(effect -> {
                logger.log(Level.DEBUG, "Loading {0}", effect);
            });

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
    private List<StatMod> statMods;

    public int getMaxDuration() {
        return maxDuration;
    }

    public List<StatMod> getStatMods() {
        return statMods;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setStatMods(List<StatMod> statMods) {
        this.statMods = statMods;
    }
}
