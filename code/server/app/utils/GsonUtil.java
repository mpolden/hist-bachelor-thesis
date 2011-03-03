package utils;

import com.google.gson.GsonBuilder;

public class GsonUtil {

    public static String renderJSONWithDateFmt(String format, Object o) {
        GsonBuilder gson = new GsonBuilder().
                setDateFormat(format);
        return gson.create().toJson(o);
    }
}
