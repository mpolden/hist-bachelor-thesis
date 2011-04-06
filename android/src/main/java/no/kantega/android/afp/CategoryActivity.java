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
public class CategoryActivity extends ListActivity {

    private static final String TAG = CategoryActivity.class.getSimpleName();
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
        this.cursor = db.getCursorTransactions(tag, month, year);
        this.adapter = new TransactionsAdapter(this, cursor);
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
                // Retrieve a new cursor in a thread, then do the actual swap on the UiThread
                cursor = db.getCursorTransactions(tag, month, year);
                runOnUiThread(handler);
            }
        }).start();
    }

    private final Runnable handler = new Runnable() {
        @Override
        public void run() {
            // Try to change to a fresh cursor
            if (!cursor.isClosed()) {
                adapter.changeCursor(cursor);
                Log.d(TAG, "Changed to a new cursor");
            } else {
                onResume();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
    }
}
