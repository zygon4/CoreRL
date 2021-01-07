package com.zygon.rl.data;

import com.zygon.rl.util.StringUtil;

/**
 * Entity is a better term but it's taken.
 */
public class Element {

    private final String id = null;
    private final String type = null;
    private final String symbol = null;
    private final String color = null;
    private String name = null;
    private String description = null;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return StringUtil.JSON.toJson(this);
    }
}
