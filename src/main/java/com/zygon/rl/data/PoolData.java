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
public final class PoolData extends Element {

    private static final Logger logger = System.getLogger(PoolData.class.getCanonicalName());

    // TODO: finish
    public static class Effects {

        private String statusEffectId;
        private int pct;

        public Effects() {
        }

        public int getPct() {
            return pct;
        }

        public String getStatusEffectId() {
            return statusEffectId;
        }

        public void setPct(int pct) {
            this.pct = pct;
        }

        public void setStatusEffectId(String statusEffectId) {
            this.statusEffectId = statusEffectId;
        }
    }

    public static enum Pools {
        HEALTH_SMALL(PoolData.get("pool_health_small")),
        HEALTH_MEDIUM(PoolData.get("pool_health_medium")),
        HEALTH_LARGE(PoolData.get("pool_health_large")),
        BLOOD_MEDIUM(PoolData.get("pool_blood_medium"));

        private final PoolData pool;

        public String getId() {
            return pool.getId();
        }

        public PoolData getEffect() {
            return pool;
        }

        public static Pools getInstance(String poolId) {
            for (Pools id : Pools.values()) {
                if (id.getId().equals(poolId)) {
                    return id;
                }
            }
            return null;
        }

        private Pools(PoolData effect) {
            this.pool = effect;
        }
    }

    private static final Map<String, PoolData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<PoolData>>() {
    }.getType();

    private static final String PATH = "/data/pool.json";

    public PoolData() {
        super();
    }

    public static void load() throws FileNotFoundException, IOException {

        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                PoolData.class.getResourceAsStream(PATH)))) {

            List<PoolData> data = StringUtil.JSON.fromJson(jsonReader, TYPE);

            data.forEach(pool -> {
                logger.log(Level.DEBUG, "Loading {0}", pool);
            });

            BY_ID.putAll(data.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static PoolData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    // TODO: Effects
    private boolean actsAsHealth;
    private int drainFrequency;
    private int min;
    private int max;

    public boolean actsAsHealth() {
        return actsAsHealth;
    }

    public int getDrainFrequency() {
        return drainFrequency;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }
}
