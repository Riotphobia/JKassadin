package com.hawolt;

import com.hawolt.io.Core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 19/07/2022 18:45
 * Author: Twitter @hawolt
 **/

public class WMIC {

    private final static Pattern pattern = Pattern.compile("\"--remoting-auth-token=(.*?)\"(.*)\"--app-port=(.*?)\"");

    private static String wmic() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("WMIC", "path", "win32_process", "get", "Caption,Processid,Commandline");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (InputStream stream = process.getInputStream()) {
            return Core.read(stream).toString();
        }
    }

    public static List<LeagueClient> retrieve() throws IOException {
        List<LeagueClient> list = new ArrayList<>();
        for (String line : wmic().split(System.lineSeparator())) {
            if (!line.startsWith("LeagueClientUx.exe")) continue;
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) list.add(new LeagueClient(matcher.group(1), matcher.group(3)));
        }
        return list;
    }
}
