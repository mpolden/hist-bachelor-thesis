package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.Transaction;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class handles parsing of JSON data to native Java types
 */
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

    /**
     * Create JSON representation of the given object
     *
     * @param o Object
     * @return JSON representation
     */
    public static String makeJSON(Object o) {
        return new GsonBuilder().setDateFormat(PORTABLE_DATE_FORMAT).create().toJson(o);
    }

    /**
     * Parse transactions from JSON
     *
     * @param s JSON
     * @return List of transactions
     */
    public static List<Transaction> parseTransactions(String s) {
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return builder.create().fromJson(s, listType);
    }

    /**
     * Parse transactions from JSON array
     *
     * @param jsonArray JSON array
     * @return List of transactions
     */
    public static List<Transaction> parseTransactions(JsonArray jsonArray) {
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return builder.create().fromJson(jsonArray, listType);
    }
}
