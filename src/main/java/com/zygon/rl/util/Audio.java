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

  private final Path filePath;

  public Audio(Path filePath) {
    this.filePath = filePath;
  }

  public void play(boolean loop) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
    try ( AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(filePath.toFile())) {
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      if (loop) {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
      }
      setVolume(clip, 0.10f);
      clip.start();
    }
  }

  public void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
    play(false);
  }

  public void playLoop() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
    play(true);
  }

  // https://stackoverflow.com/questions/40514910/set-volume-of-java-clip
  public void setVolume(Clip clip, float volume) {
    if (volume < 0f || volume > 1f) {
      throw new IllegalArgumentException("Volume not valid: " + volume);
    }
    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
      FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      gainControl.setValue(20f * (float) Math.log10(volume));
    }
  }
}
