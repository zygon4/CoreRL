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
public class DoubleAttribute extends Attribute {

    private final double value;

    private DoubleAttribute(Attribute attribute) {
        super(attribute.copy());
        this.value = getValue(attribute);
    }

    public static DoubleAttribute create(Attribute attribute) {
        return new DoubleAttribute(attribute);
    }

    public double getDoubleValue() {
        return value;
    }

    public static double getValue(Attribute attribute) {
        return Double.valueOf(attribute.getValue());
    }
}
