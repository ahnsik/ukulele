package com.example.ahnsik.mytuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

public class PlayView extends GameView {

    private Paint  pDefault;
    private Bitmap bmpBg;

    public PlayView(Context context) {
        super(context);

        pDefault = new Paint();
        pDefault.setColor(Color.rgb(90, 60, 4) );
        Typeface liryc = Typeface.createFromAsset(getContext().getAssets(), "DX_kyungpil.ttf");
        pDefault.setTypeface(liryc);
        pDefault.setTextSize(60.0f);

        bmpBg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_paper );
        Log.d("ukulele", "width="+ this.width + ", height=" + this.height );
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        if (bmpBg != null)
            canvas.drawBitmap(bmpBg, 0, 0, null);
        else
            Log.d("ukulele", "Can not draw Bitmap. because bitmap handle is null.");
//        canvas.drawBitmap(bmpBG, new Rect(0, 0, 1280, 720), new Rect(0, 0, 1280, 720), pDefault);
        canvas.drawRect(new Rect(30, 320, 1250, 324), pDefault );
        canvas.drawRect(new Rect(30, 380, 1250, 384), pDefault );
        canvas.drawRect(new Rect(30, 440, 1250, 444), pDefault );
        canvas.drawRect(new Rect(30, 500, 1250, 504), pDefault );

        Log.d("ukulele", "note.length=" );


    }

}




