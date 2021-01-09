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

        Map<Location, List<CharacterSheet>> closeCharacters = world.getAll(
                playerLocation, null, REALITY_BUBBLE);

        closeCharacters.forEach((currentLoc, characters) -> {

            for (CharacterSheet character : characters) {

                Action action = null;

                // maybe have a type enum somewhere?
                if (character.getType().equals(CommonAttributes.NPC.name())) {

                    // Is near hostile? hostile status TBD
                    if (currentLoc.getDistance(playerLocation) < 10) {
                        List<Location> pathToPlayer = currentLoc.getPath(playerLocation,
                                (l) -> world.canMove(l));

                        if (pathToPlayer != null) {
                            if (pathToPlayer.size() == 1) {
                                // TODO: log
                                //                            state = state.log(element.getName() + " attacks!");
                                CharacterSheet player = world.getPlayer();

                                // if player is alive..
                                if (player != null) {
                                    action = new MeleeAttackAction(world, getGameConfiguration(),
                                            character, player, playerLocation);
                                }
                            } else {
                                // can move may be calculated already in the pathing..
                                if (world.canMove(pathToPlayer.get(0))) {
                                    action = new MoveAction(world, character.getId(),
                                            currentLoc, pathToPlayer.get(0));
                                }
                            }
                        }// else the path became obstructed

                    } else {
                        if (random.nextDouble() > .75) {
                            action = MoveAction.createRandomWalkAction(world,
                                    character.getId(), currentLoc);
                        }
                    }
                }

                if (action != null) {
                    if (action.canExecute()) {
                        action.execute();
                    } else {
                        // TODO: log?
                        System.out.println("Can't execute action " + action);
                    }
                }
            }
        });

        return state;
    }
}
