package com.zygon.rl.world.action;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.StatusEffect;

import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author zygon
 */
public abstract class DamageAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final CharacterSheet damaged;
    private final Location defenderLocation;

    public DamageAction(GameConfiguration gameConfiguration, CharacterSheet defender,
            Location defenderLocation) {
        this.gameConfiguration = gameConfiguration;
        this.damaged = Objects.requireNonNull(defender, "Need defender");
        this.defenderLocation = defenderLocation;
    }

    protected abstract DamageResolution getDamage(GameState state);

    protected CharacterSheet getDamaged() {
        return damaged;
    }

    protected Location getDefenderLocation() {
        return defenderLocation;
    }

    protected GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    @Override
    public GameState execute(GameState state) {
        DamageResolution damage = getDamage(state);

        state = state.log(damage.toString());

        // Note: could also result in knockback (ie location change) or status
        // effects (wounds, etc).
        CharacterSheet updatedDefender = damaged.loseHitPoints(damage.getTotalDamage());

        if (!updatedDefender.isDead()) {
            state.getWorld().add(updatedDefender, defenderLocation);
            updateToHostile(state.getWorld(), updatedDefender, defenderLocation);
        } else {
            state = state.log(updatedDefender.getName() + " died!");
            state.getWorld().remove(updatedDefender, defenderLocation);
            state.getWorld().add(getCorpseId(damaged), defenderLocation);
        }

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
    private static Identifable getCorpseId(CharacterSheet character) {
        return Corpse.get("corpse");
    }
}
