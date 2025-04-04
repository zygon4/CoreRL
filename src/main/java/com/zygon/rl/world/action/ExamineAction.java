package com.zygon.rl.world.action;

import java.util.List;
import java.util.stream.Collectors;

import com.zygon.rl.data.Terrain;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;

/**
 * I don't love this. Examine needs a different UI view IMO so this doesn't
 * fully work. Also don't use this code to manipulate the game state.
 *
 * @author zygon
 */
public class ExamineAction extends Action {

    private final Location location;

    public ExamineAction(Location location) {
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {

        World world = state.getWorld();
        List<CharacterSheet> characters = world.getAll(location);

        String examineLog = null;

        if (!characters.isEmpty()) {
            examineLog = "You see:\n" + characters.stream()
                    .map(e -> " - " + e.getName())
                    .collect(Collectors.joining("\n"));
        } else {
            List<Tangible> things = world.getAll(location, null);

            if (!things.isEmpty()) {
                examineLog = "You see:\n" + things.stream()
                        .map(e -> " - " + e.getName())
                        .collect(Collectors.joining("\n"));
            } else {
                Terrain terrain = world.getTerrain(location);
                examineLog = "You see: " + terrain.getDescription();
            }
        }

        return state.log(examineLog);
    }
}
