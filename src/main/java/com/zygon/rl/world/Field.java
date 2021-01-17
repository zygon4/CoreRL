package com.zygon.rl.world;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.field.FieldData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Runtime characteristics of a field. This is mostly runtime because fields can
 * be used in many ways. A lightning bolt can hit a target and "crackle". Or a
 * charm spell can "poof" around the room.
 *
 * This is still pretty alpha-level. It works a little bit, but could use some
 * more definition.
 *
 * ..And some units
 *
 * @author zygon
 */
public class Field implements Identifable {

    public static enum PropagationDirection {
        EMIT,
        TARGET
    }

    public static enum PropagationStyle {
        RANDOM_WALK,
        STRAIGHT
    }

    public static enum PropagationPotency {
        VERY_WEAK,
        WEAK,
        STRONG,
        VERY_STRONG
        // What others? str dictates range so "full" is too much, full could 90%?
        // maybe a different scale like: weak, medium, strong?
    }

    private final FieldData template;
    private final PropagationDirection direction;
    private final PropagationStyle style;
    private final PropagationPotency potency;
    private final Location origin;
    private final int strength;

    public Field(FieldData template, PropagationDirection direction, PropagationStyle style,
            PropagationPotency potency, Location origin, int strength) {
        this.template = template;
        this.direction = direction;
        this.style = style;
        this.potency = potency;
        this.origin = origin;
        this.strength = strength;
    }

    public Field(FieldData template, PropagationDirection direction, PropagationStyle style,
            PropagationPotency potency, Location origin) {
        this(template, direction, style, potency, origin, 100);
    }

    @Override
    public String getId() {
        return template.getId();
    }

    public FieldData getTemplate() {
        return template;
    }

    public int getStrength() {
        return strength;
    }

    // Returns a map of new fields and where they are INCLUDING the current
    // field as it weakens.
    public Map<Location, Field> propagate(Location currentLocation) {

        Map<Location, Field> fieldsByLocation = new HashMap<>();

        if (strength > 1) {

            double strOffset = 1.0;
            switch (potency) {
                case VERY_WEAK -> {
                    strOffset = 0.15;
                }
                case WEAK -> {
                    strOffset = 0.35;
                }
                case STRONG -> {
                    strOffset = 0.65;
                }
                case VERY_STRONG -> {
                    strOffset = 0.85;
                }
            }
            // Possibly sketchy casting
            int str = (int) (strength * strOffset);

            // Self field propagation
            fieldsByLocation.put(currentLocation, new Field(template, direction,
                    style, potency, origin, str));

            // Nearby field propagation
            switch (direction) {
                case EMIT -> {
                    Set<Location> targets = getTargets(origin, currentLocation);
                    for (Location target : targets) {
                        fieldsByLocation.put(target, new Field(template, direction,
                                style, potency, origin, str));
                    }
                }
                case TARGET -> {
                    if (style == PropagationStyle.STRAIGHT) {
                        Location target = getTarget(origin, currentLocation);
                        fieldsByLocation.put(target, new Field(template, direction,
                                style, potency, origin, str));
                    }
                }
            }
        }

        return fieldsByLocation;
    }

    private static Location getTarget(Location origin, Location current) {
        int x = origin.getX() < current.getX()
                ? current.getX() + 1 : (origin.getX() > current.getX()) ? current.getX() - 1 : current.getX();
        int y = origin.getY() < current.getY()
                ? current.getY() + 1 : (origin.getY() > current.getY()) ? current.getY() - 1 : current.getY();

        return Location.create(x, y);
    }

    private static Set<Location> getTargets(Location origin, Location current) {

        Set<Location> targets = new HashSet<>();

        if (origin.getX() < current.getX()) {
            if (origin.getY() < current.getY()) {
                // target NE
                targets.add(Location.create(current.getX() + 1, current.getY()));
                targets.add(Location.create(current.getX() + 1, current.getY() + 1));
                targets.add(Location.create(current.getX(), current.getY() + 1));
            } else if (origin.getY() > current.getY()) {
                // target SE
                targets.add(Location.create(current.getX() + 1, current.getY()));
                targets.add(Location.create(current.getX() + 1, current.getY() - 1));
                targets.add(Location.create(current.getX(), current.getY() - 1));
            } else {
                // target E
                targets.add(Location.create(current.getX() + 1, current.getY() + 1));
                targets.add(Location.create(current.getX() + 1, current.getY()));
                targets.add(Location.create(current.getX() + 1, current.getY() - 1));
            }
        } else if (origin.getX() > current.getX()) {
            if (origin.getY() > current.getY()) {
                // target SW
                targets.add(Location.create(current.getX() - 1, current.getY()));
                targets.add(Location.create(current.getX() - 1, current.getY() - 1));
                targets.add(Location.create(current.getX(), current.getY() - 1));
            } else if (origin.getY() < current.getY()) {
                // target NW
                targets.add(Location.create(current.getX() - 1, current.getY()));
                targets.add(Location.create(current.getX() - 1, current.getY() + 1));
                targets.add(Location.create(current.getX(), current.getY() + 1));
            } else {
                // target W
                targets.add(Location.create(current.getX() - 1, current.getY() - 1));
                targets.add(Location.create(current.getX() - 1, current.getY() + 1));
                targets.add(Location.create(current.getX() - 1, current.getY()));
            }
        } else {
            if (origin.getY() > current.getY()) {
                // target S
                targets.add(Location.create(current.getX() + 1, current.getY() - 1));
                targets.add(Location.create(current.getX() - 1, current.getY() - 1));
                targets.add(Location.create(current.getX(), current.getY() - 1));
            } else if (origin.getY() < current.getY()) {
                // target N
                targets.add(Location.create(current.getX() - 1, current.getY() + 1));
                targets.add(Location.create(current.getX() + 1, current.getY() + 1));
                targets.add(Location.create(current.getX(), current.getY() + 1));
            }
        }

        return targets;
    }
}
