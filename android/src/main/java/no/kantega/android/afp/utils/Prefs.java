package no.kantega.android.afp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

public class Prefs {

    private static final String TAG = Prefs.class.getSimpleName();
    private static final String PROPERTIES_FILE = "url.properties";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("AFP_PREFS", Context.MODE_APPEND);
    }

    public static Properties getProperties(Context context) {
        final Properties properties = new Properties();
        try {
            properties.load(context.getAssets().open(PROPERTIES_FILE));
            return properties;
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
        return null;
    }
}
