/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public class IntegerAttribute extends Attribute {

    private final int value;

    private IntegerAttribute(Attribute attribute) {
        super(attribute.copy());
        this.value = getValue(attribute);
    }

    public static IntegerAttribute create(Attribute attribute) {
        return new IntegerAttribute(attribute);
    }

    public int getIntegerValue() {
        return value;
    }

    public static int getValue(Attribute attribute) {
        return Integer.valueOf(attribute.getValue());
    }
}
