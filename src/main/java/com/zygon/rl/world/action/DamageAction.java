package com.zygon.rl.world.action;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.zygon.rl.data.Effect;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.StatusEffect;

/**
 *
 * @author zygon
 */
public abstract class DamageAction extends Action {

    private static final System.Logger logger = System.getLogger(DamageAction.class.getCanonicalName());

    private final GameConfiguration gameConfiguration;
    private final CharacterSheet damaged;
    private final Location defenderLocation;

    public DamageAction(GameConfiguration gameConfiguration,
            CharacterSheet defender,
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

        logger.log(System.Logger.Level.INFO,
                "DAMAGE: " + updatedDefender.getId() + " at " + defenderLocation + " from " + damage);

        if (!updatedDefender.isDead()) {
            state.getWorld().add(updatedDefender, defenderLocation);

            // TODO: some should flee..
            updateToHostile(state, updatedDefender, defenderLocation);
        } else {
            final String reason = damage.toString();
            logger.log(System.Logger.Level.INFO,
                    "DEAD: " + updatedDefender.getId() + " at " + defenderLocation + " due to " + reason);

            Map<CharacterSheet.TriggerType, Action> triggers = updatedDefender.getTriggers();
            if (triggers.containsKey(CharacterSheet.TriggerType.DEATH)) {

                Action action = triggers.get(CharacterSheet.TriggerType.DEATH);
                if (action.canExecute(state)) {
                    state = action.execute(state);
                } else {
                    state = updateToDead(state, updatedDefender, reason);
                }
            } else {
                state = updateToDead(state, updatedDefender, reason);
            }
        }

        return state;
    }

    private GameState updateToDead(GameState state,
            CharacterSheet updatedDefender, final String reason) {
        DeathAction deathAction = new DeathAction(updatedDefender, defenderLocation, reason);
        if (deathAction.canExecute(state)) {
            state = deathAction.execute(state);
        }

        return state;
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
