package com.zygon.rl.data.buildings;

/**
 *
 */
public class Layout {

    private BuildingLayout structure;
    private BuildingLayout items;

    public Layout() {
    }

    public BuildingLayout getItems() {
        return items;
    }

    public BuildingLayout getStructure() {
        return structure;
    }

    public void setItems(BuildingLayout items) {
        this.items = items;
    }

    public void setStructure(BuildingLayout structure) {
        this.structure = structure;
    }
}
