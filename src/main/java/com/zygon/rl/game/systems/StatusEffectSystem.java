package com.zygon.rl.game.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.Effect.EffectNames;
import static com.zygon.rl.data.Effect.EffectNames.BLEEDING_MAJOR;
import static com.zygon.rl.data.Effect.EffectNames.CONFUSION;
import static com.zygon.rl.data.Effect.EffectNames.ENHANCED_SPEED;
import static com.zygon.rl.data.Effect.EffectNames.HEALING_MINOR;
import static com.zygon.rl.data.Effect.EffectNames.TERRIFIED;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Weather;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetCharacterAction;
import com.zygon.rl.world.action.SetPoolAction;
import com.zygon.rl.world.action.StatusDamageAction;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Pool;
import com.zygon.rl.world.character.StatusEffect;

/**
 * Check for status effects. Confirm if they should be applied now, continue, or
 * removed. The static (ie stats) buffs/debuffs are applied in the
 * {@link CharacterSheet} itself. Any dynamic effects (ie damage) are performed
 * here.
 *
 * @author zygon
 */
public class StatusEffectSystem extends GameSystem {

    private static class EffectSystem extends GameSystem {

        public EffectSystem(GameConfiguration gameConfiguration) {
            super(gameConfiguration);
        }

        @Override
        public GameState apply(GameState state) {
            Map<Location, CharacterSheet> closeCharacters = state.getWorld().getAll(
                    state.getWorld().getPlayerLocation(), null, REALITY_BUBBLE);

            Collection<Action> actions = new ArrayList<>();

            for (var npc : closeCharacters.entrySet()) {
                actions.addAll(auditStatusEffects(state, npc.getValue(), npc.getKey()));
                actions.addAll(getStatusEffectActions(state, npc.getValue(), npc.getKey()));
            }

            CharacterSheet player = state.getWorld().getPlayer();
            actions.addAll(auditStatusEffects(state, player, state.getWorld().getPlayerLocation()));
            actions.addAll(getStatusEffectActions(state, player, state.getWorld().getPlayerLocation()));

            for (Action action : actions) {
                state = action.execute(state);
            }

            return state;
        }

        private Collection<Action> auditStatusEffects(GameState state,
                CharacterSheet character, Location location) {

            Collection<Action> actions = new ArrayList<>();

            for (String flag : STATUS_FLAGS) {
                Boolean statusFlag = character.getTemplate().getFlag(flag);

                // Only look at the flags that this character is effected by
                if (Objects.isNull(statusFlag) || !statusFlag) {
                    continue;
                }

                // can't use the CommonAttributes becaue it's not a constant expression :(
                switch (flag) {
                    case "WEAK_TO_SUN" -> {
                        final String sunFeverMinorId = Effect.EffectNames.SUN_FEVER_MINOR.getId();

                        // is it sunny here?
                        if (state.getWorld().getWeather() == Weather.CLEAR) { // TODO: and outdoors
                            // are we effected by the sun?
                            if (!character.getStatus().getEffects().containsKey(sunFeverMinorId)) {

                                // TODO: common utility in this class to set effects..
                                SetCharacterAction setSunFever = new SetCharacterAction(
                                        character.set(character.getStatus()
                                                .addEffect(new StatusEffect(
                                                        Effect.get(sunFeverMinorId),
                                                        state.getTurnCount()))), location);
                                actions.add(setSunFever);
                            }
                        } else {
                            if (character.getStatus().getEffects().containsKey(sunFeverMinorId)) {
                                SetCharacterAction removeSunFever
                                        = new SetCharacterAction(
                                                character.set(character.getStatus()
                                                        .removeEffect(sunFeverMinorId)), location);
                                actions.add(removeSunFever);
                            }
                        }
                    }
                }
            }

            return actions;
        }

