package com.zygon.rl.game;

import com.zygon.rl.world.Calendar;
import com.zygon.rl.world.character.CharacterSheet;

import java.util.function.Function;

/**
 * This will watch an attribute at a regular interval and adjust according to
 * the provided function. Can be taught to raise status effects if desired.
 *
 * NOTE: this only works for integer attributes right now..
 *
 * @author zygon
 */
public class AttributeTimedAdjustmentSystem extends GameSystem {

    private final String attributeName;
    private final long frequencySeconds;
    private final Function<GameState, Integer> attributeAdjustmentFn;
    private final Function<Integer, String> statusAdjustmentFn;

    private Calendar cal;

    public AttributeTimedAdjustmentSystem(GameConfiguration gameConfiguration, String attributeName,
            long frequencySeconds, Function<GameState, Integer> attributeAdjustmentFn,
            Function<Integer, String> statusAdjustmentFn) {
        super(gameConfiguration);
        this.attributeName = attributeName;
        this.frequencySeconds = frequencySeconds;
        this.attributeAdjustmentFn = attributeAdjustmentFn;
        this.statusAdjustmentFn = statusAdjustmentFn;
    }

    @Override
    public GameState apply(GameState state) {
        Calendar current = state.getWorld().getCalendar();
        CharacterSheet player = state.getWorld().get("player");

        if (cal != null) {
            long secondsDiff = current.getDifferenceSeconds(cal);
            long numberOfAdjustments = secondsDiff / frequencySeconds;

            if (numberOfAdjustments > 0) {

                for (int i = 0; i < numberOfAdjustments; i++) {
                    Integer currentValue = player.getStatus().getEffects().get(attributeName);

                    int adjustment = attributeAdjustmentFn.apply(state);
                    int adjustedValue = currentValue != null ? currentValue.intValue() : 0 + adjustment;

                    state.getWorld().move(player.set(
                            player.getStatus().addEffect(attributeName, adjustedValue)),
                            state.getWorld().getPlayerLocation(),
                            state.getWorld().getPlayerLocation());
                }
                cal = current;
            }
        } else {
            cal = current;
        }

        if (statusAdjustmentFn != null) {

            // TODO:
//            Integer watchValue = player.getStatus().getEffects().get(attributeName);
//            String statusName = CharacterSheet.STATUS_PREFIX + attributeName;
//            Integer statusAttr = player.getStatus().getEffects().containsKey(statusName);
//            String status = statusAdjustmentFn.apply(watchValue);
//
//            if (status == null) {
//                // clear status
//                if (statusAttr != null) {
//                    state.getWorld().add(player.set(
//                            player.getStatus().removeEffect(
//                                    CharacterSheet.STATUS_PREFIX + attributeName)), null);
//                }
//            } else {
//                // add or adjust status
//                if (statusAttr == null || !statusAttr.getValue().equals(status)) {
//                    state.getWorld().add(player.add(Attribute.builder()
//                            .setName(CharacterSheet.STATUS_PREFIX + attributeName)
//                            //                        .setDescription(statusAttr.getDescription())
//                            .setValue(status)
//                            .build()));
//                }
//            }
        }

        return state;
    }
}
