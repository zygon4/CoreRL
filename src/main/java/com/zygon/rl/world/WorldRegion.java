package com.zygon.rl.world;

import com.zygon.rl.data.Terrain;

/**
 * Could be programmable with json as well..
 */
public enum WorldRegion {

    DEEP_WATER(Terrain.Ids.DEEP_WATER.getId()),
    SHALLOW_WATER(Terrain.Ids.PUDDLE.getId()),
    SHORE(Terrain.Ids.SAND.getId()),
    SHORT_FIELD(Terrain.Ids.GRASS.getId()),
    TALL_FIELD(Terrain.Ids.TALL_GRASS.getId()),
    FOREST(Terrain.Ids.TREE.getId()),
    TOWN_OUTER(Terrain.Ids.GRASS.getId()),
    TOWN_RESIDENCE(Terrain.Ids.DIRT.getId());

    private final String defaultTerrainId;

    private WorldRegion(String defaultTerrainId) {
        this.defaultTerrainId = defaultTerrainId;
    }

    public String getDefaultTerrainId() {
        return defaultTerrainId;
    }

    public Terrain getDefaultTerrain() {
        return Terrain.get(defaultTerrainId);
    }
}
