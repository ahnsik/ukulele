package com.example.ahnsik.mytuner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import static android.os.SystemClock.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayingActivity extends AppCompatActivity implements Runnable {

    private PlayView  mPlayingView;
    private MediaPlayer mp;
    private NoteData  mSongData = new NoteData();
    private Recording mRecording;

    private Thread    mThread = null;
    private long      mGameStartClock = 0;
    private int       playing_pos = 0;      // index of note data (next position what it will be played.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayingView = new PlayView(this);
        setContentView(mPlayingView);
/*        setContentView(R.layout.activity_playing);
        mPlayingView = (PlayView)findViewById(R.id.viewGameView);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
*/
        // 연주할 파일의 파일 이름을 가져 옴.
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("filename");
        // 파일 이름으로 부터 연주할 데이터를 가져옴.
        mSongData.loadFromFile( getFilesDir(), fileName);
        mPlayingView.setSongData(mSongData);
        // initialize local data

        // 녹음 시작,
        mRecording = new Recording();

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mp = new MediaPlayer();
        Log.d("ukulele", "!@@@@@@@@@@ MP3 Play ? @@@@@@@@@@@@@@@@@@@@");
        try {
            mp.reset();
            mp.setDataSource(mSongData.mMusicURL);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
        mPlayingView.pause();
        mRecording.end();
//        mp.pause();
        this.finish();
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
        mPlayingView.resume();
        mRecording.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.
//        mp.start();
    }

    public void run() {
        long playing_clock = 0;

        playing_pos = 0;
//        mPlayingView.setPlayPosition( mSongData.timeStamp[playing_pos] );      // 맨 처음 위치에서 시작.

        while (true) {
            playing_clock = System.currentTimeMillis() - mGameStartClock - mSongData.mStartOffset;     // 시작 싯점의 시스템 클럭을 저장.
//            if (mSongData.playtime <= playing_clock ) {
//                Log.d("ukulele", "End of this song." + playing_clock );
//                finish();
//                break;          // break for while.
//            }
            mPlayingView.setPlayPosition( playing_clock );

            mRecording.parseSpectrum();
            mPlayingView.setPlayedNote(mRecording.notes_detected);
            mPlayingView.setSpectruData(mRecording.spectrum);

/*            playing_clock = System.currentTimeMillis()-mGameStartClock;
            if ( playing_clock < mSongData.timeStamp[playing_pos] ) {
                // 만약 현재 시간이 연주했어야 하는 시간 보다 이른 시간이라면 천천히 timer 를 갱신해 나가고..
                mPlayingView.setPlayPosition(playing_clock);      // 다음 연주해야 할 위치의 시점으로 이동
            } else {    // 그렇지 않으면.. 즉, 연주 타이밍을 놓쳐서 delay 가 발생했다면, 발생한 delay 만큼 mGameStartClock 을 조정하여 계속 기다리게 한다.
                    mGameStartClock = System.currentTimeMillis() - mSongData.timeStamp[playing_pos];
            }
*/
            // 제대로 연주가 되었다면, 다음 note로 이동.
            if ( mRecording.isStroked() && isPlayedOk(playing_pos) ) {
//                playing_pos++;
                // 데이터의 끝까지 모두 다 연주가 끝났다면.. 액티비티 종료.
//                if (playing_pos >= mSongData.numNotes ) {
//                    finish();
//                    break;          // break for while.
//                }
//                mPlayingView.setPlayPosition(mSongData.timeStamp[playing_pos]);      // 다음 연주해야 할 위치의 시점으로 이동

                // 디버깅용 코드.
                String dbgStr = "Next, you have to play : ";
                for (int k=0; k<mSongData.note[playing_pos].length; k++)
                    dbgStr += mSongData.note[playing_pos][k];
                Log.d("ukulele", dbgStr );
            }

            sleep(5);
        }
    }


    //연주할 위치에 있는 악보데이터 (음계)가 모두 Play 되고 있는지 판단. 즉, 연습자가 제대로 코드를 연주 했는지 확인하는 함수.
    private boolean isPlayedOk(int pos) {
        int j;
        boolean result = true;
        String[] notes = mSongData.note[pos];       // 연주 되어야 할 데이터 (악보).
        String  dbgStr = "...Check:";

        for (j=0; j<notes.length; j++) {            // 모든 데이터(악보)에 해당하는 소리가 났는지 판단.
            if (!mRecording.isPlayed(notes[j])) {   // 해당하는 음계(주파수)의 소리가 녹음 되지 않았다면, 연주 실패.
                mSongData.note_played[pos][j] = false;
                result = false;
                dbgStr += notes[j] + "(X),";
            } else {
                dbgStr += notes[j] + "(O),";
            }
        }
        Log.d("ukulele", dbgStr );
        return result;
    }

}
