package com.hawolt.data;

import java.util.HashMap;

/**
 * Created: 27/07/2022 18:31
 * Author: Twitter @hawolt
 **/

public class StoreLocale {
    private final static HashMap<String, String> mapping = new HashMap<String, String>() {{
        put("EUNE", "eun");
        put("OCE", "oc1");
    }};

    public static String retrieve(String webRegion) {
        return mapping.getOrDefault(webRegion, webRegion);
    }
}
