package no.kantega.android.afp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SynchronizeActivity extends Activity {

    private static final String TAG = SynchronizeActivity.class.getSimpleName();
    private static final int PROGRESS_DIALOG = 0;
    private Transactions db;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    private TextView lastSynchronized;
    private TextView transactionCount;
    private TextView tagCount;
    private TextView dirtyCount;
    private TextView untaggedCount;
    private AlertDialog alertDialog;
    private int dbTransactionCount;
    private int dbTagCount;
    private int dbDirtyCount;
    private int dbUntaggedCount;

    /**
     * Called when the activity is starting. Attaches click listeners and
     * creates a database handle.
     *
     * @param savedInstanceState Saved instance
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
        Button clearDbButton = (Button) findViewById(R.id.clearDbButton);
        clearDbButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                db.emptyTables();
                onResume();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.db_cleared),
                        Toast.LENGTH_SHORT).show();
            }
        });
        db = new Transactions(getApplicationContext());
        lastSynchronized = (TextView) findViewById(R.id.last_synchronized);
        transactionCount = (TextView) findViewById(R.id.internal_t_count);
        tagCount = (TextView) findViewById(R.id.internal_tag_count);
        dirtyCount = (TextView) findViewById(R.id.unsynced_count);
        untaggedCount = (TextView) findViewById(R.id.untagged_count);
        preferences = Prefs.get(getApplicationContext());
        alertDialog = createAlertDialog();
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.server_unavailable)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

    /**
     * Update statistics on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbTransactionCount = db.getCount();
                dbTagCount = db.getTagCount();
                dbDirtyCount = db.getDirtyCount();
                dbUntaggedCount = db.getUntaggedCount();
                runOnUiThread(populate);
            }
        }).start();
    }

    private final Runnable populate = new Runnable() {
        @Override
        public void run() {
            lastSynchronized.setText(preferences.getString("syncDate",
                    getResources().getString(R.string.not_synchronized)));
            transactionCount.setText(String.valueOf(dbTransactionCount));
            tagCount.setText(String.valueOf(dbTagCount));
            dirtyCount.setText(String.valueOf(dbDirtyCount));
            untaggedCount.setText(String.valueOf(dbUntaggedCount));
        }
    };

    /**
     * Save stats to internal preferences
     */
    private void saveStats() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("syncDate", FmtUtil.dateToString("yyyy-MM-dd HH:mm:ss",
                new Date()));
        editor.commit();
    }

    /**
     * Called when a dialog is created. Configures the progress dialog.
     *
     * @param id Dialog ID
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
     * @param id     Dialog ID
     * @param dialog The Dialog
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
     * Read URLs from properties file and start a task that synchronizes the
     * database
     */
    private void synchronizeDatabase() {
        final Properties properties = Prefs.getProperties(
                getApplicationContext());
        final Object urlNew = properties.get("newTransactions");
        final Object urlAll = properties.get("allTransactions");
        final Object urlSave = properties.get("saveTransactions");
        if (urlNew != null && urlAll != null && urlSave != null) {
            new TransactionsTask().execute(urlNew.toString(),
                    urlAll.toString(), urlSave.toString());
        } else {
            Log.e(TAG, "Missing one or more entries in properties file");
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

    private class TransactionsTask
            extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            return getTransactions(urls[0], urls[1]) &&
                    putTransactions(urls[2]);
        }

        /**
         * Post "dirty" transactions to an URL
         *
         * @param url Save URL
         */
        private boolean putTransactions(final String url) {
            List<Transaction> dirtyTransactions = db.getDirty();
            if (!dirtyTransactions.isEmpty()) {
                final String json = GsonUtil.makeJSON(dirtyTransactions);
                final InputStream in = post(url,
                        new ArrayList<NameValuePair>() {{
                            add(new BasicNameValuePair("json", json));
                        }});
                if (in == null) {
                    return false;
                }
                final List<Transaction> updated = GsonUtil.
                        parseTransactions(in);
                if (updated == null) {
                    return false;
                }
                progressDialog.setMax(updated.size());
                int i = 0;
                for (Transaction t : updated) {
                    t.setDirty(false);
                    db.update(t);
                    publishProgress(++i);
                }
            }
            return true;
        }

        /**
         * Retrieve transactions from server
         *
         * @param url    URL for new transactions
         * @param urlAll URL for all transactions
         */
        private boolean getTransactions(final String url, final String urlAll) {
            final Transaction latest = db.getLatestExternal();
            final InputStream in;
            if (latest != null) {
                in = post(String.format(url, latest.getTimestamp()),
                        new ArrayList<NameValuePair>());
            } else {
                in = post(urlAll, new ArrayList<NameValuePair>());
            }
            if (in == null) {
                return false;
            }
            final List<Transaction> transactions = GsonUtil.
                    parseTransactions(in);
            if (transactions == null) {
                return false;
            }
            progressDialog.setMax(transactions.size());
            int i = 0;
            for (Transaction t : transactions) {
                db.add(t);
                publishProgress(++i);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            final Message msg = handler.obtainMessage();
            msg.arg1 = values[0];
            handler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.dismiss();
            if (!success) {
                alertDialog.show();
            }
            saveStats();
            onResume();
        }
    }

    private InputStream post(String url, List<NameValuePair> values) {
        values.add(new BasicNameValuePair("registrationId",
                preferences.getString(Register.REGISTRATION_ID_KEY, null)));
        return HttpUtil.post(url, values);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}