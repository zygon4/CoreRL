package com.zygon.rl.game.systems;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.game.SpawnContext;
import com.zygon.rl.game.SpawnContext.SpawnContextType;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;

/**
 *
 * @author zygon
 */
public class SpawnSystem extends GameSystem {

    private static final System.Logger LOGGER = System.getLogger(SpawnSystem.class.getCanonicalName());

    // Almost like spawn zones, we don't necessarily need to spawn on these
    // spots (however for buildings it could be good), but just spawn items
    // *near* these areas.
    final Map<SpawnContextType, Set<Location>> spawnLocationsByContext = new HashMap<>();
    private final Random random;

    public SpawnSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.random = gameConfiguration.getRandom();

    }

    @Override
    public GameState apply(GameState state) {
        for (SpawnContextType type : SpawnContextType.values()) {
            state = spawn(state, spawnLocationsByContext, type, state.getWorld().getPlayerLocation());
        }
        return state;
    }

    private GameState spawn(final GameState state,
            final Map<SpawnContextType, Set<Location>> spawnLocationsByContext,
            final SpawnContextType contextType, final Location center) {

        Set<Location> spawnedLocations = spawnLocationsByContext.computeIfAbsent(
                contextType, k -> new HashSet<>());

        // Possible new spawn locations (as the player moves around)
        Set<Location> spawnLocations = contextType.getGetSpawnLocFn().apply(center);
        spawnLocations.removeAll(spawnedLocations);

        if (!spawnLocations.isEmpty()) {
            CompletionService<GameState> completionService = new ExecutorCompletionService<>(getExecutor());

            for (Location spawnLocation : spawnLocations) {
                LOGGER.log(Level.DEBUG, "Spawning at locations {0}", spawnLocation);

                List<Action> spawnActions = contextType.getGetSpawnActionsFn()
                        .apply(new SpawnContext.SpawnActionContext(state, spawnLocation, random));

                Callable<GameState> itemSpawnAction = () -> {
                    GameState actionState = state;
                    for (Action a : spawnActions) {
                        if (a.canExecute(actionState)) {
                            actionState = a.execute(actionState);
                        }
                    }
                    return actionState;
                };

                completionService.submit(itemSpawnAction);
                spawnedLocations.add(spawnLocation);
            }

            GameState gameState = state;
            for (int i = 0; i < spawnLocations.size(); i++) {
                try {
                    Future<GameState> futureState = completionService.take();
                    gameState = futureState.get();
                } catch (ExecutionException | InterruptedException intr) {
                    // TODO: log?
                    intr.printStackTrace();
                }
            }
            return gameState;
        }

        return state;
    }
}
