package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Pool;
import com.zygon.rl.world.character.Status;

/**
 * TODO: future work, option to fail if outside the pool range.
 *
 *
 * @author zygon
 */
public class SetPoolAction extends SetCharacterAction {

    private final String poolId;
    private final int ammount; //exact number

    // Use??
    //  private final StatusResolver statusResolver;
    public SetPoolAction(CharacterSheet character, Location location,
            String poolId, int ammount) {
        super(character, location);
        this.poolId = poolId;
        this.ammount = ammount;
//        this.statusResolver = new StatusResolver(gameConfiguration.getRandom());
    }

    @Override
    public boolean canExecute(GameState state) {
        Pool pool = getPool();

        return pool != null
                && ammount >= pool.getMin()
                && ammount <= pool.getMax();
    }

    @Override
    public GameState execute(GameState state) {

        Pool pool = getPool();
        Status current = getCharacter().getStatus();
        CharacterSheet updated = getCharacter().copy()
                .status(current.setPool(pool.set(ammount)))
                .build();

        state.getWorld().add(updated, getLocation());
        return state;
    }

    private Pool getPool() {
        CharacterSheet character = getCharacter();
        Status status = character.getStatus();
        return status.getPool(this.poolId);
    }
}
