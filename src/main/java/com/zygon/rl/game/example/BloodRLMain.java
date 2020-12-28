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
            return Target.ADJACENT_LIVING;
        }

        @Override
        public GameState use(GameState state) {

            // TODO: receive target
            Entity player = state.getWorld().get(playerUuid);
            Location playerLocation = player.getLocation();
//            Location destination = getRelativeLocation(playerLocation, input);
//
//            Entity victim = state.getWorld().get(destination);
//
//            System.out.println("Biting " + victim.getName());
//
//            // TODO: biting is a special case attack
//            // needs combat resolution
//            state.getWorld().remove(victim);

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

        World world = new World();
        world.add(playerEntity);

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
