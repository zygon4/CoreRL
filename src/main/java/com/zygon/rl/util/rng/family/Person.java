package com.zygon.rl.util.rng.family;

// TODO: implement equals/hash
public final class Person {

    private final Name name;
    private final Sex sex;

    public Person(Name name, Sex sex) {
        this.name = name;
        this.sex = sex;
    }

    public Name getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return name + " [" + sex.getIcon() + "]";
    }
}
