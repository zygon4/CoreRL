package com.zygon.rl.util.rng.family;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public enum Sex {
    MALE("m"), FEMALE("f");
    private final String icon;

    private Sex(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
