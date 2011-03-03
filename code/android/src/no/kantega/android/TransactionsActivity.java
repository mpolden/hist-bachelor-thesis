package no.kantega.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import no.kantega.android.models.Transaction;
import no.kantega.android.utils.FmtUtil;
import no.kantega.android.utils.GsonUtil;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TransactionsActivity extends Activity {

    private static final String TAG = OverviewActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        readProperties();
    }

    private void readProperties() {
        try {
            InputStream inputStream = getAssets().open("url.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            new TransactionsTask().execute(
                    properties.get("allTransactions").toString());
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }
    }

    private void addTransaction(Transaction t) {
        TableLayout tl = (TableLayout) findViewById(R.id.transaction_table_layout);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f);
        tl.addView(getSeparator());
        TableRow tr = new TableRow(this);
        tr.addView(getTextView("Date", tvParams, true));
        tr.addView(getTextView(FmtUtil.date("yyyy-MM-dd", t
                .accountingDate), tvParams, true));
        tl.addView(tr);
        tl.addView(getSeparator());
        tr = new TableRow(this);
        tr.addView(getTextView("Text", tvParams, false));
        tr.addView(getTextView(FmtUtil.trimTransactionText(t.text),
                tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Type", tvParams, false));
        tr.addView(getTextView(t.type.name, tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Category", tvParams, false));
        tr.addView(getTextView(t.tags.get(0).name, tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Amount", tvParams, false));
        tr.addView(getTextView(FmtUtil.currency(t.amountOut),
                tvParams, false));
        tl.addView(tr);
        tl.addView(getSeparator());
    }

    private TextView getTextView(String s, TableRow.LayoutParams lp,
                                 boolean bold) {
        TextView tv = new TextView(this);
        if (bold) {
            tv.setText(Html.fromHtml("<b>" + s + "</b>"));
        } else {
            tv.setText(s);
        }
        tv.setLayoutParams(lp);
        return tv;
    }

    private View getSeparator() {
        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT, 2));
        v.setBackgroundColor(Color.GRAY);
        return v;
    }

    private class TransactionsTask
            extends AsyncTask<String, Integer, List<Transaction>> {

        protected List<Transaction> doInBackground(String... urls) {
            return GsonUtil.parseTransactions(GsonUtil.getJSON(urls[0]));
        }

        protected void onPostExecute(List<Transaction> transactions) {
            for (Transaction t : transactions) {
                addTransaction(t);
            }
        }
    }
}
