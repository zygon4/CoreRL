/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.quest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 *
 * @author djc
 */
final class GraphUtil {

    private GraphUtil() {
        // private ctr
    }

    // could have more args
    public static <T extends Comparable<T>> Graph<T> randomGraph(
            Supplier<T> vertexGenerator) {

        List<Graph.Vertex<T>> verticies = new ArrayList<>();
        Random random = new Random();

        int verticesCount = 3 + random.nextInt(2);

        for (int i = 0; i < verticesCount; i++) {
            verticies.add(new Graph.Vertex<>(vertexGenerator.get()));
        }

        Set<Graph.Edge<T>> edges = new HashSet<>();

        for (int i = 0; i < verticesCount; i++) {
            Graph.Vertex<T> v = verticies.get(i);

            int randomEdgeCount = 1 + random.nextInt(verticesCount - 1);
            if (randomEdgeCount >= verticesCount) {
                throw new IllegalStateException();
            }

            Set<Integer> exceptions = new HashSet<>();
            exceptions.add(i);

            for (int j = 0; j < randomEdgeCount; j++) {
                int vIndex = randomExcept(random, verticies.size(), exceptions);
                exceptions.add(vIndex);

                Graph.Edge<T> e = new Graph.Edge<>(1, v, verticies.get(vIndex));
                if (edges.contains(e)) {
                    throw new RuntimeException("Duplicate edges");
                }
                edges.add(e);
            }
        }

        return new Graph<>(verticies, edges);
    }

    // could be dangerous with the stack..
    private static int randomExcept(Random random, int nextIntBound,
            Set<Integer> except) {
        int rand = random.nextInt(nextIntBound);
        if (!except.contains(rand)) {
            return rand;
        }
        return randomExcept(random, nextIntBound, except);
    }
}
