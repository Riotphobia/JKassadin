package com.hawolt.lcu;

import com.hawolt.Application;
import com.hawolt.Call;
import com.hawolt.LeagueClient;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created: 29/07/2022 00:32
 * Author: Twitter @hawolt
 **/

public class Telemetry {

    public static void startTelemetryFlow(LeagueClient client) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/telemetry/v1/events/rcp-fe-lol-paw-event", client.getPort());
            Request request = new Call(client, endpoint, Method.POST, true);
            JSONObject object = new JSONObject();
            object.put("id", "lol_paw_load_item_success");
            object.put("spec", "high");
            object.put("isLowSpecModeOn", "false");
            request.write(object.toString());
            Logger.debug("/rcp-fe-lol-paw-event lol_paw_load_item_success {}", request.execute().getCode());
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("Telemetry", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    public static void startTelemetryPurchase(LeagueClient client, JSONObject... payloads) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/telemetry/v1/events/rcp-fe-lol-paw-event", client.getPort());
            Request request = new Call(client, endpoint, Method.POST, true);
            JSONObject object = new JSONObject();
            object.put("id", "lol_paw_start_purchase");
            JSONArray purchaseDataItems = new JSONArray();
            for (JSONObject payload : payloads) purchaseDataItems.put(payload);
            object.put("purchaseDataItems", purchaseDataItems.toString());
            object.put("spec", "high");
            object.put("isLowSpecModeOn", "false");
            request.write(object.toString());
            Logger.debug("/rcp-fe-lol-paw-event lol_paw_start_purchase {}", request.execute().getCode());
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("Telemetry", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    public static JSONObject buildTelemetryPurchase(JSONObject details) {
        JSONObject object = new JSONObject();
        object.put("id", "lol_paw_purchase_success");
        object.put("purchaseDetails", details);
        object.put("itemIds", build(details.getJSONArray("items"), "itemId", "itemKey"));
        object.put("inventoryTypes", build(details.getJSONArray("items"), "inventoryType", "itemKey"));
        object.put("sources", build(details.getJSONArray("items"), "source"));
        object.put("transactionIds", build(details.getJSONArray("transactions"), "transactionId"));
        object.put("spec", "high");
        object.put("isLowSpecModeOn", "false");
        return object;
    }

    public static String build(JSONArray source, String key, String... pathing) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            JSONObject target = source.getJSONObject(i);
            for (String path : pathing) {
                target = target.getJSONObject(path);
            }
            if (builder.length() != 0) builder.append(",");
            builder.append(target.get(key));
        }
        return builder.toString();
    }

    public static void finishTelemetryPurchase(LeagueClient client, JSONObject details) {
        try {
            String endpoint = String.format("https://127.0.0.1:%s/telemetry/v1/events/rcp-fe-lol-paw-event", client.getPort());
            Request request = new Call(client, endpoint, Method.POST, true);
            JSONObject object = buildTelemetryPurchase(details);
            object.put("purchaseDetails", details);
            request.write(object.toString());
            Logger.debug("/rcp-fe-lol-paw-event lol_paw_purchase_success {}", request.execute().getCode());
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("Telemetry", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }
}
