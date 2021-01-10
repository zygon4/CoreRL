package com.zygon.rl.game;

import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.MeleeAttackAction;
import com.zygon.rl.world.action.MoveAction;
import com.zygon.rl.world.character.CharacterSheet;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author zygon
 */
final class AISystem extends GameSystem {

    private static final int REALITY_BUBBLE = 20;

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

        closeCharacters.forEach((currentLoc, character) -> {

            Behavior behavior = get(character, currentLoc, world);

            Action action = null;
            if (behavior != null) {
                action = behavior.get(character);
            }

            if (action != null) {
                if (action.canExecute()) {
                    action.execute();
                } else {
                    // TODO: log?
                    System.out.println("Can't execute action " + action);
                }
            }
        });

        return state;
    }

    private Behavior get(CharacterSheet character, Location characterLocation,
            World world) {

        // This is hacked for the specific "familiar follower" usecase - expand it!
        if (character.getType().equals(CommonAttributes.MONSTER.name())) {
            // TODO: check for status effects that would cause a follow event e.g. familiar
            // TODO: also the familiar should have a "owner" not just the player,
            // so this is a hack.
            // TODO: different familars stay closer than others.
            return follow(character, characterLocation, world.getPlayerLocation(), world, 5);
        } else if (character.getType().equals(CommonAttributes.NPC.name())) {

            // TODO: attack whatever it's hostile to..
            if (characterLocation.getNeighbors().contains(world.getPlayerLocation())) {
                CharacterSheet player = world.getPlayer();
                return (c) -> {
                    return new MeleeAttackAction(world, getGameConfiguration(),
                            c, player, world.getPlayerLocation());
                };
            } else {
                // Again follow whoever is hostile
                return follow(character, characterLocation, world.getPlayerLocation(), world, 1);
            }
        }
        return null;
    }

    private Behavior follow(CharacterSheet follower, Location followerLocation,
            Location destination, World world, int close) {

        if (followerLocation.getDistance(destination) > close) {
            List<Location> pathToDest = followerLocation.getPath(destination,
                    (l) -> world.canMove(l));

            if (pathToDest != null && pathToDest.size() > 1) {
                if (world.canMove(pathToDest.get(0))) {
                    return (character) -> {
                        return new MoveAction(world, character.getId(),
                                followerLocation, pathToDest.get(0));
                    };
                }
            }
        } else {
            if (random.nextDouble() > .75) {
                return (character) -> {
                    return MoveAction.createRandomWalkAction(world,
                            character.getId(), followerLocation);
                };
            }
        }

        return null;
    }
}
