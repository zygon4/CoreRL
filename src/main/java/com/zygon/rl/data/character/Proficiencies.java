package com.zygon.rl.data.character;

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
import com.zygon.rl.data.Element;
import com.zygon.rl.util.StringUtil;

/**
 *
 * @author zygon
 */
public final class Proficiencies extends Element {

    private static final Logger logger = System.getLogger(Proficiencies.class.getCanonicalName());

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
    public static enum Names {
        ILLUSION(Proficiencies.get("illusion")),
        DIVINATION(Proficiencies.get("divination")),
        CHARM(Proficiencies.get("charm")),
        ALCHEMY(Proficiencies.get("alchemy")),
        CONJURATION(Proficiencies.get("conjuration")),
        NECROMANCY(Proficiencies.get("necromancy")),
        ARCANE(Proficiencies.get("arcane")),
        ENCHANTING(Proficiencies.get("enchanting")),
        STEALTH(Proficiencies.get("stealth")),
        MELEE(Proficiencies.get("melee")),
        RANGED(Proficiencies.get("ranged")),
        ANATOMY(Proficiencies.get("anatomy"));

        private final Proficiencies proficiency;

        public String getId() {
            return proficiency.getId();
        }

        public Proficiencies getProficiency() {
            return proficiency;
        }

        public static Names getInstance(String effectId) {
            for (Names effectName : Names.values()) {
                if (effectName.getId().equals(effectId)) {
                    return effectName;
                }
            }
            return null;
        }

        private Names(Proficiencies effect) {
            this.proficiency = effect;
        }
    }

    private static final Map<String, Proficiencies> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Proficiencies>>() {
    }.getType();

    private static final String PATH = "/data/character/proficiencies.json";

    public Proficiencies() {
        super();
    }

    public static void load() throws FileNotFoundException, IOException {

        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                Proficiencies.class.getResourceAsStream(PATH)))) {

            List<Proficiencies> data = StringUtil.JSON.fromJson(jsonReader, TYPE);

            data.forEach(json -> {
                logger.log(Level.DEBUG, "Loading {0}", json);
            });

            BY_ID.putAll(data.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static Proficiencies get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    private List<StatMod> statMods;

    public List<StatMod> getStatMods() {
        return statMods;
    }

    public void setStatMods(List<StatMod> statMods) {
        this.statMods = statMods;
    }
}
