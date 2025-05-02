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
public class Melee extends ItemClass {

    private static final Map<String, Melee> MELEE_BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<Melee>>() {
    }.getType();

    private static final String MELEE_PATH = "/data/items/melee.json";

    public static void load() throws FileNotFoundException, IOException {

        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                Melee.class.getResourceAsStream(MELEE_PATH)))) {
            List<Melee> melee = StringUtil.JSON.fromJson(jsonReader, TYPE);

            MELEE_BY_ID.putAll(melee.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static Melee get(String id) {
        return MELEE_BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return MELEE_BY_ID.keySet();
    }

    private int damage;
    private int dice;
    private int toHit;
    private int toDamage;

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

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public void setToHit(int toHit) {
        this.toHit = toHit;
    }

    public void setToDamage(int toDamage) {
        this.toDamage = toDamage;
    }

    @Override
    public void toDisplay(List<String> toDisplay) {
        super.toDisplay(toDisplay);

        toDisplay.add(getDice() + "d" + getDamage());
        if (getToHit() > 0) {
            toDisplay.add("+" + getToHit());
        }
    }
}
