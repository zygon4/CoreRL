package com.zygon.rl.world;

import com.zygon.rl.data.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mutable state here..
 *
 * @author zygon
 */
public class EntityManager {

    private final Map<Location, List<Element>> entitiesByLocation = new HashMap<>();
    private final Map<String, Element> elementsById = new HashMap<>();

    public void delete(String id, Location location) {
        List<Element> existing = entitiesByLocation.get(location);

        List<Element> newElements = new ArrayList<>();

        // There HAS to be a better way to remove the first instance of
        // an element???
        boolean removedFirst = false;

        for (int i = 0; i < existing.size(); i++) {

            if (existing.get(i).getId().equals(id)) {
                if (!removedFirst) {
                    removedFirst = true;
                }
            } else {
                newElements.add(existing.get(i));
            }
        }

        if (newElements.isEmpty()) {
            entitiesByLocation.remove(location);
        } else {
            entitiesByLocation.put(location, newElements);
        }

        elementsById.remove(id);
    }

    public Map<Location, List<Element>> get(Location location, String type, int radius) {

        Map<Location, List<Element>> elementsByLocation = new HashMap<>();

        location.getNeighbors(radius).stream().forEach(loc -> {
            List<Element> elements = get(loc);
            if (elements != null && !elements.isEmpty()) {
                List<Element> filtered = elements.stream()
                        .filter(ele -> type == null || ele.getType().equals(type))
                        .collect(Collectors.toList());

                if (filtered != null && !filtered.isEmpty()) {
                    elementsByLocation.put(loc, filtered);
                }
            }
        });

        return elementsByLocation;
    }

    public List<Element> get(Location location, String type) {
        List<Element> elements = get(location);

        return elements != null ? elements.stream()
                .filter(ele -> type == null || ele.getType().equals(type))
                .collect(Collectors.toList()) : List.of();
    }

    public List<Element> get(Location location) {
        return entitiesByLocation.get(location);
    }

    public Element get(String id) {
        return elementsById.get(id);
    }

    public void save(Element entity, Location location) {

        List<Element> entities = entitiesByLocation.get(location);
        if (entities == null) {
            entities = new ArrayList<>();
        }
        entities.add(entity);
        entitiesByLocation.put(location, entities);

        elementsById.put(entity.getId(), entity);
    }
}
