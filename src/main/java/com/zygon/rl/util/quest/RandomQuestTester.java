/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author djc
 */
public class RandomQuestTester {

    public enum Profession {

    }

    public enum Relationship {
        // directional relationships are a little harder
        //        BROTHER,
        //        MOTHER,
        //        FATHER,
        //        SISTER,
        FRIEND,
        SPOUSE,
        SIBLING,
        COUSIN,
        ACQUAINTANCE
        // MORE
    }

    public enum Location {
        HOUSE,
        ROOM,
        VILLAGE,
        FOREST,
        WELL
    }

    public enum Quest {
        RETRIEVAL, // themes: cloths for the king (thanks Sam!)
        PERFORM_RITUAL, // themes: anger gods, create artwork, interact with machinery
        GET_INFORMATION, // needs better name "go talk to that person", also "spy", generic "interact with that thing/person/animal"
        // MORE!!!! Need a lot more here
        LOVE, // for Sam
        SUBTERFUGE,
        INCITE,

    }

    private static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = RANDOM.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // The idea is random world building:
    //// RNG world groupings
    //// RNG relationships
    //// So maybe: random relationships first, and then "fill in" the rest of that
    ////           world building. E.g. we have a COUSIN relationship, so build
    ////           that family. E.g. we have a BOSS/COMMANDER relathionship so build
    ////           that military concept.
    // Organization
    // Family
    // Govt
    //
    private static final class RandoPeople implements Comparable<RandoPeople> {

        private final UUID uuid;

        private RandoPeople(UUID uuid) {
            this.uuid = Objects.requireNonNull(uuid);
        }

        public static RandoPeople create(UUID uuid) {
            return new RandoPeople(uuid);
        }

        @Override
        public int compareTo(RandoPeople o) {
            if (o == null) {
                return 1;
            }
            return uuid.compareTo(o.getUuid());
        }

        @Override
        public boolean equals(Object rp) {
            if (!(rp instanceof RandoPeople)) {
                return false;
            }

            final RandoPeople r = (RandoPeople) rp;

            return this.uuid.equals(r.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.uuid);
        }

        public UUID getUuid() {
            return uuid;
        }

        @Override
        public String toString() {
            return uuid.toString();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Graph<RandoPeople> randomGraph = GraphUtil.randomGraph(() -> {
            return RandoPeople.create(UUID.randomUUID());
        });

        Map<RandoPeople, Map<RandoPeople, Relationship>> relationships = new HashMap<>();

        randomGraph.getEdges().forEach(edge -> {
            System.out.println(edge);
            System.out.println(edge.getFromVertex().getValue() + " -> " + edge.getToVertex().getValue());
            System.out.println("+-----------------------------------------------------------------------------------------------------------+");

            RandoPeople v1 = edge.getFromVertex().getValue();
            RandoPeople v2 = edge.getToVertex().getValue();

            Map<RandoPeople, Relationship> relations = relationships.computeIfAbsent(v1, (v) -> new HashMap<>());

            // TODO: true relationship from other info (splice in family graph).
            relations.put(v2, randomEnum(Relationship.class));

        });

        System.out.println(relationships);

        // TODO: stack data layers - relationships, locations, quests
        // TODO: overlap random quests on top of the people graph
        // how?
    }
}
