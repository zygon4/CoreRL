package com.zygon.rl.data;

import com.zygon.rl.util.StringUtil;

import java.util.Map;

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

    public final Element setSymbol(String symbol) {
        return new Element(id, type, symbol, color, name, description, flags);
    }

    public final Element setColor(String color) {
        return new Element(id, type, symbol, color, name, description, flags);
    }

    public final Element setName(String name) {
        return new Element(id, type, symbol, color, name, description, flags);
    }

    public final Element setDescription(String description) {
        return new Element(id, type, symbol, color, name, description, flags);
    }

    public final Element setFlags(Map<String, Object> flags) {
        return new Element(id, type, symbol, color, name, description, flags);
    }

    @Override
    public String toString() {
        return StringUtil.JSON.toJson(this);
    }
}
