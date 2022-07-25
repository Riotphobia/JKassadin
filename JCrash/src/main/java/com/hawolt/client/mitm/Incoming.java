package com.hawolt.client.mitm;

import com.hawolt.Configuration;
import com.hawolt.JCrash;
import com.hawolt.client.StreamCallback;
import com.hawolt.client.global.ChampionSelect;
import com.hawolt.client.xmpp.InputCallback;
import com.hawolt.client.xmpp.XMLBuffer;
import com.hawolt.logger.Logger;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 16/06/2022 07:55
 * Author: Twitter @hawolt
 **/

public class Incoming extends Connection implements InputCallback {

    private final String[] PRESENCE = new String[]{"queueId", "championId", "rankedLosses", "profileIcon", "mapSkinId", "masteryScore", "companionId", "gameId", "rankedWins", "damageSkinId", "level", "rankedSplitRewardLevel", "timeStamp", "mapId"};
    private final String[] REGALIA = new String[]{"bannerType", "crestType", "selectedPrestigeCrest"};

    private final Pattern TIMESTAMP_PATTERN = Pattern.compile("<s\\.t>(.*?)</s\\.t>");
    private final Pattern PRESENCE_PATTERN = Pattern.compile("<p>(.*?)</p>");

    private final XMLBuffer buffer = new XMLBuffer(this);

    private OutputStream stream;

    public Incoming(Socket in, Socket out, StreamCallback callback) {
        super(in, out, callback);
    }

    private String check(String value) {
        if (value.trim().length() == 0) return value;
        try {
            Long.parseLong(value);
            return value;
        } catch (NumberFormatException e) {
            return String.valueOf(0L);
        }
    }

    @Override
    public void onInput(String line) {
        Logger.debug("[IN] {}", line);
        boolean join = line.contains("join_muc");
        boolean leave = line.contains("leave_muc");
        StringBuilder builder = new StringBuilder(line);
        if (join || leave) {
            try {
                Matcher timestampMatcher = TIMESTAMP_PATTERN.matcher(line);
                while (timestampMatcher.find()) {
                    String result = timestampMatcher.group(1);
                    int index = builder.indexOf(result);
                    if (index == -1) continue;
                    builder.replace(index, index + result.length(), String.valueOf(check(result)));
                }
                Matcher presenceMatcher = PRESENCE_PATTERN.matcher(line);
                while (presenceMatcher.find()) {
                    String unformatted = presenceMatcher.group(1);
                    JSONObject presence = new JSONObject(unformatted.replaceAll("&quot;", "\""));
                    for (String crashable : PRESENCE) {
                        if (!presence.has(crashable)) continue;
                        presence.put(crashable, check(String.valueOf(presence.get(crashable))));
                    }
                    if (presence.has("regalia")) {
                        JSONObject regalia = new JSONObject(presence.getString("regalia"));
                        for (String crashable : REGALIA) {
                            if (!regalia.has(crashable)) continue;
                            regalia.put(crashable, check(String.valueOf(regalia.get(crashable))));
                        }
                        presence.put("regalia", regalia);
                    }
                    String safe = presence.toString().replaceAll("\"", "&quot;");
                    int index = builder.indexOf(unformatted);
                    if (index == -1) continue;
                    builder.replace(index, index + unformatted.length(), safe);
                }
                JSONObject object = XML.toJSONObject(line);
                JSONObject presence = object.getJSONObject("presence");
                JSONObject x = presence.getJSONObject("x");
                JSONObject item = x.getJSONObject("item");
                JSONObject lol = item.getJSONObject("lol");
                if (JCrash.undercover) {
                    if ((leave && !line.contains("post-game")) || (join && ChampionSelect.isFull())) {
                        return;
                    }
                }
                if (join) ChampionSelect.add(lol.get("name").toString());
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        try {
            Logger.debug("[OUT-MOD] {}", builder.toString());
            stream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    @Override
    public void run() {
        try (InputStream input = in.getInputStream()) {
            stream = out.getOutputStream();
            int code;
            while ((code = input.read()) != -1) {
                buffer.append(code);
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        if (callback == null) return;
        callback.onClose();
    }
}
