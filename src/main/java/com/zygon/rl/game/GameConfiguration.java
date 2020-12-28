package com.zygon.rl.game;

import com.zygon.rl.world.character.Ability;

import java.util.Map;
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
    // Ability isn't serializable right now..
    private Map<String, Ability> customAbilities;

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

    public Map<String, Ability> getCustomAbilities() {
        return customAbilities;
    }

    public void setCustomAbilities(Map<String, Ability> customAbilities) {
        this.customAbilities = customAbilities;
    }
}
