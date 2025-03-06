package com.zygon.rl.game.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.zygon.rl.data.Effect.EffectNames;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;
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
            case BLEEDING_MAJOR:
                // Doing this "every X turns" should be a utility..
                int currentTurn = state.getTurnCount();
                int inceptionTurn = effect.getTurn();
                if ((inceptionTurn - currentTurn) % 10 == 0) {
                    return new StatusDamageAction(getGameConfiguration(), effect, sheet, location);
                }
                return null;
            default:
                return null;
        }
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
            actions.addAll(getStatusEffectActions(state, npc.getValue(), npc.getKey()));
        }

        CharacterSheet player = state.getWorld().getPlayer();
        actions.addAll(getStatusEffectActions(state, player, state.getWorld().getPlayerLocation()));

        for (Action action : actions) {
            state = action.execute(state);
        }

        return state;
    }
}
