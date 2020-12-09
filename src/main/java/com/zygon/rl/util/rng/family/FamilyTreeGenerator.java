package com.zygon.rl.util.rng.family;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// Just a thought: somehow able to use a generic algo for this?
public class FamilyTreeGenerator {

    private static final Random NAME_RANDOM = new Random();

    public static Family join(Family family1, Family family2) {

        // Need permutations of m/f pairs from each family,
        // and choose one at random
        Map<Sex, Set<Person>> fam1 = family1.getChildren().stream()
                .collect(Collectors.groupingBy(Person::getSex, Collectors.toSet()));
        Map<Sex, Set<Person>> fam2 = family2.getChildren().stream()
                .collect(Collectors.groupingBy(Person::getSex, Collectors.toSet()));

        return null;
        // For each person of each sex in fam1, pair with
        // each person of each other sex in fam2
    }

    public static Person create() {

        boolean male = NAME_RANDOM.nextBoolean();

        Name name = male ? Names.getRandomMaleName() : Names.getRandomFemaleName();
        Sex sex = male ? Sex.MALE : Sex.FEMALE;

        return new Person(name, sex);
    }
}
