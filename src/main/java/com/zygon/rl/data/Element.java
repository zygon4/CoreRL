package com.zygon.rl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class Element implements Identifable {

    private String id = null;
    private String type = null;
    private String symbol = null;
    private String color = null;
    private String name = null;
    private String description = null;
    private Map<String, Object> flags = null;

    public Element(String id, String type, String symbol, String color,
            String name, String description, Map<String, Object> flags) {
        this.id = id;
        this.type = type;
        this.symbol = symbol;
        this.color = color;
        this.name = name;
        this.description = description;
        this.flags = flags;
    }

    public Element() {

    }

    protected Element(Element element) {
        this(element.getId(), element.getType(), element.getSymbol(), element.getColor(),
                element.getName(), element.getDescription(), element.getFlags());
    }

    @Override
    public final String getId() {
        return id;
    }

    public final String getType() {
        return type;
    }

    public final String getSymbol() {
        return symbol;
    }

    public final String getColor() {
        return color;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public Map<String, Object> getFlags() {
        return flags;
    }

    public <T> T getFlag(String name) {
        if (flags == null || flags.isEmpty()) {
            return null;
        }
        Object flag = flags.get(name);
        return flag != null ? (T) flags.get(name) : null;
    }

    // Kind of a weak way to implement this check.
    public boolean isWorldElement() {
        return false;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public final void setColor(String color) {
        this.color = color;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setFlags(Map<String, Object> flags) {
        this.flags = flags;
    }

    public void toDisplay(List<String> toDisplay) {
        toDisplay.add(getName());
        toDisplay.add(getDescription());
        // TODO: display flags in a meaningful way.
    }

    @Override
    public String toString() {
        ArrayList<String> ls = new ArrayList<>();
        toDisplay(ls);
        return ls.stream().collect(Collectors.joining(","));
    }
}
