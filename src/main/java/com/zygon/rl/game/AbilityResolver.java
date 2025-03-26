/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.lang.System.Logger.Level;
import java.util.Optional;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetCharacterAction;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.AbilityActionSet;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class AbilityResolver {

    private static final System.Logger logger = System.getLogger(AbilityResolver.class.getCanonicalName());

    // TODO: dynamic xp
    private final int xpGain = 5;

    private final Ability ability;

    public AbilityResolver(Ability ability) {
        this.ability = ability;
    }

    public GameState resolve(GameState state, Optional<Identifable> targetEntity,
            Optional<Location> targetLocation) {

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
            CharacterSheet player = state.getWorld().getPlayer();

            player = player.copy()
                    .progress(player.getProgress().add(ability.getProficiencyId(), xpGain))
                    .build();

            SetCharacterAction setProgress = new SetCharacterAction(
                    state.getWorld().getPlayerLocation(), player);

            if (setProgress.canExecute(state)) {
                state = setProgress.execute(state);
                logger.log(Level.INFO, "{0} gained {1} xp in {2}",
                        new Object[]{
                            player.getName(),
                            xpGain,
                            Proficiencies.get(ability.getProficiencyId()).getName()}
                );
            }
        }

        return state;
    }
}
