package com.zygon.rl.world.character;

import java.util.Random;

// could add more features like rolling multiple dice at once
public class DiceRoller {

    private final Random rand;

    public DiceRoller(Random rand) {
        this.rand = rand;
    }

    public int rollD20() {
        return rand.nextInt(20) + 1;
    }

    public int rollD12() {
        return rand.nextInt(12) + 1;
    }

    public int rollD8() {
        return rand.nextInt(8) + 1;
    }

    public int rollD6() {
        return rand.nextInt(6) + 1;
    }

    public int rollD4() {
        return rand.nextInt(4) + 1;
    }

    public int roll(int max) {
        return rand.nextInt(max) + 1;
    }

}
