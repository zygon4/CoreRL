/*
 * Copyright Liminal Data Systems 2024
 */
package com.zygon.rl.world.character;

import java.util.List;
import java.util.Optional;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.DialogAction;

/**
 * A built in ability for initiating dialog. Useful as an ability because the
 * UI/targeting already exists.
 *
 * @author djc
 */
public class InitiateDialog implements Ability {

    private final GameConfiguration gameConfiguration;

    public InitiateDialog(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    @Override
    public String getDescription() {
        return "Talk to an adjacent being";
    }

    @Override
    public String getName() {
        return "INITIATE_DIALOG";
    }

    @Override
    public String getProficiencyId() {
        return Proficiencies.Names.CHARM.getId();
    }

    @Override
    public Target getTargeting() {
        return Target.ADJACENT_LIVING;
    }

    @Override
    public AbilityActionSet use(GameState state,
            Optional<Identifable> targetEntity,
            Optional<Location> targetLocation) {

        DialogAction dialogAction = new DialogAction(
                this.gameConfiguration, targetLocation.get());

        return AbilityActionSet.create(List.of(dialogAction));
    }
}
