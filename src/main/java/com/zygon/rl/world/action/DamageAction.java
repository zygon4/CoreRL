package com.zygon.rl.world.action;

import java.lang.System.Logger.Level;
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

        if (damage.isXpGained()) {
            state = resolveXpGain(state, damage);
        }

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

    protected GameState resolveXpGain(GameState state, DamageResolution damage) {
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

    /*
     These utilities below could/should be moved somewhere more common..
     */
    public static void updateToHostile(GameState state,
            CharacterSheet characterSheet, Location location) {
        addStatusEffect(state, characterSheet, location, Effect.EffectNames.HOSTILE.getId());
    }

    public static void addStatusEffect(GameState state,
            CharacterSheet characterSheet, Location location,
            final String statusEffectId) {
        if (!characterSheet.getId().equals("player")) {
            if (!characterSheet.getStatus().isEffected(statusEffectId)) {

                Effect effect = Effect.EffectNames.getInstance(statusEffectId).getEffect();

                logger.log(Level.INFO, "{0} {1} is now effected by {2}",
                        new Object[]{characterSheet.getSpecies(), characterSheet.getName(), effect.getName()});

                CharacterSheet updated = characterSheet
                        .set(characterSheet.getStatus()
                                .addEffect(new StatusEffect(
                                        effect,
                                        state.getTurnCount())));

                new SetCharacterAction(updated, location).execute(state);

                // TODO: set message log?
            }
        }
    }

    // Also seems like a possible pattern..
    public static void updateToHostile(GameState state,
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
