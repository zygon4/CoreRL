package com.zygon.rl.world.action;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.Field;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

import java.util.Map;

/**
 *
 * @author zygon
 */
public class FieldInteractionAction extends DamageAction {

    private static final Map<String, DamageType> getDmgByField
            = Map.of("fd_electricity", DamageType.Lightning,
                    "fd_poison_gas", DamageType.Poison);

    private final Field field;

    public FieldInteractionAction(GameConfiguration gameConfiguration,
            CharacterSheet characterSheet, Location location, Field field) {
        super(gameConfiguration, characterSheet, location);
        this.field = field;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    protected DamageResolution getDamage(GameState state) {
        DamageResolution dmg = new DamageResolution(field.getTemplate().getName(),
                getDamaged().getName(), false, false);

        DamageType type = getDmgByField.get(field.getId());

        // straight strength -> dmg
        // TODO: this needs to be scaled
        dmg.set(type, field.getStrength());

        return dmg;
    }
}
