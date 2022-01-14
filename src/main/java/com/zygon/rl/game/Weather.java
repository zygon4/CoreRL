package com.zygon.rl.game;

/**
 * Weather enum
 */
public enum Weather {
    CLEAR(0, "Clear"),
    CLOUDY(1, "Cloudy"),
    RAINY(2, "Rainy");

    private Weather(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    private final int value;
    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    int getValue() {
        return value;
    }

    public static Weather valueOf(int value) {
        for (Weather weather : Weather.values()) {
            if (weather.getValue() == value) {
                return weather;
            }
        }
        throw new UnsupportedOperationException("Unknown value " + value);
    }
}
