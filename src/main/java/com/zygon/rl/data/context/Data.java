package com.zygon.rl.data.context;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.npc.Npc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public class Data {

    private static final Map<String, Element> elementsById = new HashMap<>();

    static {
        try {
            Melee.load();
            Npc.load();
            load();
        } catch (IOException io) {
            io.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void load() {
        // get key -> val vs. just gimme the map directly?
        for (var v : Melee.getAllIds()) {
            elementsById.put(v, Melee.get(v));
        }

        for (var v : Npc.getAllIds()) {
            elementsById.put(v, Npc.get(v));
        }
    }

    public static Element get(String id) {
        return elementsById.get(id);
    }
}
