package com.zygon.rl.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Simple audio player, no state/start/stop
 */
public class Audio {

    private final Clip clip;
    private final AudioInputStream audioInputStream;

    public Audio(Path filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // create AudioInputStream object
        audioInputStream
                = AudioSystem.getAudioInputStream(filePath.toFile());

        clip = AudioSystem.getClip();

    }

    // Method to play the audio
    public void play() throws LineUnavailableException, IOException {
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
}
