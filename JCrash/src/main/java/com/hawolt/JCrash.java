package com.hawolt;

import com.hawolt.client.proxy.LocalHTTP;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 25/07/2022 20:22
 * Author: Twitter @hawolt
 **/

public class JCrash extends Module implements Runnable {

    public static boolean undercover;

    private final String _TOKEN = "f93b81cdee427b57f7c1fcdb033bddffdc894231f9953c990a422e2bf5067533";
    private final String _IP = "5.161.64.108";

    private final ScheduledFuture<?> future;
    private final MenuItem crash;

    private boolean notification;
    private File directory;
    private String pid;

    public JCrash(String name) {
        super(name);
        this.crash = new MenuItem("Crash");
        this.crash.addActionListener(listener -> {
            if (pid == null) return;
            try {
                Request request = new Request("http://" + _IP + ":7005/v1/crash/" + pid, Method.POST, false);
                request.addHeader("X-RIOTPHOBIA-TOKEN", _TOKEN);
                Response response = request.execute();
                String message = response.getCode() == 200 ? "Successfully crashed lobby " + pid : "Failed to crash lobby";
                if (!notification) return;
                Application.notification("JCrash", message, response.getCode() == 200 ? TrayIcon.MessageType.INFO : TrayIcon.MessageType.ERROR);
            } catch (IOException e) {
                Logger.error("Failed to establish connection with Riotphobia");
                Logger.error(e);
            }
        });
        this.add(crash);
        future = Application.service.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
        CheckboxMenuItem notification = new CheckboxMenuItem("Windows Notification");
        notification.addItemListener(listener -> this.notification = notification.getState());
        this.add(notification);
        CheckboxMenuItem undercover = new CheckboxMenuItem("Hide MUC");
        undercover.addItemListener(listener -> JCrash.undercover = undercover.getState());
        this.add(undercover);
        MenuItem launch = new MenuItem("Launch Safe Client");
        launch.setEnabled(false);
        launch.addActionListener(listener -> {
            if (directory == null) return;
            String client = directory.toPath().resolve("Riot Client").resolve("RiotClientServices.exe").toString();
            try {
                Runtime.getRuntime().exec(String.join(" ", client, "--client-config-url=\"http://127.0.0.1:7000\"", "--launch-product=league_of_legends", "--launch-patchline=live", "--allow-multiple-clients"));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
        this.add(launch);
        MenuItem directory = new MenuItem("Select Game directory");
        directory.addActionListener(listener -> {
            Application.service.execute(() -> {
                JOptionPane.showMessageDialog(null, "Please select your Riot Games directory to proceed");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    this.directory = fileChooser.getSelectedFile();
                }
                launch.setEnabled(this.directory != null);
            });
        });
        this.add(directory);
        this.setEnabled(true);
        new LocalHTTP("https://clientconfig.rpg.riotgames.com").start();
    }

    @Override
    public void run() {
        if (client == null) this.crash.setEnabled(false);
        else {
            try {
                String endpoint = String.format("https://127.0.0.1:%s/lol-chat/v1/conversations", client.getPort());
                Request request = new Call(client, endpoint, Method.GET, false);
                Response response = request.execute();
                if (response.getCode() != 200) return;
                JSONArray array = new JSONArray(response.getBodyAsString());
                boolean available = false;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject conversation = array.getJSONObject(i);
                    if (!conversation.has("pid")) continue;
                    String pid = conversation.getString("pid");
                    if (!pid.contains("@champ-select")) continue;
                    available = true;
                    this.pid = pid;
                }
                if (!available) this.pid = null;
                crash.setEnabled(available);
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    @Override
    boolean isBackground() {
        return false;
    }

    @Override
    void onUpdate() {

    }

    @Override
    void onExit() {
        if (future != null && !future.isCancelled()) future.cancel(true);
    }
}
