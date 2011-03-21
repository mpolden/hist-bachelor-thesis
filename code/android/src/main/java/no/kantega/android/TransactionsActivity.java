package no.kantega.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.Transaction;
import no.kantega.android.utils.FmtUtil;

import java.util.Date;

public class TransactionsActivity extends ListActivity {

    private static final String TAG = OverviewActivity.class.getSimpleName();
    private Transactions db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);
        db = new Transactions(getApplicationContext());
        Cursor transactionsCursor = db.getCursor();
        startManagingCursor(transactionsCursor);
        String[] from = {"accountingDate", "text", "tag", "amountOut"};
        int[] to = {R.id.trow_tv_date, R.id.trow_tv_text, R.id.trow_tv_category, R.id.trow_tv_amount};
        this.setListAdapter(new TransactionsAdapter(this, R.layout.transactionrow, transactionsCursor, from, to));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selection = l.getItemAtPosition(position).toString();
        Log.i("SELECTION??", selection);
        Object o = l.getItemAtPosition(position);
        if (o instanceof Cursor) {
            Cursor cursor = (Cursor) o;
            int transaction_id = cursor.getInt(cursor.getColumnIndex("_id"));
            Transaction t = db.getById(transaction_id);
            Intent intent;
            if (t.isInternal()) {
                intent = new Intent(getApplicationContext(), EditTransactionActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), EditExternalTransactionActivity.class);
            }
            intent.putExtra("transaction", t);
            startActivity(intent);
        }
    }

    private class TransactionsAdapter extends SimpleCursorAdapter {

        private int layout;

        public TransactionsAdapter(Context context, int layout, Cursor cursor,
                                   String[] from, int[] to) {
            super(context, layout, cursor, from, to);
            this.layout = layout;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View v = inflater.inflate(layout, parent, false);
            populateView(v, cursor);
            return v;
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            populateView(v, c);
        }

        private void populateView(View v, Cursor c) {
            String date = c.getString(c.getColumnIndex("accountingDate"));
            String text = c.getString(c.getColumnIndex("text"));
            String tag = c.getString(c.getColumnIndex("tag"));
            String amount = c.getString(c.getColumnIndex("amountOut"));
            ImageView image = (ImageView) v.findViewById(R.id.tag_icon);
            TextView tv_date = (TextView) v.findViewById(R.id.trow_tv_date);
            TextView tv_text = (TextView) v.findViewById(R.id.trow_tv_text);
            TextView tv_tag = (TextView) v.findViewById(R.id.trow_tv_category);
            TextView tv_amount = (TextView) v.findViewById(R.id.trow_tv_amount);
            if (tv_date != null) {
                Date d = FmtUtil.stringToDate("yyyy-MM-dd HH:mm:ss", date);
                tv_date.setText(FmtUtil.dateToString("yyyy-MM-dd", d));
            }
            if (text != null) {
                tv_text.setText(FmtUtil.trimTransactionText(text));
            }
            if (tag != null) {
                tv_tag.setText(tag);
                image.setImageDrawable(getImageIdByTag(tag));
            }
            if (amount != null) {
                tv_amount.setText(amount);
            }
        }
    }

    private Drawable getImageIdByTag(String tag) {
        if ("Ferie".equals(tag)) {
            return getResources().getDrawable(R.drawable.suitcase);
        } else if ("Klær".equals(tag)) {
            return getResources().getDrawable(R.drawable.tshirt);
        } else if ("Restaurant".equals(tag)) {
            return getResources().getDrawable(R.drawable.forkknife);
        } else if ("Dagligvarer".equals(tag)) {
            return getResources().getDrawable(R.drawable.chicken);
        } else if ("Bil".equals(tag)) {
            return getResources().getDrawable(R.drawable.fuel);
        } else if ("Vin".equals(tag)) {
            return getResources().getDrawable(R.drawable.winebottle);
        } else if ("Datautstyr".equals(tag)) {
            return getResources().getDrawable(R.drawable.imac);
        } else if ("Overtidsmiddag".equals(tag)) {
            return getResources().getDrawable(R.drawable.forkknife);
        } else {
            return getResources().getDrawable(R.drawable.user);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
