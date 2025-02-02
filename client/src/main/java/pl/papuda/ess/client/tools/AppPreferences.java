package pl.papuda.ess.client.tools;

import java.util.prefs.Preferences;

public class AppPreferences {

    private static final Preferences prefs = Preferences.userNodeForPackage(Web.class);

    public static void set(String key, String value) {
        prefs.put(key, value);
    }

    public static void unset(String key) {
        prefs.remove(key);
    }

    public static String read(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }
}
