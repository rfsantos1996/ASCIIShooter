package com.jabyftw.gameclient.maps.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Rafael on 05/01/2015.
 */
public class BlockStorage implements Json.Serializable {

    public int x, y;
    public Material material;

    public BlockStorage() {
    }

    public BlockStorage(int x, int y, Material material) {
        this.x = x;
        this.y = y;
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof BlockStorage && ((BlockStorage) o).x == x && ((BlockStorage) o).y == y;
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", x, Integer.class);
        json.writeValue("y", y, Integer.class);
        json.writeValue("material", material.name(), String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.x = jsonData.getInt("x");
        this.y = jsonData.getInt("y");
        this.material = Material.valueOf(jsonData.getString("material"));
    }
}
