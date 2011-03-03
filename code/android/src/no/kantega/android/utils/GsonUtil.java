package no.kantega.android.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import no.kantega.android.models.AggregatedTag;
import no.kantega.android.models.AverageConsumption;
import no.kantega.android.models.Transaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GsonUtil {

    private static final String TAG = GsonUtil.class.getSimpleName();
    private static final Gson gson = new Gson();

    public static String getJSON(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        String body = null;
        try {
            HttpResponse response = httpClient.execute(method);
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
        }
        return body;
    }

    public static List<AggregatedTag> parseTags(String json) {
        Type listType = new TypeToken<List<AggregatedTag>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public static AverageConsumption parseAvg(String json) {
        return gson.fromJson(json, AverageConsumption.class);
    }

    public static List<Transaction> parseTransactions(String json) {
        Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }
}
