package pl.papuda.ess.client.tools;

import java.util.prefs.Preferences;
import pl.papuda.ess.client.interfaces.Strategy;
import pl.papuda.ess.client.notificationStrategy.DialogNotificationStrategy;
import pl.papuda.ess.client.notificationStrategy.EmailNotificationStrategy;

public class AppPreferences {

    private static final Preferences prefs = Preferences.userNodeForPackage(Web.class);
    private static final Strategy popupNotificationStrategy = new DialogNotificationStrategy();
    private static final Strategy emailNotificationStrategy = new EmailNotificationStrategy();

    public static void set(String key, String value) {
        prefs.put(key, value);
    }

    public static void unset(String key) {
        prefs.remove(key);
    }

    public static String read(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    public static Strategy getNotificationPreference() {
        return read("notificationMethod", "popup").equals("popup")
                ? popupNotificationStrategy
                : emailNotificationStrategy;
    }
}
