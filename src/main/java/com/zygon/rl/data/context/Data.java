package com.zygon.rl.data.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.Element;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.buildings.BuildingData;
import com.zygon.rl.data.field.FieldData;
import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.data.items.BookData;
import com.zygon.rl.data.items.Building;
import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;

/**
 * Contains all of the data by Id.
 *
 * @author zygon
 */
public class Data {

    private static final Map<String, Element> elementsById = new HashMap<>();

    static {
        try {
            ArmorData.load();
            BookData.load();
            Building.load(); // building items
            BuildingData.load(); // building templates
            Effect.load();
            FieldData.load();
            Melee.load();
            Monster.load();
            Npc.load();
            Corpse.load();
            Terrain.load();
        } catch (IOException io) {
            io.printStackTrace(System.err);
            System.exit(1);
        }
    }

    // And put above
    public static void load() {
        // get key -> val vs. just gimme the map directly?
        for (var v : ArmorData.getAllIds()) {
            elementsById.put(v, ArmorData.get(v));
        }

        for (var v : BookData.getAllIds()) {
            elementsById.put(v, BookData.get(v));
        }

        for (var v : Building.getAllIds()) {
            elementsById.put(v, Building.get(v));
        }

        for (var v : BuildingData.getAllIds()) {
            elementsById.put(v, BuildingData.get(v));
        }

        for (var v : Effect.getAllIds()) {
            elementsById.put(v, Effect.get(v));
        }

        for (var v : FieldData.getAllIds()) {
            elementsById.put(v, FieldData.get(v));
        }

        for (var v : Melee.getAllIds()) {
            elementsById.put(v, Melee.get(v));
        }

        for (var v : Monster.getAllIds()) {
            elementsById.put(v, Monster.get(v));
        }

        for (var v : Npc.getAllIds()) {
            elementsById.put(v, Npc.get(v));
        }

        for (var v : Corpse.getAllIds()) {
            elementsById.put(v, Corpse.get(v));
        }

        for (var v : Terrain.getAllIds()) {
            elementsById.put(v, Terrain.get(v));
        }
    }

    public static <T extends Element> T get(String id) {
        return (T) elementsById.get(id);
    }
}
