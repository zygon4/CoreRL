package com.zygon.rl.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
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
        setVolume(0.10f);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    // https://stackoverflow.com/questions/40514910/set-volume-of-java-clip
    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("Volume not valid: " + volume);
        }
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }
}
