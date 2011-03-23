package no.kantega.android.afp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.c2dm.C2DMBaseReceiver;
import no.kantega.android.afp.utils.Prefs;
import no.kantega.android.afp.utils.Register;

import java.io.IOException;

public class C2DMReceiver extends C2DMBaseReceiver {

    private static final String TAG = C2DMReceiver.class.getSimpleName();
    private static final int MESSAGE_ID = 1;
    private NotificationManager notificationManager;

    public C2DMReceiver() {
        super(Register.SENDER_ID);
    }

    @Override
    public void onRegistered(Context context, String registrationId) throws IOException {
        Log.d(TAG, "Registered: " + registrationId);
        SharedPreferences prefs = Prefs.get(context);
        if (prefs.getString(Register.REGISTRATION_ID_KEY, null) == null) {
            prefs.edit().putString(Register.REGISTRATION_ID_KEY, registrationId);
            prefs.edit().commit();
        }
        //Register.registerWithServer(registrationId);
    }

    @Override
    public void onUnregistered(Context context) {
        Log.d(TAG, "Unregistered");
        SharedPreferences prefs = Prefs.get(context);
        String deviceRegistrationID = prefs.getString(
                Register.REGISTRATION_ID_KEY, null);
        if (deviceRegistrationID != null) {
            prefs.edit().remove(Register.REGISTRATION_ID_KEY);
            prefs.edit().commit();
            //Register.unregisterFromServer(deviceRegistrationID);
        }
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Error: " + errorId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        Log.w(TAG, "Message received: " + message);
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        Notification notification = new Notification(R.drawable.medical,
                message, System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notification.setLatestEventInfo(getApplicationContext(),
                getResources().getString(R.string.notification_title),
                message, pendingIntent);
        notificationManager.notify(MESSAGE_ID, notification);
    }
}
