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

    public static String makeJSON(Object o) {
        return new GsonBuilder().setDateFormat(PORTABLE_DATE_FORMAT).create().toJson(o);
    }

    public static List<Transaction> parseTransactions(JsonArray jsonArray) {
        final GsonBuilder builder = new GsonBuilder();
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
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return builder.create().fromJson(jsonArray, listType);
    }
}
