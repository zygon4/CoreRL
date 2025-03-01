package com.zygon.rl.world.action;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.StatusEffect;
import com.zygon.rl.world.character.StatusResolver;

/**
 *
 *
 * @author zygon
 */
public class StatusDamageAction extends DamageAction {

    private final StatusEffect effect;
    private final StatusResolver statusResolver;

    public StatusDamageAction(GameConfiguration gameConfiguration,
            StatusEffect effect, CharacterSheet defender,
            Location defenderLocation) {
        super(gameConfiguration, defender, defenderLocation);
        this.effect = effect;
        this.statusResolver = new StatusResolver(gameConfiguration.getRandom());
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    protected DamageResolution getDamage(GameState state) {

        int currentTurn = state.getTurnCount();
        int effectStartTurn = effect.getTurn();

        int intensity = (effectStartTurn - currentTurn) % 10;

        if (intensity < 0) {
            intensity = 0;
        }

        // TODO: damage based on the status, poisoned vs acid, etc. 
        return this.statusResolver.resolveStatusDamage(getDamaged(), intensity, DamageType.Bludgeoning);
    }
}
