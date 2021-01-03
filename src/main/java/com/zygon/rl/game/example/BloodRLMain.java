/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.game.AttributeTimedAdjustmentSystem;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameUI;
import com.zygon.rl.util.rng.family.FamilyTreeGenerator;
import com.zygon.rl.util.rng.family.Person;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.Calendar;
import com.zygon.rl.world.CommonAttributeValues;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.IntegerAttribute;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This is a testing area until a new BloodRL2.0 project is made.
 *
 * @author zygon
 */
public class BloodRLMain {

    private static final class BiteAbility implements Ability {

        private final UUID playerUuid;

        public BiteAbility(UUID playerUuid) {
            this.playerUuid = playerUuid;
        }

        @Override
        public String getName() {
            return "Bite";
        }

        @Override
        public String availableContext() {
            return "OUTER";
        }

        @Override
        public Target getTargeting() {
            return Target.ADJACENT;
        }

        @Override
        public GameState use(GameState state, Optional<Entity> empty,
                Optional<Location> victimLocation) {

            Entity playerEnt = state.getWorld().get(playerUuid);

            GameState.Builder copy = state.copy();

            // TODO: add game log
            Entity victim = state.getWorld().get(victimLocation.get());
            if (victim != null) {
                if (victim.getId() != playerUuid) {
                    // TODO: biting is a special case attack
                    // needs combat resolution
                    // TODO: calculate bite stats and what happens to the player, etc.
                    // gain health
                    copy.addLog("You feed on the blood of " + victim.getName());
                    state.getWorld().remove(victim);

                    IntegerAttribute hungerLevel = IntegerAttribute.create(
                            playerEnt.getAttribute("HUNGER_CLOCK"));

                    // this is very very clunky to adjust a scalar attribute..
                    state.getWorld().add(playerEnt.copy()
                            .setAttributeValue("HUNGER_CLOCK", String.valueOf(
                                    hungerLevel.getIntegerValue() - 10))
                            .build());
                    copy.setWorld(state.getWorld()
                            .setCalendar(state.getWorld().getCalendar().addTime(30)));
                } else {
                    // special case future ability?
                    copy.addLog("Cannot bite yourself");
                }
            } else {
                copy.addLog("Cannot bite that");
            }

            return copy.build();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace(System.err);
        });

        String strTmp = System.getProperty("java.io.tmpdir");
        // Lots of optionscbvsf or optimizing the audio file(s) into temp space
        File themeFile = Path.of(strTmp, "bloodtheme.wav").toFile().getAbsoluteFile();
        if (!themeFile.exists()) {
            IOUtils.copy(BloodRLMain.class.getResourceAsStream("/audio.wav"),
                    new FileOutputStream(themeFile));
        }

        GameConfiguration config = new GameConfiguration();
        config.setGameName("BloodRL");
        config.setPlayerUuid(UUID.randomUUID());
        config.setMusicFile(themeFile.toPath());
        config.setRandom(new Random());

        Ability bite = new BiteAbility(config.getPlayerUuid());
        config.setCustomAbilities(Set.of(bite));

        int daysPerYear = 20;
        int startingYear = 1208;
        World world = new World(new Calendar(
                TimeUnit.HOURS.toSeconds(7), startingYear * daysPerYear, daysPerYear));

        CharacterSheet pc = new CharacterSheet(
                "Alucard",
                "He's cool",
                new Stats(10, 10, 16, 12, 12),
                new Status(19, 100, Set.of()),
                Set.of(bite),
                Set.of());

        Entity playerEntity = pc.toEntity();
        playerEntity = playerEntity.copy()
                .setId(config.getPlayerUuid())
                .setLocation(Location.create(0, 0))
                // TODO: initial hunger based on starting scenario
                .setAttributeValue("HUNGER_CLOCK", String.valueOf(150))
                .build();

        world.add(playerEntity);

        for (int i = -3; i < 3; i++) {
            Person npc = FamilyTreeGenerator.create();
            Entity npcEntity = Entities.createMonster(npc.getName().toString())
                    .setOrigin(Location.create(i, 1))
                    .setLocation(Location.create(i, 1))
                    .addAttributes(Attribute.builder()
                            .setName(CommonAttributes.TEMPERMENT.name())
                            .setValue(CommonAttributeValues.HOSTILE.name())
                            .build())
                    .build();

            world.add(npcEntity);
        }

        GameState initialState = GameState.builder(config)
                .setWorld(world)
                .build();

        Game game = Game.builder(config)
                .addGameSystem(new AttributeTimedAdjustmentSystem(config,
                        "HUNGER_CLOCK",
                        TimeUnit.HOURS.toSeconds(1),
                        gs -> 1l,
                        hungerValue -> {
                            if (hungerValue < 0) {
                                return "FULL";
                            } else if (hungerValue > 99) {
                                return "HUNGRY";
                            } else {
                                return null;
                            }
                        }))
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
