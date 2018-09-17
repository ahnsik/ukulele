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

    private final static int    PLAYING_POSITION=460;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,

    private Paint  pText, pBG, pCursor;
    private Bitmap bmpBg, bmpFinger;

    private final static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };


    public PlayView(Context context) {
        super(context);
        resource_load();
    }

    private void resource_load() {
        // 전체적으로 기본적으로 사용될 Font 및 기본 색상 등을 지정.
        pText = new Paint();
        Typeface liryc = Typeface.createFromAsset(getContext().getAssets(), "DX_kyungpil.ttf");
        pText.setColor(Color.rgb(90, 60, 4) );
        pText.setTypeface(liryc);
        pText.setTextSize(60.0f);

        // 배경을 그리는 데 사용될 색상 및 Font를 지정.
        pBG = new Paint();
        Typeface bgText = Typeface.createFromAsset(getContext().getAssets(), "coolvetica.ttf");
        pBG.setColor(Color.rgb(120, 80, 4) );    // 오선지(X) TAB악보 라인 의 색상 / Font색상.
        pBG.setTypeface(bgText);
        pBG.setTextSize(60.0f);     // T.A.B  on 탭악보 위의 TAB 글자.

        pCursor = new Paint(pBG);
        pCursor .setColor(Color.rgb(209, 73, 46) );    // 커서 색상은 빨간색

        bmpBg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_paper );
        bmpFinger = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger_guide );

        Log.d("ukulele", "width="+ this.width + ", height=" + this.height );
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        drawPaper(canvas);
        drawInfo(canvas);
        drawScore(canvas);
        drawChord(canvas);
        drawLyric(canvas);
        // ......
        drawCursor(canvas, 32);
        drawWholeNotes(canvas);
    }


    private final static int    LINE_Y=320;        // TAB악보 가로라인의 세로위치

    private void drawPaper(Canvas canvas) {
        if (bmpBg != null)
            canvas.drawBitmap(bmpBg, 0, 0, null);
        else {
            Paint pPaper = new Paint(pBG);
            pPaper.setColor(Color.rgb(234, 193, 122));    // 오선지(X) TAB악보 라인 의 색상 / Font색상.
            canvas.drawRect(new Rect(0, 0, 1280, 720), pPaper);
            Log.d("ukulele", "Can not draw Bitmap. because bitmap handle is null.");
        }

        canvas.drawRect(new Rect(30, LINE_Y+0, 1250, LINE_Y+4), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+60, 1250, LINE_Y+64), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+120, 1250, LINE_Y+124), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+180, 1250, LINE_Y+184), pBG);
        canvas.drawText("T",40,LINE_Y+50, pBG);
        canvas.drawText("A",38,LINE_Y+110, pBG);
        canvas.drawText("B",40,LINE_Y+170, pBG);

        if (bmpFinger != null) {
            canvas.drawBitmap(bmpFinger, 1130, 540, null);
        }
    }

    private void drawCursor(Canvas canvas, int beat_offset) {
        canvas.drawText("▼",PLAYING_POSITION-28,LINE_Y-beat_offset, pCursor);
        canvas.drawText("▲",PLAYING_POSITION-28,LINE_Y+222+beat_offset, pCursor);
        canvas.drawRect(new Rect(PLAYING_POSITION-5, 300, PLAYING_POSITION+5, 520), pCursor );       // 플레이 위치 가이드.
    }


    private final static int    TITLE_POSITION_Y=70;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static int    SCORE_POSITION_X=920;

    private void drawInfo(Canvas canvas) {
        Paint  pTitle = new Paint(pText);
        pTitle.setTextSize(60.0f);
        canvas.drawText("바보같은 노래 제목. - Dummy Title",12,TITLE_POSITION_Y, pTitle);
    }

    private void drawScore(Canvas canvas) {
        Paint  pScore= new Paint(pBG);

        Rect eraserRect = new Rect(SCORE_POSITION_X, TITLE_POSITION_Y-58, 1280, TITLE_POSITION_Y+34);
        canvas.drawBitmap(bmpBg, eraserRect, eraserRect, null );

        pScore.setTextSize(48.0f);
        canvas.drawText("Score:" + 38382929, SCORE_POSITION_X, TITLE_POSITION_Y-12, pScore);
        pScore.setTextSize(24.0f);  // 디버깅용 clock offset
        canvas.drawText("clock:" + 38382929, SCORE_POSITION_X+40, TITLE_POSITION_Y+24, pScore);
    }


    private final static int    CHORD_POSITION_Y=LINE_Y-60;

    private void drawChord(Canvas canvas) {
        Paint  pChordFont= new Paint(pBG);
        pChordFont.setTextSize(48.0f);
        pChordFont.setColor(Color.rgb(90, 60, 4) );
        canvas.drawText("B   F      G7  Cm    A", PLAYING_POSITION+40, CHORD_POSITION_Y, pChordFont);
    }

    private final static int    LYRIC_POSITION_Y=LINE_Y+260;

    private void drawLyric(Canvas canvas) {
        Paint  pLyricFont= new Paint(pText);
        pLyricFont.setTextSize(48.0f);
        canvas.drawText("후루이   아루 바 무  메구리~", PLAYING_POSITION-240, LYRIC_POSITION_Y, pLyricFont);
    }


    private void drawWholeNotes(Canvas canvas) {
        Paint  onColor, offColor, dispColor;
        onColor = new Paint(pBG);
        onColor.setTextSize(20.0f);
        onColor.setColor(Color.rgb(90, 60, 4));   // 잘 보이는 짙은 색상
        offColor = new Paint(onColor);
        offColor.setColor(Color.rgb(160, 110,24));  // 잘 안 보이는 옅은 색상

//        if (display_notes==null) {
//            return;
//        }
        int yoffset=0;
        int  length = note_name.length;
        for (int i =0; i<length; i++) {
//            if (display_notes[i] ) {
//                dispColor = onColor;
//            } else {
                dispColor = offColor;
//            }

            if (note_name[i].indexOf("#") >= 0)
                yoffset = -30;
            else
                yoffset = 0;
            canvas.drawText( note_name[i], 30+i*30, 680 + yoffset, dispColor );
        }
//        parseSpectrum(c);


    }
/*
    private final static float SPECTRUM_DISPLAY_X=(1280-140);        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_DISPLAY_Y_BUTTOM=680;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_SCALE=30;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
*/

}




