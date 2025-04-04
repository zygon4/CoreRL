package com.zygon.rl.data.monstergroups;

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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.Element;
import com.zygon.rl.util.NoiseUtil;
import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class MonsterGroups extends Element {

    private static final System.Logger logger = System.getLogger(MonsterGroups.class.getCanonicalName());

    public static final String MONSTER_TYPE = "MONSTER";

    private static final Map<String, MonsterGroups> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<MonsterGroups>>() {
    }.getType();

    private static final String CITY_PATH = "/data/monstergroups/city.json";
    private static final String WILDERNESS_PATH = "/data/monstergroups/wilderness.json";

    // String type ??
    private List<Group> groups;

    public MonsterGroups() {
        super();
    }

    private static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                MonsterGroups.class.getResourceAsStream(path)))) {
            List<MonsterGroups> ls = StringUtil.JSON.fromJson(jsonReader, TYPE);

            logger.log(Level.DEBUG, "Loading : {0}", path);
            ls.forEach(m -> {
                logger.log(Level.DEBUG, "Loading : {0}", m);
            });

            BY_ID.putAll(ls.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(CITY_PATH);
        load(WILDERNESS_PATH);
    }

    public static MonsterGroups get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Weighted random group based on the groups' weight.
     *
     * @param random
     * @return
     */
    public Group getGroup(Random random) {
        List<Integer> weights = groups.stream()
                .map(Group::getWeight)
                .collect(Collectors.toList());
        int index = NoiseUtil.getWeightedRandom(random, weights);
        return groups.get(index);
    }
}
