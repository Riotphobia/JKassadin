package com.hawolt.lcu;

import com.hawolt.Application;
import com.hawolt.Call;
import com.hawolt.LeagueClient;
import com.hawolt.SummonerProfile;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created: 29/07/2022 01:50
 * Author: Twitter @hawolt
 **/

public class ChampionSelect {

    public static void refreshInventory(LeagueClient client) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/lol-champ-select/v1/session/simple-inventory", client.getPort());
            Request request = new Call(client, endpoint, Method.POST, true);
            request.addHeader("X-Riot-Source", "rcp-fe-lol-champ-select");
            request.addHeader("Content-Length", "0");
            request.write("");
            Logger.debug("/lol-champ-select/v1/session/simple-inventory {}", request.execute().getCode());
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    public static void selectSkin(LeagueClient client, long id) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/lol-champ-select/v1/session/my-selection", client.getPort());
            Request request = new Call(client, endpoint, Method.PATCH, true);
            JSONObject object = new JSONObject();
            object.put("selectedSkinId", id);
            request.addHeader("Content-Length", String.valueOf(object.toString().length()));
            request.write(object.toString());
            Logger.debug("/lol-champ-select/v1/session/my-selection {}", request.execute().getCode());
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    public static void selectChampion(LeagueClient client, SummonerProfile profile, long champId) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/lol-champ-select/v1/session", client.getPort());
            Request request = new Call(client, endpoint, Method.GET, false);
            Response response = request.execute();
            JSONObject object = new JSONObject(response.getBodyAsString());
            if (object.has("errorCode")) return;
            JSONObject timer = object.getJSONObject("timer");
            if (!timer.has("phase") || !timer.getString("phase").equals("BAN_PICK")) return;
            Logger.debug("MY SUMMONER ID {}", profile.getSummonerId());
            long myCellId = object.getLong("localPlayerCellId");
            JSONArray actions = object.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++) {
                JSONArray type = actions.getJSONArray(i);
                for (int j = 0; j < type.length(); j++) {
                    JSONObject action = type.getJSONObject(j);
                    if (action.getLong("actorCellId") != myCellId) continue;
                    if (!action.getString("type").equals("pick")) continue;
                    if (!action.getBoolean("isInProgress")) return;
                    if (action.getBoolean("completed")) return;
                    String uri = String.format("https://127.0.0.1:%s/lol-champ-select/v1/session/actions/" + action.getLong("id"), client.getPort());
                    JSONObject body = new JSONObject();
                    body.put("championId", champId);
                    Request patch1 = new Call(client, uri, Method.PATCH, true);
                    patch1.write(body.toString());
                    Logger.debug("/lol-champ-select/v1/session/actions/ {}", patch1.execute().getCode());
                    body.put("completed", true);
                    Request patch2 = new Call(client, uri, Method.PATCH, true);
                    patch2.write(body.toString());
                    Logger.debug("/lol-champ-select/v1/session/actions/ {}", patch2.execute().getCode());
                }
            }
        } catch (Exception e) {
            Logger.error(e);
            Logger.error(e.getMessage());
        }
    }

}
