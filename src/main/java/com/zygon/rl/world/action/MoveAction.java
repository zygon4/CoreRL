package com.zygon.rl.world.action;

import com.zygon.rl.data.Element;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class MoveAction extends Action {

    private static final System.Logger logger = System.getLogger(MoveAction.class.getCanonicalName());

    private final String id;
    private final Location from;
    private final Location to;

    public MoveAction(String id, Location from, Location to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean canExecute(GameState state) {
        // needs to be that id at the location expected, and can move to the
        // next location
        World world = state.getWorld();
        List<CharacterSheet> all = world.getAll(from);
        Element fromElements = all != null ? world.getAll(from).stream()
                .filter(element -> element.getId().equals(id))
                .findAny().orElse(null) : null;
        return fromElements != null && world.canMove(to);
    }

    @Override
    public GameState execute(GameState state) {
        World world = state.getWorld();

        List<CharacterSheet> elements = world.getAll(from).stream()
                .filter(element -> element.getId().equals(id))
                .collect(Collectors.toList());

        // this will move everything of the id, but we don't expect to double
        // move NPCs..
        for (CharacterSheet element : elements) {
            if (world.canMove(to)) {
                logger.log(System.Logger.Level.TRACE,
                        "MOVE: " + element.getId() + " from " + from + " to" + to);
                world.move(element, from, to);
            } else {
                // TODO: maybe log?
            }
        }
        return state;
    }

    public Location getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public Location getTo() {
        return to;
    }

    public static MoveAction createRandomWalkAction(World world, String id, Location from) {
        List<Location> neighboringLocations = world.getPassableNeighbors(from);

        if (!neighboringLocations.isEmpty()) {
            Collections.shuffle(neighboringLocations);
            Location to = neighboringLocations.get(0);

            return new MoveAction(id, from, to);
        }

        return null;
    }

}
