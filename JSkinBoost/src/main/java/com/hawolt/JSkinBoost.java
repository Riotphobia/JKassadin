package com.hawolt;

import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.impl.PopupModule;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 20/07/2022 09:56
 * Author: Twitter @hawolt
 **/

public class JSkinBoost extends PopupModule {

    private final ScheduledFuture<?> boost;

    private String jwt;

    public JSkinBoost(String name) {
        super(name);
        MenuItem action = new MenuItem("Boost");
        action.addActionListener(listener -> {
            Application.service.execute(() -> {
                if (this.jwt == null) return;
                String endpoint = String.format("https://127.0.0.1:%s/lol-login/v1/session/invoke?destination=lcdsServiceProxy&method=call&args=[\"\",\"teambuilder-draft\",\"activateBattleBoostV1\",\"{\\\"signedWalletJwt\\\":\\\"" + this.jwt + "\\\"}\"]", client.getPort());
                try {
                    Request request = new Call(client, endpoint, Method.POST, false);
                    Response response = request.execute();
                    Application.icon.displayMessage("Skin Boost", response.getCode() == 200 ? "Success" : "Error", response.getCode() == 200 ? TrayIcon.MessageType.INFO : TrayIcon.MessageType.ERROR);
                } catch (IOException e) {
                    Logger.error(e);
                }
            });
        });
        this.add(action);

        MenuItem load = new MenuItem("Load JWT");
        load.addActionListener(listener -> {
            try {
                Path path = Paths.get("jwt.log");
                if (!path.toFile().exists()) {
                    Application.notification("JWT Loader", "jwt.log not present", TrayIcon.MessageType.ERROR);
                } else {
                    List<String> list = Files.readAllLines(path);
                    if (list.size() > 0) {
                        String[] data = list.get(list.size() - 1).split(":");
                        this.jwt = data[2];
                        JSONObject object = new JSONObject(new String(Base64.getDecoder().decode(data[2].split("\\.")[1])));
                        String puuid = object.getString("sub");
                        if (profile.getPUUID().equals(puuid)) {
                            Application.notification("JWT Loader", "Loaded JWT", TrayIcon.MessageType.INFO);
                        } else {
                            Application.notification("JWT Loader", "JWT is not for selected account", TrayIcon.MessageType.ERROR);
                        }
                    } else {
                        Application.notification("JWT Loader", "No JWT stored", TrayIcon.MessageType.ERROR);
                    }
                }
            } catch (IOException e) {
                Logger.error(e);
                Application.notification("JWT Loader", "Unable to load jwt.log", TrayIcon.MessageType.ERROR);
            }
        });
        this.add(load);

        MenuItem store = new MenuItem("Store JWT");
        store.addActionListener(listener -> {
            Application.service.execute(() -> {
                String endpoint = String.format("https://127.0.0.1:%s/lol-inventory/v1/signedWallet/RP", client.getPort());
                try {
                    Request request = new Call(client, endpoint, Method.GET, false);
                    Response response = request.execute();
                    JSONObject object = new JSONObject(response.getBodyAsString());
                    if (!object.has("RP")) return;
                    String jwt = object.getString("RP");
                    try (FileWriter writer = new FileWriter("jwt.log", true)) {
                        writer.write(profile.getDisplayName() + ":" + System.currentTimeMillis() + ":" + jwt + System.lineSeparator());
                    }
                    this.jwt = jwt;
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                }
            });
        });
        this.add(store);

        this.boost = Application.service.scheduleAtFixedRate(() -> action.setEnabled(!(this.jwt == null)), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBackground() {
        return false;
    }

    @Override
    public void onUpdate() {
        this.setEnabled(client != null);
        this.jwt = null;
    }

    @Override
    public void onExit() {
        if (boost != null && !boost.isCancelled()) boost.cancel(true);
    }
}
