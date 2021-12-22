package com.zygon.rl.game;

import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hexworks.zircon.api.uievent.KeyCode.ESCAPE;

/**
 *
 * @author zygon
 */
public class TargetingInputHandler extends DirectionInputHandler {

    private final Location targetLocation;
    private final Set<Location> targetPath = new HashSet<>();
    private final Map<Input, Function<Location, Action>> targetActionByInput;
    private final boolean popContextOnAction;

    public TargetingInputHandler(GameConfiguration gameConfiguration,
            Location centralLocation, Location targetLocation,
            Map<Input, Function<Location, Action>> targetActionByInput,
            boolean popContextOnAction) {
        super(gameConfiguration, centralLocation,
                addInputs(Set.of(Input.valueOf(ESCAPE.getCode())), targetActionByInput.keySet()));

        this.targetLocation = targetLocation != null
                ? targetLocation : getCentralLocation();

        if (!this.targetLocation.equals(getCentralLocation())) {
            targetPath.addAll(getCentralLocation().getLine(this.targetLocation).stream()
                    .collect(Collectors.toSet()));
        }

        this.targetActionByInput = Collections.unmodifiableMap(targetActionByInput);
        this.popContextOnAction = popContextOnAction;
    }

    public TargetingInputHandler(GameConfiguration gameConfiguration, Location centralLocation,
            Map<Input, Function<Location, Action>> targetActionByInput) {
        this(gameConfiguration, centralLocation, null, targetActionByInput, false);
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;

        Function<Location, Action> getActionFn = targetActionByInput.get(input);

        if (getActionFn != null) {
            Action action = getActionFn.apply(targetLocation);

            if (action.canExecute(newState)) {
                newState = action.execute(newState);
                if (popContextOnAction) {
                    newState = popInputContext(newState);
                }
            }
        } else {

            // The new target is offset from the previous target so it "walks"
            Location target = getTarget(targetLocation, input);

            if (target != null) {
                newState = popInputContext(newState).copy()
                        .addInputContext(GameState.InputContext.builder()
                                .setName("TARGET")
                                .setHandler(new TargetingInputHandler(
                                        getGameConfiguration(), getCentralLocation(),
                                        target, targetActionByInput, popContextOnAction))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build()).build();
            } else {
                return popInputContext(newState);
            }
        }

        return newState;
    }

    public final Location getTargetLocation() {
        return targetLocation;
    }

    public boolean isOnPath(Location location) {
        return targetPath.contains(location);
    }
}
