/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.data.monstergroups;

import java.util.List;
import java.util.Random;

/**
 *
 * @author djc
 */
public class Group {

    private String id;
    private int weight;
    private List<Integer> packSize; // idx:0 = min, idx:1 = max | inclusive

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Integer> getPackSize() {
        return packSize;
    }

    public void setPackSize(List<Integer> packSize) {
        this.packSize = packSize;
    }

    public int getMinPackSize() {
        return orOne(0);
    }

    public int getMaxPackSize() {
        return orOne(1);
    }

    public int getPackSize(Random random) {
        return random.nextInt(getMinPackSize(), getMaxPackSize() + 1);
    }

    private int orOne(int idx) {
        List<Integer> size = getPackSize();
        return size != null
                ? size.get(idx)
                : 1;
    }
}
