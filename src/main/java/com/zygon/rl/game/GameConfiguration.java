package com.zygon.rl.game;

import com.zygon.rl.world.character.Ability;

import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Starting off as a POJO for now in case we need to JSON it.
 */
public class GameConfiguration {

    private String gameName = "";
    private UUID playerUuid;
    private Path musicFile;
    // Ability isn't serializable right now..
    private Set<Ability> customAbilities;
    private Random random;

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

    public void setMusicFile(Path musicFile) {
        this.musicFile = musicFile;
    }

    public Path getMusicFile() {
        return musicFile;
    }

    public Set<Ability> getCustomAbilities() {
        return customAbilities;
    }

    public void setCustomAbilities(Set<Ability> customAbilities) {
        this.customAbilities = customAbilities;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
