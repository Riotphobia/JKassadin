package com.hawolt;

import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 20/07/2022 14:18
 * Author: Twitter @hawolt
 **/

public class JUnlock extends Module {
    private final ScheduledFuture<?> unlock;

    private RegionLocale locale;
    private Session session;

    public JUnlock(String name) {
        super(name);
        MenuItem action = new MenuItem("Refund last transaction");
        action.addActionListener(listener -> {
            if (this.client == null) return;
            if (this.locale == null || this.session == null) {
                Application.notification("JUnlock", "Missing information", TrayIcon.MessageType.ERROR);
            } else {
                Application.service.execute(() -> {
                    String endpoint = String.format("https://%s.store.leagueoflegends.com/storefront/v3/history/purchase", this.locale.getWebRegion());
                    try {
                        Request request = new Request(endpoint, Method.GET, false);
                        request.addHeader("Authorization", "Bearer " + session.getIdToken());
                        Response response = request.execute();
                        JSONObject history = new JSONObject(response.getBodyAsString());
                        JSONArray transactions = history.getJSONArray("transactions");
                        if (transactions.length() == 0) {
                            Application.notification("JUnlock", "No transaction available", TrayIcon.MessageType.ERROR);
                        } else {
                            JSONObject transaction = transactions.getJSONObject(0);
                            String transactionId = transaction.getString("transactionId");
                            String inventoryType = transaction.getString("inventoryType");
                            JSONObject payload = new JSONObject();
                            payload.put("accountId", profile.getAccountId());
                            payload.put("transactionId", transactionId);
                            payload.put("inventoryType", inventoryType);
                            payload.put("language", locale.getWebLanguage());
                            refund(payload);
                        }
                    } catch (IOException e) {
                        Logger.error(e);
                    }
                });
            }
        });
        this.add(action);
        this.unlock = Application.service.scheduleAtFixedRate(() -> action.setEnabled(!(this.client == null)), 0, 1, TimeUnit.SECONDS);
    }

    private void refund(JSONObject payload) throws IOException {
        String endpoint = String.format("https://%s.store.leagueoflegends.com/storefront/v3/refund", this.locale.getWebRegion());
        Request request = new Request(endpoint, Method.POST, true);
        request.addHeader("Authorization", "Bearer " + session.getIdToken());
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Content-Length", String.valueOf(payload.length()));
        request.write(payload.toString());
        Response response = request.execute();
        if (response.getCode() == 200) {
            Application.notification("JUnlock", "Refunded last transaction", TrayIcon.MessageType.INFO);
        } else {
            Application.notification("JUnlock", "Failed to refund last transaction", TrayIcon.MessageType.ERROR);
        }
    }

    @Override
    boolean isBackground() {
        return false;
    }

    @Override
    void onUpdate() {
        this.setEnabled(client != null);
        this.session = null;
        this.locale = null;
        if (client == null) return;
        Application.service.execute(() -> {
            try {
                String endpoint = String.format("https://127.0.0.1:%s/riotclient/get_region_locale", client.getPort());
                Request request = new Call(client, endpoint, Method.GET, false);
                Response response = request.execute();
                this.locale = new RegionLocale(new JSONObject(response.getBodyAsString()));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
        Application.service.execute(() -> {
            try {
                String endpoint = String.format("https://127.0.0.1:%s/lol-login/v1/session", client.getPort());
                Request request = new Call(client, endpoint, Method.GET, false);
                Response response = request.execute();
                this.session = new Session(new JSONObject(response.getBodyAsString()));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
    }

    @Override
    void onExit() {
        if (unlock != null && !unlock.isCancelled()) unlock.cancel(true);
    }
}
