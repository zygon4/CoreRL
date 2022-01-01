package com.zygon.rl.world.action;

import com.zygon.rl.data.ItemClass;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.Building;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;

import java.util.List;

/**
 * This is a convenience wrapper on remove/set actions specifically for doors
 * which are expected to be a single per tile.
 *
 * @author zygon
 */
public class OpenDoorAction extends Action {

    private final Location location;

    public OpenDoorAction(Location location) {
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {

        Item closedDoor = getDoor(state);
        WorldElement element = Data.get(closedDoor.getId());

        Boolean closed = element.getFlag(CommonAttributes.CLOSED.name());
        if (closed != null && closed.booleanValue()) {
            // Can open?
            Boolean locked = element.getFlag(CommonAttributes.LOCKED.name());
            if (locked != null && locked.booleanValue()) {
                state.copy().addLog("Locked!");
                return false;
            }
        }

        Action removeItem = new RemoveItemAction(closedDoor, location);
        Action setItem = new SetItemAction(closedDoor, location);

        return removeItem.canExecute(state) && setItem.canExecute(state);
    }

    @Override
    public GameState execute(GameState state) {

        if (canExecute(state)) {
            Item closedDoor = getDoor(state);

            Action removeItem = new RemoveItemAction(closedDoor, location);
            state = removeItem.execute(state);

            // TODO: the closed door should probably link to its open variety.
            ItemClass openDoor = Data.get("t_door_open");

            Action setItem = new SetItemAction(new Item(openDoor), location);
            state = setItem.execute(state);

            state.copy().addLog("You open the door.");

            return state;
        }

        return state;
    }

    private Item getDoor(GameState state) {
        // Lousy generics.. wish this would return a List<Item>
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
