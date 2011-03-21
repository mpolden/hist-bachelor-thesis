package no.kantega.android.utils;

import android.util.Log;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import no.kantega.android.models.AggregatedTag;
import no.kantega.android.models.AverageConsumption;
import no.kantega.android.models.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GsonUtil {

    private static final String TAG = GsonUtil.class.getSimpleName();
    private static final String PORTABLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Gson gson;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                SimpleDateFormat format = new SimpleDateFormat(
                        PORTABLE_DATE_FORMAT);
                try {
                    return format.parse(json.getAsJsonPrimitive().
                            getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });
        gson = builder.create();
    }

    /**
     * Parse transactions from an InputStream
     *
     * @param in
     * @return List of transactions
     */
    public static List<Transaction> parseTransactionsFromStream(
            final InputStream in) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(
                    in, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                Transaction t = gson.fromJson(reader,
                        Transaction.class);
                transactions.add(t);
            }
            reader.endArray();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
        }
        return transactions;
    }

    /**
     * Serialize to JSON using a portable date format
     *
     * @param o
     * @return JSON representation
     */
    public static String makeJSON(final Object o) {
        return new GsonBuilder().setDateFormat(PORTABLE_DATE_FORMAT).create().
                toJson(o);
    }

    /**
     * Parse aggregated tags from the given JSON
     *
     * @param json
     * @return List of aggregated tags
     */
    public static List<AggregatedTag> parseTags(final String json) {
        Type listType = new TypeToken<List<AggregatedTag>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    /**
     * Parse average consumption from the given JSON
     *
     * @param json
     * @return Average consumption
     */
    public static AverageConsumption parseAvg(final String json) {
        return gson.fromJson(json, AverageConsumption.class);
    }

    /**
     * Parse transactions from the given JSON
     *
     * @param json
     * @return List of transactions
     */
    public static List<Transaction> parseTransactions(final String json) {
        final Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }
}
