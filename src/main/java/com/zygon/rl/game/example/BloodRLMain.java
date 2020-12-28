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

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

        // audio seems to slow the game down a lot??
//        Audio audio = new Audio(Paths.get("/home/zygon/src/github/CoreRL/audio.wav"));
//        audio.play();
//
        GameConfiguration config = new GameConfiguration();
        config.setGameName("BloodRL");
        config.setNpcSpawnRate(0.00000001);
        config.setPlayerUuid(UUID.randomUUID());
        config.setCustomAbilities(Map.of("Bite", new BiteAbility(config.getPlayerUuid())));

        World world = new World();

        CharacterTBD pc = new CharacterTBD(
                "Joe",
                14,
                new Stats(10, 10, 16, 12, 12),
                new Status(50, Set.of()),
                Set.of(config.getCustomAbilities().get("Bite")),
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
                .setConfiguration(config)
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
