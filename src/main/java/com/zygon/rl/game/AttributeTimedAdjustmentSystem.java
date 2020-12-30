package com.zygon.rl.game;

import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.Calendar;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.IntegerAttribute;
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
    private final Function<GameState, Long> attributeAdjustmentFn;
    private final Function<Long, String> statusAdjustmentFn;

    private Calendar cal;

    public AttributeTimedAdjustmentSystem(GameConfiguration gameConfiguration, String attributeName,
            long frequencySeconds, Function<GameState, Long> attributeAdjustmentFn,
            Function<Long, String> statusAdjustmentFn) {
        super(gameConfiguration);
        this.attributeName = attributeName;
        this.frequencySeconds = frequencySeconds;
        this.attributeAdjustmentFn = attributeAdjustmentFn;
        this.statusAdjustmentFn = statusAdjustmentFn;
    }

    @Override
    public GameState apply(GameState state) {
        Calendar current = state.getWorld().getCalendar();
        Entity player = state.getWorld().get(getGameConfiguration().getPlayerUuid());

        if (cal != null) {
            long secondsDiff = current.getDifferenceSeconds(cal);
            long numberOfAdjustments = secondsDiff / frequencySeconds;

            if (numberOfAdjustments > 0) {

                for (int i = 0; i < numberOfAdjustments; i++) {
                    IntegerAttribute currentValue = IntegerAttribute.create(
                            player.getAttribute(attributeName));

                    long adjustment = attributeAdjustmentFn.apply(state);
                    long adjustedValue = currentValue.getIntegerValue() + adjustment;

                    state.getWorld().add(player.copy()
                            .setAttributeValue(attributeName, String.valueOf(adjustedValue))
                            .build());
                }
                cal = current;
            }
        } else {
            cal = current;
        }

        if (statusAdjustmentFn != null) {
            long watchAttrValue = IntegerAttribute.create(player.getAttribute(attributeName)).getIntegerValue();
            Attribute statusAttr = player.getAttribute(CharacterSheet.STATUS_PREFIX + attributeName);
            String status = statusAdjustmentFn.apply(watchAttrValue);

            if (status == null) {
                // clear status
                if (statusAttr != null) {
                    state.getWorld()
                            .add(player.remove(CharacterSheet.STATUS_PREFIX + attributeName));
                }
            } else {
                // add or adjust status
                if (statusAttr == null || !statusAttr.getValue().equals(status)) {
                    state.getWorld().add(player.add(Attribute.builder()
                            .setName(CharacterSheet.STATUS_PREFIX + attributeName)
                            //                        .setDescription(statusAttr.getDescription())
                            .setValue(status)
                            .build()));
                }
            }
        }

        return state;
    }
}
