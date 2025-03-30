package com.zygon.rl.world.action;

import java.util.Objects;

import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.combat.CombatResolver;

/**
 * Eventually can introduce a generic CombatAction that Melee and Ranged hang
 * off.
 *
 * @author zygon
 */
public class MeleeAttackAction extends DamageAction {

    private final CharacterSheet attacker;
    private final Location attackerLocation;

    public MeleeAttackAction(GameConfiguration gameConfiguration,
            CharacterSheet attacker, Location attackerLocation,
            CharacterSheet defender, Location defenderLocation) {
        super(gameConfiguration, defender, defenderLocation);
        this.attacker = Objects.requireNonNull(attacker, "Need attacker");
        this.attackerLocation = Objects.requireNonNull(attackerLocation, "Need attacker location");
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

    @Override
    protected GameState resolveXpGain(GameState state, DamageResolution damage) {
        state = super.resolveXpGain(state, damage);
        GainXpAction gainXp = new GainXpAction(Proficiencies.Names.MELEE.getId(), attacker, attackerLocation);
        return gainXp.execute(state);
    }
}
