package no.kantega.android.afp;

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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.utils.FmtUtil;

import java.util.Date;

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

    public Transactions getDb() {
        return db;
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

    private class TransactionsAdapter extends CursorAdapter {

        public TransactionsAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.transactionrow, parent, false);
            populateView(view, getCursor());
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            populateView(view, getCursor());
        }

        private void populateView(View view, Cursor cursor) {
            String date = cursor.getString(cursor.getColumnIndex("accountingDate"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String tag = cursor.getString(cursor.getColumnIndex("tag"));
            String amount = cursor.getString(cursor.getColumnIndex("amountOut"));
            ImageView image = (ImageView) view.findViewById(R.id.tag_icon);
            TextView tv_date = (TextView) view.findViewById(R.id.trow_tv_date);
            TextView tv_text = (TextView) view.findViewById(R.id.trow_tv_text);
            TextView tv_tag = (TextView) view.findViewById(R.id.trow_tv_category);
            TextView tv_amount = (TextView) view.findViewById(R.id.trow_tv_amount);
            tv_date.setText(null);
            tv_text.setText(null);
            tv_tag.setText(null);
            image.setImageDrawable(null);
            tv_amount.setText(null);
            if (date != null) {
                Date d = FmtUtil.stringToDate("yyyy-MM-dd HH:mm:ss", date);
                tv_date.setText(FmtUtil.dateToString("yyyy-MM-dd", d));
            }
            if (text != null) {
                tv_text.setText(FmtUtil.trimTransactionText(text));
            }
            if (tag != null) {
                tv_tag.setText(tag);
                image.setImageDrawable(getImageId(cursor));
            }
            if (amount != null) {
                tv_amount.setText(amount);
            }
        }
    }

    private Drawable getImageId(Cursor cursor) {
        final int imageId = cursor.getInt(cursor.getColumnIndex("imageId"));
        if (imageId > 0) {
            return getResources().getDrawable(imageId);
        }
        return null;
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
