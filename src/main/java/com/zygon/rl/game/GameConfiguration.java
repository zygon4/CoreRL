package com.zygon.rl.game;

import java.util.UUID;

/**
 * Starting off as a POJO for now in case we need to JSON it.
 */
public class GameConfiguration {

    private String gameName = "";
    // this needs to take in account other factors like context (where are the
    // people spawned, in a town?)
    private double npcSpawnRate = 0.01;
    private UUID playerUuid;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String name) {
        this.gameName = name;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public double getNpcSpawnRate() {
        return npcSpawnRate;
    }

    public void setNpcSpawnRate(double npcSpawnRate) {
        this.npcSpawnRate = npcSpawnRate;
    }
}
