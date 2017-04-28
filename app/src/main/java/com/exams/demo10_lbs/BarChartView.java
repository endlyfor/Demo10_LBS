package com.exams.demo10_lbs;

/**
 * Created by fanxiaoli on 2017/3/30.
 */
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义组件：条形统计图
 * Created by hanj on 14-12-30.
 */
public class BarChartView extends View {
    private int screenW, screenH;

    private BarChartItemBean[] mItems;
    //max value in mItems.
    private float maxValue;
    //max height of the bar
    private int maxHeight;
       private int[] mBarColors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN};
  //  private int[] mBarColors = new int[]{Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE};

    private Paint barPaint, linePaint, textPaint;
    private Rect barRect, leftWhiteRect, rightWhiteRect;
    private Path textPath;

    private int leftMargin, topMargin, smallMargin;
    //the width of one bar item
    private int barItemWidth;
    //the spacing between two bar items.
    private int barSpace;
    //the width of the line.
    private int lineStrokeWidth;

    /**
     * The x-position of y-index and the y-position of the x-index..
     */
    private float x_index_startY, y_index_startX;

    private Bitmap arrowBmp;
    private Rect x_index_arrowRect, y_index_arrowRect;

    private static final int BG_COLOR = Color.parseColor("#E5E5E5");


    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }
    /*720*1280
    * */
    private void init(Context context) {
        screenW = ScreenUtils.getScreenW(context);//screenW = 720
        screenH = ScreenUtils.getScreenH(context);//1280
        if(screenW < screenH)
        {
            int i = screenH;
            screenH = screenW;
            screenW = i;
        }
        String str = String.valueOf(screenW);
        Log.i("fanxiaoli screenW ",str);

        leftMargin = ScreenUtils.dp2px(context, 16); //leftMargin = 32
        str = String.valueOf(leftMargin);
        Log.i("fanxiaoli leftMargin",str);
        topMargin = ScreenUtils.dp2px(context, 40);//topMargin 80
        str = String.valueOf(topMargin);
        Log.i("fanxiaoli topMargin",str);
        smallMargin = ScreenUtils.dp2px(context, 6);

        barPaint = new Paint();
        //barPaint.setColor(mBarColors[0]);

        linePaint = new Paint();
        lineStrokeWidth = ScreenUtils.dp2px(context, 1);
        linePaint.setStrokeWidth(lineStrokeWidth);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);

        barRect = new Rect(0, 0, 0, 0);
        textPath = new Path();

        leftWhiteRect = new Rect(0, 0, 0, screenH);
        rightWhiteRect = new Rect(screenW - leftMargin, 0, screenW, screenH);

        arrowBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_up);



    }

    //标记是否已经获取过状态拉的高度
    private boolean statusHeightHasGet;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!statusHeightHasGet) {
            subStatusBarHeight();
            statusHeightHasGet = true;
        }

        //draw background
        canvas.drawColor(BG_COLOR);

        //bounds
        checkLeftMoving();

        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 16));
        String str = String.valueOf(mItems.length);
        Log.i("mItems.length ",str);
        for (int i = 0; i < mItems.length; i++) {
            //draw bar rect
            barRect.left = (int) y_index_startX + barItemWidth * i + barSpace * (i + 1) - (int) leftMoving;
            barRect.top = topMargin * 2 + (int) (maxHeight * (1.0f - mItems[i].itemValue / maxValue));
            barRect.right = barRect.left + barItemWidth;

            barPaint.setColor(mBarColors[i % mBarColors.length]);
            canvas.drawRect(barRect, barPaint);

            //draw type text
            String typeText = mItems[i].itemType;
            float textPathStartX = barRect.left + barItemWidth / 2 -
                    (float) (Math.sin(Math.PI / 6)) * textPaint.measureText("0") / 2;
            float textPathStartY = barRect.bottom;
            String str1 = String.valueOf(textPathStartY);
            Log.i("textPathStartY",str1);
            textPath.reset();
            textPath.moveTo(textPathStartX, textPathStartY);
            textPath.lineTo(textPathStartX + (float) (1000 * Math.tan(Math.PI / 6)), textPathStartY + 1000);
            canvas.drawTextOnPath(typeText, textPath, smallMargin * 1.5f, smallMargin * 2, textPaint);

            //draw value text
            String valueText = String.valueOf(mItems[i].itemValue);
            canvas.drawText(valueText, barRect.left - (textPaint.measureText(valueText) - barItemWidth) / 2,
                    barRect.top - smallMargin, textPaint);
        }

        //draw left white space and right white space
        int c = barPaint.getColor();
        barPaint.setColor(BG_COLOR);
        leftWhiteRect.right = (int) y_index_startX;

        canvas.drawRect(leftWhiteRect, barPaint);
        canvas.drawRect(rightWhiteRect, barPaint);
        barPaint.setColor(c);

        //draw x-index line.
        canvas.drawLine(
                y_index_startX - lineStrokeWidth / 2,
                x_index_startY,
                screenW - leftMargin,
                x_index_startY,
                linePaint);
        //draw y-index line.
        canvas.drawLine(
                y_index_startX,
                x_index_startY + lineStrokeWidth / 2,
                y_index_startX,
                topMargin / 2,
                linePaint);


        canvas.drawBitmap(arrowBmp, null, y_index_arrowRect, null);
        canvas.save();
        canvas.rotate(90, (x_index_arrowRect.left + x_index_arrowRect.right) / 2, (x_index_arrowRect.top + x_index_arrowRect.bottom) / 2);
        canvas.drawBitmap(arrowBmp, null, x_index_arrowRect, null);
        canvas.restore();

        //draw division value
        int maxDivisionValueHeight = (int) (maxHeight * 1.0f / maxValue * maxDivisionValue);
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 15));
        float y30 = 0.0f;
        float y40 = 0.0f;
        float y50 = 0.0f;
        for (int i = 1; i <= 10; i++) {
            float startY = barRect.bottom - maxDivisionValueHeight * 0.1f * i;
            if (startY < topMargin / 2) {
                break;
            }
            canvas.drawLine(y_index_startX, startY, y_index_startX + 10, startY, linePaint);

            String text = String.valueOf(maxDivisionValue * 0.1 * i);
            canvas.drawText(text,
                    y_index_startX - textPaint.measureText(text) - 5,
                    startY + textPaint.measureText("0") / 2,
                    textPaint);//y的标度
///////////////////////////划线 需要调整格式/////////////
            canvas.drawLine(
                    y_index_startX,
                    startY,
                    screenW,
                    startY,
                    linePaint);
            /////////////////////////////////////////////
            if(i == 3)
            {
                y30 = startY;
                String str1 = String.valueOf(y30);
                Log.i(" y30",str1);

            }
            if(i == 4)
            {
                y40= startY;
                String str1 = String.valueOf(y40);
                Log.i(" y40",str1);
            }
            if(i == 5)
            {
                y50 = startY;
                String str1 = String.valueOf(y50);
                Log.i(" y50",str1);
            }

        }
        linePaint.setColor(Color.RED);
        canvas.drawLine(
                y_index_startX,
                (y30 + y40)/2,
                screenW,
                (y30 + y40)/2,
                linePaint);
        linePaint.setColor(Color.GREEN);
        canvas.drawLine(
                y_index_startX,
                (y40 + y50)/2,
                screenW,
                (y40 + y50)/2,
                linePaint);
        linePaint.setColor(Color.BLACK);

    }

    private float leftMoving;
    private float lastPointX;
    private float movingLeftThisTime = 0.0f;

    @Override
    public boolean onTouchEvent( MotionEvent event) {
        int type = event.getAction();

        switch (type) {
            case MotionEvent.ACTION_DOWN:
                lastPointX = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                movingLeftThisTime = lastPointX - x;

                leftMoving += movingLeftThisTime;
                lastPointX = x;

                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                //smooth scroll
                new Thread(new SmoothScrollThread(movingLeftThisTime)).start();
                break;

            default:
                return super.onTouchEvent(event);
        }

        return true;
    }

    /**
     * Check the value of leftMoving to ensure that the view is not out of the screen.
     */
    private void checkLeftMoving() {
        if (leftMoving < 0) {
            leftMoving = 0;
        }

        if (leftMoving > (maxRight - minRight)) {
            leftMoving = maxRight - minRight;
        }
    }

    public BarChartItemBean[] getItems() {
        return mItems;
    }


    public void setItems(BarChartItemBean[] items) {
        if (items == null) {
            throw new RuntimeException("BarChartView.setItems(): the param items cannot be null.");
        }
        if (items.length == 0) {
            return;
        }

        this.mItems = items;

        //Calculate the max value.
        maxValue = items[0].itemValue;
        for (BarChartItemBean bean : items) {
            if (bean.itemValue > maxValue) {
                maxValue = bean.itemValue;
            }
        }

        //Calculate the max division value.
        getRange(maxValue, 0);

        //Get the width of each bar.
        getBarItemWidth(screenW, items.length);

        //Refresh the view.
        invalidate();
    }

    private int maxRight, minRight;

    /**
     * Get the width of each bar which is depended on the screenW and item count.
     */
    private void getBarItemWidth(int screenW, int itemCount) {
        //The min width of the bar is 50dp.
        int minBarWidth = ScreenUtils.dp2px(getContext(), 20);
        //The min width of spacing.
        int minBarSpacing = ScreenUtils.dp2px(getContext(), 10);

        barItemWidth = (screenW - leftMargin * 2) / (itemCount + 3);
        barSpace = (screenW - leftMargin * 2 - barItemWidth * itemCount) / (itemCount + 1);

        if (barItemWidth < minBarWidth || barSpace < minBarSpacing) {
            barItemWidth = minBarWidth;
            barSpace = minBarSpacing;
        }

        maxRight = (int) y_index_startX + lineStrokeWidth + (barSpace + barItemWidth) * mItems.length;
        minRight = screenW - leftMargin - barSpace;
    }

    /**
     * Sub the height of status bar and action bar to get the accurate height of screen.
     */
    private void subStatusBarHeight() {
        //The height of the status bar
        int statusHeight = ScreenUtils.getStatusBarHeight((Activity) getContext());
        String str1 = String.valueOf(statusHeight);
        Log.i(" statusHeight",str1);
        //The height of the actionBar
      //  ActionBar ab = ((AppCompatActivity) getContext()).getSupportActionBar();
      //  int abHeight = ab == null ? 0 : ab.getHeight();
        int abHeight=96;
        str1 = String.valueOf(abHeight);
        Log.i(" abHeight",str1);
        screenH -= (statusHeight + abHeight);
        str1 = String.valueOf(screenH);
        Log.i(" screenH",str1);

        barRect.top = topMargin * 2;

        /*barRect.bottom = screenH - topMargin * 3;*/
        barRect.bottom = screenH - topMargin;// add 横屏以后空白太多topMargin=80
        maxHeight = barRect.bottom - barRect.top;

        x_index_startY = barRect.bottom;
        x_index_arrowRect = new Rect(screenW - leftMargin, (int) (x_index_startY - 10),
                screenW - leftMargin + 10, (int) (x_index_startY + 10));
    }

    //The max and min division value.
    private float maxDivisionValue, minDivisionValue;

    //Get the max and min division value by the max and min value in mItems.
    private void getRange(float maxValue, float minValue) {
        //max
        int scale = Utility.getScale(maxValue);//scale 1
        String str1 = String.valueOf(scale);
        Log.i("scale",str1);

        float unscaledValue = (float) (maxValue / Math.pow(6, scale)); //unscaledValue = 6
        str1 = String.valueOf(unscaledValue);
        Log.i(" unscaledValue",str1);

        maxDivisionValue = (float) (getRangeTop(unscaledValue) * Math.pow(10, scale));//maxDivisionValue 80
        str1 = String.valueOf(maxDivisionValue);
        Log.i(" maxDivisionValue",str1);
        y_index_startX = getDivisionTextMaxWidth(maxDivisionValue) + 10; //y_index_startX 40
        str1 = String.valueOf(y_index_startX);
        Log.i(" y_index_startX",str1);
        y_index_arrowRect = new Rect((int) (y_index_startX - 5), topMargin / 2 - 20,
                (int) (y_index_startX + 5), topMargin / 2);

    }

    private float getRangeTop(float value) {
        //value: [1,10)
        if(value < 1.2)
        if (value < 1.2) {
            return 1.2f;
        }

        if (value < 1.5) {
            return 1.5f;
        }

        if (value < 2.0) {
            return 2.0f;
        }

        if (value < 3.0) {
            return 3.0f;
        }

        if (value < 4.0) {
            return 4.0f;
        }

        if (value < 5.0) {
            return 5.0f;
        }

        if (value < 6.0|value == 6.0) {
            return 6.0f;
        }

        if (value < 8.0) {
            return 8.0f;
        }

        return 10.0f;
    }

    /**
     * Get the max width of the division value text.
     */
    private float getDivisionTextMaxWidth(float maxDivisionValue) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 15));

        float max = textPaint.measureText(String.valueOf(maxDivisionValue * 0.1f));
        for (int i = 2; i <= 10; i++) {
            float w = textPaint.measureText(String.valueOf(maxDivisionValue * 0.1f * i));
            if (w > max) {
                max = w;
            }
        }

        return max;
    }

    /**
     * Use this thread to create a smooth scroll after ACTION_UP.
     */
    private class SmoothScrollThread implements Runnable {
        float lastMoving;
        boolean scrolling = true;

        private SmoothScrollThread(float lastMoving) {
            this.lastMoving = lastMoving;
            scrolling = true;
        }

        @Override
        public void run() {
            while (scrolling) {
                long start = System.currentTimeMillis();
                lastMoving = (int) (0.9f * lastMoving);
                leftMoving += lastMoving;

                checkLeftMoving();
                postInvalidate();

                if (Math.abs(lastMoving) < 5) {
                    scrolling = false;
                }

                long end = System.currentTimeMillis();
                if (end - start < 20) {
                    try {
                        Thread.sleep(20 - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * A model class to keep the bar item info.
     */
    static class BarChartItemBean {
        private String itemType;
        private int itemValue;

        public BarChartItemBean(String itemType, int itemValue) {
            this.itemType = itemType;
            this.itemValue = itemValue;
        }
    }
}