package com.hawolt;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 16/06/2022 07:35
 * Author: Twitter @hawolt
 **/

public class Configuration {

    private final static Map<String, String> CONFIG_MAP = new HashMap<>();

    static {
        CONFIG_MAP.put("trustStore", "certificate.p12");
        CONFIG_MAP.put("trustStorePassword", "abc123");
    }

    public static void put(String key, String value) {
        CONFIG_MAP.put(key, value);
    }

    public static String get(String key) {
        String value = CONFIG_MAP.getOrDefault(key, null);
        if (value == null) {
            System.err.format("Unable to access config %s, exiting\n", key);
            System.exit(1);
        }
        return value;
    }
}

