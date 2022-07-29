package com.hawolt;

import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.impl.ItemModule;
import com.hawolt.impl.PopupModule;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.hawolt.WMIC.retrieve;

/**
 * Created: 20/07/2022 10:10
 * Author: Twitter @hawolt
 **/

public class JSessionTracker extends PopupModule implements Runnable {

    private final ScheduledFuture<?> lcu;

    public JSessionTracker(String title) {
        super(title);
        this.lcu = Application.service.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
        this.setEnabled(true);
    }

    @Override
    public boolean isBackground() {
        return false;
    }

    @Override
    public void onUpdate() {
        if (client == null || profile == null) return;
        String message = String.join(System.lineSeparator(), profile.getDisplayName(), profile.getPUUID());
        Application.notification("Account Selector", message, TrayIcon.MessageType.INFO);
    }

    @Override
    public void run() {
        try {
            List<LeagueClient> list = retrieve();
            List<String> names = new ArrayList<>();
            for (LeagueClient client : list) {
                try {
                    String endpoint = String.format("https://127.0.0.1:%s/lol-summoner/v1/current-summoner/", client.getPort());
                    Request request = new Call(client, endpoint, Method.GET, false);
                    Response response = request.execute();
                    JSONObject object = new JSONObject(response.getBodyAsString());
                    if (!object.has("displayName")) continue;
                    String name = object.getString("displayName");
                    names.add(name);
                    boolean match = Application.list.stream().anyMatch(item -> item.getLabel().equals(name));
                    if (match) continue;
                    MenuItem item = new MenuItem(name);
                    item.addActionListener(listener -> {
                        SummonerProfile profile = new SummonerProfile(object);
                        Application.ping(client, profile);
                    });
                    Application.list.add(item);
                    this.add(item);
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                }
            }
            for (int i = Application.list.size() - 1; i >= 0; i--) {
                MenuItem item = Application.list.get(i);
                String target = item.getLabel();
                boolean match = false;
                for (String name : names) {
                    if (name.equals(target)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    this.remove(item);
                    Application.list.remove(item);
                }
            }
        } catch (IOException e) {
            Logger.error(e);
            Application.icon.displayMessage("Exception", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    @Override
    public void onExit() {
        if (lcu != null && !lcu.isCancelled()) lcu.cancel(true);
    }

}
