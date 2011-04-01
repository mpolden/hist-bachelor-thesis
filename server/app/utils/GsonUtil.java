package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.Transaction;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GsonUtil {

    private static final String PORTABLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final GsonBuilder builder = new GsonBuilder();

    static {
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                SimpleDateFormat format = new SimpleDateFormat(PORTABLE_DATE_FORMAT);
                try {
                    return format.parse(json.getAsJsonPrimitive().
                            getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });
    }

    public static String makeJSON(Object o) {
        return new GsonBuilder().setDateFormat(PORTABLE_DATE_FORMAT).create().toJson(o);
    }

    public static List<Transaction> parseTransactions(String s) {
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return builder.create().fromJson(s, listType);
    }

    public static List<Transaction> parseTransactions(JsonArray jsonArray) {
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return builder.create().fromJson(jsonArray, listType);
    }
}
