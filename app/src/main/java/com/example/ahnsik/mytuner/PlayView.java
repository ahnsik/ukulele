package com.example.ahnsik.mytuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import static android.graphics.Color.*;

public class PlayView extends GameView {

    private final static int    PLAYING_POSITION=460;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static int    MAX_BEAT_POS = 60;
    private final static int    FINGERGUIDE_X=1130;        // TAB악보 가로라인의 세로위치
    private final static int    FINGERGUIDE_Y=560;        // TAB악보 가로라인의 세로위치
    private final static int    LINE_Y=320;        // TAB악보 가로라인의 세로위치


    private NoteData songData;
    private double   a_beat;  // 1비트당 시간 (mili-second),  1분(60000밀리초)를 bpm 으로 나눈 값. bpm은 1분당 비트 수
    private int      score;
    private long     mGame_clock;

    private boolean display_notes[];
    public  double[] spectrum;

    private Paint  pText, pBG, pCursor;
    private Bitmap bmpBg, bmpFinger;
    private int c_thumb, c_index, c_middle, c_appoint, c_child;

    private final static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };

    public PlayView(Context context) {
        super(context);
        resource_load();
        // 게임 정보 초기화
        score = 0;
        mGame_clock = 0;
    }

    public void setSongData(NoteData songData) {
        this.songData = songData;
        a_beat = (60000.0/songData.mBpm);  // bpm은 1분당 비트 수 이므로, 1비트가 몇 밀리초 인지 계산. 1분=60초, 1비트당 약 612밀리초
    }

    public void setPlayPosition(long clock) {
        mGame_clock = clock;
    }

    private void resource_load() {
        // 비트맵 로딩이 Multi Threading 이 된다면.. 먼저 로드를 시작해 두고 나머지를 설정하는 편이 좋다.
        bmpBg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_paper );
        bmpFinger = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger_guide );

        // 전체적으로 기본적으로 사용될 Font 및 기본 색상 등을 지정.
        pText = new Paint();
        Typeface liryc = Typeface.createFromAsset(getContext().getAssets(), "DX_kyungpil.ttf");
        pText.setColor(rgb(90, 60, 4) );
        pText.setTypeface(liryc);
        pText.setTextSize(60.0f);

        // 배경을 그리는 데 사용될 색상 및 Font를 지정.
        pBG = new Paint();
        Typeface bgText = Typeface.createFromAsset(getContext().getAssets(), "coolvetica.ttf");
        pBG.setColor(rgb(120, 80, 4) );    // 오선지(X) TAB악보 라인 의 색상 / Font색상.
        pBG.setTypeface(bgText);
        pBG.setTextSize(60.0f);     // T.A.B  on 탭악보 위의 TAB 글자.

        pCursor = new Paint(pBG);
        pCursor.setColor(rgb(209, 73, 46) );    // 커서 색상은 빨간색
        c_thumb = rgb(120,80,4);  //rgb(96, 96, 96);
        c_index = rgb(0, 152, 0);
        c_middle = rgb(203, 51, 203);
        c_appoint = rgb(51, 152, 152);
        c_child = rgb(51, 51, 203);

        Log.d("ukulele", "width="+ this.width + ", height=" + this.height );
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        //  게임에서 기본적으로 그려져야 할 것들.
        drawPaper(canvas);
        drawInfo(canvas);
        drawScore(canvas);
        //  mGame_clock 기준으로 그려질 내용들
        drawNotes(canvas);

        // ......
        double beat_diff = ((double)mGame_clock % a_beat); // 재생시간(클럭)을 비트수로 나눈 나머지를 가지고 메트로놈 계산.
        double metronom_pos = ((double)MAX_BEAT_POS*beat_diff)/ a_beat;

        drawCursor(canvas, (int)metronom_pos);    //(int)(bpm-beat)/20);
        drawWholeNotes(canvas);
        // 아래의 내용은 optional. - 디버깅 및 미관상으로 녹음되는 스펙트럼을 그려서 보여주는 정도.
        drawSpectrum(canvas);
    }


    private void drawPaper(Canvas canvas) {
        if (bmpBg != null)
            canvas.drawBitmap(bmpBg, 0, 0, null);
        else {
            Paint pPaper = new Paint(pBG);
            pPaper.setColor(rgb(234, 193, 122));    // 오선지(X) TAB악보 라인 의 색상 / Font색상.
            canvas.drawRect(new Rect(0, 0, 1280, 720), pPaper);
            Log.d("ukulele", "Can not draw Bitmap. because bitmap handle is null.");
        }

        canvas.drawRect(new Rect(30, LINE_Y, 1250, LINE_Y+4), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+60, 1250, LINE_Y+64), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+120, 1250, LINE_Y+124), pBG);
        canvas.drawRect(new Rect(30, LINE_Y+180, 1250, LINE_Y+184), pBG);
        canvas.drawText("T",40,LINE_Y+50, pBG);
        canvas.drawText("A",38,LINE_Y+110, pBG);
        canvas.drawText("B",40,LINE_Y+170, pBG);

        if (bmpFinger != null) {
            canvas.drawBitmap(bmpFinger, FINGERGUIDE_X, FINGERGUIDE_Y, null);
        }
    }

    private long beat_clock = 0;
    private void drawCursor(Canvas canvas, int beat_offset) {
        canvas.drawText("▼",PLAYING_POSITION-28,LINE_Y-beat_offset, pCursor);
        canvas.drawText("▲",PLAYING_POSITION-28,LINE_Y+222+beat_offset, pCursor);
        if (beat_offset<5) {
            beat_clock = mGame_clock;
            canvas.drawRect(new Rect(PLAYING_POSITION-10, 320, PLAYING_POSITION+10, 500), pCursor );       // 정확한 타이밍 일 때
        } else {
            canvas.drawRect(new Rect(PLAYING_POSITION-5, 300, PLAYING_POSITION+5, 520), pCursor );       // 플레이 위치 가이드.
        }
        canvas.drawText(" " + beat_clock, PLAYING_POSITION-60, LINE_Y-MAX_BEAT_POS, pCursor );
    }


    private final static int    TITLE_POSITION_Y=70;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static int    SCORE_POSITION_X=920;

    private void drawInfo(Canvas canvas) {
        Paint  pTitle = new Paint(pText);

        // 노랙 제목 표시.
        pTitle.setTextSize(60.0f);
        if (songData != null) {
            canvas.drawText(songData.mSongTitle,12,TITLE_POSITION_Y, pTitle);
        } else {
            canvas.drawText("곡 제목 정보가 없습니다.",12,TITLE_POSITION_Y, pTitle);
        }
    }

    private void drawScore(Canvas canvas) {
        Paint  pScore= new Paint(pBG);

        Rect eraserRect = new Rect(SCORE_POSITION_X, TITLE_POSITION_Y-58, 1280, TITLE_POSITION_Y+34);
        canvas.drawBitmap(bmpBg, eraserRect, eraserRect, null );

        pScore.setTextSize(48.0f);
        canvas.drawText("Score: " + score, SCORE_POSITION_X, TITLE_POSITION_Y-12, pScore);
        pScore.setTextSize(24.0f);  // 디버깅용 clock offset
        canvas.drawText("clock: " + mGame_clock, SCORE_POSITION_X+40, TITLE_POSITION_Y+24, pScore);
    }



    private final static int    LYRIC_POSITION_Y=LINE_Y+260;

    private void drawNotes(Canvas canvas) {
        Paint  pLyric = new Paint(pText);
        pLyric.setTextSize(48.0f);
        if (songData == null) {
            return;
        }
        int length = songData.numNotes;

        for (int i =0; i<length; i++) {
            int xpos = PLAYING_POSITION + (int)( songData.timeStamp[i]-mGame_clock)/6;  // 진행 속도에 따라 /6 의 값은 변동적으로 해야 함. - bps 를 고려하는 계산을 나중에 처리해야 함.
            if (xpos < 90) continue;        // 화면 밖으로 나가는 것 들은 그릴 필요 없음.
            if (xpos > 1200 ) continue;     // 화면 밖으로 나가는 것 들은 그릴 필요 없음.

            if ( (songData.chordName[i] != null) ) {
                Log.d("ukulele", "chord="+ songData.chordName[i] + " at "+xpos );
                drawChord(canvas, xpos, songData.chordName[i] );
            }
            for (int j=0; j<songData.tab[i].length; j++) {
                drawNote(canvas, xpos, songData.tab[i][j] );
            }
            if ( (songData.lyric[i] != null) ) {
                canvas.drawText( songData.lyric[i], xpos,LYRIC_POSITION_Y, pLyric);
            }
            if (songData.score[i] < 999) {
                canvas.drawText( ":"+ songData.score[i], xpos,660, pText);
            }
        }
    }

    private void drawNote(Canvas canvas, int xpos, String note) {
        Paint fingerColor = new Paint(pBG);
        int y;
        String  flet;
        char    finger;

        switch( note.charAt(0) ) {
            case 'G' :  // 4번줄
                y = LINE_Y+200;        // TAB악보 가로라인의 세로위치  520 = 320+200
                break;
            case 'C' :  // 3번줄
                y = LINE_Y+140;        // 320+140= 460;
                break;
            case 'E' :  // 2번줄
                y = LINE_Y+140;        // 320+80= 400;
                break;
            case 'A' :  // 1번줄
                y = LINE_Y+140;        // 320+20= 340;
                break;
            default: y=0;
        }

        if ((note.length() > 2) && (note.charAt(2) > '0' && note.charAt(2) < '9')) {
            flet = note.substring(1,3); // 두자리 숫자.
            if (note.length() > 3)
                finger = note.charAt(3);
            else
                finger = 'p';
        } else {
            flet = note.substring(1,2); // 한자리 숫자.
            if (note.length() > 2)
                finger = note.charAt(2);
            else
                finger = 'p';
        }

        switch(finger) {
            case 'i':   // 검지손가락 (index finger)
                fingerColor.setColor(c_index);    // 검지는 녹색
                break;
            case 'm':   // 중지손가락 (middle finger)
                fingerColor.setColor(c_middle);    // 중지는 보라색
                break;
            case 'a':   // 약지손가락 (appointment finger)
                fingerColor.setColor(c_appoint);    // 약지는 시안(어두운)
                break;
            case 'c':   // 새끼손가락 (child? finger)
                fingerColor.setColor(c_child);    // 새끼는 파랑
                break;
//            case 'p':   // 엄지손가락 (사용 불가)
            default:
                fingerColor.setColor(c_thumb);    // 나머지 (엄지)는 회색
                break;
        }
        Rect eraserRect = new Rect(xpos-8, y-60, xpos+30, y);
        canvas.drawBitmap(bmpBg, eraserRect, eraserRect, null );
        canvas.drawText(flet,xpos,y, fingerColor );
    }

    private final static int    CHORD_POSITION_Y=LINE_Y-60;

    private void drawChord(Canvas canvas, int x, String chordName ) {
//        Paint  pChordFont= new Paint(pBG);
//        pChordFont.setTextSize(48.0f);
//        pChordFont.setColor(rgb(90, 60, 4) );
        canvas.drawText(chordName, x, CHORD_POSITION_Y, pBG);
    }

    private void drawWholeNotes(Canvas canvas) {
        Paint  onColor, offColor, dispColor;
        onColor = new Paint(pBG);
        onColor.setTextSize(20.0f);
        onColor.setColor(rgb(90, 60, 4));   // 잘 보이는 짙은 색상
        offColor = new Paint(onColor);
        offColor.setColor(rgb(160, 110,24));  // 잘 안 보이는 옅은 색상

        if (display_notes==null) {
            return;
        }
        int yoffset=0;
        int  length = note_name.length;
        for (int i =0; i<length; i++) {
            if (display_notes[i] ) {
                dispColor = onColor;
            } else {
                dispColor = offColor;
            }

            if (note_name[i].indexOf("#") >= 0)
                yoffset = -30;
            else
                yoffset = 0;
            canvas.drawText( note_name[i], 30+i*30, 680 + yoffset, dispColor );
        }
//        parseSpectrum(c);


    }

    public void setPlayedNote(boolean[] played_note) {
        display_notes = played_note;
    }
    public void setSpectruData(double[] data) {
        spectrum = data;
    }


    private final static float SPECTRUM_DISPLAY_X=(1280-140);        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_DISPLAY_Y_BUTTOM=680;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_SCALE=30;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,

    public void drawSpectrum(Canvas c) {
        // 우선 스펙트럼 데이터를 살짝 뭉개서 주변값들을 통합할 필요가 있다.
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        // 그 주파수를 기준으로 음계를 찾아 플래그 설정.
        int length = spectrum.length;
//        Paint spectrumPaint = new Paint(pBG);
//        spectrumPaint.setTextSize(20.0f);
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        for (int i = 1; i< (length/2)-1; i++) {
//            if ( (spectrum[i-1]<spectrum[i])&&(blur[i]>blur[i+1]) && (mRec.magnitude(i) > PEAK_MINIMUM_DB) ) {   // PEAK 값
//                // 임시로 스펙트럼을 그리기 위한 것. (주파수 값 표시)
////                c.drawText( "." +(int)mRec.frequency(i)+"Hz", SPECTRUM_DISPLAY_X-40+i, SPECTRUM_DISPLAY_Y_BUTTOM-mRec.magnitude(i)*SPECTRUM_SCALE, spectrumPaint );
//            }
            // 임시로 스펙트럼을 그리기 위한 것. (막대그래프 표시)
            c.drawLine( SPECTRUM_DISPLAY_X+(i-37), SPECTRUM_DISPLAY_Y_BUTTOM, SPECTRUM_DISPLAY_X+(i-37), SPECTRUM_DISPLAY_Y_BUTTOM-(int)(spectrum[i]*SPECTRUM_SCALE), pText ) ;
        }
    }

}




