package com.zygon.rl.world.action;

import com.zygon.rl.data.Effect;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.CorpseItem;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.Item;
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

    private static final System.Logger logger = System.getLogger(DamageAction.class.getCanonicalName());

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

        logger.log(System.Logger.Level.TRACE,
                "DAMAGE: " + updatedDefender.getId() + " at " + defenderLocation);

        if (!updatedDefender.isDead()) {
            state.getWorld().add(updatedDefender, defenderLocation);
            updateToHostile(state.getWorld(), updatedDefender, defenderLocation);
        } else {
            logger.log(System.Logger.Level.TRACE,
                    "DEAD: " + updatedDefender.getId() + " at " + defenderLocation);

            state = state.log(updatedDefender.getName() + " died!");
            state.getWorld().remove(updatedDefender, defenderLocation);
            Item corpse = CorpseItem.create(damaged);
            state.getWorld().add(corpse, defenderLocation);
        }

        return state;
    }

    private void updateToHostile(World world, CharacterSheet characterSheet, Location location) {
        if (!characterSheet.getId().equals("player")) {
            if (!characterSheet.getStatus().isEffected(Effect.EffectNames.HOSTILE.getId())) {
                CharacterSheet updatedToHostile = characterSheet.set(characterSheet.getStatus()
                        .addEffect(new StatusEffect(Effect.EffectNames.HOSTILE.getEffect())));

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
}
