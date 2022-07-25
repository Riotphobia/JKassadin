package com.hawolt.client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 16/06/2022 07:35
 * Author: Twitter @hawolt
 **/

public class Configuration {

    private final static Map<String, String> CONFIG_MAP = new HashMap<>();
    private final static String DEFAULT_FILE = "startup.configuration";

    private static final File _configuration;

    static {
        _configuration = find(new File(System.getProperty("user.dir")));
        if (_configuration == null) {
            System.err.println("Unable to locate startup.configuration in root directory");
            System.exit(1);
        }
        reload();
    }

    public static void put(String key, String value) {
        CONFIG_MAP.put(key, value);
    }

    public static String get(String key) {
        String value = CONFIG_MAP.getOrDefault(key, null);
        if (value == null) {
            System.err.format("Unable to access config %s, exiting\n", value);
            System.exit(1);
        }
        return value;
    }

    private static File find(File base) {
        File[] files = base.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().equals(DEFAULT_FILE)) {
                return file;
            }
        }
        return null;
    }

    public static void reload() {
        try (FileReader base = new FileReader(_configuration)) {
            try (BufferedReader reader = new BufferedReader(base)) {
                CONFIG_MAP.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] pair = line.split("=", 2);
                    CONFIG_MAP.put(pair[0], pair[1]);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to locate startup.configuration in root directory");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to read startup.configuration");
            System.exit(1);
        }
    }
}

