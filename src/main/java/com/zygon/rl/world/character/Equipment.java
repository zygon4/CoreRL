package com.zygon.rl.world.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.zygon.rl.data.items.ArmorData;

/**
 *
 * @author zygon
 */
public class Equipment {

    private final Map<Slot, Integer> slotCounts;
    private final Map<Slot, Collection<Armor>> slots;
    private final Map<Armor, Collection<Slot>> armor;

    // max count is the count of Slot.HAND
    private final List<Weapon> weapons;

    private Equipment(Map<Slot, Integer> slotCounts,
            Map<Slot, Collection<Armor>> slots,
            Map<Armor, Collection<Slot>> armor,
            List<Weapon> weapons) {
        this.slotCounts = Collections.unmodifiableMap(slotCounts);
        this.slots = Collections.unmodifiableMap(slots);
        this.armor = Collections.unmodifiableMap(armor);
        this.weapons = Collections.unmodifiableList(weapons);
    }

    public static Equipment create(Map<Slot, Integer> slotCounts) {
        return new Equipment(slotCounts, Map.of(), Map.of(), List.of());
    }

    public boolean canWear(Armor armor) {

        Map<Slot, Long> requiredSlotCounts = getRequiredSlotCounts(armor);

        boolean canWear = true;
        for (var slotCount : requiredSlotCounts.entrySet()) {
            Slot slot = slotCount.getKey();
            // This is what we need..
            int requiredCount = slotCount.getValue().intValue();

            // this is what is equipped
            Collection<Armor> equipped = slots.get(slot);
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

    public Map<Slot, Collection<Armor>> getEquipmentBySlot() {
        return slots;
    }

    public Collection<Armor> listEquipped() {
        return this.armor.keySet();
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public Equipment remove(Armor armor) {
        Map<Slot, Long> requiredSlotCounts = getRequiredSlotCounts(armor);
        Map<Slot, Collection<Armor>> newSlots = new HashMap<>();

        for (Slot slot : requiredSlotCounts.keySet()) {
            Long count = requiredSlotCounts.get(slot);

            Collection<Armor> wornArmor = slots.get(slot);
            List<Armor> newWornArmor = new ArrayList<>();

            int removeCount = 0;
            for (Armor worn : wornArmor) {
                if (worn.getId().equals(armor.getId())
                        && removeCount < count.longValue()) {
                    removeCount++;
                } else {
                    newWornArmor.add(worn);
                }
            }

            if (!newWornArmor.isEmpty()) {
                newSlots.put(slot, newWornArmor);
            }
        }

        Map<Armor, Collection<Slot>> newArmor = new HashMap<>();
        for (Armor worn : this.armor.keySet()) {
            if (!worn.getId().equals(armor.getId())) {
                newArmor.put(worn, this.armor.get(worn));
            }
        }

        return new Equipment(slotCounts, newSlots, newArmor, weapons);
    }

    public Equipment wear(Armor armor) {

        Map<Slot, Long> requiredSlotCounts = getRequiredSlotCounts(armor);

        if (canWear(armor)) {
            Map<Slot, Collection<Armor>> newSlots = new HashMap<>(slots);

            for (var slot : requiredSlotCounts.entrySet()) {
                Collection<Armor> equipped = newSlots.computeIfAbsent(
                        slot.getKey(), s -> new ArrayList<>());
                Collection<Armor> newList = new ArrayList<>(equipped);

                for (int i = 0; i < slot.getValue(); i++) {
                    newList.add(armor);
                }

                newSlots.put(slot.getKey(), newList);
            }

            Map<Armor, Collection<Slot>> newArmor = new HashMap<>(this.armor);
            Collection<Slot> equipped = newArmor.computeIfAbsent(armor, s -> new ArrayList<>());
            Collection<Slot> newList = new ArrayList<>(equipped);
            newArmor.put(armor, newList);

            for (var reqSlot : requiredSlotCounts.entrySet()) {
                Long count = requiredSlotCounts.get(reqSlot.getKey());
                for (int i = 0; i < count; i++) {
                    newList.add(reqSlot.getKey());
                }
            }

            return new Equipment(slotCounts, newSlots, newArmor, weapons);
        }

        // throw?
        return null;
    }

    public Equipment wield(Weapon weapon) {

        Integer maxCount = slotCounts.get(Slot.HAND);

        if (weapons.size() < maxCount) {
            List<Weapon> newWeapons = new ArrayList<>(weapons);
            newWeapons.add(weapon);
            return new Equipment(slotCounts, slots, armor, newWeapons);
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
