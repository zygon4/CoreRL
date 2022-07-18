package com.zygon.rl.game;

import com.zygon.rl.util.Audio;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * TODO: document the known/used sound names.
 *
 */
public class SoundEffects {

  private final Path soundPackPath;
  private final Map<String, String> soundEffectsByFileName;
  private final Map<String, Audio> soundEffectsByName = new HashMap<>();

  /**
   * Creates a sound effects object
   *
   * @param soundPackPath Path to a zip file of individual sound effects.
   * @param soundEffectsByFileName A mapping of effect name to file name (no
   * additional paths).
   */
  public SoundEffects(Path soundPackPath,
          Map<String, String> soundEffectsByFileName) {
    this.soundPackPath = soundPackPath;
    this.soundEffectsByFileName = Collections.unmodifiableMap(soundEffectsByFileName);
  }

  public void load() throws IOException {
    String strTmp = System.getProperty("java.io.tmpdir");
    Path tmpDir = Files.createTempDirectory(Path.of(strTmp), "rl");

    ZipEntry entry = null;
    try ( ZipInputStream zipIn = new ZipInputStream(new FileInputStream(soundPackPath.toFile()))) {
      while ((entry = zipIn.getNextEntry()) != null) {

        if (!entry.isDirectory()) {
          String entryName = entry.getName();
          Path entryPath = Path.of(entryName);
          String fileName = entryPath.getName(entryPath.getNameCount() - 1).toString();

          byte[] bytesRead = zipIn.readAllBytes();
          Path filePath = Path.of(tmpDir.toString(), fileName);
          try ( FileOutputStream os = new FileOutputStream(filePath.toString())) {
            IOUtils.write(bytesRead, os);
          }
          String soundEffectName = soundEffectsByFileName.get(fileName);
          if (soundEffectName != null) {
            // We care about this one..
            Audio soundEffect = new Audio(filePath);
            soundEffectsByName.put(soundEffectName, soundEffect);
          }
        }
      }
    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  public void play(String name) {
    Audio soundEffect = soundEffectsByName.get(name);
    if (soundEffect != null) {
      try {
        soundEffect.play();
      } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
        // Not sure if we want to fail due to this..
        ex.printStackTrace();
      }
    }
  }
}
