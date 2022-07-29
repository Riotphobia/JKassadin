package com.hawolt.data;

import org.json.JSONObject;

/**
 * Created: 27/07/2022 19:16
 * Author: Twitter @hawolt
 **/

public class Skin implements Comparable<Skin> {
    private final long id, cost, sale;
    private final String parent, name;

    public Skin(String parent, JSONObject o) {
        this.parent = parent;
        Object cost = o.get("cost");
        if (!(cost instanceof String)) this.cost = Long.parseLong(cost.toString());
        else this.cost = -1L;
        this.name = o.getString("name");
        this.sale = o.getLong("sale");
        this.id = o.getLong("id");
    }

    public long getId() {
        return id;
    }

    public long getCost() {
        return cost;
    }

    public long getSale() {
        return sale;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.join(" ", name, parent);
    }

    @Override
    public int compareTo(Skin o) {
        return name.compareTo(o.getName());
    }
}
