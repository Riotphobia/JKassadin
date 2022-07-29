package com.hawolt;

import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.impl.ItemModule;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 20/07/2022 10:10
 * Author: Twitter @hawolt
 **/

public class JSessionInvalidator extends ItemModule implements Runnable {

    private final ScheduledFuture<?> lcu;

    public JSessionInvalidator(String title) {
        super(title);
        this.lcu = Application.service.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBackground() {
        return true;
    }

    @Override
    public void onUpdate() {
        if (client != null || profile != null) return;
        Application.notification("Account Selector", "Selected Account no longer available", TrayIcon.MessageType.INFO);
    }

    @Override
    public void run() {
        if (client == null) return;
        try {
            String endpoint = String.format("https://127.0.0.1:%s/lol-summoner/v1/current-summoner/", client.getPort());
            Request request = new Call(client, endpoint, Method.GET, false);
            Response response = request.execute();
            JSONObject object = new JSONObject(response.getBodyAsString());
            if (!object.has("displayName")) Application.ping(null, null);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            Application.ping(null, null);
        }
    }

    @Override
    public void onExit() {
        if (lcu != null && !lcu.isCancelled()) lcu.cancel(true);
    }

}
