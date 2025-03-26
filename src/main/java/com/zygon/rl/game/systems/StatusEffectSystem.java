package com.zygon.rl.game.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.Effect.EffectNames;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Weather;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetCharacterAction;
import com.zygon.rl.world.action.StatusDamageAction;
import com.zygon.rl.world.character.CharacterSheet;
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

    public StatusEffectSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    // TODO: remove status effects that have a max duration..
    //
    private Action translate(GameState state, CharacterSheet sheet,
            Location location, StatusEffect effect) {
        switch (EffectNames.getInstance(effect.getEffect().getId())) {
            case BLEEDING_MAJOR -> {
                // Doing this "every X turns" should be a utility..
                int currentTurn = state.getTurnCount();
                int inceptionTurn = effect.getTurn();
                if ((inceptionTurn - currentTurn) % 10 == 0) {
                    return new StatusDamageAction(getGameConfiguration(), effect, sheet, location);
                }
                return null;
            }
            case CONFUSION -> {
                // Doing this "every X turns" should be a utility..
                // TODO: based on character stats/abilities to shake off..
                int currentTurn = state.getTurnCount();
                int inceptionTurn = effect.getTurn();
                if ((currentTurn - inceptionTurn) > 20) {
                    final String confusionId = Effect.EffectNames.CONFUSION.getId();
                    return new SetCharacterAction(location,
                            sheet.set(sheet.getStatus().removeEffect(confusionId)));
                }
                return null;
            }
            case ENHANCED_SPEED -> {
                // Doing this "every X turns" should be a utility..
                int currentTurn = state.getTurnCount();
                int inceptionTurn = effect.getTurn();
                if ((currentTurn - inceptionTurn) > 10) {
                    final String enhancedSpeedId = Effect.EffectNames.ENHANCED_SPEED.getId();
                    return new SetCharacterAction(location,
                            sheet.set(sheet.getStatus().removeEffect(enhancedSpeedId)));
                }
                return null;
            }
            default -> {
                return null;
            }
        }
        // sun damage if weakened?
    }

    private static final Set<String> STATUS_FLAGS = Set.of(
            CommonAttributes.WEAK_TO_SUN.name(),
            CommonAttributes.ENHANCED_SPEED.name()
    );

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
                            SetCharacterAction setSunFever = new SetCharacterAction(location,
                                    character.set(character.getStatus()
                                            .addEffect(new StatusEffect(
                                                    Effect.get(sunFeverMinorId),
                                                    state.getTurnCount()))));
                            actions.add(setSunFever);
                        }
                    } else {
                        if (character.getStatus().getEffects()
                                .containsKey(sunFeverMinorId)) {
                            SetCharacterAction removeSunFever = new SetCharacterAction(location,
                                    character.set(character.getStatus().removeEffect(sunFeverMinorId)));
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
                .map(se -> translate(state, sheet, location, se))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
}
