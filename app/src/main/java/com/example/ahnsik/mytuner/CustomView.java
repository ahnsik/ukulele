package com.example.ahnsik.mytuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CustomView extends ImageView {

    Paint   myPaint;
    Bitmap  myBitmap;

    public CustomView(Context context) {
        super(context);
        commonInitialize();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        commonInitialize();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInitialize();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        commonInitialize();
    }

    private void commonInitialize() {
        //myBitmap = new Bitmap();
        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
    }

    private int xpos = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawText("Nothing", xpos++,180, 24.5f, 12.8f, myPaint);
        canvas.drawRect( new Rect(xpos++, 120, 120, 30), myPaint);
        if (xpos>300)   xpos=0;
        invalidate();
    }
}
