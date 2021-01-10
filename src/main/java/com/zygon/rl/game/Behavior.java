/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author zygon
 */
public interface Behavior {

    // I think this needs more info??
    Action get(CharacterSheet character);
}
