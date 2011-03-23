package no.kantega.android.afp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("AFP_PREFS", Context.MODE_APPEND);
    }
}
