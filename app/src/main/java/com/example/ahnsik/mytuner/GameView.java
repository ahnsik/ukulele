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
