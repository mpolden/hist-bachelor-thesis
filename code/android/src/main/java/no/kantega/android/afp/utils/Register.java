package no.kantega.android.afp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.c2dm.C2DMessaging;

public class Register {

    public static final String SENDER_ID = "androidafp@gmail.com";
    public static final String REGISTRATION_ID_KEY = "deviceRegistrationID";

    /**
     * Register the device if it doesn't already have registration ID
     *
     * @param context Application context
     */
    public static void handleRegistration(Context context) {
        SharedPreferences preferences = Prefs.get(context);
        if (preferences.getString(REGISTRATION_ID_KEY, null) == null) {
            C2DMessaging.register(context, SENDER_ID);
        }
    }

    /**
     * Register with server
     *
     * @param registrationId Registration ID for this device
     */
    public static void registerWithServer(String registrationId) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Unregister from server
     *
     * @param registrationId Registration ID for this device
     */
    public static void unregisterFromServer(String registrationId) {
        throw new IllegalStateException("Not implemented");
    }
}
