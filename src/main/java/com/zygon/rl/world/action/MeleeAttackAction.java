package com.zygon.rl.world.action;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.combat.CombatResolver;

/**
 *
 * @author zygon
 */
public class MeleeAttackAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final CharacterSheet attacker;
    private final CharacterSheet defender;

    public MeleeAttackAction(World world, GameConfiguration gameConfiguration,
            CharacterSheet attacker, CharacterSheet defender) {
        super(world);
        this.gameConfiguration = gameConfiguration;
        this.attacker = attacker;
        this.defender = defender;
    }

    @Override
    public boolean canExecute() {
        // No checking for range, etc.
        // TODO: check for dead attacker, other statuses?
        return true;
    }

    @Override
    public void execute() {
        CombatResolver combat = new CombatResolver(gameConfiguration.getRandom());
        CombatResolver.Resolution resolveCloseCombat = combat.resolveCloseCombat(attacker, defender);

        // TODO: resolve damage, add logs
        System.out.println(resolveCloseCombat);
    }
}
