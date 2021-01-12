package com.zygon.rl.world.action;

import com.zygon.rl.data.Element;
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

    private final String id;
    private final Location from;
    private final Location to;

    public MoveAction(World world, String id, Location from, Location to) {
        super(world);
        this.id = id;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean canExecute() {
        // needs to be that id at the location expected, and can move to the
        // next location
        World world = getWorld();
        List<CharacterSheet> all = world.getAll(from);
        Element fromElements = all != null ? world.getAll(from).stream()
                .filter(element -> element.getId().equals(id))
                .findAny().orElse(null) : null;
        return fromElements != null && world.canMove(to);
    }

    @Override
    public void execute() {
        World world = getWorld();

        List<CharacterSheet> elements = world.getAll(from).stream()
                .filter(element -> element.getId().equals(id))
                .collect(Collectors.toList());

        // this will move everything of the id, but we don't expect to double
        // move NPCs..
        for (CharacterSheet element : elements) {
            if (world.canMove(to)) {
                world.move(element, from, to);
            } else {
                // TODO: maybe log?
            }
        }
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

            return new MoveAction(world, id, from, to);
        }

        return null;
    }

}
