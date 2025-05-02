package com.zygon.rl.world;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.WorldElement;

/**
 *
 */
public class Tangible implements Identifable {

    private final WorldElement template;
    private final int weight;

    public Tangible(WorldElement template, int weight) {
        this.template = template;
        this.weight = weight;
    }

    public Tangible(WorldElement template) {
        this(template, template.getWeight());
    }

    public <T extends WorldElement> T getTemplate() {
        return (T) template;
    }

    @Override
    public String getId() {
        return template.getId();
    }

    public String getDescription() {
        return template.getDescription();
    }

    public String getName() {
        return template.getName();
    }

    public String getType() {
        return template.getType();
    }

    public int getWeight() {
        return weight;
    }

    public void toDisplay(List<String> toDisplay) {
        getTemplate().toDisplay(toDisplay);
        toDisplay.add("weight: " + getWeight());
    }

    @Override
    public String toString() {
        ArrayList<String> ls = new ArrayList<>();
        toDisplay(ls);
        return ls.stream().collect(Collectors.joining(","));
    }
}
