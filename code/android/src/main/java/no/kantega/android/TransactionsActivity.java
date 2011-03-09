package no.kantega.android;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.DatabaseHelper;
import no.kantega.android.utils.DatabaseOpenHelper;
import no.kantega.android.utils.FmtUtil;

import java.util.List;

public class TransactionsActivity extends Activity {

    private static final String TAG = OverviewActivity.class.getSimpleName();
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        DatabaseOpenHelper helper = new DatabaseOpenHelper(
                getApplicationContext());
        this.db = helper.getReadableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }

    private void populate() {
        clearTransactions();
        List<Transaction> transactions = DatabaseHelper.
                getOrderedByDateDesc(db, 20);
        for (Transaction t : transactions) {
            addTransaction(t);
        }
    }

    private void clearTransactions() {
        TableLayout transactions = (TableLayout) findViewById(R.id.
                transaction_table_layout);
        int transaction_count = transactions.getChildCount();
        if (transactions.getChildAt(0) != null) {
            transactions.removeViews(0, transaction_count);
        }
    }

    private void addTransaction(Transaction t) {
        TableLayout tl = (TableLayout) findViewById(R.id.transaction_table_layout);
        TableRow.LayoutParams tvParams = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f);
        tl.addView(getSeparator());
        TableRow tr = new TableRow(this);
        tr.addView(getTextView("Date", tvParams, true));
        tr.addView(getTextView(FmtUtil.date("yyyy-MM-dd", t.getAccountingDate()), tvParams, true));
        tl.addView(tr);
        tl.addView(getSeparator());
        tr = new TableRow(this);
        tr.addView(getTextView("Text", tvParams, false));
        tr.addView(getTextView(FmtUtil.trimTransactionText(t.getText()),
                tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Type", tvParams, false));
        tr.addView(getTextView(t.getType().getName(), tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Category", tvParams, false));
        tr.addView(getTextView(t.getTag().getName(), tvParams, false));
        tl.addView(tr);
        tr = new TableRow(this);
        tr.addView(getTextView("Amount", tvParams, false));
        tr.addView(getTextView(FmtUtil.currency(t.getAmountOut()),
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
        tv.setTextColor(Color.WHITE);
        return tv;
    }

    private View getSeparator() {
        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT, 2));
        v.setBackgroundColor(Color.GRAY);
        return v;
    }
}
