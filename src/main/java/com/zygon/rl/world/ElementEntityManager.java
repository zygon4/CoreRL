package com.zygon.rl.world;

import com.zygon.rl.data.Element;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zygon
 * @param <T>
 */
public class ElementEntityManager<T extends Element> extends GenericEntityManager<T> {

    public T getElement(Location location) {
        List<T> elements = get(location);
        return elements != null && !elements.isEmpty() ? elements.get(0) : null;
    }

    public Map<Location, List<T>> getAllByType(Location location, String type, int radius) {
        return get(location,
                (el) -> type == null || el.getType().equals(type),
                radius);
    }

    public List<T> getByType(Location location, String type) {
        return get(location, (el) -> type == null || el.getType().equals(type));
    }
}
