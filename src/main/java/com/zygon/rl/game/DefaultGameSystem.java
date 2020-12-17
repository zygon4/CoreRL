package com.zygon.rl.game;

/**
 *
 * @author zygon
 */
/*pkg*/ final class DefaultGameSystem extends GameSystem {

    @Override
    public GameState apply(GameState state) {
        return state.copy()
                .addTurnCount()
                .build();
    }

}
