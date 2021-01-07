package com.zygon.rl.game;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        final World world = state.getWorld();
        Location player = world.getPlayerLocation();

        Map<Location, List<Element>> closeNPCs = world.getAllNEW(
                player, CommonAttributes.NPC.name(), REALITY_BUBBLE);

        closeNPCs.forEach((currentLoc, singletonList) -> {

            // Random move pct
            if (random.nextDouble() > .75) {
                List<Location> neighboringLocations = currentLoc.getNeighbors()
                        .stream().collect(Collectors.toList());
                Collections.shuffle(neighboringLocations);
                Location to = neighboringLocations.get(0);

                // TODO: hack -> "can move" should encompass the player entity/element
                if (world.canMove(to) && !to.equals(player)) {
                    world.move(singletonList.get(0), currentLoc, to);
                }
            }
        });

        return state;
    }
    // TODO: reimplement tracking NPCs
//        for (Element npc : closeNPCs) {
//            if (npc.getAttributeValue(CommonAttributes.TEMPERMENT.name())
//                    .equals(CommonAttributeValues.HOSTILE.name())) {
//                // TODO: polished hostile behavior
//                // move towards, use available weapon
//                List<Location> pathToPlayer = npc.getLocation().getPath(player,
//                        (l) -> {
//                            return world.canMove(l);
//                        });
//
//                if (pathToPlayer == null) {
//                    // continuity error, player walked INTO NPCs
//                    continue;
//                }
//
//                // if zero, then they are ON TOP OF the player, that would be weird..
//                if (pathToPlayer.size() == 1) {
//                    // adjacent, attack!
//                    state = state.log(npc.getName() + " attacks!");
//                } else {
//                    // can move may be calculated already in the pathing..
//                    if (state.getWorld().canMove(pathToPlayer.get(0))) {
//                        state.getWorld().move(npc, pathToPlayer.get(0));
//                    }
//                }
//            } else {

}
