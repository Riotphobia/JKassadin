package com.hawolt;

import com.hawolt.logger.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created: 20/07/2022 08:57
 * Author: Twitter @hawolt
 **/

public class Main {

    public static void main(String[] args) {
        try {
            Application.launch("JKassadin");
            Application.addModule(new JSessionTracker("LCU"));
            Application.addModule(new JSessionInvalidator("JSessionInvalidator"));
            Application.addModule(new JSkinBoost("JSkinBoost"));
            Application.addModule(new JUnlock("JUnlock"));
            Application.addMenuEntry("Github", () -> {
                try {
                    Desktop.getDesktop().browse(URI.create("https://github.com/Riotphobia/JKassadin"));
                } catch (IOException e) {
                    Logger.error(e);
                }
            });
            Application.addExitOption();
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
