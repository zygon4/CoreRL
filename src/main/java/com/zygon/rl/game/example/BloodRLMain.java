/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.game.AttributeTimedAdjustmentSystem;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.ui.GameUI;
import com.zygon.rl.util.rng.family.FamilyTreeGenerator;
import com.zygon.rl.util.rng.family.Person;
import com.zygon.rl.world.Calendar;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
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

    private static final class DrainBlood implements Ability {

        private final UUID playerUuid;

        public DrainBlood(UUID playerUuid) {
            this.playerUuid = playerUuid;
        }

        @Override
        public String getName() {
            return "Drain Blood";
        }

        @Override
        public String availableContext() {
            return "OUTER";
        }

        @Override
        public Target getTargeting() {
            // TODO: and 'held' or disabled
            return Target.ADJACENT;
        }

        @Override
        public GameState use(GameState state, Optional<Element> empty,
                Optional<Location> victimLocation) {

            CharacterSheet player = state.getWorld().getPlayer();

            GameState.Builder copy = state.copy();

            // TODO: add game log
            CharacterSheet victim = state.getWorld().get(victimLocation.get());
            if (victim != null) {
                // TODO: biting is a special case attack
                // needs combat resolution
                // TODO: calculate bite stats and what happens to the player, etc.
                // gain health
                copy.addLog("You feed on the blood of " + victim.getName());
                state.getWorld().remove(victim, victimLocation.get());

                int hungerLevel = player.getStatus().getEffects().get("HUNGER_CLOCK");

                // this feels really clunky.. better than before but not perfect
//                state.getWorld().move(
//                        player.set(player.getStatus().addEffect("HUNGER_CLOCK", hungerLevel - 10)),
//                        state.getWorld().getPlayerLocation(),
//                        state.getWorld().getPlayerLocation());
                copy.setWorld(state.getWorld()
                        .setCalendar(state.getWorld().getCalendar().addTime(30)));

            } else {
                copy.addLog("Cannot drain that");
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

        Ability drainBlood = new DrainBlood(config.getPlayerUuid());
        config.setCustomAbilities(Set.of(drainBlood));

        int daysPerYear = 20;
        int startingYear = 1208;
        World world = new World(new Calendar(
                TimeUnit.HOURS.toSeconds(7), startingYear * daysPerYear, daysPerYear));

        CharacterSheet pc = new CharacterSheet(
                "Alucard",
                "He's cool",
                new Stats(12, 12, 12, 10, 10, 12),
                new Status(19, 100, Map.of("HUNGER_CLOCK", 150)),
                new Equipment(new Weapon(18, 2, 6, 2, 0, 1, 0)),
                Set.of(drainBlood),
                Set.of());

        // TODO: this is a hack, someone else should set this
        pc.setId("player");
        pc.setType("player");
        world.add(pc, Location.create(0, 0));

        try {
            //TODO: load somewhere more official
            Npc.load();
        } catch (IOException io) {
            io.printStackTrace(System.err);
        }

        Npc farmerData = Npc.get("npc_generic_farmer");
        for (int i = -3; i < 3; i++) {
            Person npcPerson = FamilyTreeGenerator.create();

            CharacterSheet npcSheet = new CharacterSheet(
                    npcPerson.getName().toString(),
                    farmerData.getDescription(),
                    new Stats(10, 8, 10, 8, 9, 6),
                    new Status(44, 40, Map.of()),
                    new Equipment(new Weapon(18, 4, 8, 3, 1, 2, 0)),
                    Set.of(),
                    Set.of());

            npcSheet.setId(farmerData.getId());
            npcSheet.setType(farmerData.getType());

            world.add(npcSheet, Location.create(i, 5));
        }

        try {
            Melee.load();
        } catch (IOException io) {
            io.printStackTrace(System.err);
        }

        Melee dagger = Melee.get("dagger");
        world.add(dagger.getId(), Location.create(0, 0));
        world.add(dagger.getId(), Location.create(0, -1));

        GameState initialState = GameState.builder(config)
                .setWorld(world)
                .build();

        Game game = Game.builder(config)
                .addGameSystem(new AttributeTimedAdjustmentSystem(config,
                        "HUNGER_CLOCK",
                        TimeUnit.HOURS.toSeconds(1),
                        gs -> 1,
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
