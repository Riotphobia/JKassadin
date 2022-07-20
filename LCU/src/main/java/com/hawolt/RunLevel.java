package com.hawolt;

import com.hawolt.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created: 19/07/2022 19:11
 * Author: Twitter @hawolt
 **/

public enum RunLevel {
    JAR, FILE, UNKNOWN;

    final static RunLevel _LEVEL;

    static {
        URL url = RunLevel.class.getResource("RunLevel.class");
        if (url == null) {
            _LEVEL = RunLevel.UNKNOWN;
        } else {
            String plain = url.toString();
            _LEVEL = plain.startsWith("file") ? FILE : plain.startsWith("jar") ? JAR : UNKNOWN;
        }
    }

    public static InputStream get(String file) {
        switch (_LEVEL) {
            case JAR:
                return RunLevel.class.getResourceAsStream("/" + file);
            case FILE:
                Path path = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve(file);
                try {
                    return new FileInputStream(path.toFile());
                } catch (FileNotFoundException e) {
                    Logger.error(e);
                }
                break;
        }
        return new ByteArrayInputStream(new byte[0]);
    }
}
