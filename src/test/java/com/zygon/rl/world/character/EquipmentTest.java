package com.zygon.rl.world.character;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.zygon.rl.data.items.ArmorData;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author zygon
 */
public class EquipmentTest {

    @BeforeClass
    public static void load() throws IOException {
        ArmorData.load();
    }

    @Test
    public void testWear1() {
        Equipment eq = Equipment.create(Map.of(Slot.ARM, 2));

        Armor armArmor = new Armor(ArmorData.get("arms_leather_bracers"));
        Equipment wear = eq.wear(armArmor);
        Map<Slot, Collection<Armor>> equipmentBySlot = wear.getEquipmentBySlot();
        Assert.assertEquals(1, equipmentBySlot.size());
        Collection<Armor> worn = equipmentBySlot.get(Slot.ARM);
        Assert.assertEquals(2, worn.size());

        for (Armor a : worn) {
            Assert.assertEquals(armArmor, a);
        }

        Collection<Armor> listEquipped = wear.listEquipped();
        Assert.assertEquals(1, listEquipped.size());

        Equipment removed = wear.remove(armArmor);
        Map<Slot, Collection<Armor>> removedEquipmentBySlot = removed.getEquipmentBySlot();
        Assert.assertEquals(0, removedEquipmentBySlot.size());
        Collection<Armor> removedEquipped = removed.listEquipped();
        Assert.assertEquals(0, removedEquipped.size());
    }
}
