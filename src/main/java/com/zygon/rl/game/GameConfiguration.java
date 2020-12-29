package com.zygon.rl.game;

import com.zygon.rl.world.character.Ability;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * Starting off as a POJO for now in case we need to JSON it.
 */
public class GameConfiguration {

    private String gameName = "";
    private UUID playerUuid;
    private Path musicFile;
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

    public void setMusicFile(Path musicFile) {
        this.musicFile = musicFile;
    }

    public Path getMusicFile() {
        return musicFile;
    }

    public Map<String, Ability> getCustomAbilities() {
        return customAbilities;
    }

    public void setCustomAbilities(Map<String, Ability> customAbilities) {
        this.customAbilities = customAbilities;
    }
}
