package no.kantega.android.afp;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import no.kantega.android.afp.adapters.TransactionsAdapter;
import no.kantega.android.afp.controllers.Transactions;

public class CategoryActivity extends ListActivity {

    private static final String TAG = OverviewActivity.class.getSimpleName();
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
                Log.d(TAG, "Changed to a new cursor");
                adapter.changeCursor(cursor);
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
