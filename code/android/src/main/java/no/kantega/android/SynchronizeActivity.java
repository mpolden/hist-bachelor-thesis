package no.kantega.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.FmtUtil;
import no.kantega.android.utils.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SynchronizeActivity extends Activity {

    private static final String TAG = SynchronizeActivity.class.getSimpleName();
    private static final int PROGRESS_DIALOG = 0;
    public static final String PREFS_NAME = "SynchronizePreferences";
    private Transactions db;
    private ProgressDialog progressDialog;
    private TextView lastSynchronized;
    private TextView transactionCount;
    private TextView tagCount;
    private int dbTransactionCount;
    private int dbTagCount;

    /**
     * Called when the activity is starting. Attaches click listeners and
     * creates a database handle.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronize);
        Button syncButton = (Button) findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(PROGRESS_DIALOG);
            }
        });
        db = new Transactions(getApplicationContext());
        lastSynchronized = (TextView) findViewById(R.id.last_synchronized);
        transactionCount = (TextView) findViewById(R.id.internal_t_count);
        tagCount = (TextView) findViewById(R.id.internal_tag_count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbTransactionCount = db.getCount();
                dbTagCount = db.getTagCount();
                runOnUiThread(populate);
            }
        }).start();
    }

    private Runnable populate = new Runnable() {
        @Override
        public void run() {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            lastSynchronized.setText(settings.getString("syncDate",
                    getResources().getString(R.string.not_synchronized)));
            transactionCount.setText(String.valueOf(dbTransactionCount));
            tagCount.setText(String.valueOf(dbTagCount));
        }
    };

    private void saveStats() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("syncDate", FmtUtil.dateToString("yyyy-MM-dd HH:mm:ss",
                new Date()));
        editor.commit();
    }

    /**
     * Called when a dialog is created. Configures the progress dialog.
     *
     * @param id
     * @return The configured dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG: {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getResources().getString(
                        R.string.wait));
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.
                        STYLE_HORIZONTAL);
                return progressDialog;
            }
            default: {
                return null;
            }
        }
    }

    /**
     * Called when preparing the dialog.
     *
     * @param id
     * @param dialog
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case PROGRESS_DIALOG: {
                progressDialog.setProgress(0);
                synchronizeDatabase();
            }
        }
    }

    /**
     * Read URL from properties file and start a task that synchronizes the
     * database
     */
    private void synchronizeDatabase() {
        try {
            InputStream inputStream = getAssets().open("url.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            new TransactionsTask().execute(
                    properties.get("freshTransactions").toString(),
                    properties.get("saveTransactions").toString());
        } catch (IOException e) {
            Log.e(TAG, "Could not read properties file", e);
        }
    }

    /**
     * Handler that updates the progress dialog
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.setProgress(msg.arg1);
        }
    };

    /**
     * Task that retrieves transactions, deserializes them from JSON and inserts
     * them into the local database
     */
    private class TransactionsTask
            extends AsyncTask<String, Integer, List<Transaction>> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<Transaction> doInBackground(String... urls) {
            getTransactions(urls[0]);
            putTransactions(urls[1]);
            return null;
        }

        private void putTransactions(String url) {
            List<Transaction> transactions = db.getDirty();
            if (!transactions.isEmpty()) {
                GsonBuilder gson = new GsonBuilder().
                        setDateFormat("yyyy-MM-dd HH:mm:ss");
                String json = gson.create().toJson(transactions);
                GsonUtil.postJSON(url, json);
                for (Transaction t : transactions) {
                    t.setDirty(false);
                    db.update(t);
                }
            }
        }

        private void getTransactions(String url) {
            Transaction latestTransaction = db.getLatestExternal();
            long timestamp = 0;
            if (latestTransaction != null) {
                timestamp = latestTransaction.getAccountingDate().getTime();
            }
            List<Transaction> transactions = GsonUtil.parseTransactions(
                    GsonUtil.getBody(String.format(url, timestamp)));
            if (transactions != null && !transactions.isEmpty()) {
                progressDialog.setMax(transactions.size());
                int i = 0;
                for (Transaction t : transactions) {
                    db.add(t);
                    publishProgress(++i);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            final Message msg = handler.obtainMessage();
            msg.arg1 = values[0];
            handler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(List<Transaction> transactions) {
            progressDialog.dismiss();
            saveStats();
            onResume();
        }
    }
}