package com.zygon.rl.game;

import com.zygon.rl.world.WorldSpawn;

import java.nio.file.Path;
import java.util.Random;

/**
 * Starting off as a POJO for now in case we need to JSON it.
 */
public class GameConfiguration {

    private String gameName = "";
    private Path musicFile;
    private Random random;
    private WorldSpawn worldSpawn;

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

    public WorldSpawn getWorldSpawn() {
        return worldSpawn;
    }

    public void setWorldSpawn(WorldSpawn worldSpawn) {
        this.worldSpawn = worldSpawn;
    }
}
