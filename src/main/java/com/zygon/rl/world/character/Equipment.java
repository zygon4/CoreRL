package com.zygon.rl.world.character;

import com.zygon.rl.data.items.ArmorData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class Equipment {

    private final Map<Slot, Integer> slotCounts;
    private final Map<Slot, List<Armor>> slots;
    // max count is the count of Slot.HAND
    private final List<Weapon> weapons;

    private Equipment(Map<Slot, Integer> slotCounts, Map<Slot, List<Armor>> slots, List<Weapon> weapons) {
        this.slotCounts = Collections.unmodifiableMap(slotCounts);
        this.slots = Collections.unmodifiableMap(slots);
        this.weapons = Collections.unmodifiableList(weapons);
    }

    public static Equipment create(Map<Slot, Integer> slotCounts) {
        return new Equipment(slotCounts, Map.of(), List.of());
    }

    public boolean canWear(Armor armor) {

        Map<Slot, Long> requiredSlotCounts = getRequiredSlotCounts(armor);

        boolean canWear = true;
        for (var slotCount : requiredSlotCounts.entrySet()) {
            Slot slot = slotCount.getKey();
            // This is what we need..
            int requiredCount = slotCount.getValue().intValue();

            // this is what is equipped
            List<Armor> equipped = slots.get(slot);
            int equippedCount = equipped != null ? equipped.size() : 0;

            // this is the max
            Integer maxSlots = slotCounts.get(slot);
            int slotMaxCount = maxSlots != null ? maxSlots.intValue() : 0;

            if (requiredCount + equippedCount > slotMaxCount) {
                canWear = false;
                break;
            }
        }

        return canWear;
    }

    public Map<Slot, List<Armor>> getEquipmentBySlot() {
        return slots;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public Equipment wear(Armor armor) {

        Map<Slot, Long> requiredSlotCounts = getRequiredSlotCounts(armor);

        Equipment newEq = this;
        if (canWear(armor)) {
            for (var slot : requiredSlotCounts.entrySet()) {
                Map<Slot, List<Armor>> newSlots = new HashMap<>(slots);
                List<Armor> equipped = newSlots.get(slot.getKey());
                List<Armor> newList = equipped == null
                        ? new ArrayList<>() : new ArrayList<>(equipped);

                for (int i = 0; i < slot.getValue(); i++) {
                    newList.add(armor);
                }

                newSlots.put(slot.getKey(), newList);

                return new Equipment(slotCounts, newSlots, weapons);
            }

            return newEq;
        }

        // Has to be all or nothing, so do some pre-checking first
//        List<Armor> equipped = slots.get(location);
//        Integer maxCount = slotCounts.get(location);
//
//        if (equipped == null) {
//            equipped = new ArrayList<>();
//        }
//
//        if (equipped.size() + coveredSlots.size() < = maxCount) {
//
//            Map<Slot, List<Armor>> newSlots = new HashMap<>(slots);
//
//            List<Armor> newList = new ArrayList<>(equipped);
//            newList.add(armor);
//
//            newSlots.put(location, newList);
//            return new Equipment(slotCounts, newSlots, weapons);
//        }
        // throw?
        return null;
    }

    public Equipment wield(Weapon weapon) {

        Integer maxCount = slotCounts.get(Slot.HAND);

        if (weapons.size() < maxCount) {
            List<Weapon> newWeapons = new ArrayList<>(weapons);
            newWeapons.add(weapon);
            return new Equipment(slotCounts, slots, newWeapons);
        }

        return null;
    }

    private Map<Slot, Long> getRequiredSlotCounts(Armor armor) {
        ArmorData template = armor.getTemplate();

        return template.getSlots().stream()
                .map(Slot::valueOf)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
