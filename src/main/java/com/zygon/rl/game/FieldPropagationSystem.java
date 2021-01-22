package com.zygon.rl.game;

import com.zygon.rl.data.field.FieldData;
import com.zygon.rl.world.Field;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.SetIdentifiableAction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zygon
 */
/*pkg*/ final class FieldPropagationSystem extends GameSystem {

    private static final System.Logger logger = System.getLogger(FieldPropagationSystem.class.getCanonicalName());

    public FieldPropagationSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {

        state.getWorld().getPlayerLocation()
                .getNeighbors(REALITY_BUBBLE, true).stream()
                .forEach(l -> propagateFields(state, l));

        return state;
    }

    private void propagateFields(GameState state, Location location) {
        World world = state.getWorld();

        List<Field> fields = world.getAll(location, FieldData.getTypeName()).stream()
                .map(fd -> (Field) fd)
                .collect(Collectors.toList());

        for (Field fd : fields) {

            logger.log(System.Logger.Level.TRACE, "FIELD) " + fd.getId()
                    + " From " + location + " " + fd.getStrength());

            // First remove all
            world.remove(fd, location);

            Map<Location, Field> propagated = fd.propagate(location);

            for (var entry : propagated.entrySet()) {
                Location propLoc = entry.getKey();
                Field propField = entry.getValue();

                logger.log(System.Logger.Level.TRACE, "FIELD) " + fd.getId()
                        + " to " + propLoc + " " + propField.getStrength());

                SetIdentifiableAction set = new SetIdentifiableAction(propLoc, propField);
                if (set.canExecute(state)) {
                    set.execute(state);
                }
            }
        }
    }
}
