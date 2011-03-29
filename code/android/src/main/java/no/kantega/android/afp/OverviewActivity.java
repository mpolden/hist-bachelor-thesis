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
import android.widget.*;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.AggregatedTag;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.utils.Register;

import java.util.List;

public class OverviewActivity extends ListActivity {

    private static final String TAG = OverviewActivity.class.getSimpleName();
    private static final String SENDER_ID = "androidafp@gmail.com";
    private Transactions db;
    private CategoryAdapter adapter;
    private Cursor cursor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        this.db = new Transactions(getApplicationContext());
        this.cursor = db.getCursorTags();
        this.adapter = new CategoryAdapter(this, cursor);
        setListAdapter(adapter);

        Button newTransactionButton = (Button) findViewById(R.id.button_new_transaction);
        newTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent().setClass(getApplicationContext(), AddTransactionActivity.class);
                startActivity(intent);

            }
        });

        Register.handleRegistration(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cursor = db.getCursorTags();
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

    private class CategoryAdapter extends CursorAdapter {

        public CategoryAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.overviewcategoryrow, parent, false);
            populateView(context, view, cursor);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            populateView(context, view, cursor);
        }

        private void populateView(Context context, View view, Cursor cursor) {
            String tag = cursor.getString(cursor.getColumnIndex("tag"));
            String consumption = cursor.getString(cursor.getColumnIndex("sum"));

            ImageView image = (ImageView) view.findViewById(R.id.overview_imageview_category);
            TextView tv_tag = (TextView) view.findViewById(R.id.overview_textview_tag);
            TextView tv_consumption = (TextView) view.findViewById(R.id.overview_textview_consumption);

            image.setImageDrawable(null);
            tv_tag.setText(null);
            tv_consumption.setText(null);

            if(tag != null) {
                tv_tag.setText(tag);
                image.setImageDrawable(getImageId(context, cursor));
            }

            if(consumption != null) {
                tv_consumption.setText(consumption);
            }
        }

        private Drawable getImageId(Context context, Cursor cursor) {
        final int imageId = cursor.getInt(cursor.getColumnIndex("imageId"));
        if (imageId > 0) {
            return context.getResources().getDrawable(imageId);
        }
        return null;
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
