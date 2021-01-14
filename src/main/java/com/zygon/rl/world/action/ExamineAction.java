package com.zygon.rl.world.action;

import com.zygon.rl.data.Element;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;

import java.util.List;
import java.util.stream.Collectors;

/**
 * I don't love this. Examine needs a different UI view IMO so this doesn't
 * fully work. Also don't use this code to manipulate the game state.
 *
 * @author zygon
 */
public class ExamineAction extends Action {

    private final Location examine;

    public ExamineAction(Location examine) {
        this.examine = examine;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {

        List<Element> allElements = state.getWorld().getAllElements(examine);
        String examineLog = allElements.stream()
                .map(e -> e.getName() + ") " + e.getDescription())
                .collect(Collectors.joining("\n"));

        return state.log(examineLog);
    }
}
