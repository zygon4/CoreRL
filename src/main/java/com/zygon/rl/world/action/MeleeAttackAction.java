package com.zygon.rl.world.action;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.combat.CombatResolver;

import java.util.Objects;

/**
 *
 * @author zygon
 */
public class MeleeAttackAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final CharacterSheet attacker;
    private final CharacterSheet defender;
    private final Location defenderLocation;

    public MeleeAttackAction(World world, GameConfiguration gameConfiguration,
            CharacterSheet attacker, CharacterSheet defender, Location defenderLocation) {
        super(world);
        this.gameConfiguration = gameConfiguration;
        this.attacker = Objects.requireNonNull(attacker, "Need attacker");
        this.defender = Objects.requireNonNull(defender, "Need defender");
        this.defenderLocation = defenderLocation;
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
        CombatResolver.Resolution combatDamage = combat.resolveCloseCombat(attacker, defender);

        System.out.println(combatDamage);

        // Note: could also result in knockback (ie location change) or status
        // effects (wounds, etc).
        CharacterSheet updatedDefender = defender.loseHitPoints(combatDamage.getTotalDamage());

        if (updatedDefender != null) {
            if (!updatedDefender.isDead()) {
                getWorld().add(updatedDefender, defenderLocation);
            } else {
                System.out.println(updatedDefender.getName() + " is dead!");
                getWorld().remove(updatedDefender, defenderLocation);
                // TODO: install corpse instead
            }
        } else {
            // maybe killed otherwise,
        }
    }
}
