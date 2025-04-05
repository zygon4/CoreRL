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
import static com.zygon.rl.data.Effect.EffectNames.SENTRY;
import static com.zygon.rl.data.Effect.EffectNames.TERRIFIED;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.Weather;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.DamageAction;
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

    private static final System.Logger LOGGER = System.getLogger(StatusEffectSystem.class.getCanonicalName());

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
                actions.addAll(auditFlagStatusEffects(state, npc.getValue(), npc.getKey()));
                actions.addAll(getStatusEffectActions(state, npc.getValue(), npc.getKey()));
            }

            CharacterSheet player = state.getWorld().getPlayer();
            actions.addAll(auditFlagStatusEffects(state, player, state.getWorld().getPlayerLocation()));
            actions.addAll(getStatusEffectActions(state, player, state.getWorld().getPlayerLocation()));

            for (Action action : actions) {
                state = action.execute(state);
            }

            return state;
        }

        /**
         * Checks all creature flags which may affect status effects.
         *
         * @param state
         * @param character
         * @param location
         * @return
         */
        private Collection<Action> auditFlagStatusEffects(GameState state,
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
                    case "SENTRY" -> {
                        final String sentryId = Effect.EffectNames.SENTRY.getId();
                        if (character.getStatus().getEffects().containsKey(sentryId)) {
                            // if already in sentry mode, look to hostile
                            if (isPlayerNearby(state.getWorld(), character, location)) {
                                DamageAction.updateToHostile(state, character, location);
                            }
                        } else {
                            // check if "sentry worthy" items are nearby and react..
                            Map<Location, Tangible> occultEvidence = discoverNearby(
                                    state.getWorld(), character, location, CommonAttributes.OCCULT.name());

                            // How harsh? any evidence? 2+ instances?
                            if (!occultEvidence.isEmpty()) {
                                DamageAction.addStatusEffect(state, character, location, Effect.EffectNames.SENTRY.getId());
                            }
                        }
                    }
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
                case CONFUSION, ENHANCED_SPEED, HOSTILE, SENTRY, TERRIFIED -> {
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

            Action playerPools = translateTurnBased(state, player,
                    state.getWorld().getPlayerLocation());
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
                        return new SetPoolAction(player, location, poolId, pool.getPoints() - 1, true, true);
                    }
                }
            }

            return null;
        }

        // TODO actions to notify
        // TODO: this is not used yet..
        private Action translateNotification(GameState state,
                CharacterSheet player, Location location) {

            for (String poolId : player.getStatus().getPoolIds()) {
                Pool pool = player.getStatus().getPool(poolId);
                int freq = pool.getPoolData().getDrainFrequency();
                if (freq > 0) {
                    if (state.getTurnCount() % freq == 0) {
                        return new SetPoolAction(player, location, poolId, pool.getPoints() - 1, true, true);
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
            CommonAttributes.SENTRY.name(),
            CommonAttributes.WEAK_TO_SUN.name()
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

    // Not sure where this belongs but at least it's used a couple times..
    /**
     * Range is wis + int
     *
     * @param world
     * @param character
     * @param location
     * @param flag
     * @return
     */
    static Map<Location, Tangible> discoverNearby(World world,
            CharacterSheet character, Location location, String flag) {
        int range = getDiscoveryRange(character);
        return world.getAllByFlag(location, flag, range, false);
    }

    static boolean isPlayerNearby(World world, CharacterSheet npc,
            Location npcLoc) {
        int npcDiscoveryRange = getDiscoveryRange(npc);
        Location playerLoc = world.getPlayerLocation();
        return playerLoc.getDistance(npcLoc) <= npcDiscoveryRange;
    }

    static int getDiscoveryRange(CharacterSheet character) {
        return character.getModifiedStats().getWisdom()
                + character.getModifiedStats().getIntelligence();
    }
}
