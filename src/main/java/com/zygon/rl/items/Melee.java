package com.zygon.rl.items;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class Melee {

    private static final Map<String, Melee> MELEE_BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Melee>>() {
    }.getType();

    public static void load(InputStream is) throws FileNotFoundException, IOException {

        List<Melee> melee = null;
        try ( Reader jsonReader = new BufferedReader(new InputStreamReader(is))) {
            melee = StringUtil.JSON.fromJson(jsonReader, TYPE);
        }

        MELEE_BY_ID.putAll(melee.stream()
                .collect(Collectors.toMap(m -> m.getId(), m -> m)));
    }

    public static Melee get(String id) {
        return MELEE_BY_ID.get(id);
    }

    private final String id = null;
    private final String type = null;
    private final String symbol = null;
    private final String name = null;
    private final String description = null;
    private int damage;
    private int dice;
    private int toHit;
    private int toDamage;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDamage() {
        return damage;
    }

    public int getDice() {
        return dice;
    }

    public int getToHit() {
        return toHit;
    }

    public int getToDamage() {
        return toDamage;
    }

    @Override
    public String toString() {
        return StringUtil.JSON.toJson(this);
    }

    /**
     * [
     * {
     * "type": "GENERIC", "id": "sword_wood", "symbol": "!", "color": "brown",
     * "name": { "str": "2-by-sword" }, "description": "A two by four with a
     * cross guard and whittled down point; not much for slashing, but much
     * better than your bare hands.", "material": "wood", "volume": "1250 ml",
     * "weight": "600 g", "bashing": 12, "cutting": 1, "to_hit": 1, "flags": [
     * "SHEATH_SWORD" ], "techniques": [ "WBLOCK_1" ] },
     *
     */
    // TODO: remove main
    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = Melee.class.getResourceAsStream("/melee.json");
        load(resourceAsStream);

        MELEE_BY_ID.forEach((s, m) -> {
            System.out.println(s + " " + m);
        });
    }
}
