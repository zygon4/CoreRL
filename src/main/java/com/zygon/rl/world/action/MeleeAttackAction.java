package com.zygon.rl.world.action;

import java.util.Objects;
import java.util.function.Predicate;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.StatusEffect;
import com.zygon.rl.world.combat.CombatResolver;

/**
 * Eventually can introduce a generic CombatAction that Melee and Ranged hang
 * off.
 *
 * @author zygon
 */
public class MeleeAttackAction extends DamageAction {

    private final CharacterSheet attacker;

    public MeleeAttackAction(GameConfiguration gameConfiguration,
            CharacterSheet attacker, CharacterSheet defender,
            Location defenderLocation) {
        super(gameConfiguration, defender, defenderLocation);
        this.attacker = Objects.requireNonNull(attacker, "Need attacker");
    }

    @Override
    public boolean canExecute(GameState state) {
        // No checking for range, etc.
        // TODO: check for dead attacker, other statuses?
        return true;
    }

    @Override
    public GameState execute(GameState state) {

        state = super.execute(state);

        // For all nearby creatures of the same type and species (if monster)
        updateToHostile(state, (couldBeHostile) -> {
            // if same type AND species AND NOT PET
            // Getting a bit weird.. let's see how it goes..

            if (couldBeHostile.getType().equals(getDamaged().getType())) {
                switch (couldBeHostile.getType()) {
                    case "MONSTER":
                        String defenderSpecies = Monster.get(getDamaged().getId()).getSpecies();
                        String couldBeSpecies = Monster.get(couldBeHostile.getId()).getSpecies();
                        return defenderSpecies.equals(couldBeSpecies);
                }

                return true;
            }

            return false;
        }, getDefenderLocation());

        return state;
    }

    @Override
    protected DamageResolution getDamage(GameState state) {
        CombatResolver combat = new CombatResolver(getGameConfiguration().getRandom());
        DamageResolution combatDamage = combat.resolveCloseCombat(attacker, getDamaged());

        return combatDamage;
    }

    private void updateToHostile(GameState state, CharacterSheet characterSheet,
            Location location) {
        if (!characterSheet.getId().equals("player")) {
            if (!characterSheet.getStatus().isEffected(Effect.EffectNames.HOSTILE.getId())) {
                CharacterSheet updatedToHostile = characterSheet.set(characterSheet.getStatus()
                        .addEffect(new StatusEffect(Effect.EffectNames.HOSTILE.getEffect(), state.getTurnCount())));

                state.getWorld().add(updatedToHostile, location);
            }
        }
    }

    // Also seems like a possible pattern..
    private void updateToHostile(GameState state,
            Predicate<CharacterSheet> isHostileFn, Location near) {
        near.getNeighbors(20).stream()
                .forEach(n -> {
                    CharacterSheet hostileCharacter = state.getWorld().get(n);
                    if (hostileCharacter != null && isHostileFn.test(hostileCharacter)) {

                        updateToHostile(state, hostileCharacter, n);
                    }
                });
    }
}
