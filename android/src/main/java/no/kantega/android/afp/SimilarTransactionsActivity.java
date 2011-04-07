package no.kantega.android.afp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.utils.FmtUtil;

import java.util.Date;
import java.util.List;

/**
 * This activity handles similar transactions
 */
public class SimilarTransactionsActivity extends ListActivity {

    private static final String TAG = SimilarTransactionsActivity.class.getSimpleName();
    private Transactions db;
    private SimilarTransactionAdapter adapter;
    private List<Transaction> similarTransactions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        this.db = new Transactions(getApplicationContext());
        //this.adapter = new TransactionsAdapter(this, cursor, R.layout.similartransactionrow);
        String transactionText = getIntent().getExtras().getString("text");
        int excludeId = getIntent().getExtras().getInt("excludeId");
        similarTransactions = db.getSimilarByText(FmtUtil.firstWord(transactionText), transactionText, excludeId);
        this.adapter = new SimilarTransactionAdapter(this, R.layout.transactionrow, similarTransactions);
        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private class SimilarTransactionAdapter extends ArrayAdapter<Transaction> {

        private List<Transaction> items;

        public SimilarTransactionAdapter(Context context, int textViewResourceId, List<Transaction> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.similartransactionrow, null);
            }
            Transaction t = items.get(position);
            if (t != null) {
                TextView tv_date = (TextView) v.findViewById(R.id.trow_tv_date);
                TextView tv_text = (TextView) v.findViewById(R.id.trow_tv_text);
                TextView tv_tag = (TextView) v.findViewById(R.id.trow_tv_category);
                TextView tv_amount = (TextView) v.findViewById(R.id.trow_tv_amount);
                tv_date.setText(null);
                tv_text.setText(null);
                tv_tag.setText(null);
                tv_amount.setText(null);
                if (t.getDate() != null) {
                    Date d = t.getDate();
                    tv_date.setText(FmtUtil.dateToString("yyyy-MM-dd", d));
                }
                if (t.getText() != null) {
                    tv_text.setText(t.getText());
                }
                if (t.getTag() != null) {
                    tv_tag.setText(t.getTag().getName());
                } else {
                    tv_tag.setText(R.string.not_tagged);
                }
                if (t.getAmount() != 0) {
                    tv_amount.setText(FmtUtil.currencyWithoutPrefix(t.getAmount()));
                }
            }
            return v;
        }
    }
}