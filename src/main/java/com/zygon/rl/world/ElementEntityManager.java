package com.zygon.rl.world;

import com.zygon.rl.data.WorldElement;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zygon
 * @param <T>
 */
public class ElementEntityManager<T extends WorldElement> extends GenericEntityManager<T> {

    public T getElement(Location location) {
        List<T> elements = get(location);
        return elements != null && !elements.isEmpty() ? elements.get(0) : null;
    }

    public Map<Location, List<T>> getAllByType(Location location, String type, int radius, boolean includeCenter) {
        return get(location,
                (el) -> type == null || el.getType().equals(type),
                radius, includeCenter);
    }

    public List<T> getByType(Location location, String type) {
        return get(location, (el) -> type == null || el.getType().equals(type));
    }
}
