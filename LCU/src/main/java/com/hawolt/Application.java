package com.hawolt;

import com.hawolt.impl.ItemModule;
import com.hawolt.impl.PopupModule;
import com.hawolt.io.Core;
import com.hawolt.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created: 20/07/2022 08:42
 * Author: Twitter @hawolt
 **/

public class Application {

    public static final ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
    public static final List<MenuItem> list = new ArrayList<>();

    private static SystemTray tray;
    private static PopupMenu popup;

    public static TrayIcon icon;

    private static BufferedImage loadIcon() throws IOException {
        return ImageIO.read(new ByteArrayInputStream(Core.read(RunLevel.get("JK.png")).toByteArray()));
    }

    private static void launchTray(String name, BufferedImage image) {
        Application.icon = new TrayIcon(image, name);
        Application.tray = SystemTray.getSystemTray();
        Application.popup = new PopupMenu();
        Application.icon.setPopupMenu(Application.popup);
        try {
            Application.tray.add(Application.icon);
        } catch (AWTException e) {
            Logger.error(e);
        }
    }

    public static void notification(String caption, String content, TrayIcon.MessageType type) {
        Application.icon.displayMessage(caption, content, type);
    }

    public static void launch(String name) throws Exception {
        if (!SystemTray.isSupported()) throw new Exception("System Tray not supported");
        BufferedImage image = loadIcon();
        launchTray(name, image);
        SSL.bypass();
    }

    public static void addExitOption() {
        addExitOption(null);
    }

    private final static List<Module> modules = new ArrayList<>();

    public static void addItemModule(ItemModule module) {
        if (!module.isBackground()) Application.popup.add(module);
        Application.modules.add(module);
    }

    public static void addPopupModule(PopupModule module) {
        if (!module.isBackground()) Application.popup.add(module);
        Application.modules.add(module);
    }

    public static void addMenuEntry(String text, Runnable action) {
        MenuItem credit = new MenuItem(text);
        credit.addActionListener(listener -> action.run());
        Application.popup.add(credit);
    }

    public static void addExitOption(Runnable runnable) {
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(listener -> {
            if (runnable != null) runnable.run();
            for (Module module : modules) {
                module.onExit();
            }
            Application.tray.remove(Application.icon);
            service.shutdown();
            System.exit(0);
        });
        Application.popup.add(exit);
    }

    public static void ping(LeagueClient client, SummonerProfile profile) {
        if (!(client == null || profile == null)) Logger.debug("Selected {} as {}", client, profile);
        for (Module module : Application.modules) {
            module.onUpdate(client, profile);
        }
    }
}
