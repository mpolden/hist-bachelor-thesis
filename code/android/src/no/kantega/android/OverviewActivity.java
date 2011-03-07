package no.kantega.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import no.kantega.android.models.AggregatedTag;
import no.kantega.android.models.AverageConsumption;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.FmtUtil;
import no.kantega.android.utils.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class OverviewActivity extends Activity {

    private static final String TAG = OverviewActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        readProperties();
    }

    private void readProperties() {
        try {
            InputStream inputStream = getAssets().open("url.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            new TransactionsTask().execute(
                    properties.get("transactions").toString());
            new TagsTask().execute(properties.get("tags").toString());
            new AverageConsumptionTask().execute(properties.get("avg").
                    toString());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
    }

    private void populateAverageConsumption(AverageConsumption avg) {
        TextView average_day = (TextView) findViewById(R.id.average_day);
        TextView average_week = (TextView) findViewById(R.id.average_week);
        average_day.setText(FmtUtil.currency(avg.getDay()));
        average_week.setText(FmtUtil.currency(avg.getWeek()));
    }

    private void populateTransactions(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            addTransaction(FmtUtil.date("yyyy-MM-dd",
                    t.accountingDate), t.type.name,
                    t.tags.get(0).name,
                    FmtUtil.currency(t.amountOut));
        }
    }

    private void addTransaction(String date, String text, String category,
                                String amount) {
        TableLayout transactions = (TableLayout) findViewById(R.id.transactionTableLayout);
        TableRow tr = new TableRow(this);
        TextView tv = new TextView(this);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,
                1f);
        tv.setText(date);
        tv.setLayoutParams(tvParams);
        tv.setTextColor(Color.WHITE);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(tvParams);
        tv.setTextColor(Color.WHITE);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(category);
        tv.setLayoutParams(tvParams);
        tv.setTextColor(Color.WHITE);
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(amount);
        tv.setLayoutParams(tvParams);
        tv.setTextColor(Color.WHITE);
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
            amount1.setText(FmtUtil.currency(tags.get(0).getAmount()));
            category2.setText(tags.get(1).getName());
            amount2.setText(FmtUtil.currency(tags.get(1).getAmount()));
            category3.setText(tags.get(2).getName());
            amount3.setText(FmtUtil.currency(tags.get(2).getAmount()));
        }
    }

    private class TransactionsTask
            extends AsyncTask<String, Integer, List<Transaction>> {

        protected List<Transaction> doInBackground(String... urls) {
            return GsonUtil.parseTransactions(GsonUtil.getJSON(urls[0]));
        }

        protected void onPostExecute(List<Transaction> transactions) {
            populateTransactions(transactions);
        }
    }

    private class TagsTask
            extends AsyncTask<String, Integer, List<AggregatedTag>> {

        protected List<AggregatedTag> doInBackground(String... urls) {
            return GsonUtil.parseTags(GsonUtil.getJSON(urls[0]));
        }

        protected void onPostExecute(List<AggregatedTag> tags) {
            populateCategories(tags);
        }
    }

    private class AverageConsumptionTask
            extends AsyncTask<String, Integer, AverageConsumption> {

        protected AverageConsumption doInBackground(String... urls) {
            return GsonUtil.parseAvg(GsonUtil.getJSON(urls[0]));
        }

        protected void onPostExecute(AverageConsumption avg) {
            populateAverageConsumption(avg);
        }
    }
}
