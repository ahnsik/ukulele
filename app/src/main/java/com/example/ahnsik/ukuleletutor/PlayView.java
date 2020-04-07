package com.example.ahnsik.ukuleletutor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import static android.graphics.Color.*;

public class PlayView extends GameView {

    private final static int    PLAYING_POSITION=460;
    private final static int    MAX_BEAT_POS = 60;
    private final static int    FINGERGUIDE_X=1460;     // 손가락 guide 비트맵 표시 위치
    private final static int    FINGERGUIDE_Y=660;

    // TAB 악보를 그리기 위한 배경색, 기본 좌표 등..
    private final static int    TAB_LEFT_END = 40;
    private final static int    TAB_RIGHT_END = 1880;
    private final static int    TAB_LINE_SPACE = 80;
    private final static int    LINE_Y=420;        // TAB악보 가로라인의 세로위치
    private final static int    CHORD_POSITION_Y=LINE_Y-90;

    private final static int    TITLE_POSITION_Y=80;
    private final static int    SCORE_POSITION_X=1200;
    private final static int    LYRIC_POSITION_Y=LINE_Y+TAB_LINE_SPACE*4;

    // 각종 색상을 미리 정의 함. (Font 색상 등..)
    private final static int    BG_PAPER_COLOR = rgb(234, 193, 122);        // 악보 배경(크래프트 종이느낌) 색상
    private final static int    BG_TAB_LINE_COLOR = rgb(120, 80, 4);        // 오선지(X) TAB악보 라인 의 색상 / Font색상.
    private final static int    DEFAULT_TEXT_COLOR = rgb(90, 60, 4);        // 기본 글자 색상
    private final static int    CURSOR_COLOR = rgb(209, 73, 46);        // 커서 색상은 빨간색
    private final static int    THUMB_FINGER_COLOR = rgb(120,80,4);     // 엄지손가락 rgb(96, 96, 96);
    private final static int    INDEX_FINGER_COLOR = rgb(32, 192, 0);    // 검지손가락
    private final static int    MIDDLE_FINGER_COLOR = rgb(203, 51, 203);    // 중지손가락
    private final static int    APPOINT_FINGER_COLOR = rgb(101, 162, 202);   // 약지손가락
    private final static int    CHILD_FINGER_COLOR = rgb(51, 51, 203);  // 새끼손가락

    private final static int    SCORE_COLOR_PERFECT = rgb(144, 144, 204);
    private final static int    SCORE_COLOR_GOOD = rgb(144, 196, 144);
    private final static int    SCORE_COLOR_BAD = rgb(144, 144, 144);
    private final static int    SCORE_COLOR_MISS = rgb(190, 144, 144);
    private final static int    SCORE_COLOR_TOO_FAST = rgb(204, 204, 150);
    private final static int    SCORE_COLOR_TOO_LATE = rgb(204, 192, 150);
    private final static int    SPECTRUM_COLOR = rgb(168, 168, 128);
    private final static int    SOUND_ON_COLOR = rgb(90, 60, 4);
    private final static int    SOUND_OFF_COLOR = rgb(160, 110,24);
//    int     c_Miss = rgb(192, 64, 64);


    private NoteData songData;
    private double   a_beat;  // 1비트당 시간 (mili-second),  1분(60000밀리초)를 bpm 으로 나눈 값. bpm은 1분당 비트 수
    private int      score;
    private long     mGame_clock;

    private boolean[] display_notes;    // 검출된 음 (판단용이 아닌 display용도) - 바깥 클래스(액티비티) 에서 판단한 배열을 복사해서 저장.
    public  double[] spectrum;          // 바깥 클래스(액티비티)에서 녹음/FFT 분석을 마친 스펙트럼 데이터를 실시간으로 저장.

    private Paint  pText, pBG, pPaper, pCursor, pSpectrum, pTitle, pLyric;
    private Paint  pInfo;       // 디버깅 정보 등, 부가적인 정보를 표시하기 위한 색상.
    private Bitmap bmpBg, bmpFinger;

    private final static String[] note_name = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6"};

    public PlayView(Context context) {
        super(context);
        resource_load();
        // 게임 정보 초기화
        score = 0;
        mGame_clock = 0;
    }

