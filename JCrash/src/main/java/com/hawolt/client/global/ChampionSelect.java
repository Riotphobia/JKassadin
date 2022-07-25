package com.hawolt.client.global;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 16/06/2022 09:06
 * Author: Twitter @hawolt
 **/

public class ChampionSelect {
    private static final List<String> team = new ArrayList<>();

    private static String select, mucId;

    public static void add(String name) {
        if (isFull()) return;
        team.add(name);
    }

    public static void reset() {
        team.clear();
    }

    public static int getSize() {
        return team.size();
    }

    public static boolean isFull() {
        return team.size() >= 5;
    }

    public static String getMucId() {
        return mucId;
    }

    public static void setMucId(String mucId) {
        ChampionSelect.mucId = mucId;
    }

    public static String getSelect() {
        return select;
    }

    public static void setSelect(String select) {
        ChampionSelect.select = select;
    }
}
