package com.hawolt.client.proxy;

import com.hawolt.Application;
import com.hawolt.Configuration;
import com.hawolt.client.mitm.XMPPManager;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import io.javalin.Javalin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

/**
 * Created: 16/06/2022 07:41
 * Author: Twitter @hawolt
 **/

public class LocalHTTP {

    private final String target;

    public LocalHTTP(String target) {
        this.target = target;
    }

    public void start() {
        Javalin app = Javalin.create().start(7000);
        app.get("*", context -> {
            try {
                String url = String.format("%s%s?%s", target, context.path(), context.queryString());
                Request request = new Request(url);
                request.addHeader("User-Agent", context.header("User-Agent"));
                if (context.header("X-Riot-Entitlements-JWT") != null) {
                    request.addHeader("X-Riot-Entitlements-JWT", context.header("X-Riot-Entitlements-JWT"));
                }
                if (context.header("Authorization") != null) {
                    request.addHeader("Authorization", context.header("Authorization"));
                }
                Response response = request.execute();
                byte[] b = response.getBody();
                String raw = new String(b);
                JSONObject object = new JSONObject(raw);
                if (object.has("chat.host")) {
                    object.put("chat.host", "127.0.0.1");
                }
                if (object.has("chat.port")) {
                    Configuration.put("chat.port", String.valueOf(object.getInt("chat.port")));
                    object.put("chat.port", Integer.parseInt(Configuration.get("chat.port")));
                }
                if (object.has("chat.affinities")) {
                    JSONObject affinities = object.getJSONObject("chat.affinities");
                    if (object.getBoolean("chat.affinity.enabled")) {
                        Request affinity = new Request("https://riot-geo.pas.si.riotgames.com/pas/v1/service/chat");
                        affinity.addHeader("Authorization", context.header("Authorization"));
                        String relevant = affinity.execute().getBodyAsString().split("\\.")[1];
                        JSONObject data = new JSONObject(new String(Base64.getDecoder().decode(relevant)));
                        Configuration.put("host", affinities.getString(data.getString("affinity")));
                        Application.service.execute(XMPPManager::start);
                    }
                    JSONObject patched = new JSONObject();
                    for (String key : affinities.keySet()) {
                        patched.put(key, "127.0.0.1");
                    }
                    object.put("chat.affinities", patched);
                }
                if (object.has("chat.allow_bad_cert.enabled")) {
                    object.put("chat.allow_bad_cert.enabled", true);
                }
                String string = object.toString();
                context.status(response.getCode());
                context.header("Content-Length", String.valueOf(string.length()));
                context.header("Content-Type", "application/json");
                context.result(string);
            } catch (JSONException ex) {
                //ignored
            }
        });
    }
}
