package no.kantega.android.afp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import no.kantega.android.afp.adapters.TransactionsAdapter;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;

/**
 * This activity displays transactiosn for the given tag in the given period
 */
public class TransactionsPerTagActivity extends ListActivity {

    private static final String TAG = TransactionsPerTagActivity.class.getSimpleName();
    private Transactions db;
    private TransactionsAdapter adapter;
    private Cursor cursor;
    private String year;
    private String month;
    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        this.tag = getIntent().getExtras().getString("tag");
        this.month = getIntent().getExtras().getString("month");
        this.year = getIntent().getExtras().getString("year");
        this.db = new Transactions(getApplicationContext());
        this.adapter = new TransactionsAdapter(this, cursor, R.layout.transactionrow);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Object o = l.getItemAtPosition(position);
        if (o instanceof Cursor) {
            Cursor cursor = (Cursor) o;
            int transaction_id = cursor.getInt(cursor.getColumnIndex("_id"));
            Transaction t = db.getById(transaction_id);
            Intent intent;
            intent = new Intent(getApplicationContext(), EditTransactionActivity.class);
            intent.putExtra("transaction", t);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = db.getCursorTransactions(tag, month, year);
                runOnUiThread(handler);
            }
        }).start();
    }

    private final Runnable handler = new Runnable() {
        @Override
        public void run() {
            adapter.changeCursor(cursor);
            Log.d(TAG, "Changed to a new cursor");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeCursor(cursor);
        db.close();
    }
}
