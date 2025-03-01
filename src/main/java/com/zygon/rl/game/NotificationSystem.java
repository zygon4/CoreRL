package com.zygon.rl.game;

/**
 * @author zygon
 */
/*pkg*/ final class NotificationSystem extends GameSystem {

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
