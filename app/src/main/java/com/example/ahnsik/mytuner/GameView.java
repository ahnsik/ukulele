package com.example.ahnsik.mytuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public int width;
    public int height;
    private SurfaceHolder holder;
    private Thread  thread = null;

    public GameView (Context c) {
        super(c);
        Display d = ((WindowManager)c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = d.getWidth();
        height = d.getHeight();
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
    }


    public void surfaceCreated(SurfaceHolder h) {
        thread = new Thread(this);
        thread.start();
    }
    public void surfaceChanged(SurfaceHolder h, int a, int b, int c) {

    }
    public void surfaceDestroyed(SurfaceHolder h) {
//        if (thread != null) {
//            thread.stop();
//        }
    }

    public void run() {

        while(true) {
            Canvas c = null;

            try {
                c = holder.lockCanvas(null);
                synchronized (holder) {
                    onDraw(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    public void onDraw(Canvas c) {

    }

/*
    private boolean mRunning;
    private Context mContext;
    private Thread mGameThread = null;
    private Paint   paintColor, drawColor;
    private Bitmap bmpBackground;
    private long    systemClock;

    public GameView (Context context, AttributeSet attrs) {
        super(context, attrs);
Log.d("ukulele", "Trace #1.. GameView");
        paintColor = new Paint();
        paintColor.setColor(Color.RED);
        paintColor.setStrokeWidth (3.5f) ;
        drawColor = new Paint();
        drawColor.setColor(Color.rgb(40, 20, 10) );
        drawColor.setTextSize(48.2f); ;
        drawColor.setStrokeWidth (3.5f) ;
        bmpBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_paper);

        systemClock = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("ukulele", "Trace #6.. I'm drawing.");
        systemClock = System.currentTimeMillis();

        drawBackground( canvas );
        drawNoteSample(canvas);
    }

    public void drawBackground(Canvas canvas) {
        canvas.drawBitmap( bmpBackground, 0, 0, null);
//        canvas.drawRect(new Rect(10, 10, 1270, 710), paintColor );
        canvas.drawText("T", 40, 370, drawColor);
        canvas.drawText("A", 40, 430, drawColor);
        canvas.drawText("B", 40, 490, drawColor);
        canvas.drawLine( 20, 320, 1260, 320, drawColor );
        canvas.drawLine( 20, 380, 1260, 380, drawColor );
        canvas.drawLine( 20, 440, 1260, 440, drawColor );
        canvas.drawLine( 20, 500, 1260, 500, drawColor );
    }

    public void drawNoteSample(Canvas canvas) {
        canvas.drawText("1", 240, 340, drawColor);
        canvas.drawText("2", 240, 400, drawColor);
        canvas.drawText("3", 240, 460, drawColor);
        canvas.drawText("4", 240, 520, drawColor);
    }

//    private int mViewWidth;
//    private int mViewHeight;

*/
    public void pause() {
//        mRunning = false;
//        try {
//            // Stop the thread == rejoin the main thread.
//            mGameThread.join();
//        } catch (InterruptedException e) {
//        }
    }

    public void resume() {
//        mRunning = true;
//        mGameThread = new Thread(this);
//        mGameThread.start();
    }
}
