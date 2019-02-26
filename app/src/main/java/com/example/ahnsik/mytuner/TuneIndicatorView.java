package com.example.ahnsik.mytuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TuneIndicatorView extends View {

    private Paint mPaint, mDetectedNote;
    private Typeface mBgText;

    private int arrowColor[] = {
            Color.LTGRAY, Color.LTGRAY, Color.LTGRAY, 0xFF000000, 0xFF000000, 0xFF000000
    };
    private float   arrowVerts[] = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
    private float   detectFreq;
    private float   idealFreq;
    private int     note;

    //  locally referenced tables.
    private final static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };

    public TuneIndicatorView(Context context) {
        super(context);
        init();
    }

    public TuneIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TuneIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mBgText = Typeface.createFromAsset(getContext().getAssets(), "coolvetica.ttf");
        mPaint.setTypeface(mBgText);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(3.0f);
        arrowColor[0] = arrowColor[1] = arrowColor[2] = 0xFF000000;
        mDetectedNote = new Paint(mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float   textSize = (float)getHeight()*3/4;
        mDetectedNote.setTextSize( textSize );
        mPaint.setTextSize( textSize );
    }

    public void setDetectFreq(float freq) {
        detectFreq = freq;
        note = FindNote.findIndex(detectFreq);
        idealFreq = FindNote.getCenterFreq(note_name[note]);
        //Log.d("ukulele", "recorded_freq: "+detectFreq+", Detect Note: "+note_name[note]);
        this.invalidate();
    }

    @Override
    protected void  onDraw(Canvas c) {
        float   start_offset;
        float   center = 240;       // 중앙좌표를 계산해야 함.
        float   grid = 40;       // 중앙좌표를 계산해야 함.
        float   textYpos;

        grid = (float)getWidth() / 24;
        textYpos = (float)getHeight()*3/4;   //120.0f;

        start_offset = (idealFreq-detectFreq)*grid;
        center = (float)getWidth() / 2 + start_offset;

        Log.d("ukulele", "Drawing...(width="+getWidth()+") [recorded_freq: "+detectFreq+", Detect Note: "+note_name[note]+"]");

        c.drawLine(center, 0.0f, center, 40.0f, mPaint);
        for (int i=1; i<20; i++) {
            c.drawLine(center - i*grid, 0.0f, center - i*grid, 20.0f, mPaint);
            c.drawLine(center + i*grid, 0.0f, center + i*grid, 20.0f, mPaint);
        }

        if ( (start_offset < 60)&&(start_offset > -60) ) {
            mDetectedNote.setColor(Color.GREEN);
        } else {
            mDetectedNote.setColor(Color.RED);
        }
        if (note > 2) {
            c.drawText(note_name[note-2], center-(grid*20), textYpos, mPaint );
            c.drawText(note_name[note-1], center-(grid*10), textYpos, mPaint );
            c.drawText(note_name[note], center, textYpos, mDetectedNote );
            c.drawText(note_name[note+1], center+(grid*10), textYpos, mPaint );
            c.drawText(note_name[note+2], center+(grid*20), textYpos, mPaint );
        }

        arrowColor[0] = arrowColor[1] = arrowColor[2] = Color.RED;
        center = (float)getWidth() / 2;
        arrowVerts[0] = center-grid;    arrowVerts[1] = 0;
        arrowVerts[2] = center;         arrowVerts[3] = 40;
        arrowVerts[4] = center+grid;    arrowVerts[5] = 0;
        c.drawVertices(Canvas.VertexMode.TRIANGLES, arrowVerts.length, arrowVerts, 0, null, 0, arrowColor,   0, null, 0, 0, mPaint);
    }
}