    public PlayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);

        // PlayView 가 표시되고 있는 동안에는 계속 Sleep 모드로 들어 가지 않는다.
        setKeepScreenOn(true);

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

    public void setPlayedNote(boolean[] played_note) {
        display_notes = played_note;
    }
    public void setSpectruData(double[] data) {
        spectrum = data;
    }

    private void resource_load() {
        // 비트맵 로딩이 Multi Threading 이 된다면.. 먼저 로드를 시작해 두고 나머지를 설정하는 편이 좋다.
//        bmpBg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_paper );
        bmpBg = null;
        bmpFinger = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger_guide );

        // 전체적으로 기본적으로 사용될 Font 및 기본 색상 등을 지정.
        pText = new Paint();
        Typeface liryc = Typeface.createFromAsset(getContext().getAssets(), "DX_kyungpil.ttf");
        pText.setColor( DEFAULT_TEXT_COLOR );
        pText.setTypeface(liryc);
        pText.setTextSize(60.0f);

        // 배경을 그리는 데 사용될 색상 및 Font를 지정.
        pBG = new Paint();
        Typeface bgText = Typeface.createFromAsset(getContext().getAssets(), "coolvetica.ttf");
        pBG.setColor( BG_TAB_LINE_COLOR );
        pBG.setTypeface(bgText);
        pBG.setTextSize(80.0f);     // T.A.B  on 탭악보 위의 TAB 글자.

        pTitle = new Paint(pText);
        pTitle.setTextSize(80.0f);

        pPaper = new Paint(pBG);
        pPaper.setColor(BG_PAPER_COLOR);

        pCursor = new Paint(pBG);
        pCursor.setColor( CURSOR_COLOR );

        pSpectrum = new Paint(pBG);
        pSpectrum.setColor( SPECTRUM_COLOR );    // 스펙트럼 그래프는 회색(?)
        pSpectrum.setTextSize(20.0f);
        pInfo = new Paint(pSpectrum);

        pLyric = new Paint(pText);
        pLyric.setTextSize(48.0f);

        Log.d("ukulele", "width="+ this.width + ", height=" + this.height );
        if (this.width <= 0 ) {     // width 를 모른다.

        }
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

        drawSpectrum(canvas);
        drawWholeNotes(canvas);
        // 아래의 내용은 optional. - 디버깅 및 미관상으로 녹음되는 스펙트럼을 그려서 보여주는 정도.
    }


    private void drawPaper(Canvas canvas) {
        if (bmpBg != null)
            canvas.drawBitmap(bmpBg, 0, 0, null);
        else {
            canvas.drawRect( new Rect(0, 0, 1920, 1080), pPaper);
//            Log.d("ukulele", "Can not draw Bitmap. because bitmap handle is null.");
        }

        canvas.drawRect(new Rect(TAB_LEFT_END, LINE_Y, TAB_RIGHT_END, LINE_Y+4), pBG);
        canvas.drawRect(new Rect(TAB_LEFT_END, LINE_Y+TAB_LINE_SPACE, TAB_RIGHT_END, LINE_Y+TAB_LINE_SPACE+4), pBG);
        canvas.drawRect(new Rect(TAB_LEFT_END, LINE_Y+TAB_LINE_SPACE*2, TAB_RIGHT_END, LINE_Y+TAB_LINE_SPACE*2+4), pBG);
        canvas.drawRect(new Rect(TAB_LEFT_END, LINE_Y+TAB_LINE_SPACE*3, TAB_RIGHT_END, LINE_Y+TAB_LINE_SPACE*3+4), pBG);
        canvas.drawText("T",40,LINE_Y+TAB_LINE_SPACE-10, pBG);
        canvas.drawText("A",38,LINE_Y+TAB_LINE_SPACE*2-10, pBG);
        canvas.drawText("B",40,LINE_Y+TAB_LINE_SPACE*3-10, pBG);

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




    private void drawInfo(Canvas canvas) {
        // 노랙 제목 표시.
        pTitle.setTextSize(80.0f);
        if (songData != null) {
            canvas.drawText(songData.mSongTitle, TAB_LEFT_END, TITLE_POSITION_Y, pTitle);
            pTitle.setTextSize(40.0f);
            canvas.drawText("BPM:"+songData.mBpm, TAB_LEFT_END,CHORD_POSITION_Y-100, pTitle);
        } else {
            canvas.drawText("곡 제목 정보가 없습니다.", TAB_LEFT_END, TITLE_POSITION_Y, pTitle);
        }
    }


    private void drawScore(Canvas canvas) {

        Rect eraserRect = new Rect(SCORE_POSITION_X, TITLE_POSITION_Y-58, 1280, TITLE_POSITION_Y+34);
        if (null != bmpBg) {
            canvas.drawBitmap(bmpBg, eraserRect, eraserRect, null );
        } else {
            canvas.drawRect(eraserRect, pPaper);
        }

        pTitle.setTextSize(48.0f);
        canvas.drawText("Score: " + score, SCORE_POSITION_X, TITLE_POSITION_Y-12, pTitle);
        pTitle.setTextSize(30.0f);  // 디버깅용 clock offset
        canvas.drawText("clock: " + mGame_clock, SCORE_POSITION_X+40, TITLE_POSITION_Y+24, pTitle);
    }



    private void drawNotes(Canvas canvas) {

        if (songData == null) {
            return;
        }
        int length = songData.numNotes;

        for (int i =0; i<length; i++) {
            int xpos = PLAYING_POSITION + (int)( songData.timeStamp[i]-mGame_clock)/4;  // 진행 속도에 따라 /6 의 값은 변동적으로 해야 함. - bps 를 고려하는 계산을 나중에 처리해야 함.
            if (xpos < TAB_LEFT_END+60 ) continue;        // 화면 밖으로 나가는 것 들은 그릴 필요 없음.
            if (xpos > TAB_RIGHT_END-80 ) continue;     // 화면 밖으로 나가는 것 들은 그릴 필요 없음.

            if ( (songData.chordName[i] != null) ) {
                drawChordName(canvas, xpos, songData.chordName[i] );
            }
            if ( (songData.stroke[i] != null) ) {
                drawStroke(canvas, xpos, songData.stroke[i] );
            }

            for (int j=0; j<songData.tab[i].length; j++) {
                drawNote(canvas, xpos, songData.tab[i][j], SCORE_COLOR_TOO_FAST );
            }
            if ( (songData.lyric[i] != null) ) {
                canvas.drawText( songData.lyric[i], xpos,LYRIC_POSITION_Y, pLyric);
            }
            if (songData.score[i] < 999) {
                canvas.drawText( ":"+ songData.score[i], xpos,660, pText);
            }
        }
    }

    private void drawNote(Canvas canvas, int xpos, String note, int scoredColor) {
        Paint fingerColor = new Paint(pBG);     // Font 크기 등을 그대로 가져오기 위해 상속.
        String  flet;
        char    finger;
        boolean doubledigit=false;
        int     y;

        switch( note.charAt(0) ) {
            case 'G' :  // 4번줄
                y = LINE_Y+TAB_LINE_SPACE*3+30;        // TAB악보 가로라인의 세로위치
                break;
            case 'C' :  // 3번줄
                y = LINE_Y+TAB_LINE_SPACE*2+30;
                break;
            case 'E' :  // 2번줄
                y = LINE_Y+TAB_LINE_SPACE+30;
                break;
            case 'A' :  // 1번줄
                y = LINE_Y+30;
                break;
            default: y=0;
        }

        finger = 'p';       // 손가락 정보가 없으면 default색상.
        if ((note.length() > 2) && (note.charAt(2) >= '0' && note.charAt(2) <= '9')) {
            doubledigit = true;
            flet = note.substring(1,3); // 두자리 숫자.
            if (note.length() > 3)
                finger = note.charAt(3);
        } else {
            doubledigit = false;
            flet = note.substring(1,2); // 한자리 숫자.
            if (note.length() > 2)
                finger = note.charAt(2);
        }

        if (xpos >= PLAYING_POSITION ) {      // 아직 timestamp 가 지나가지 않은 note 들은 연주할 손가락을 알려 줄 수 있는 색으로.
            switch(finger) {
                case 'i':   // 검지손가락 (index finger)
                    fingerColor.setColor(INDEX_FINGER_COLOR);    // 검지는 녹색
                    break;
                case 'm':   // 중지손가락 (middle finger)
                    fingerColor.setColor(MIDDLE_FINGER_COLOR);    // 중지는 보라색
                    break;
                case 'a':   // 약지손가락 (appointment finger)
                    fingerColor.setColor(APPOINT_FINGER_COLOR);    // 약지는 시안(어두운)
                    break;
                case 'c':   // 새끼손가락 (child? finger)
                    fingerColor.setColor(CHILD_FINGER_COLOR);    // 새끼는 파랑
                    break;
//            case 'p':   // 엄지손가락 (사용 불가)
                default:
                    fingerColor.setColor(THUMB_FINGER_COLOR);    // 나머지 (엄지)는 회색
                    break;
            }
        } else              // 이미 timestamp 가 지나간 이후엔, score 에 따른 색상으로.
        {
            fingerColor.setColor(scoredColor);
            /*
                private final static int    SCORE_COLOR_PERFECT = rgb(144, 144, 204);
                private final static int    SCORE_COLOR_GOOD = rgb(144, 196, 144);
                private final static int    SCORE_COLOR_BAD = rgb(144, 144, 144);
                private final static int    SCORE_COLOR_MISS = rgb(190, 144, 144);
                private final static int    SCORE_COLOR_TOO_FAST = rgb(204, 204, 150);
                private final static int    SCORE_COLOR_TOO_LATE = rgb(204, 192, 150);
            */
        }

        Rect eraserRect = new Rect(xpos-8, y-80, xpos+((doubledigit)?60:30), y);       // note 표시하는 만큼을 지우고.
        if (null != bmpBg) {
            canvas.drawBitmap(bmpBg, eraserRect, eraserRect, null );
        } else {
            canvas.drawRect(eraserRect, pPaper);
        }
        canvas.drawText(flet,xpos,y, fingerColor );     // 연주할 TAB 정보를 표시
    }

    private void drawChordName(Canvas canvas, int x, String chordName ) {
        canvas.drawText(chordName, x, CHORD_POSITION_Y, pBG);
    }

    private void drawStroke(Canvas canvas, int x, String strokedirection ) {
        switch(strokedirection.charAt(0) ) {
            case 'u':
            case 'U':
                canvas.drawLine(x, CHORD_POSITION_Y-20, x, CHORD_POSITION_Y-60, pText );
                canvas.drawLine(x, CHORD_POSITION_Y-60, x+30, CHORD_POSITION_Y-60, pText );
                canvas.drawLine(x+30, CHORD_POSITION_Y-20, x+30, CHORD_POSITION_Y-60, pText );
                break;
            case 'd':
            case 'D':
                canvas.drawLine(x, CHORD_POSITION_Y-60, x+15, CHORD_POSITION_Y-20, pText );
                canvas.drawLine(x+15, CHORD_POSITION_Y-20, x+30, CHORD_POSITION_Y-60, pText );
                break;
            default:
                // 아무것도 안 그림.
                break;
        }
    }

    private void drawWholeNotes(Canvas canvas) {
        if (display_notes==null) {
            return;
        }
        int yoffset=0;
        int  length = note_name.length;
        for (int i =0; i<length; i++) {
            if (display_notes[i] ) {
                pInfo.setColor(SOUND_ON_COLOR );
            } else {
                pInfo.setColor(SOUND_OFF_COLOR );
            }

            if (note_name[i].contains("#"))     //   (note_name[i].indexOf("#") >= 0)  <-- 이 식의 JAVA 식 표현
                yoffset = -40;
            else
                yoffset = 0;
            canvas.drawText( note_name[i], TAB_LEFT_END+i*40, 920 + yoffset, pInfo);
        }
//        parseSpectrum(c);
    }

    //// 여기 정의된 값들은 모두 SPECTRUM 을 그리는 데에만 사용.
    private final static float SPECTRUM_BAR_THICK=50;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_DISPLAY_X=SPECTRUM_BAR_THICK;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,
    private final static float SPECTRUM_DISPLAY_Y_BUTTOM=880;
    private final static float SPECTRUM_SCALE=10;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,

    public void drawSpectrum(Canvas c) {
        int length = spectrum.length;       // 76.5는 좀 크다.  72.5 는 쬐끔 작다.
        double  xpos = 170.0, diff=94.5;    // 수많은 시도 끝에 찾아 낸 숫자들.  xpos 는 그냥 G3 음이 시작하는 위치값을 맞춘 거고, diff 는 음계 별로 건너 뛰는 값 X위치 diff 값이다.
        float   value = 0.0f,   prev_xpos = 15.0f, prev_ypos = (float)SPECTRUM_DISPLAY_Y_BUTTOM;

        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        for (int i = 16; i< (length)-1; i++) {
            value = (float)SPECTRUM_DISPLAY_Y_BUTTOM-(int)(spectrum[i]*SPECTRUM_SCALE);
            c.drawRect( (float)xpos, value, (float)(xpos+SPECTRUM_BAR_THICK-2), (float)SPECTRUM_DISPLAY_Y_BUTTOM, pSpectrum );
            c.drawLine( (float)xpos, (float)SPECTRUM_DISPLAY_Y_BUTTOM, (float)xpos, (float)SPECTRUM_DISPLAY_Y_BUTTOM+60, pSpectrum );

            c.drawLine( (float)prev_xpos, (float)prev_ypos, (float)xpos, (float)value, pCursor );

            prev_xpos = (float)xpos;   prev_ypos = value;
            xpos += diff/2;
            diff = diff/1.05946;
        // 1.05946f (비율) - 음과 음(반음) 사이의 비율, 즉 C 와 C# 과의 주파수 비율, C#과 D 와의 주파수 비율은 1.05946 배 이다.
        }
    }

}
