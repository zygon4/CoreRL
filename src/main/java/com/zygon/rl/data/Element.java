package com.zygon.rl.data;

import com.zygon.rl.util.StringUtil;

/**
 * This is more of a template/prototype pattern. This should not be saved
 */
public class Element implements Identifable {

    private String id = null;
    private String type = null;
    private String symbol = null;
    private String color = null;
    private String name = null;
    private String description = null;

    @Override
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

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setColor(String color) {
        this.color = color;
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
