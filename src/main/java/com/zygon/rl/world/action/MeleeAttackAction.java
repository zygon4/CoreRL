package com.zygon.rl.world.action;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.StatusEffect;
import com.zygon.rl.world.combat.CombatResolver;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Eventually can introduce a generic CombatAction that Melee and Ranged hang
 * off.
 *
 * @author zygon
 */
public class MeleeAttackAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final CharacterSheet attacker;
    private final CharacterSheet defender;
    private final Location defenderLocation;

    public MeleeAttackAction(GameConfiguration gameConfiguration,
            CharacterSheet attacker, CharacterSheet defender, Location defenderLocation) {
        this.gameConfiguration = gameConfiguration;
        this.attacker = Objects.requireNonNull(attacker, "Need attacker");
        this.defender = Objects.requireNonNull(defender, "Need defender");
        this.defenderLocation = defenderLocation;
    }

    @Override
    public boolean canExecute(GameState state) {
        // No checking for range, etc.
        // TODO: check for dead attacker, other statuses?
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        CombatResolver combat = new CombatResolver(gameConfiguration.getRandom());
        CombatResolver.Resolution combatDamage = combat.resolveCloseCombat(attacker, defender);

        state = state.log(combatDamage.toString());

        // Note: could also result in knockback (ie location change) or status
        // effects (wounds, etc).
        CharacterSheet updatedDefender = defender.loseHitPoints(combatDamage.getTotalDamage());

        if (!updatedDefender.isDead()) {
            state.getWorld().add(updatedDefender, defenderLocation);
            updateToHostile(state.getWorld(), updatedDefender, defenderLocation);
        } else {
            state = state.log(updatedDefender.getName() + " died!");
            state.getWorld().remove(updatedDefender, defenderLocation);
            state.getWorld().add(getCorpseId(defender), defenderLocation);
        }

        // For all nearby creatures of the same type and species (if monster)
        updateToHostile(state.getWorld(), (couldBeHostile) -> {
            // if same type AND species AND NOT PET
            // Getting a bit weird.. let's see how it goes..

            if (couldBeHostile.getType().equals(defender.getType())) {
                switch (couldBeHostile.getType()) {
                    case "MONSTER":
                        String defenderSpecies = Monster.get(defender.getId()).getSpecies();
                        String couldBeSpecies = Monster.get(couldBeHostile.getId()).getSpecies();
                        return defenderSpecies.equals(couldBeSpecies);
                }

                return true;
            }

            return false;
        }, defenderLocation);

        return state;
    }

    private void updateToHostile(World world, CharacterSheet characterSheet, Location location) {
        if (!characterSheet.getId().equals("player")) {
            if (!characterSheet.getStatus().isEffected(Effect.EffectNames.HOSTILE.getId())) {
                CharacterSheet updatedToHostile = characterSheet.set(characterSheet.getStatus()
                        .addEffect(new StatusEffect(Effect.EffectNames.HOSTILE.getId())));

                world.add(updatedToHostile, location);
            }
        }
    }

    // Also seems like a possible pattern..
    private void updateToHostile(World world, Predicate<CharacterSheet> isHostileFn, Location near) {
        near.getNeighbors(20).stream()
                .forEach(n -> {
                    CharacterSheet hostileCharacter = world.get(n);
                    if (hostileCharacter != null && isHostileFn.test(hostileCharacter)) {

                        updateToHostile(world, hostileCharacter, n);
                    }
                });
    }

    // Incredibly simple "corpse" for anything
    // TODO: future use more info from the character info to get an appropriate corpse
    // can use rng if desired for weight, etc.
    private static String getCorpseId(CharacterSheet character) {
        return Corpse.get("corpse").getId();
    }
}
