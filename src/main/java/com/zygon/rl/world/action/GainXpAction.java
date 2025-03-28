package com.zygon.rl.world.action;

import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author zygon
 */
public class GainXpAction extends Action {

    private static final System.Logger logger = System.getLogger(GainXpAction.class.getCanonicalName());

    // TODO: dynamic xp
    private final int xpGain = 5;

    private final String proficiencyId;
    private final CharacterSheet player;
    private final Location location;

    public GainXpAction(String proficiencyId, CharacterSheet player,
            Location location) {
        this.proficiencyId = proficiencyId;
        this.player = player;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        return addXp(state, player, location, proficiencyId, xpGain);
    }

    private GameState addXp(GameState state, CharacterSheet player,
            Location location, String proficiencyId, int xp) {
        player = player.copy()
                .progress(player.getProgress().add(proficiencyId, xp))
                .build();

        SetCharacterAction setProgress = new SetCharacterAction(
                player, location);

        if (setProgress.canExecute(state)) {
            state = setProgress.execute(state);
            logger.log(System.Logger.Level.INFO, "{0} gained {1} xp in {2}",
                    new Object[]{
                        player.getName(),
                        xp,
                        Proficiencies.get(proficiencyId).getName()}
            );
        }

        return state;
    }
}
