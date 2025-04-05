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
    private final boolean capUnder;
    private final boolean capOver;

    // Use??
    //  private final StatusResolver statusResolver;
    public SetPoolAction(CharacterSheet character, Location location,
            String poolId, int ammount, boolean capUnder, boolean capOver) {
        super(character, location);
        this.poolId = poolId;
        this.ammount = ammount;
        this.capUnder = capUnder;
        this.capOver = capOver;
//        this.statusResolver = new StatusResolver(gameConfiguration.getRandom());
    }

    @Override
    public boolean canExecute(GameState state) {
        // The actual update will cap the value (min or max) always, but we
        // can choose to fail *this* check too when we need exact change. e.g.
        // spending mana on a spell.
        Pool pool = getPool();

        return (ammount >= pool.getMin() || capUnder)
                && (ammount <= pool.getMax() || capOver);
    }

    @Override
    public GameState execute(GameState state) {

        Pool pool = getPool();
        Status current = getCharacter().getStatus();

        int finalAmmount = Math.max(pool.getMin(), ammount);
        finalAmmount = Math.min(pool.getMax(), finalAmmount);

        CharacterSheet updated = getCharacter().copy()
                .status(current.setPool(pool.set(finalAmmount)))
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
