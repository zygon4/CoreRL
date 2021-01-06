package com.zygon.rl.items;

import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class Item {

    private final String id = null;
    private final String type = null;
    private final String symbol = null;
    private final String name = null;
    private final String description = null;

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

    @Override
    public String toString() {
        return StringUtil.JSON.toJson(this);
    }
}
