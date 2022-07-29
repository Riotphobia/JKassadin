package com.hawolt.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 27/07/2022 19:16
 * Author: Twitter @hawolt
 **/

public class Champion implements Comparable<Champion>, Iterable<Skin> {

    private final List<Skin> list = new ArrayList<>();

    private final long id, blueEssence, riotPoints, sale;
    private final String name;

    public Champion(String name, JSONObject object) {
        this.name = name;
        this.id = object.getLong("id");
        JSONObject price = object.getJSONObject("price");
        this.blueEssence = price.getLong("blueEssence");
        this.riotPoints = price.getLong("rp");
        this.sale = price.getLong("saleRp");
        JSONArray skins = object.getJSONArray("skins");
        for (int i = 0; i < skins.length(); i++) {
            JSONObject data = skins.getJSONObject(i);
            if (data.getBoolean("isBase")) continue;
            Skin skin = new Skin(name, data);
            if (skin.getCost() == -1) continue;
            list.add(skin);
        }
    }

    public List<Skin> getList() {
        return list;
    }

    public long getId() {
        return id;
    }

    public long getBlueEssence() {
        return blueEssence;
    }

    public long getRiotPoints() {
        return riotPoints;
    }

    public long getSale() {
        return sale;
    }

    public String getName() {
        return name;
    }

    @Override
    public Iterator<Skin> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Champion o) {
        return name.compareTo(o.getName());
    }
}
