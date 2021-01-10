package com.zygon.rl.world.character;

import java.util.Set;

/**
 * // TODO: what else? ie how does this interact with the game?
 *
 * @author zygon
 */
public class Class {

    private final String name;
    private final Set<Trait> traits;

    public Class(String name, Set<Trait> traits) {
        this.name = name;
        this.traits = traits;
    }

    public String getName() {
        return name;
    }

    public Set<Trait> getTraits() {
        return traits;
    }
}
