package no.kantega.android.afp;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.utils.PieItem;
import no.kantega.android.afp.views.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChartActivity extends Activity {
    List<PieItem> PieData = new ArrayList<PieItem>(0);
    private Transactions db;
    private Cursor cursor;
    private int maxCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie);

        String month = getIntent().getExtras().getString("Month");
        String year = getIntent().getExtras().getString("Year");

        this.db = new Transactions(getApplicationContext());
        this.cursor = db.getCursorTags(month, year);
        createPieDataFromCursor();

        int overlayId = R.drawable.cam_overlay_big;

        int size = 480;

        int bgColor = 0xffa1a1a1;

        Bitmap backgroundImage = Bitmap.createBitmap(size, size+250, Bitmap.Config.RGB_565);

        PieChart pieChart = new PieChart(this);
        pieChart.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        pieChart.setGeometry(size, size, 5, 5, 5, 5, overlayId);
        pieChart.setSkinParams(bgColor);
        pieChart.setData(PieData, maxCount);
        pieChart.invalidate();

        pieChart.draw(new Canvas(backgroundImage));
        pieChart = null;

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setBackgroundColor(bgColor);
        imageView.setImageBitmap(backgroundImage);

        LinearLayout targetPieView = (LinearLayout) findViewById(R.id.pie_container);
        targetPieView.addView(imageView);
    }

    private void createPieDataFromCursor() {
        PieItem item;
        Random numGen = new Random();
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false) {
            item = new PieItem();
            item.Count = (int) cursor.getDouble(cursor.getColumnIndex("sum"));
            String tag = cursor.getString(cursor.getColumnIndex("tag"));
            item.setLabel(tag);
            item.Color = 0xff000000 + 256*256*numGen.nextInt(256) + 256*numGen.nextInt(256) + numGen.nextInt(256);
            PieData.add(item);
            maxCount += item.Count;
            cursor.moveToNext();
        }
        cursor.close();
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
