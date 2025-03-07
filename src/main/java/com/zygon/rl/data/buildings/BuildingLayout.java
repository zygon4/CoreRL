package com.zygon.rl.data.buildings;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class BuildingLayout {

    // rows are: 0 is the top
    private List<String> rows;
    private Map<String, String> ids;

    public BuildingLayout() {
    }

    public int getHeight() {
        return rows.size();
    }

    public int getHeightFromCenter() {
        return (getHeight() - 1) / 2;
    }

    public int getWidth() {
        return rows.get(0).length();
    }

    public int getWidthFromCenter() {
        return (getWidth() - 1) / 2;
    }

    public String getId(int x, int y) {
        String row = rows.get(x);
        String col = row.substring(y, y + 1);

        return col.isBlank() ? null : ids.get(col);
    }

    public void setIds(Map<String, String> ids) {
        this.ids = ids;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }
}
