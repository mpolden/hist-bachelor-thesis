package no.kantega.android.afp;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import no.kantega.android.afp.adapters.TransactionsAdapter;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import no.kantega.android.afp.utils.GsonUtil;
import no.kantega.android.afp.utils.HttpUtil;
import no.kantega.android.afp.utils.Prefs;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

/**
 * This activity displays transactiosn for the given tag in the given period
 */
public class TransactionsPerTagActivity extends ListActivity {

    private static final String TAG = TransactionsPerTagActivity.class.getSimpleName();
    private static final int PROGRESS_DIALOG_ID = 0;
    private Transactions db;
    private TransactionsAdapter adapter;
    private Cursor cursor;
    private String year;
    private String month;
    private String tag;
    private TextView transactionsCount;
    private Properties properties;
    private final Runnable handler = new Runnable() {
        @Override
        public void run() {
            adapter.changeCursor(cursor);
            Log.d(TAG, "Changed to a new cursor");
            transactionsCount.setText(String.format(getResources().getString(R.string.transaction_count),
                    adapter.getCount()));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        this.tag = getIntent().getExtras().getString("tag");
        this.month = getIntent().getExtras().getString("month");
        this.year = getIntent().getExtras().getString("year");
        this.transactionsCount = (TextView) findViewById(R.id.tv_transactioncount);
        this.db = new Transactions(getApplicationContext());
        this.adapter = new TransactionsAdapter(this, cursor, R.layout.transactionrow);
        this.properties = Prefs.getProperties(getApplicationContext());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transactionspertagmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_suggest: {
                new TransactionTagTask().execute();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG_ID: {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getResources().getString(R.string.fetching_tags));
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return progressDialog;
            }
            default: {
                return null;
            }
        }
    }

    private class TransactionTagTask extends AsyncTask<List<Transaction>, Integer, Map<Integer, TransactionTag>> {

        @Override
        protected void onPreExecute() {
            showDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected Map<Integer, TransactionTag> doInBackground(List<Transaction>... lists) {
            return findSuggestions(db.getUntagged(month, year));
        }

        /**
         * Find suggestions for the given transactions
         *
         * @param transactions List of transactions
         * @return Map of TransactionTags keyed on _id of the Transaction it's meant for
         */
        private Map<Integer, TransactionTag> findSuggestions(final List<Transaction> transactions) {
            final String body = HttpUtil.postAndReturnString(properties.getProperty("suggestTagAll"),
                    new ArrayList<NameValuePair>() {{
                        add(new BasicNameValuePair("json", GsonUtil.toJson(transactions)));
                    }});
            if (body == null) {
                return Collections.emptyMap();
            }
            return GsonUtil.toMap(body);
        }

        @Override
        protected void onPostExecute(Map<Integer, TransactionTag> suggestions) {
            dismissDialog(PROGRESS_DIALOG_ID);
            final Intent intent = new Intent(getApplicationContext(), SimilarTransactionsActivity.class);
            if (suggestions instanceof HashMap) {
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                intent.putExtra("suggestions", (HashMap<Integer, TransactionTag>) suggestions);
                startActivity(intent);
            } else {
                Log.e(TAG, "Type of suggestions is not HashMap");
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeCursor(cursor);
        db.close();
    }
}
