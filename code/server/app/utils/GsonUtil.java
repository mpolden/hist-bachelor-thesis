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

    public static String renderJSONWithDateFmt(String format, Object o) {
        GsonBuilder gson = new GsonBuilder().
                setDateFormat(format);
        return gson.create().toJson(o);
    }

    public static List<Transaction> parseTransactions(JsonArray jsonArray) {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
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
