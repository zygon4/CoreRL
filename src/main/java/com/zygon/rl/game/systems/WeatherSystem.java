package com.zygon.rl.game.systems;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;

import java.util.Random;

import com.zygon.rl.world.Weather;

/**
 * Simple markov weather system.
 */
public class WeatherSystem extends GameSystem {

    // TODO: can add more!
    private static final double[][] WEATHER = {
        {0.999, 0.001, 0.000}, // clear
        {0.001, 0.998, 0.001}, // cloudy
        {0.000, 0.001, 0.999} // rainy
    };

    public WeatherSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {
        Weather current = state.getWorld().getWeather();

        Weather forecast = step(current, getGameConfiguration().getRandom());

        if (forecast != current) {
            return state.copy()
                    .setWorld(state.getWorld().setWeather(forecast))
                    .build();
        } else {
            return state;
        }
    }

    private Weather step(Weather current, Random random) {
        double r = random.nextDouble();
        double sum = 0.0;

        Weather next = null;

        // determine next state
        for (int j = 0; j < WEATHER.length; j++) {
            sum += WEATHER[current.getValue()][j];
            if (r <= sum) {
                next = Weather.valueOf(j);
                break;
            }
        }

        return next;
    }
}
