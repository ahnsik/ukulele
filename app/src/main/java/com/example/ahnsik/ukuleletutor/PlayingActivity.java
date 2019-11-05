package com.example.ahnsik.ukuleletutor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import static android.os.SystemClock.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayingActivity extends AppCompatActivity implements Runnable {

    private PlayView  mPlayingView;
    private MediaPlayer mp;
    private NoteData  mSongData = new NoteData();
    private long      mScoreData[];
    private Recording mRecording;

    private Thread    mThread = null;
    private long      mGameStartClock = 0, endofSong = 0;
    private int       playing_pos = 0;      // index of note data (next position what it will be played.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayingView = new PlayView(this);
        setContentView(mPlayingView);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       // 연습 도중에 무조작으로 Sleep 모드로 들어가면 곤란하므로, Sleep Mode 로 가지 않게 설정.

        // 연주할 파일의 파일 이름을 가져 옴.
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("filename");
        // 파일 이름으로 부터 연주할 데이터를 가져옴.
        mSongData.loadFromFile( getFilesDir(), fileName);
        mPlayingView.setSongData(mSongData);
        // initialize local data
        endofSong = mSongData.mStartOffset + mSongData.timeStamp[mSongData.numNotes-1] + 2000 + 3000;       // 마지막 노트 데이터의 2초 뒤.    +3000 은 setPlayPosition 에서의 옵셋
        mScoreData = new long[mSongData.numNotes];      // 각 음당 평가점수를 저장할 배열

        // 녹음 시작,
        mRecording = new Recording();

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mp = new MediaPlayer();
        Log.d("ukulele", "!@@@@@@@@@@ MP3 Play ? @@@@@@@ : " + getFilesDir()+"/"+mSongData.mMusicURL );

        try {
            mp.reset();
            mp.setDataSource( getFilesDir()+"/"+mSongData.mMusicURL );
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
        mPlayingView = null;
        mRecording.end();
        mp.pause();
        this.finish();
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
//        mPlayingView.resume();
        mRecording.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.
        mp.start();
    }

    public void run() {
        long playing_clock = 0;

        playing_pos = 0;

        while (true) {
            playing_clock = System.currentTimeMillis() - mGameStartClock;     // 시작 싯점의 시스템 클럭을 저장.
            if (endofSong <= playing_clock ) {
                Log.d("ukulele", "End of this song." + playing_clock + "("+endofSong+")" );
                finish();
                break;          // break for while.
            }
            mPlayingView.setPlayPosition( playing_clock - 3000 );

            mRecording.parseSpectrum();
            mPlayingView.setPlayedNote(mRecording.notes_detected);
            mPlayingView.setSpectruData(mRecording.spectrum);

            playing_clock = System.currentTimeMillis()-mGameStartClock;
//            if ( playing_clock < mSongData.timeStamp[playing_pos] ) {
//                // 만약 현재 시간이 연주했어야 하는 시간 보다 이른 시간이라면 천천히 timer 를 갱신해 나가고..
//                mPlayingView.setPlayPosition(playing_clock);      // 다음 연주해야 할 위치의 시점으로 이동
//            } else {    // 그렇지 않으면.. 즉, 연주 타이밍을 놓쳐서 delay 가 발생했다면, 발생한 delay 만큼 mGameStartClock 을 조정하여 계속 기다리게 한다.
//                    mGameStartClock = System.currentTimeMillis() - mSongData.timeStamp[playing_pos];
//            }

/*            // 제대로 연주가 되었다면, 다음 note로 이동.
            if ( mRecording.isStroked() && isPlayedOk(playing_pos) ) {
                playing_pos++;

                // 디버깅용 코드.
                String dbgStr = "Next, you have to play : ";
                for (int k=0; k<mSongData.note[playing_pos].length; k++)
                    dbgStr += mSongData.note[playing_pos][k];
                Log.d("ukulele", dbgStr );
            }
*/
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
