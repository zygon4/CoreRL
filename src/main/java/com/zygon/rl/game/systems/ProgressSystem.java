package com.zygon.rl.game.systems;

import java.lang.System.Logger.Level;
import java.util.Map;
import java.util.stream.Collectors;

import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.world.action.SetCharacterAction;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Proficiency;
import com.zygon.rl.world.character.ProficiencyProgress;
import com.zygon.rl.world.character.Progress;

/**
 * @author zygon
 */
public final class ProgressSystem extends GameSystem {

    private static final System.Logger logger = System.getLogger(ProgressSystem.class.getCanonicalName());

    private static final double SCALE = 1.10;

    public ProgressSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
    }

    @Override
    public GameState apply(GameState state) {

        CharacterSheet player = state.getWorld().getPlayer();
        Progress progress = player.getProgress();

        for (String id : Proficiencies.getAllIds()) {
            ProficiencyProgress proficiencyProgress = progress.getProficiencyProgress(id);
            if (proficiencyProgress != null && proficiencyProgress.levelUp()) {
                state = levelUp(state, proficiencyProgress);
            }
        }

        return state;
    }

    private GameState levelUp(GameState state,
            ProficiencyProgress proficiencyProgress) {

        CharacterSheet player = state.getWorld().getPlayer();
        Map<String, Integer> profById = player.getProficiencies().stream()
                .collect(Collectors.toMap(p -> p.getProficiency().getId(), p -> p.getPoints()));

        ProficiencyProgress prog = proficiencyProgress;
        Proficiencies prof = Proficiencies.get(prog.getProficiencyId());
        final String profId = prog.getProficiencyId();

        while (prog.levelUp()) {
            int currentPoints = profById.get(profId);
            int requiredXp = proficiencyProgress.getRequiredXp();
            int currentXp = proficiencyProgress.getXp();

            if (currentXp < requiredXp) {
                throw new IllegalStateException();
            }

            int nextPoints = currentPoints++;
            int nextRequiredXp = (int) (requiredXp * SCALE);
            int nextXp = currentXp - requiredXp; // leftover xp

            prog = ProficiencyProgress.create(profId, nextRequiredXp)
                    .add(nextXp);

            // Set the new state on the character..
            Proficiency currentProf = player.getProficiencies().stream()
                    .filter(p -> p.getProficiency().getId().equals(profId))
                    .findAny().orElseGet(() -> new Proficiency(prof));

            player = player
                    .set(currentProf.incPoints()).copy()
                    .progress(player.getProgress().set(prog))
                    .build();

            SetCharacterAction setProf = new SetCharacterAction(
                    player, state.getWorld().getPlayerLocation());

            if (setProf.canExecute(state)) {
                state = setProf.execute(state);
                logger.log(Level.INFO, "{0} leveled {1} to {2}",
                        new Object[]{player.getName(), prof.getName(), nextPoints});
            }
        }

        return state;
    }
}
