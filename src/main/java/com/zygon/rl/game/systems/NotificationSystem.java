package com.zygon.rl.game.systems;

import com.zygon.rl.game.ContinueInputHandler;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.game.Notification;

/**
 * @author zygon
 */
public final class NotificationSystem extends GameSystem {

    private static final System.Logger logger = System.getLogger(NotificationSystem.class.getCanonicalName());

    public NotificationSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {

        Notification note = state.getNotification();
        if (note != null) {
            return state.copy()
                    .addInputContext(GameState.InputContext.builder()
                            .setName("TEXT")
                            .setHandler(ContinueInputHandler.create(getGameConfiguration()))
                            .setPrompt(GameState.InputContextPrompt.MODAL)
                            .build())
                    .build();
        }
        return state;
    }
}
