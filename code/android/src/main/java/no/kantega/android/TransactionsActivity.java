package no.kantega.android;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import no.kantega.android.controllers.Transactions;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;
import no.kantega.android.utils.FmtUtil;

import java.util.ArrayList;

public class TransactionsActivity extends ListActivity implements GestureDetector.OnGestureListener {

    private final int SWIPE_MIN_DISTANCE = 120;
    private final int SWIPE_MAX_OFF_PATH = 250;
    private final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector detector;

    private ViewFlipper viewFlipper;

    private static final String TAG = OverviewActivity.class.getSimpleName();
    private Transactions db;
    private ProgressDialog progressDialog;
    private ArrayList<Transaction> transactions;
    private OrderAdapter listAdapter;
    private Runnable viewOrders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);       ;

        detector = new GestureDetector(this, this);

        this.db = new Transactions(getApplicationContext());
        transactions = new ArrayList<Transaction>();
        listAdapter = new OrderAdapter(this, R.layout.transactionrow, transactions);
        setListAdapter(listAdapter);
        //refreshList();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.detector.onTouchEvent(motionEvent)) {
            return true;
        }
        //no gesture detected, let Activity handle touch event
        return super.onTouchEvent(motionEvent);
    }

    private void refreshList() {
        viewOrders = new Runnable() {
            @Override
            public void run() {
                getTransactions();
            }
        };
        Thread thread = new Thread(null, viewOrders);
        thread.start();
        progressDialog = ProgressDialog.show(TransactionsActivity.this,
                getResources().getString(R.string.please_wait),
                getResources().getString(R.string.retrieving_date),
                true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long transactionCount = db.getCount();
        //if (transactions.size() < transactionCount) {
        refreshList();
        //transactions = new ArrayList<Transaction>(db.get(7));
        //listAdapter.notifyDataSetChanged();
        //}
    }


    private void getTransactions() {
        try {
            transactions = new ArrayList<Transaction>(db.get(100));
            //Thread.sleep(2000);
            Log.i("ARRAY", "" + transactions.size());
        } catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }
        runOnUiThread(returnRes);
    }

    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            if (transactions != null && transactions.size() > 0) {
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
                /*for (int i = 0; i < transactions.size(); i++) {
                    listAdapter.add(transactions.get(i));

                }*/
                for (int i = 0; i < 5; i++) {
                    listAdapter.add(transactions.get(i));
                }
            }
            progressDialog.dismiss();
            listAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selection = l.getItemAtPosition(position).toString();
        Intent intent = null;
        Object o = l.getItemAtPosition(position);
        if (o instanceof Transaction) {
            Transaction t = (Transaction) o;
            if (t.isInternal()) {
                intent = new Intent(getApplicationContext(), EditTransactionActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), EditExternalTransactionActivity.class);
            }
            intent.putExtra("transaction", t);
            startActivity(intent);
            //transactions.get(0).setAmountOut(20000.00);
            //listAdapter.notifyDataSetChanged();
        }
    }

    private class OrderAdapter extends ArrayAdapter<Transaction> {

        private ArrayList<Transaction> items;

        public OrderAdapter(Context context, int textViewResourceId,
                            ArrayList<Transaction> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.transactionrow, null);
            }
            Transaction t = items.get(position);
            if (t != null) {
                ImageView image = (ImageView) v.findViewById(R.id.tag_icon);
                TextView date = (TextView) v.findViewById(R.id.trow_tv_date);
                TextView text = (TextView) v.findViewById(R.id.trow_tv_text);
                TextView category = (TextView) v.findViewById(R.id.trow_tv_category);
                TextView amount = (TextView) v.findViewById(R.id.trow_tv_amount);
                if (date != null) {
                    date.setText(FmtUtil.dateToString("yyyy-MM-dd", t.getAccountingDate()));
                }
                if (text != null) {
                    text.setText(FmtUtil.trimTransactionText(t.getText()));
                }
                if (category != null && t.getTag() != null) {
                    category.setText(t.getTag().getName());
                    image.setImageDrawable(getImageIdByTag(t.getTag()));
                }
                if (amount != null) {
                    amount.setText(String.valueOf(t.getAmountOut()));
                }
            }
            return v;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("---onFling---", e1.toString() + e2.toString());
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(inFromRightAnimation());
                viewFlipper.setOutAnimation(outToLeftAnimation());
                viewFlipper.showNext();
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(inFromLeftAnimation());
                viewFlipper.setInAnimation(outToRightAnimation());
                viewFlipper.showPrevious();
            }
        } catch (Exception e) {

        }
        return false;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("---onSingleTapUp---", e.toString());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("---onScroll---", e1.toString() + e2.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("---onShowPress---", e.toString());
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("---onLongPress---", e.toString());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // TODO Auto-generated method stub
        //Log.d("soydan", String.valueOf(deb++));
        detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    private Drawable getImageIdByTag(TransactionTag tag) {
        if ("Ferie".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.suitcase);
        } else if ("Klær".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.tshirt);
        } else if ("Restaurant".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.forkknife);
        } else if ("Dagligvarer".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.chicken);
        } else if ("Bil".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.fuel);
        } else if ("Vin".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.winebottle);
        } else if ("Datautstyr".equals(tag.getName())) {
            return getResources().getDrawable(R.drawable.imac);
        } else if ("Overtidsmiddag".equals(tag.getName())) {
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


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
}
