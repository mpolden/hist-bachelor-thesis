package no.kantega.android.afp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.c2dm.C2DMBaseReceiver;

import java.io.IOException;

public class C2DMReceiver extends C2DMBaseReceiver {

    private static final String TAG = C2DMReceiver.class.getSimpleName();

    public C2DMReceiver() {
        super("this.is.not@real.biz");
    }

    @Override
    public void onRegistered(Context context, String registrationId) throws IOException {
        Log.w(TAG, "C2DMReceiver-onRegistered: " + registrationId);
    }

    @Override
    public void onUnregistered(Context context) {
        Log.w(TAG, "C2DMReceiver-onUnregistered: " + "got here!");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.w(TAG, "C2DMReceiver-onError: " + errorId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.w(TAG, "C2DMReceiver: " + intent.getStringExtra("payload"));
    }
}
