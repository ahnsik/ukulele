package com.example.ahnsik.mytuner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static android.os.SystemClock.sleep;

public class TrainingActivity extends AppCompatActivity implements Runnable {

    private PlayView  mGameView;
    private NoteData  mSongData = new NoteData();
    private Recording mRecording;

    //  locally used variables.
    private double    prev_amplitude = 0;      // index of note data (next position what it will be played.)
    private boolean   vol_increment = false;

    private Thread    mThread = null;
    private long      mGameStartClock = 0;
    private int       playing_pos = 0;      // index of note data (next position what it will be played.)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameView = new PlayView(this);
        setContentView(mGameView);

        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 연주할 파일의 파일 이름을 가져 옴.
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("filename");
        // 파일 이름으로 부터 연주할 데이터를 가져옴.
        mSongData.loadFromFile( getFilesDir(), fileName);
        mGameView.setSongData(mSongData);
        // initialize local data
//        display_notes = new boolean[NUM_OF_NOTE_UKE];
        prev_amplitude = 0.0;
        // 녹음 시작,
        mRecording = new Recording();

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

//        mGameView.setPlayPosition(mGameStartClock);
    }

    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
        mGameView.pause();
        mRecording.end();
//        mp.pause();
        this.finish();
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
        mGameView.resume();
        mRecording.start();
//        mp.start();
    }


    public void run() {
        long playing_clock = 0;

        vol_increment = false;
        playing_pos = 0;
        mGameView.setPlayPosition( mSongData.timeStamp[playing_pos] );      // 맨 처음 위치에서 시작.

        while (true) {
            mRecording.parseSpectrum();
            mGameView.setPlayedNote(mRecording.notes_detected);
            mGameView.setSpectruData(mRecording.spectrum);

//            Log.d("ukulele", "mSongData:"+mSongData );
//            Log.d("ukulele", "  timeStamp:"+mSongData.timeStamp + "vol:"+ mRecording.detected_volume );
//            Log.d("ukulele", "  playing_pos:"+mSongData.timeStamp[playing_pos] );

            playing_clock = System.currentTimeMillis()-mGameStartClock;
            if ( playing_clock < mSongData.timeStamp[playing_pos] ) {
                // 만약 현재 시간이 연주했어야 하는 시간 보다 이른 시간이라면 천천히 timer 를 갱신해 나가고..
                mGameView.setPlayPosition(playing_clock);      // 다음 연주해야 할 위치의 시점으로 이동
            } else {    // 그렇지 않으면.. 즉, 연주 타이밍을 놓쳐서 delay 가 발생했다면, 발생한 delay 만큼 mGameStartClock 을 조정하여 계속 기다리게 한다.
                mGameStartClock = System.currentTimeMillis() - mSongData.timeStamp[playing_pos];
            }

            // 제대로 연주가 되었다면, 다음 note로 이동.
            if ( isStroked() && isPlayedOk(playing_pos) ) {
                playing_pos++;
                // 데이터의 끝까지 모두 다 연주가 끝났다면.. 액티비티 종료.
                if (playing_pos >= mSongData.numNotes ) {
                    finish();
                    break;          // break for while.
                }
                mGameView.setPlayPosition(mSongData.timeStamp[playing_pos]);      // 다음 연주해야 할 위치의 시점으로 이동

                // 디버깅용 코드.
                String dbgStr = "Next, you have to play : ";
                for (int k=0; k<mSongData.note[playing_pos].length; k++)
                    dbgStr += mSongData.note[playing_pos][k];
                Log.d("ukulele", dbgStr );
            }

            sleep(10);
        }
    }

    private boolean isStroked() {
        boolean  stroked = false;
//            Log.d("ukulele", "  check peak- , prev:"+ (int)prev_amplitude + ", now:" + (int)mRecording.detected_volume + ", inc:"+vol_increment );
        if ( vol_increment && (prev_amplitude > mRecording.detected_volume )) {     // prev_amplitude 의 값이 peak 이어야 함.
            Log.d("ukulele", "  Stroke detected !!   vol:"+ mRecording.detected_volume + ", freq:" + mRecording.center_freq );
            stroked = true;
        }

        if ( prev_amplitude > mRecording.detected_volume)
            vol_increment = false;
        if ( prev_amplitude < mRecording.detected_volume)
            vol_increment = true;
        // 과거의 음량을 갱신 기억.
        prev_amplitude = mRecording.detected_volume;
        return stroked;
    }

    //연주할 위치에 있는 악보데이터 (음계)가 모두 Play 되고 있는지 판단. 즉, 연습자가 제대로 코드를 연주 했는지 확인하는 함수.
    private boolean isPlayedOk(int pos) {
        int j;
        boolean result = true;
        String[] notes = mSongData.note[pos];       // 연주 되어야 할 데이터 (악보).

        for (j=0; j<notes.length; j++) {            // 모든 데이터(악보)에 해당하는 소리가 났는지 판단.
            if (!mRecording.isPlayed(notes[j])) {   // 해당하는 음계(주파수)의 소리가 녹음 되지 않았다면, 연주 실패.
                mSongData.note_played[pos][j] = false;
                result = false;
            }
        }
        return result;
    }

}
