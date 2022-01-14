package com.zygon.rl.util.rng.family;

// could go in RNG core
// TODO: implement equals/hash
public class Name {

    private final String first;
    private final String last;

    public Name(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    @Override
    public String toString() {
        return first + " " + last;
    }

}
