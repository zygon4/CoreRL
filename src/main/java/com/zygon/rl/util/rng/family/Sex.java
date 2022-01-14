package com.zygon.rl.util.rng.family;

public enum Sex {
    MALE("m"), FEMALE("f");
    private final String icon;

    private Sex(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
