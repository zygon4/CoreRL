package com.zygon.rl.util.rng.family;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Collections;
import java.util.Set;

/**
 *
 * @author zygon
 */
public final class Family {

    private final Person p1;
    private final Person p2;
    private final Set<Person> children;
    private final FamilyTreeGenerator outer;

    public Family(Person p1, Person p2, Set<Person> children, final FamilyTreeGenerator outer) {
        this.outer = outer;
        this.p1 = p1;
        this.p2 = p2;
        this.children = children != null ? Collections.unmodifiableSet(children) : Collections.emptySet();
    }

    public Person getP1() {
        return p1;
    }

    public Person getP2() {
        return p2;
    }

    public Set<Person> getChildren() {
        return children;
    }
    // TODO: toString

}
