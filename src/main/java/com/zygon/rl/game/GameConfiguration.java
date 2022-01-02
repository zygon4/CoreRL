package com.zygon.rl.game;

import java.nio.file.Path;
import java.util.Random;

/**
 * Starting off as a POJO for now in case we need to JSON it.
 */
public class GameConfiguration {

    private final SpawnContext worldSpawn = new SpawnContext();
    private String gameName = "";
    private Path musicFile;
    private Random random;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String name) {
        this.gameName = name;
    }

    public void setMusicFile(Path musicFile) {
        this.musicFile = musicFile;
    }

    public Path getMusicFile() {
        return musicFile;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public SpawnContext getSpawnContext() {
        return worldSpawn;
    }
}
