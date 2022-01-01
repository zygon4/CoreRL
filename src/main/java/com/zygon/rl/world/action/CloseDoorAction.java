package com.zygon.rl.world.action;

import com.zygon.rl.data.ItemClass;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.Building;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

import java.util.List;

/**
 * This is a convenience wrapper on remove/set actions specifically for doors
 * which are expected to be a single per tile.
 *
 * @author zygon
 */
public class CloseDoorAction extends Action {

    private final Location location;

    public CloseDoorAction(Location location) {
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {

        Item openedDoor = getDoor(state);

        // Check if there's a door
        if (openedDoor == null) {
            return false;
        }

        // And it's open
        Boolean closed = openedDoor.getTemplate().getFlag(CommonAttributes.CLOSED.name());
        if (closed != null && !closed.booleanValue()) {
            return false;
        }

        // TODO: Using weight, would probably be better as volume+weight.
        World world = state.getWorld();
        int totalWeight = world.getTotalWeight(location, (t) -> {
            return !t.getType().equals(Building.TypeNames.DOOR.name());
        });

        // This only leaves elements like gasses
        if (totalWeight > 0) {
            return false;
        }

        Action removeItem = new RemoveItemAction(openedDoor, location);
        Action setItem = new SetItemAction(openedDoor, location);

        return removeItem.canExecute(state) && setItem.canExecute(state);
    }

    @Override
    public GameState execute(GameState state) {

        Item openedDoor = getDoor(state);

        Action removeItem = new RemoveItemAction(openedDoor, location);
        state = removeItem.execute(state);

        // TODO: the open door should probably link to its open variety.
        ItemClass closedDoor = Data.get("t_door_closed");

        Action setItem = new SetItemAction(new Item(closedDoor), location);
        state = setItem.execute(state);

        state.copy().addLog("You close the door.");

        return state;
    }

    private Item getDoor(GameState state) {
        List<Item> doors = state.getWorld().getAll(location, Building.TypeNames.DOOR.name());

        if (doors.isEmpty()) {
            return null;
        }

        // Would be weird, check anyways.
        if (doors.size() > 1) {
            throw new IllegalStateException("Too many doors!");
        }

        return doors.get(0);
    }
}
