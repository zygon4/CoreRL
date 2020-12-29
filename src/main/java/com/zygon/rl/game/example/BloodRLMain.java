/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.game.GameUI;
import com.zygon.rl.util.rng.family.FamilyTreeGenerator;
import com.zygon.rl.util.rng.family.Person;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.CharacterTBD;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This is a testing area until a new BloodRL2.0 project is made.
 *
 * @author zygon
 */
public class BloodRLMain {

    // TODO:
    private static final class PlayerHealth extends GameSystem {

        public PlayerHealth() {
        }

        @Override
        public GameState apply(GameState t) {
            return t;
        }
    }

    private static final class NPCWalk extends GameSystem {

        private static final int REALITY_BUBBLE = 50;

        private final UUID playerUuid;
        private final Random random;

        public NPCWalk(UUID playerUuid, Random random) {
            this.playerUuid = playerUuid;
            this.random = random;
        }

        @Override
        public GameState apply(GameState state) {

            Entity playerEnt = state.getWorld().get(playerUuid);
            Location player = playerEnt.getLocation();
            Set<Entity> closeNPCs = state.getWorld().getAll(playerEnt.getLocation(), REALITY_BUBBLE);

            for (Entity npc : closeNPCs) {
                if (!npc.getId().equals(playerUuid)) {
                    // Random move pct
                    if (random.nextDouble() > .75) {
                        List<Location> neighboringLocations = npc.getLocation().getNeighbors().stream()
                                .collect(Collectors.toList());
                        Collections.shuffle(neighboringLocations);

                        if (canMove(neighboringLocations.get(0), state.getWorld())) {
                            state.getWorld().move(npc, neighboringLocations.get(0));
                        }
                    }
                }
            }

            return state;
        }

        private boolean canMove(Location destination, World world) {
            // TODO: terrain impassable as well.. using null is.. weird
            Entity dest = world.get(destination);
            return dest == null || dest.getAttribute(CommonAttributes.IMPASSABLE.name()) == null;
        }
    }

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
            CharacterTBD characterSheet = CharacterTBD.fromEntity(playerEnt);

            // TODO: add game log
            Entity victim = state.getWorld().get(victimLocation.get());
            if (victim != null) {
                if (victim.getId() != playerUuid) {
                    // TODO: biting is a special case attack
                    // needs combat resolution
                    // TODO: calculate bite stats and what happens to the player, etc.
                    // gain health
                    System.out.println("Biting " + victim.getName());
                    state.getWorld().remove(victim);
                } else {
                    // special case future ability?
                    System.out.println("Cannot bite yourself");
                }
            } else {
                System.out.println("Cannot bite that");
            }

            return state;
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

        Ability bite = new BiteAbility(config.getPlayerUuid());
        config.setCustomAbilities(Set.of(bite));

        World world = new World();

        CharacterTBD pc = new CharacterTBD(
                "Joe",
                14,
                new Stats(10, 10, 16, 12, 12),
                new Status(50, Set.of()),
                Set.of(bite),
                Set.of());

        Entity playerEntity = pc.toEntity();
        playerEntity = playerEntity.copy()
                .setId(config.getPlayerUuid())
                .setLocation(Location.create(0, 0))
                .build();

        world.add(playerEntity);

        for (int i = -5; i < 5; i++) {
            Person npc = FamilyTreeGenerator.create();
            Entity npcEntity = Entities.createMonster(npc.getName().toString())
                    .setOrigin(Location.create(i, 1))
                    .setLocation(Location.create(i, 1))
                    .build();

            world.add(npcEntity);
        }

        GameState initialState = GameState.builder(config)
                .setWorld(world)
                .build();

        Game game = Game.builder()
                .addGameSystem(new PlayerHealth())
                .addGameSystem(new NPCWalk(config.getPlayerUuid(), new Random()))
                .setConfiguration(config)
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
