package com.zygon.rl.game;

import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

import java.util.Collections;
import java.util.List;
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
        Set<Entity> closeNPCs = state.getWorld().getAll(playerEnt.getLocation(), REALITY_BUBBLE);
        for (Entity npc : closeNPCs) {
            if (!npc.getId().equals(playerUuid)) {
                // Random move pct
                if (random.nextDouble() > .75) {
                    List<Location> neighboringLocations = npc.getLocation().getNeighbors().stream().collect(Collectors.toList());
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