        Collection<Action> getStatusEffectActions(GameState state,
                CharacterSheet sheet, Location location) {
            Map<String, StatusEffect> currentStatusEffects = sheet.getStatus().getEffects();

            return currentStatusEffects.values().stream()
                    .map(se -> translateToAction(state, sheet, location, se))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // Remove status effects that have a max duration.
        private Action translateToAction(GameState state, CharacterSheet sheet,
                Location location, StatusEffect effect) {
            final int currentTurn = state.getTurnCount();
            switch (EffectNames.getInstance(effect.getEffect().getId())) {
                case BLEEDING_MAJOR -> {
                    // Doing this "every X turns" should be a utility..
                    int inceptionTurn = effect.getTurn();
                    if ((inceptionTurn - currentTurn) % MAJOR_EFFECT_FREQ == 0) {
                        return new StatusDamageAction(getGameConfiguration(), effect, sheet, location);
                    }
                    return translateTurnBased(currentTurn, sheet, location, effect);
                }
                case HEALING_MINOR -> {
                    int inceptionTurn = effect.getTurn();
                    // TODO: the frequency should be in JSON
                    if ((inceptionTurn - currentTurn) % MINOR_EFFECT_FREQ == 0) {
                        return new SetCharacterAction(sheet.gainHitPoints(1), location);
                    }
                    return translateTurnBased(currentTurn, sheet, location, effect);
                }
                case CONFUSION, TERRIFIED, ENHANCED_SPEED -> {
                    return translateTurnBased(currentTurn, sheet, location, effect);
                }
                default -> {
                    return null;
                }
            }
        }

        private Action translateTurnBased(int currentTurn, CharacterSheet player,
                Location location, StatusEffect statusEffect) {
            int inceptionTurn = statusEffect.getTurn();
            final String id = statusEffect.getEffect().getId();
            Effect effectData = Effect.get(id);

            if ((currentTurn - inceptionTurn) > effectData.getMaxDuration()) {
                return new SetCharacterAction(player.set(player.getStatus()
                        .removeEffect(id)), location);
            }

            return null;
        }
    }

    private static class PoolSystem extends GameSystem {

        public PoolSystem(GameConfiguration gameConfiguration) {
            super(gameConfiguration);
        }

        @Override
        public GameState apply(GameState state) {
            Map<Location, CharacterSheet> closeCharacters = state.getWorld().getAll(
                    state.getWorld().getPlayerLocation(), null, REALITY_BUBBLE);

            Collection<Action> actions = new ArrayList<>();

            // look at each pool, discover if they require autodraining
            for (var npc : closeCharacters.entrySet()) {
                Action npcPools = translateTurnBased(state, npc.getValue(), npc.getKey());
                if (npcPools != null) {
                    actions.add(npcPools);
                }
            }

            CharacterSheet player = state.getWorld().getPlayer();

            Action playerPools = translateTurnBased(state, player, state.getWorld().getPlayerLocation());
            if (playerPools != null) {
                actions.add(playerPools);
            }

            for (Action action : actions) {
                state = action.execute(state);
            }

            return state;
        }

        private Action translateTurnBased(GameState state, CharacterSheet player,
                Location location) {

            for (String poolId : player.getStatus().getPoolIds()) {
                Pool pool = player.getStatus().getPool(poolId);
                int freq = pool.getPoolData().getDrainFrequency();
                if (freq > 0) {
                    if (state.getTurnCount() % freq == 0) {
                        return new SetPoolAction(player, location, poolId, pool.getPoints() - 1);
                    }
                }
            }

            return null;
        }
    }

    // TODO: in JSON
    private static final int MINOR_EFFECT_FREQ = 2;
    private static final int MAJOR_EFFECT_FREQ = 4;

    private static final Set<String> STATUS_FLAGS = Set.of(
            CommonAttributes.WEAK_TO_SUN.name(),
            CommonAttributes.ENHANCED_SPEED.name()
    );

    private final EffectSystem effectSystem;
    private final PoolSystem poolSystem;

    public StatusEffectSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.effectSystem = new EffectSystem(gameConfiguration);
        this.poolSystem = new PoolSystem(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {
        state = effectSystem.apply(state);
        state = poolSystem.apply(state);
        return state;
    }
}
