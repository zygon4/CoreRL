package com.zygon.rl.game.systems;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Effect;
import com.zygon.rl.data.field.FieldData;
import com.zygon.rl.data.monster.Species;
import com.zygon.rl.game.Behavior;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Field;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.FieldInteractionAction;
import com.zygon.rl.world.action.MeleeAttackAction;
import com.zygon.rl.world.action.MoveAction;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author zygon
 */
public final class AISystem extends GameSystem {

    private final Random random;

    public AISystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.random = gameConfiguration.getRandom();
    }

    @Override
    public GameState apply(GameState state) {
        final World world = state.getWorld();
        Location playerLocation = world.getPlayerLocation();

        Map<Location, CharacterSheet> closeCharacters = world.getAll(
                playerLocation, null, REALITY_BUBBLE);

        for (var entry : closeCharacters.entrySet()) {
            Location currentLoc = entry.getKey();
            CharacterSheet character = entry.getValue();

            character = character.powerUp();

            while (character.getStatus().canAct()) {

                // Next the AIs behave
                Behavior behavior = get(character, currentLoc, state.getWorld());

                Action action = null;
                if (behavior != null) {
                    action = behavior.get(character);
                }

                if (action != null) {
                    if (action.canExecute(state)) {
                        state = action.execute(state);
                        // Hate casting!!
                        if (action.getClass().isAssignableFrom(MoveAction.class)) {
                            currentLoc = ((MoveAction) action).getTo();
                        }
                    } else {
                        state = state.log("Can't execute that");
                    }
                }

                character = character.coolDown();
            }

            state.getWorld().add(character, currentLoc);
        }

        return state;
    }

    private Collection<Action> getEnvironmentalActions(World world,
            Location location,
            CharacterSheet character) {

        // First resolve fields in the air
        return world.getAll(location, FieldData.getTypeName()).stream()
                .map(fd -> (Field) fd)
                .map(field -> new FieldInteractionAction(getGameConfiguration(), character, location, field))
                .collect(Collectors.toList());

    }

    private Behavior get(CharacterSheet character, Location characterLocation,
            World world) {

        if (character.getStatus().isEffected(Effect.EffectNames.PET.getId())) {
            Location playerLocation = world.getPlayerLocation();
            // player location can be null if dead
            if (playerLocation != null) {
                Behavior defend = defend(characterLocation, playerLocation, world, 5);
                if (defend != null) {
                    return defend;
                } else {
                    return follow(characterLocation, playerLocation, world, 5);
                }
            }
        }

        if (isHostile(character)) {
            Location playerLocation = world.getPlayerLocation();
            if (playerLocation != null) {
                return hostile(characterLocation, world.getPlayerLocation(), world);
            }
        }

        if (character.getType().equals(CommonAttributes.MONSTER.name())) {
            Creature creatureTemplate = character.getTemplate();
            Species species = Species.valueOf(creatureTemplate.getSpecies());

            // The actual behaviors should be different: e.g. "flock" vs "hunt"
            // with different flee/fight caracteristics.
            switch (species) {
                // This seems like a common pattern of behavior.
                case AMPHIBIAN, MAMMAL -> {
                    // If next to, attack
                    if (characterLocation.getNeighbors().contains(world.getPlayerLocation())) {
                        CharacterSheet player = world.getPlayer();
                        return (c) -> new MeleeAttackAction(getGameConfiguration(),
                                c, player, world.getPlayerLocation());
                    } else {
                        // If within aggression range follow
                        if (characterLocation.getNeighbors(creatureTemplate.getAggression())
                                .contains(world.getPlayerLocation())) {
                            Location playerLocation = world.getPlayerLocation();
                            // player location can be null if dead
                            if (playerLocation != null) {
                                return follow(characterLocation, playerLocation, world, 1);
                            }
                        } else {
                            // Otherwise ignore
                            return wander(characterLocation, world);
                        }
                    }
                }
            }

        } else if (character.getType().equals(CommonAttributes.NPC.name())) {

            if (isHostile(character)) {
                return hostile(characterLocation, world.getPlayerLocation(), world);
            } else {
                return wander(characterLocation, world);
            }
        }

        return null;
    }

    private Behavior defend(Location defenderLoc, Location toDefendLoc,
            World world, int close) {

        for (var possibleHostile : toDefendLoc.getNeighbors(close)) {
            CharacterSheet neighborToDefendLoc = world.get(possibleHostile);
            if (neighborToDefendLoc != null
                    && isHostile(neighborToDefendLoc)
                    && !isPet(neighborToDefendLoc)) {
                // found someone hostile to MY boss
                // either hit em or chase em
                if (defenderLoc.getNeighbors().contains(possibleHostile)) {
                    // "defender" is confusing here because it's the attacking
                    // character. They're defending someone else.
                    CharacterSheet defender = world.get(defenderLoc);
                    if (defender != null) {
                        return (c) -> new MeleeAttackAction(getGameConfiguration(),
                                defender, neighborToDefendLoc, possibleHostile);
                    }
                } else {
                    return follow(defenderLoc, possibleHostile, world, 1);
                }
            }
        }

        return null;
    }

    private Behavior follow(Location followerLocation, Location destination,
            World world, int close) {

        if (followerLocation.getDistance(destination) > close) {
            List<Location> pathToDest = followerLocation.getPath(destination,
                    (l) -> world.canMove(l));

            if (pathToDest != null && pathToDest.size() > 1) {
                if (world.canMove(pathToDest.get(0))) {
                    return (character) -> new MoveAction(character.getId(),
                            followerLocation, pathToDest.get(0));
                }
            }
        } else {
            if (random.nextDouble() > .75) {
                return (character) -> MoveAction.createRandomWalkAction(world,
                        character.getId(), followerLocation);
            }
        }

        return null;
    }

    private Behavior hostile(Location hostileLocation, Location destination,
            World world) {
        if (hostileLocation.getNeighbors().contains(world.getPlayerLocation())) {
            CharacterSheet player = world.getPlayer();
            return (c) -> new MeleeAttackAction(getGameConfiguration(),
                    c, player, world.getPlayerLocation());
        } else {
            return follow(hostileLocation, destination, world, 1);
        }
    }

    private Behavior wander(Location fromLocation, World world) {
        if (random.nextDouble() > .75) {
            return (c) -> MoveAction.createRandomWalkAction(world,
                    c.getId(), fromLocation);
        }

        return null;
    }

    private static boolean isHostile(CharacterSheet character) {
        return character.getStatus().isEffected(Effect.EffectNames.HOSTILE.getId());
    }

    private static boolean isPet(CharacterSheet character) {
        return character.getStatus().isEffected(Effect.EffectNames.PET.getId());
    }
}
