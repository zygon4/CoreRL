package com.zygon.rl.world;

import com.zygon.rl.data.Identifable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Mutable state here..
 *
 * @author zygon
 * @param <T>
 */
public class GenericEntityManager<T extends Identifable> {

    private final Map<Location, List<T>> entitiesByLocation = new HashMap<>();

    public void delete(T id, Location location) {
        List<T> existing = entitiesByLocation.get(location);

        AtomicBoolean removedFirst = new AtomicBoolean(false);
        List<T> newElements = existing == null ? List.of() : existing.stream()
                .filter(ele -> {
                    if (ele.getId().equals(id.getId()) && !removedFirst.get()) {
                        removedFirst.set(true);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        if (newElements.isEmpty()) {
            entitiesByLocation.remove(location);
        } else {
            entitiesByLocation.put(location, newElements);
        }
    }

    public List<T> get(Location location) {
        List<T> entities = new ArrayList<>();

        List<T> byLocation = entitiesByLocation.get(location);
        if (byLocation != null) {
            entities.addAll(byLocation);
        }

        return entities;
    }

    public Map<Location, List<T>> get(Location location, Predicate<T> filter, int radius) {

        Map<Location, List<T>> elementsByLocation = new HashMap<>();

        Set<Location> neighbors = location.getNeighbors(radius);
        if (neighbors != null) {
            neighbors.stream().forEach(loc -> {
                List<T> elements = get(loc);
                if (elements != null && !elements.isEmpty()) {
                    List<T> filtered = elements.stream()
                            .filter(filter)
                            .collect(Collectors.toList());

                    if (!filtered.isEmpty()) {
                        elementsByLocation.put(loc, filtered);
                    }
                }
            });
        }

        return elementsByLocation;
    }

    public List<T> get(Location location, Predicate<T> filter) {
        List<T> elements = get(location);

        return elements != null ? elements.stream()
                .filter(filter)
                .collect(Collectors.toList()) : List.of();
    }

    public void add(T entity, Location location) {

        List<T> entities = entitiesByLocation.get(location);
        if (entities == null) {
            entities = new ArrayList<>();
        }
        entities.add(entity);
        entitiesByLocation.put(location, entities);
    }

    // Will replace the entity at the given location by checking the id value
    public void save(T entity, Location location) {

        List<T> entities = entitiesByLocation.get(location);
        if (entities == null) {
            entities = new ArrayList<>();
            entities.add(entity);
        } else {
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(entity.getId())) {
                    entities.set(i, entity);
                }
            }
        }

        entitiesByLocation.put(location, entities);
    }
}
