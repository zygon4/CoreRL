/*
 *
 */
package com.zygon.rl.game.example;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.zygon.rl.data.Creature;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.monster.Species;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.SoundEffects;
import com.zygon.rl.game.ui.GameUI;
import com.zygon.rl.world.Calendar;
import com.zygon.rl.world.CorpseItem;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Weather;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;

import org.apache.commons.io.IOUtils;

/**
 * This is a testing area until a new BloodRL2.0 project is made.
 *
 * @author zygon
 */
public class BloodRLMain {

//    private static final StatusEffect THIRST_STATUS = new StatusEffect("blood_status_thirst",
//            "Blood Thirst", "You thirst for blood of the living", true, 150);
//    private static final class DrainBlood implements Ability {
//
//        @Override
//        public String getDescription() {
//            return "Drain the blood of an adjacent being";
//        }
//
//        @Override
//        public String getName() {
//            return "Drain Blood";
//        }
//
//        @Override
//        public Target getTargeting() {
//            // TODO: and 'held' or disabled
//            return Target.ADJACENT;
//        }
//
//        @Override
//        public GameState use(GameState state, Optional<Identifable> empty,
//                Optional<Location> victimLocation) {
//
//            GameState.Builder copy = state.copy();
//
//            CharacterSheet victim = state.getWorld().get(victimLocation.get());
//
//            if (victim != null && !victimLocation.get().equals(state.getWorld().getPlayerLocation())) {
//                // TODO: biting is a special case attack
//                // needs combat resolution
//                // TODO: calculate bite stats and what happens to the player, etc.
//                // gain health
//                copy.addLog("You feed on the blood of " + victim.getName());
//                state.getWorld().remove(victim, victimLocation.get());
//
//                // TODO: finish after re-implementing status effects engine
////                int hungerLevel = player.getStatus().getEffects().get(THIRST_STATUS.getId()).getValue();
//                // this feels really clunky.. better than before but not perfect
////                state.getWorld().move(
////                        player.set(player.getStatus().addEffect("Hunger", hungerLevel - 10)),
////                        state.getWorld().getPlayerLocation(),
////                        state.getWorld().getPlayerLocation());
//                copy.setWorld(state.getWorld().addTime(30));
//            } else {
//                copy.addLog("Cannot drain that");
//            }
//
//            return copy.build();
//        }
//    }
//
//    // TODO: reimplement Ability to return an Action to call
//    private static final class SummonFamiliar implements Ability {
//
//        private final Random random;
//
//        public SummonFamiliar(Random random) {
//            this.random = random;
//        }
//
//        @Override
//        public String getDescription() {
//            return "Summon a random pet";
//        }
//
//        @Override
//        public String getName() {
//            return "Summon Familiar";
//        }
//
//        @Override
//        public Ability.Target getTargeting() {
//            return Ability.Target.NONE;
//        }
//
//        @Override
//        public GameState use(GameState state, Optional<Identifable> empty,
//                Optional<Location> expectEmpty) {
//
//            String id = random.nextBoolean() ? "mon_simple_bat" : "mon_wolf";
//
//            SummonAction summonAction = new SummonAction(
//                    state.getWorld().getPlayerLocation(), 1, SummonAction.DEFAULT_RADIUS, id,
//                    Set.of(Effect.EffectNames.PET.getEffect()), random);
//
//            GameState.Builder copy = state.copy();
//
//            if (summonAction.canExecute(state)) {
//                summonAction.execute(state);
//                copy.addLog("You summon a familar!");
//
//                // TODO: this is clunky to move the time
//                // TODO: also it skips over the world's actions. We need a more
//                // generic "energy" system so everyone gets time to act.
//                copy.setWorld(state.getWorld().addTime(TimeUnit.MINUTES.toSeconds(1)));
//            } else {
//                copy.addLog("Cannot summon a familar here.");
//            }
//
//            return copy.build();
//        }
//    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace(System.err);
            System.exit(1);
        });

        String strTmp = System.getProperty("java.io.tmpdir");
        // Lots of optionscbvsf or optimizing the audio file(s) into temp space
        File themeFile = Path.of(strTmp, "bloodtheme.wav").toFile().getAbsoluteFile();
        if (!themeFile.exists()) {
            IOUtils.copy(BloodRLMain.class.getResourceAsStream("/audio.wav"),
                    new FileOutputStream(themeFile));
        }

        File soundEffectFile = Path.of(strTmp, "bloodsounds.zip").toFile().getAbsoluteFile();
        if (!soundEffectFile.exists()) {
            IOUtils.copy(BloodRLMain.class.getResourceAsStream("/rpg_sound_pack.zip"),
                    new FileOutputStream(soundEffectFile));
        }
        SoundEffects soundEffects = new SoundEffects(soundEffectFile.toPath(), Map.of("door.wav", "door_open"));
        soundEffects.load();

        Data.load();

        GameConfiguration config = new GameConfiguration();
        config.setGameName("BloodRL");
        config.setMusicFile(themeFile.toPath());
        config.setSoundEffects(soundEffects);
        config.setRandom(new Random());

