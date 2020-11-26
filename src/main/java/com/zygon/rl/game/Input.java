/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import java.util.Objects;

/**
 *
 * @author zygon
 */
public class Input {

    public static int UNKNOWN_INPUT = -1;

    public static Input getUnknown() {
        return new Input(UNKNOWN_INPUT);
    }

    // So input shouldn't have a character, it should TAKE a
    // character(for example) and turn it into a logical input
    private final int input;

    public Input(int character) {
        this.input = character;
    }

    @Deprecated
    public char getCharacter() {
        return (char) input;
    }

    public int getInput() {
        return input;
    }

    public boolean isUnknown() {
        return getInput() == UNKNOWN_INPUT;
    }

    public static Input valueOf(int character) {
        return new Input(character);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.input);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Input other = (Input) obj;
        return this.input == other.input;
    }

    @Override
    public String toString() {
        return "" + getCharacter();
    }
}
