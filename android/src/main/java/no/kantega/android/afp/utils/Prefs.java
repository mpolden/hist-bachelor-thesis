package no.kantega.android.afp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * This class manages preferences and properties
 */
public class Prefs {

    private static final String TAG = Prefs.class.getSimpleName();
    private static final String PROPERTIES_FILE = "url.properties";

    /**
     * Get shared preferences
     *
     * @param context Application context
     * @return Shared preferences
     */
    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("AFP_PREFS", Context.MODE_APPEND);
    }

    /**
     * Retrieve properties file
     *
     * @param context Application context
     * @return Properties
     */
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