//        Ability drainBlood = new DrainBlood();
//        Ability summonFamiliar = new SummonFamiliar(config.getRandom());
        int daysPerYear = 20;
        int startingYear = 1208;
        World world = new World(
                new Calendar(TimeUnit.HOURS.toSeconds(7), startingYear * daysPerYear, daysPerYear),
                Weather.RAINY);

        Melee dagger = Melee.get("dagger");
        Melee scythe = Melee.get("scythe");

        Set<Ability> abilities = new LinkedHashSet<>();
//        abilities.add(drainBlood);
//        abilities.add(summonFamiliar);

        // Not using right now
//        Effect effect = Effect.get("effect_terror");
//        StatusEffect statusEffect = new StatusEffect(effect);
//
        WorldElement eleTemplate = new WorldElement("player", "player", "@", "PaleVioletRed", "Alucard", "He's cool but moody", Map.of(), 150);
        CharacterSheet pc = new CharacterSheet(
                new Creature(eleTemplate, Species.MAMMAL.name(), 100, 120, 100),
                "Alucard",
                new Stats(16, 16, 14, 12, 12, 16),
                new Status(19, 100, Set.of()),
                null,
                null,
                abilities,
                Set.of(),
                Set.of());

        ArmorData dataTunic = ArmorData.get("torso_tunic_black");
        ArmorData dataBoots = ArmorData.get("boots_leather");
        ArmorData dataPants = ArmorData.get("legs_velvet_pants");
        ArmorData dataArms = ArmorData.get("arms_leather_vambraces");

        Armor arms = new Armor(dataArms);
        Armor tunic = new Armor(dataTunic);
        Armor pants = new Armor(dataPants);
        Armor boots = new Armor(dataBoots);
        Weapon weapon = new Weapon(scythe, 18, 4, 0);

        pc = pc.add(boots).equip(boots);
        pc = pc.add(pants).equip(pants);
        pc = pc.add(tunic).equip(tunic);
        pc = pc.add(arms).equip(arms);
        pc = pc.add(weapon).wield(weapon);

        world.add(pc, Location.create(0, 0));

//        Npc farmerData = Npc.get("npc_generic_farmer");
//        for (int i = -1; i < 1; i++) {
//            Person npcPerson = FamilyTreeGenerator.create();
//
//            CharacterSheet npcSheet = new CharacterSheet(
//                    farmerData.setName(npcPerson.getName().toString())
//                            .setDescription(farmerData.getDescription()),
//                    new Stats(10, 8, 10, 8, 9, 6),
//                    new Status(44, 40, Set.of()),
//                    new Equipment(new Weapon(18, 2, dagger, 0)),
//                    Set.of(),
//                    Set.of());
//
//            world.add(npcSheet, Location.create(i, 5));
//        }
//
//        Monster frog = Monster.get("mon_giant_frog");
//        for (int i = 0; i < 20; i++) {
//
//            Location rand = Location.create(
//                    20 + config.getRandom().nextInt(10),
//                    20 + config.getRandom().nextInt(10));
//
//            if (world.canMove(rand)) {
//                CharacterSheet npcSheet = new CharacterSheet(
//                        frog,
//                        new Stats(4, 4, 6, 3, 3, 3),
//                        new Status(2, frog.getHitPoints(), Set.of()),
//                        null,
//                        Set.of(),
//                        Set.of());
//                world.add(npcSheet, rand);
//            }
//        }
        world.add(new Weapon(dagger, 18, 4, 0), Location.create(0, 0));
        world.add(new Weapon(dagger, 18, 4, 0), Location.create(0, -1));
        world.add(new CorpseItem(Corpse.get("corpse"), 50), Location.create(0, 1));

        GameState initialState = GameState.builder(config)
                .setWorld(world)
                .build();

        Game game = Game.builder(config)
                //                .addGameSystem(new AttributeTimedAdjustmentSystem(config,
                //                        THIRST_STATUS.getId(),
                //                        TimeUnit.HOURS.toSeconds(1),
                //                        gs -> 1,
                //                        hungerValue -> {
                //                            if (hungerValue < 0) {
                //                                return "FULL";
                //                            } else if (hungerValue > 99) {
                //                                return "HUNGRY";
                //                            } else {
                //                                return null;
                //                            }
                //                        }))
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
