package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class OverviewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        parseTags(getJSON("http://10.10.10.77:9000/t/tags/3"));
        parseAvg(getJSON("http://10.10.10.77:9000/t/avg"));
        parseTransactions(getJSON("http://10.10.10.77:9000/t/transactions/10"));
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
        populateCategories(tags);
    }

    private void parseAvg(String json) {
        Gson gson = new Gson();
        AverageConsumption averageConsumption = gson.fromJson(json,
                AverageConsumption.class);
        populateAverageConsumption(averageConsumption);
    }

    private void parseTransactions(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaction>>() {
        }.getType();
        List<Transaction> transactions = gson.fromJson(json, listType);
        populateTransactions(transactions);
    }

    private void populateAverageConsumption(AverageConsumption averageConsumption) {
        TextView average_day = (TextView) findViewById(R.id.average_day);
        TextView average_week = (TextView) findViewById(R.id.average_week);
        average_day.setText(averageConsumption.getDay().toString());
        average_week.setText(averageConsumption.getWeek().toString());
    }

    private void populateTransactions(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            addTransaction(t.accountingDate.toString(), t.type.name, t.tags.get(0).name,
                    t.amountOut.toString());
        }
    }

    private void addTransaction(String date, String text, String category,
                                String amount) {
        TableLayout transactions = (TableLayout) findViewById(R.id.transactionTableLayout);
        // int children = transactions.getChildCount();
        TableRow tr = new TableRow(this);
        TextView tv = new TextView(this);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,
                1f);
        tv.setText(date);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(category);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(amount);
        tv.setLayoutParams(tvParams);
        tr.addView(tv);
        transactions.addView(tr, 4);
    }

    private void populateCategories(List<AggregatedTag> tags) {
        TextView category1 = (TextView) findViewById(R.id.top3_category_1);
        TextView category2 = (TextView) findViewById(R.id.top3_category_2);
        TextView category3 = (TextView) findViewById(R.id.top3_category_3);
        TextView amount1 = (TextView) findViewById(R.id.top3_amount_1);
        TextView amount2 = (TextView) findViewById(R.id.top3_amount_2);
        TextView amount3 = (TextView) findViewById(R.id.top3_amount_3);
        if (tags.size() == 3) {
            category1.setText(tags.get(0).getName());
            amount1.setText(tags.get(0).getAmount().toString());
            category2.setText(tags.get(1).getName());
            amount2.setText(tags.get(1).getAmount().toString());
            category3.setText(tags.get(2).getName());
            amount3.setText(tags.get(2).getAmount().toString());
        }
    }
}
