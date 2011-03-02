package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import no.kantega.android.models.AggregatedTag;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        parseTags(getJSON("http://127.0.0.1:9000/t/tags/3"));
    }

    private String getJSON(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        String body = null;
        try {
            HttpResponse response = httpClient.execute(method);
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d("Exception", "IOException", e);
        }
        return body;
    }

    private void parseTags(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AggregatedTag>>() {
        }.getType();
        List<AggregatedTag> tags = gson.fromJson(json, listType);
        for (AggregatedTag t : tags) {
            Log.d("tag", t.toString());
        }
    }

    private void getTopTags() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(
                "http://10.10.10.77:9000/transactions/topTags/3");
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            String body = EntityUtils.toString(response.getEntity());
            Map<String, String> map = new HashMap<String, String>();
            int i = 1;
            for (String line : body.split("\n")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    map.put("category" + i, parts[0]);
                    map.put("amount" + i, parts[1]);
                    i++;
                }
            }
            //populateCategories(map);
        } catch (IOException e) {
            Log.d("Exception", "IOException", e);
        }
    }

    private void populateCategories(List<AggregatedTag> aggregatedTags) {
        TextView category1 = (TextView) findViewById(R.id.top3_category_1);
        TextView category2 = (TextView) findViewById(R.id.top3_category_2);
        TextView category3 = (TextView) findViewById(R.id.top3_category_3);
        TextView amount1 = (TextView) findViewById(R.id.top3_amount_1);
        TextView amount2 = (TextView) findViewById(R.id.top3_amount_2);
        TextView amount3 = (TextView) findViewById(R.id.top3_amount_3);
        /*category1.setText(values.get("category1"));
        category2.setText(values.get("category2"));
        category3.setText(values.get("category3"));
        amount1.setText(values.get("amount1"));
        amount2.setText(values.get("amount2"));
        amount3.setText(values.get("amount3"));*/
    }

    private void populateAverageConsumption(String average_day_amount, String average_week_amount) {
        TextView average_day = (TextView) findViewById(R.id.average_day);
        TextView average_week = (TextView) findViewById(R.id.average_week);
        average_day.setText(average_day_amount);
        average_week.setText(average_week_amount);
    }
}
