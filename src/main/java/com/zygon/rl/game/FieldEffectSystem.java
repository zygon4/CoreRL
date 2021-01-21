package com.zygon.rl.game;

import com.zygon.rl.data.field.FieldData;
import com.zygon.rl.world.Field;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.FieldInteractionAction;
import com.zygon.rl.world.character.CharacterSheet;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
final class FieldEffectSystem extends GameSystem {

    public FieldEffectSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {
        final World world = state.getWorld();
        Location playerLocation = world.getPlayerLocation();

        Map<Location, CharacterSheet> closeCharacters = world.getAll(
                playerLocation, null, REALITY_BUBBLE, true);

        for (var entry : closeCharacters.entrySet()) {
            Location currentLoc = entry.getKey();
            CharacterSheet character = entry.getValue();

            // First resolve fields in the air where the the character is.
            Collection<Action> fieldActions = getEnvironmentalActions(state.getWorld(), currentLoc, character);

            for (Action fd : fieldActions) {
                if (fd.canExecute(state)) {
                    state = fd.execute(state);
                }
            }
        }

        return state;
    }

    private Collection<Action> getEnvironmentalActions(World world, Location location,
            CharacterSheet character) {

        // First resolve fields in the air
        return world.getAll(location, FieldData.getTypeName()).stream()
                .map(fd -> (Field) fd)
                .map(field -> new FieldInteractionAction(getGameConfiguration(), character, location, field))
                .collect(Collectors.toList());

    }
}
