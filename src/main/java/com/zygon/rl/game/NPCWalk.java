package com.zygon.rl.game;

import com.zygon.rl.world.CommonAttributeValues;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
final class NPCWalk extends GameSystem {

    private static final int REALITY_BUBBLE = 50;

    private final UUID playerUuid;
    private final Random random;

    public NPCWalk(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.playerUuid = gameConfiguration.getPlayerUuid();
        this.random = gameConfiguration.getRandom();
    }

    @Override
    public GameState apply(GameState state) {
        Entity playerEnt = state.getWorld().get(playerUuid);
        Location player = playerEnt.getLocation();

        Set<Entity> closeNPCs = state.getWorld().getAll(playerEnt.getLocation(),
                REALITY_BUBBLE,
                Map.of(CommonAttributes.NPC.name(), CommonAttributeValues.TRUE.name(),
                        CommonAttributes.LIVING.name(), CommonAttributeValues.TRUE.name()));

        final World world = state.getWorld();

        for (Entity npc : closeNPCs) {
            if (npc.getAttributeValue(CommonAttributes.TEMPERMENT.name())
                    .equals(CommonAttributeValues.HOSTILE.name())) {
                // TODO: polished hostile behavior
                // move towards, use available weapon
                List<Location> pathToPlayer = npc.getLocation().getPath(player,
                        (l) -> {
                            return world.canMove(l);
                        });

                if (pathToPlayer == null) {
                    // continuity error, player walked INTO NPCs
                    continue;
                }

                // if zero, then they are ON TOP OF the player, that would be weird..
                if (pathToPlayer.size() == 1) {
                    // adjacent, attack!
                    state = state.log(npc.getName() + " attacks!");
                } else {
                    // can move may be calculated already in the pathing..
                    if (state.getWorld().canMove(pathToPlayer.get(0))) {
                        state.getWorld().move(npc, pathToPlayer.get(0));
                    }
                }
            } else {
                // Random move pct
                if (random.nextDouble() > .75) {
                    List<Location> neighboringLocations = npc.getLocation().getNeighbors()
                            .stream().collect(Collectors.toList());
                    Collections.shuffle(neighboringLocations);
                    if (state.getWorld().canMove(neighboringLocations.get(0))) {
                        state.getWorld().move(npc, neighboringLocations.get(0));
                    }
                }
            }
        }
        return state;
    }
}
