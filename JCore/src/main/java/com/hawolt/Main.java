package com.hawolt;

import com.hawolt.logger.Logger;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created: 20/07/2022 08:57
 * Author: Twitter @hawolt
 **/

public class Main {

    public static void main(String[] args) {
        try {
            allowHttpMethods("PATCH");
            Application.launch("JKassadin");
            Application.addPopupModule(new JSessionTracker("LCU"));
            Application.addItemModule(new JSessionInvalidator("JSessionInvalidator"));
            Application.addPopupModule(new JSkinBoost("JSkinBoost"));
            Application.addItemModule(new JUnlock("JUnlock"));
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

    private static void allowHttpMethods(String... methods) {
        try {
            Field declaredFieldMethods = HttpURLConnection.class.getDeclaredField("methods");
            Field declaredFieldModifiers = Field.class.getDeclaredField("modifiers");
            declaredFieldModifiers.setAccessible(true);
            declaredFieldModifiers.setInt(declaredFieldMethods, declaredFieldMethods.getModifiers() & ~Modifier.FINAL);
            declaredFieldMethods.setAccessible(true);
            String[] previous = (String[]) declaredFieldMethods.get(null);
            Set<String> current = new LinkedHashSet<>(Arrays.asList(previous));
            current.addAll(Arrays.asList(methods));
            String[] patched = current.toArray(new String[0]);
            declaredFieldMethods.set(null, patched);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
