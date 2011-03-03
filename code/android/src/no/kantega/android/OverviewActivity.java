package no.kantega.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import no.kantega.android.models.AggregatedTag;
import no.kantega.android.models.AverageConsumption;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.GsonUtil;

import java.util.List;

public class OverviewActivity extends Activity {

    private static final String TAG = OverviewActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
    }

    private void populate() {
        populateAverageConsumption(GsonUtil.parseAvg(GsonUtil.
                getJSON("http://10.10.10.77:9000/t/avg")));
        populateTransactions(GsonUtil.parseTransactions(GsonUtil.
                getJSON("http://10.10.10.77:9000/t/avg")));
        populateCategories(GsonUtil.parseTags(GsonUtil.
                getJSON("http://10.10.10.77:9000/t/tags/3")));
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
