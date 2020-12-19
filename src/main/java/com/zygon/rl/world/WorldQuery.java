package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public class WorldQuery {

    private final Map<String, String> attributes;

    private WorldQuery(Builder builder) {
        this.attributes = Collections.unmodifiableMap(builder.attributes);
    }

    /*pkg*/ Map<String, String> getAttributes() {
        return attributes;
    }

    public class Builder {

        private Map<String, String> attributes = new HashMap<>();

        public Builder addAttribute(String name, String value) {
            attributes.put(name, value);
            return this;
        }

        public WorldQuery build() {
            return new WorldQuery(this);
        }
    }
}
