package no.kantega.android.afp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import no.kantega.android.afp.R;
import no.kantega.android.afp.utils.FmtUtil;

import java.util.Date;

public class TransactionsAdapter extends CursorAdapter {

    public TransactionsAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.transactionrow, parent, false);
        populateView(context, view, getCursor());
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        populateView(context, view, getCursor());
    }

    private void populateView(Context context, View view, Cursor cursor) {
        String date = cursor.getString(cursor.getColumnIndex("date"));
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String tag = cursor.getString(cursor.getColumnIndex("tag"));
        Double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
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
            tv_text.setText(text);
        }
        if (tag != null) {
            tv_tag.setText(tag);
            image.setImageDrawable(getImageId(context, cursor));
        }
        if (amount != null) {
            tv_amount.setText(FmtUtil.currency(amount));
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
