/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.util.Optional;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.GainXpAction;
import com.zygon.rl.world.action.SetPoolAction;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.AbilityActionSet;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Pool;

/**
 * Tool to run an ability and apply and progression to the character. This is
 * player character only (for now). <br>
 * Another option is to level proficiencies at a specific time (e.g. when
 * sleeping).
 *
 * @author djc
 */
public class AbilityResolver {

    private static final System.Logger logger = System.getLogger(AbilityResolver.class.getCanonicalName());

    private final Ability ability;

    public AbilityResolver(Ability ability) {
        this.ability = ability;
    }

    public GameState resolve(GameState state, Optional<Identifable> targetEntity,
            Optional<Location> targetLocation) {

        Optional<Ability.Cost> cost = this.ability.getCost();
        if (cost.isPresent()) {
            String poolId = cost.get().pool();
            int poolAmmountChange = cost.get().ammount();
            CharacterSheet player = state.getWorld().getPlayer();
            Pool pool = player.getStatus().getPool(poolId);
            int setPoolValue = pool.getPoints() - poolAmmountChange;

            SetPoolAction updatePoolAction = new SetPoolAction(
                    player,
                    state.getWorld().getPlayerLocation(),
                    poolId,
                    setPoolValue);
            if (!updatePoolAction.canExecute(state)) {
                // Need additional Pylons
                return state;
            }
            state = updatePoolAction.execute(state);
        }

        AbilityActionSet abilityActions = this.ability.use(state, targetEntity, targetLocation);

        boolean failed = false;
        for (Action abilityAction : abilityActions.actions()) {
            if (abilityAction.canExecute(state)) {
                state = abilityAction.execute(state);
            } else {
                // stop if anything can't execute..
                failed = true;
                break;
            }
        }

        if (!failed) {
            Optional<String> proficiencyId = ability.getProficiencyId();
            if (proficiencyId.isPresent()) {
                CharacterSheet player = state.getWorld().getPlayer();
                GainXpAction gainXpAction = new GainXpAction(proficiencyId.get(),
                        player, state.getWorld().getPlayerLocation());
                state = gainXpAction.execute(state);
            }
        }

        return state;
    }
}
