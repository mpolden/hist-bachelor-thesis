package no.kantega.android.afp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.*;

/**
 * This activity handles synchronization with our external server
 */
public class SynchronizeActivity extends Activity {

    private static final String TAG = SynchronizeActivity.class.getSimpleName();
    private static final int FETCH_PROGRESS_DIALOG_ID = 0;
    private static final int UPDATE_PROGRESS_DIALOG_ID = 1;
    private static final int ALERT_DIALOG_ID = 2;
    private Transactions db;
    private SharedPreferences preferences;
    private ProgressDialog updateProgressDialog;
    private TextView lastSynchronized;
    private TextView transactionCount;
    private TextView tagCount;
    private TextView dirtyCount;
    private TextView untaggedCount;
    private long dbTransactionCount;
    private long dbTagCount;
    private long dbDirtyCount;
    private long dbUntaggedCount;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateProgressDialog.setProgress(msg.arg1);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronize);
        Button syncButton = (Button) findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizeDatabase();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.synchronizemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
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
            new TransactionTask().execute(urlNew.toString(),
                    urlAll.toString(), urlSave.toString());
        } else {
            Log.e(TAG, "Missing one or more entries in properties file");
        }
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
            case FETCH_PROGRESS_DIALOG_ID: {
                ProgressDialog fetchProgressDialog = new ProgressDialog(this);
                fetchProgressDialog.setMessage(getResources().getString(R.string.fetching_transactions));
                fetchProgressDialog.setCancelable(false);
                fetchProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return fetchProgressDialog;
            }
            case UPDATE_PROGRESS_DIALOG_ID: {
                updateProgressDialog = new ProgressDialog(this);
                updateProgressDialog.setMessage(getResources().getString(R.string.wait));
                updateProgressDialog.setCancelable(false);
                updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                return updateProgressDialog;
            }
            case ALERT_DIALOG_ID: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.server_unavailable)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                return builder.create();
            }
            default: {
                return null;
            }
        }
    }

    /**
     * This task handles retrieval of new transactions and sending of dirty ones
     */
    private class TransactionTask extends AsyncTask<String, Integer, Object> {

        private List<Transaction> freshTransactions;
        private List<Transaction> dirtyTransactions;

        @Override
        protected void onPreExecute() {
            showDialog(FETCH_PROGRESS_DIALOG_ID);
        }

        @Override
        protected Object doInBackground(String... urls) {
            this.freshTransactions = getTransactions(urls[0], urls[1]);
            this.dirtyTransactions = putTransactions(urls[2]);
            return null;
        }

        /**
         * Post "dirty" transactions to an URL
         *
         * @param url Save URL
         * @return True if successful
         */
        private List<Transaction> putTransactions(final String url) {
            List<Transaction> dirtyTransactions = db.getDirty();
            if (!dirtyTransactions.isEmpty()) {
                final String json = GsonUtil.toJson(dirtyTransactions);
                final InputStream in = post(url,
                        new ArrayList<NameValuePair>() {{
                            add(new BasicNameValuePair("json", json));
                        }});
                if (in == null) {
                    return null;
                }
                return GsonUtil.toList(in);
            }
            return Collections.emptyList();
        }

        /**
         * Retrieve transactions from server
         *
         * @param url    URL for new transactions
         * @param urlAll URL for all transactions
         * @return True if successful
         */
        private List<Transaction> getTransactions(final String url, final String urlAll) {
            final Transaction latest = db.getLatestExternal();
            final InputStream in;
            if (latest != null) {
                in = post(String.format(url, latest.getTimestamp()),
                        new ArrayList<NameValuePair>());
            } else {
                in = post(urlAll, new ArrayList<NameValuePair>());
            }
            if (in == null) {
                return null;
            }
            return GsonUtil.toList(in);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Object object) {
            dismissDialog(FETCH_PROGRESS_DIALOG_ID);
            if (freshTransactions == null || dirtyTransactions == null) {
                showDialog(ALERT_DIALOG_ID);
            } else if (!freshTransactions.isEmpty() || !dirtyTransactions.isEmpty()) {
                new UpdateTask().execute(freshTransactions, dirtyTransactions);
            }
        }
    }

    /**
     * This task handles update of transactions
     */
    private class UpdateTask extends AsyncTask<List<Transaction>, Integer, Object> {

        @Override
        protected void onPreExecute() {
            showDialog(UPDATE_PROGRESS_DIALOG_ID);
        }

        @Override
        protected Object doInBackground(List<Transaction>... lists) {
            // Add new transactions
            updateProgressDialog.setMax(lists[0].size() + lists[1].size());
            int i = 0;
            for (Transaction t : lists[0]) {
                db.add(t);
                publishProgress(++i);
            }
            // Send dirty transactions
            for (Transaction t : lists[1]) {
                t.setDirty(false);
                db.update(t);
                publishProgress(++i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            final Message msg = handler.obtainMessage();
            msg.arg1 = values[0];
            handler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(Object object) {
            dismissDialog(UPDATE_PROGRESS_DIALOG_ID);
            saveStats();
            onResume();
        }
    }

    /**
     * Post values to the given URL with registration ID
     *
     * @param url    The URL
     * @param values Values to include in POST
     * @return Body of the response
     */
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