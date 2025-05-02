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
    private SoundEffects soundEffects;
    private Random random;
    private String weightUnit = "lbs";

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

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public SoundEffects getSoundEffects() {
        return soundEffects;
    }

    public void setSoundEffects(SoundEffects soundEffects) {
        this.soundEffects = soundEffects;
    }

    public SpawnContext getSpawnContext() {
        return worldSpawn;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
}
