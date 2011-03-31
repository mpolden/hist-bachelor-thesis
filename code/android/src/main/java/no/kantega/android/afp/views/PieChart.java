package no.kantega.android.afp.views;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.EventLogTags;
import android.view.View;
import no.kantega.android.afp.utils.PieItem;

import java.text.DecimalFormat;
import java.util.List;

public class PieChart extends View {
    private static final int WAIT = 0;
    private static final int IS_READY_TO_DRAW = 1;
    private static final int IS_DRAW = 2;
    private static final float START_INC = 30;
    private Paint bgPaints = new Paint();
    private Paint linePaints = new Paint();
    private Paint textPaints = new Paint();
    private String tag = null;
    private int overlayId;
    private int width;
    private int height;
    private int gapLeft;
    private int gapRight;
    private int gapTop;
    private int gapBottom;
    private int bgColor;
    private int DESCRIPTION_MARGIN_LEFT = 75;
    private int DESCRIPTION_MARGIN_TOP = 50;
    private int COLUMN_MARGIN_WIDTH = 150;
    private int DESCRIPTION_CIRCLE_RADIUS = 10;
    private int CIRCLE_MARGIN_LEFT = 15;
    private int state = WAIT;
    private float start;
    private float sweep;
    private int maxConnection;
    private List<PieItem> dataArray;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (state != IS_READY_TO_DRAW) return;
        canvas.drawColor(bgColor);

        bgPaints.setAntiAlias(true);
        bgPaints.setStyle(Paint.Style.FILL);
        bgPaints.setColor(0x88FF0000);
        bgPaints.setStrokeWidth(0.5f);

        linePaints.setAntiAlias(true);
        linePaints.setStyle(Paint.Style.STROKE);
        linePaints.setColor(0xff000000);
        linePaints.setStrokeWidth(0.5f);
        linePaints.setTextSize(15);

        textPaints.setAntiAlias(true);
        textPaints.setStyle(Paint.Style.FILL_AND_STROKE);       
        textPaints.setStrokeWidth(0.5f);
        textPaints.setColor(Color.BLACK);
        textPaints.setTextSize(20);

        RectF ovals = new RectF(gapLeft, gapTop, width - gapRight, height - gapBottom);

        start = START_INC;
        float lblX;
        float lblY;
        String LblPercent;
        float Percent;
        DecimalFormat FloatFormatter = new DecimalFormat("0.## %");
        float CenterOffset = (width / 2); // Pie Center from Top-Left origin
        float Conv = (float) (2 * Math.PI / 360);     // Constant for convert Degree to rad.
        float Radius = 2 * (width / 2) / 3;     // Radius of the circle will be drawn the legend.
        Rect bounds = new Rect();
        PieItem item;
        for (int i = 0; i < dataArray.size(); i++) {
            item = (PieItem) dataArray.get(i);
            bgPaints.setColor(item.Color);
            sweep = (float) 360 * ((float) item.Count / (float) maxConnection);
            canvas.drawArc(ovals, start, sweep, true, bgPaints);
            canvas.drawArc(ovals, start, sweep, true, linePaints);
            Percent = (float) item.Count / (float) maxConnection;
            sweep = (float) 360 * Percent;
            // Format Label
            LblPercent = FloatFormatter.format(Percent);
            // Get Label width and height in pixels
            linePaints.getTextBounds(LblPercent, 0, LblPercent.length(), bounds);
            // Claculate final coords for Label
            lblX = (float) ((float) CenterOffset + Radius * Math.cos(Conv * (start + sweep / 2))) - bounds.width() / 2;
            lblY = (float) ((float) CenterOffset + Radius * Math.sin(Conv * (start + sweep / 2))) + bounds.height() / 2;
            // Dwraw Label on Canvas
            canvas.drawText(LblPercent, lblX, lblY, textPaints);
            canvas.drawCircle(DESCRIPTION_MARGIN_LEFT, height + DESCRIPTION_MARGIN_TOP, DESCRIPTION_CIRCLE_RADIUS, bgPaints);
            tag = item.getLabel();

            if (tag == null) {
                tag = "Ukategorisert";
            }
            if (i % 2 == 0) {
                canvas.drawText(tag, DESCRIPTION_MARGIN_LEFT + CIRCLE_MARGIN_LEFT, height + DESCRIPTION_MARGIN_TOP, textPaints);
                DESCRIPTION_MARGIN_LEFT += COLUMN_MARGIN_WIDTH;
            } else {
                canvas.drawText(tag, DESCRIPTION_MARGIN_LEFT + CIRCLE_MARGIN_LEFT, height + DESCRIPTION_MARGIN_TOP, textPaints);
                DESCRIPTION_MARGIN_TOP += 40;
                DESCRIPTION_MARGIN_LEFT -= COLUMN_MARGIN_WIDTH;
            }


            start += sweep;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap OverlayBitmap = BitmapFactory.decodeResource(getResources(), overlayId, options);
        int overlay_width = OverlayBitmap.getWidth();
        int overlay_height = OverlayBitmap.getHeight();
        float scaleWidth = (((float) width) / overlay_width) * 0.678899083f;
        float scaleHeight = (((float) height) / overlay_height) * 0.678899083f;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(OverlayBitmap, 0, 0, overlay_width, overlay_height, matrix, true);

        //canvas.drawBitmap(resizedBitmap, 0.0f, 0.0f, null);

        state = IS_DRAW;
    }

    public void setGeometry(int width, int height, int gapLeft, int gapRight, int gapTop, int gapBottom, int overlayId) {
        this.width = width;
        this.height = height;
        this.gapLeft = gapLeft;
        this.gapRight = gapRight;
        this.gapTop = gapTop;
        this.gapBottom = gapBottom;
        this.overlayId = overlayId;
    }

    public void setSkinParams(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setData(List<PieItem> data, int maxConnection) {
        dataArray = data;
        this.maxConnection = maxConnection;
        state = IS_READY_TO_DRAW;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getColorValue(int index) {
        if (dataArray == null) return 0;
        if (index < 0) {
            return ((PieItem) dataArray.get(0)).Color;
        } else if (index >= dataArray.size()) {
            return ((PieItem) dataArray.get(dataArray.size() - 1)).Color;
        } else {
            return ((PieItem) dataArray.get(dataArray.size() - 1)).Color;
        }
    }

}
