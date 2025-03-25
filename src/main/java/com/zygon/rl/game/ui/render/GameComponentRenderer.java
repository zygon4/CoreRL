/*
 *
 */
package com.zygon.rl.game.ui.render;

import com.zygon.rl.game.GameState;

/**
 * Implements renderers for specific game components e.g. inventory, status.
 *
 */
public interface GameComponentRenderer {

    /**
     * Clears the screen.
     */
    void clear();

    /**
     * Renders the given GameState.
     *
     * @param gameState the GameState.
     */
    void render(GameState gameState);
}
