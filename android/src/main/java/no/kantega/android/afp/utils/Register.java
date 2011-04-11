package no.kantega.android.afp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.c2dm.C2DMessaging;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class provides helper methods for C2DM registration
 */
public class Register {

    private static final String TAG = Register.class.getSimpleName();
    public static final String SENDER_ID = "androidafp@gmail.com";
    public static final String REGISTRATION_ID_KEY = "deviceRegistrationID";
    public static final String USERNAME_KEY = "username";

    /**
     * Register the device if it doesn't already have registration ID
     *
     * @param context Application context
     */
    public static void handleRegistration(Context context) {
        final SharedPreferences preferences = Prefs.get(context);
        final String deviceId = preferences.getString(REGISTRATION_ID_KEY, null);
        if (deviceId == null) {
            C2DMessaging.register(context, SENDER_ID);
        }
        Log.d(TAG, "Existing Device ID: " + deviceId);
        registerWithServer(context);
    }

    /**
     * Register with server
     *
     * @param context Application context
     */
    public static void registerWithServer(Context context) {
        final SharedPreferences preferences = Prefs.get(context);
        final String deviceId = preferences.getString(REGISTRATION_ID_KEY, null);
        final String username = preferences.getString(USERNAME_KEY, null);
        final Properties properties = Prefs.getProperties(context);
        if (deviceId != null && username != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.post(properties.getProperty("register").toString(),
                            new ArrayList<NameValuePair>() {{
                                add(new BasicNameValuePair("username", username));
                                add(new BasicNameValuePair("deviceId", deviceId));
                            }});
                    Log.d(TAG, String.format("Registered %s with device ID: %s", username, deviceId));
                }
            }).start();
        }
    }
}
