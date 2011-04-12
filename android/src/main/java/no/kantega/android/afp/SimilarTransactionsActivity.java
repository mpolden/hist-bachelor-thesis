package no.kantega.android.afp;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import no.kantega.android.afp.utils.FmtUtil;

import java.util.*;

/**
 * This activity handles similar transactions
 */
public class SimilarTransactionsActivity extends ListActivity {

    private static final String TAG = SimilarTransactionsActivity.class.getSimpleName();
    private static final int PROGRESS_DIALOG_ID = 0;
    private int selectedCount;
    private TextView tvSelectedCount;
    private Transactions db;
    private SimilarTransactionAdapter adapter;
    private ProgressDialog progressDialog;
    private Transaction t;
    private List<Transaction> similarTransactions;
    private Map<Integer, TransactionTag> suggestions;
    private final View.OnClickListener saveTransactionsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new UpdateTask().execute();
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.similartransactions);
        this.db = new Transactions(getApplicationContext());
        this.suggestions = (HashMap<Integer, TransactionTag>) getIntent().getExtras().get("suggestions");
        this.t = (Transaction) getIntent().getExtras().get("transaction");
        if (this.t != null) {
            this.similarTransactions = db.getSimilarByText(String.format("%s %%", FmtUtil.firstWord(t.getText())),
                    t.getText(), t.get_id());
        } else {
            final String month = getIntent().getExtras().get("month").toString();
            final String year = getIntent().getExtras().get("year").toString();
            this.similarTransactions = db.getUntagged(month, year);
            // Remove transactions that we didn't find a tag for
            final List<Transaction> remove = new ArrayList<Transaction>();
            for (Transaction aTransaction : similarTransactions) {
                if (!suggestions.containsKey(aTransaction.get_id())) {
                    remove.add(aTransaction);
                }
            }
            similarTransactions.removeAll(remove);
        }
        this.adapter = new SimilarTransactionAdapter(this, similarTransactions);
        setListAdapter(adapter);
        Button saveButton = (Button) findViewById(R.id.button_save_transactions);
        saveButton.setOnClickListener(saveTransactionsButtonListener);
        this.tvSelectedCount = (TextView) findViewById(R.id.tv_similarselected);
        this.selectedCount = similarTransactions.size();
        updateSelectedCount();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Transaction transaction = (Transaction) l.getItemAtPosition(position);
        if (transaction.isChecked()) {
            setTransactionChecked(transaction, false);
            selectedCount--;
        } else {
            setTransactionChecked(transaction, true);
            selectedCount++;
        }
        updateSelectedCount();
        adapter.notifyDataSetChanged();
    }

    /**
     * Set check state for a transaction and update the visible tag
     *
     * @param transaction The transaction to change
     * @param checked     True if transaction should be checked
     */
    private void setTransactionChecked(final Transaction transaction, final boolean checked) {
        transaction.setChecked(checked);
        if (checked) {
            if (suggestions != null) {
                transaction.setTag(suggestions.get(transaction.get_id()));
            } else {
                transaction.setTag(t.getTag());
            }
        } else {
            transaction.setTag(new TransactionTag(getResources().getString(R.string.not_tagged)));
        }
    }

    /**
     * Update selected count
     */
    private void updateSelectedCount() {
        tvSelectedCount.setText(String.format(getResources().getString(R.string.selected), selectedCount,
                similarTransactions.size()));
    }

    /**
     * Select all transactions in list
     */
    private void selectAll() {
        for (Transaction transaction : similarTransactions) {
            setTransactionChecked(transaction, true);
        }
        adapter.notifyDataSetChanged();
        selectedCount = similarTransactions.size();
    }

    /**
     * Unselect all transactions in list
     */
    private void unselectAll() {
        for (Transaction transaction : similarTransactions) {
            setTransactionChecked(transaction, false);
        }
        adapter.notifyDataSetChanged();
        selectedCount = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.similartransactionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_select_all: {
                selectAll();
                updateSelectedCount();
                return true;
            }
            case R.id.menu_deselect_all: {
                unselectAll();
                updateSelectedCount();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG_ID: {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getResources().getString(
                        R.string.please_wait));
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

    private final Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.setProgress(msg.arg1);
        }
    };

    /**
     * This task handles batch updates of tags
     */
    private class UpdateTask extends AsyncTask<Object, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            showDialog(PROGRESS_DIALOG_ID);
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            final List<Transaction> changed = new ArrayList<Transaction>();
            for (Transaction transaction : similarTransactions) {
                if (transaction.isChecked()) {
                    changed.add(transaction);
                }
            }
            progressDialog.setMax(changed.size());
            int i = 0;
            for (Transaction toUpdate : changed) {
                if (t != null) {
                    toUpdate.setTag(t.getTag());
                } else {
                    toUpdate.setTag(suggestions.get(toUpdate.get_id()));
                }
                toUpdate.setDirty(true);
                db.update(toUpdate);
                publishProgress(++i);
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            final Message msg = progressHandler.obtainMessage();
            msg.arg1 = values[0];
            progressHandler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dismissDialog(PROGRESS_DIALOG_ID);
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * Custom adapter for similar transaction with checkbox
     */
    private class SimilarTransactionAdapter extends ArrayAdapter<Transaction> {

        private final List<Transaction> items;

        /**
         * Create a new adapter for the given items
         *
         * @param context Application context
         * @param items   Transactions
         */
        public SimilarTransactionAdapter(Context context, List<Transaction> items) {
            super(context, R.layout.transactionrow, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.similartransactionrow, null);
            }
            Transaction transaction = items.get(position);
            if (transaction != null) {
                TextView tv_date = (TextView) v.findViewById(R.id.trow_tv_date);
                TextView tv_text = (TextView) v.findViewById(R.id.trow_tv_text);
                TextView tv_tag = (TextView) v.findViewById(R.id.trow_tv_category);
                TextView tv_amount = (TextView) v.findViewById(R.id.trow_tv_amount);
                CheckBox bCheck = (CheckBox) v.findViewById(R.id.checkbox_similartransaction);
                bCheck.setChecked(transaction.isChecked());
                tv_date.setText(null);
                tv_text.setText(null);
                tv_tag.setText(null);
                tv_amount.setText(null);
                if (transaction.getDate() != null) {
                    Date d = transaction.getDate();
                    tv_date.setText(FmtUtil.dateToString("yyyy-MM-dd", d));
                }
                if (transaction.getText() != null) {
                    tv_text.setText(transaction.getText());
                }
                if (transaction.getTag() != null) {
                    tv_tag.setText(transaction.getTag().getName());
                } else if (bCheck.isChecked()) {
                    if (t != null) {
                        tv_tag.setText(t.getTag().getName());
                    } else {
                        tv_tag.setText(suggestions.get(transaction.get_id()).getName());
                    }
                } else {
                    tv_tag.setText(R.string.not_tagged);
                }
                if (transaction.getAmount() != 0) {
                    tv_amount.setText(FmtUtil.currencyWithoutPrefix(transaction.getAmount()));
                }
            }
            return v;
        }
    }
}