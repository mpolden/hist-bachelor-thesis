package no.kantega.android;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.DatabaseHelper;
import no.kantega.android.utils.DatabaseOpenHelper;
import no.kantega.android.utils.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class SynchronizeActivity extends Activity {

    private static final String TAG = SynchronizeActivity.class.getSimpleName();
    private DatabaseOpenHelper helper;
    private SQLiteDatabase db;
    private OnClickListener syncButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            populateDatabase();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronize);
        ImageButton syncButton = (ImageButton) findViewById(R.id.syncButton);
        syncButton.setImageResource(R.drawable.syncbutton);
        syncButton.setOnClickListener(syncButtonListener);
        helper = new DatabaseOpenHelper(getApplicationContext());
        db = helper.getWritableDatabase();
    }

    private void populateDatabase() {
        try {
            InputStream inputStream = getAssets().open("url.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            new TransactionsTask().execute(
                    properties.get("allTransactions").toString());
        } catch (IOException e) {
            Log.e(TAG, "Could not read properties file", e);
        }
    }

    private class TransactionsTask
            extends AsyncTask<String, Integer, List<Transaction>> {

        @Override
        protected List<Transaction> doInBackground(String... urls) {
            return GsonUtil.parseTransactions(GsonUtil.getJSON(urls[0]));
        }

        @Override
        protected void onPostExecute(List<Transaction> transactions) {
            if (transactions != null && !transactions.isEmpty()) {
                DatabaseHelper.emptyTables(db);
                for (Transaction t : transactions) {
                    DatabaseHelper.insert(db, t);
                }
                Toast.makeText(getApplicationContext(), "Synchronized database",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}