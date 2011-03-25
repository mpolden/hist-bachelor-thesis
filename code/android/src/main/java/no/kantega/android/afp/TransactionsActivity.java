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

public class TransactionsActivity extends ListActivity {

    private static final String TAG = OverviewActivity.class.getSimpleName();
    private Transactions db;
    private TransactionsAdapter adapter;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        this.db = new Transactions(getApplicationContext());
        this.cursor = db.getCursor(false);
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
                cursor = db.getCursor(false);
                runOnUiThread(handler);
            }
        }).start();
    }

    private final Runnable handler = new Runnable() {
        @Override
        public void run() {
            // Change to a fresh cursor, the old one will be automatically closed
            Log.d(TAG, "Changed to a new cursor");
            adapter.changeCursor(cursor);
        }
    };

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
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
    }
}
