package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import static android.os.SystemClock.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayingActivity extends AppCompatActivity implements Runnable {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 121106;
    private PlayView  mPlayingView;
    private MediaPlayer mp;
    private NoteData  mSongData = new NoteData();
    private long      mScoreData[];
    private Recording mRecording;

    private Thread    mThread = null;
    private boolean   running, metronom_on;
    private long      mGameStartClock = 0, endofSong = 0;
    private int       playing_pos = 0;      // index of note data (next position what it will be played.)
    private int       total_playLength = 0;      // index of note data (next position what it will be played.)
    private Metronom  mMetronom;
    private SharedPreferences preferences;
    ImageButton btnPlay0_5, btnPlay0_75, btnPlay1_0, btnPlay1_25, btnPlay1_5, btnPlay2_0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);                  // 그러나, SurfaceView 와 함께 다른 UI 객체들을 함께 사용할 때에는
        mPlayingView = (PlayView)findViewById(R.id.viewGameView);   // ContentView 에는 Layout 을 먼저 설정하고, 그 Layout 속에 들어 있는 SurfaceView (customView) 를 가져와서 Control 하면 된다.

        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       // 연습 도중에 무조작으로 Sleep 모드로 들어가면 곤란하므로, Sleep Mode 로 가지 않게 설정.

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPlay0_5 = (ImageButton)findViewById(R.id.btnPlay0_5);
        btnPlay0_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 0.5f );
                btnPlay0_5.setImageResource(R.drawable.on_slow0_5);
            }
        });
        btnPlay0_75 = (ImageButton)findViewById(R.id.btnPlay0_75);
        btnPlay0_75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 0.75f );
                btnPlay0_75.setImageResource(R.drawable.on_slow0_75);
            }
        });
        btnPlay1_0 = (ImageButton)findViewById(R.id.btnPlay1_0);
        btnPlay1_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 1.0f );
                btnPlay1_0.setImageResource(R.drawable.on_ff1_0);
            }
        });
        btnPlay1_25 = (ImageButton)findViewById(R.id.btnPlay1_25);
        btnPlay1_25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 1.25f );
                btnPlay1_25.setImageResource(R.drawable.on_ff1_25);
            }
        });
        btnPlay1_5 = (ImageButton)findViewById(R.id.btnPlay1_5);
        btnPlay1_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 1.5f );
                btnPlay1_5.setImageResource(R.drawable.on_ff1_5);
            }
        });
        btnPlay2_0 = (ImageButton)findViewById(R.id.btnPlay2_0);
        btnPlay2_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSpeed( 2.0f );
                btnPlay2_0.setImageResource(R.drawable.on_ff2_0);
            }
        });

        // 연주할 파일의 파일 이름을 가져 옴.
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("filename");
        // 파일 이름으로 부터 연주할 데이터를 가져옴.
        mSongData.loadFromFile( getFilesDir(), fileName);
        // 곡 정보(제목,템포)를 표시
        TextView title = (TextView)findViewById(R.id.textSongTitle);
        title.setText(mSongData.mSongTitle);
        TextView bpmText = (TextView)findViewById(R.id.textTempo);
        bpmText.setText("♩="+mSongData.mBpm);

        mPlayingView.setSongData(mSongData);
        // initialize local data
        endofSong = mSongData.mStartOffset + mSongData.timeStamp[mSongData.numNotes-1] + 2000 + 3000;       // 마지막 노트 데이터의 2초 뒤.    +3000 은 setPlayPosition 에서의 옵셋
        mScoreData = new long[mSongData.numNotes];      // 각 음당 평가점수를 저장할 배열

        // initialize local data
        mMetronom = new Metronom(this);
        mMetronom.setBpm(mSongData.mBpm);        // defailt 100 BPM for test
        mMetronom.setBeat(4);                   // 4/4 박자 메트로놈.
        // 녹음 시작,
        mRecording = new Recording();
        mThread = new Thread(this);
        mThread.start();
        running = true;
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mp = new MediaPlayer();
        Log.d("ukulele", "!@@@@@@@@@@ MP3 Play ? @@@@@@@ : " + getFilesDir()+"/"+mSongData.mMusicURL );
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                total_playLength = mp.getDuration();
            }
        });

        try {
            mp.reset();
            mp.setDataSource( getFilesDir()+"/"+mSongData.mMusicURL );
            mp.prepare();
            total_playLength = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMetronom.start(mGameStartClock);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        metronom_on = preferences.getBoolean("playing_metronom_onoff", false);
        Log.d("ukulele", "metronom : " + metronom_on );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayingView = null;
    }

    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
//        mPlayingView = null;
        mRecording.end();
        mp.pause();
        this.finish();
        running = false;

        Intent i = new Intent();
        ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
        i.setComponent(name);
        i.putExtra("mode", "PlayingMode");
        startActivity(i);
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
//        mPlayingView.resume();
        mRecording.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.
        mp.start();
//        total_playLength = mp.getDuration();
    }

    public void run() {
        long playing_clock = 0;
        long stroked_clock = 0;

        playing_pos = 0;

        while (running) {
            if ( (mp != null)&&(mp.isPlaying()) ) {
                playing_clock = mp.getCurrentPosition();
//                if (mp.isPlaying())
//                    total_playLength = mp.getDuration();
//                Log.d("ukulele", "Playing Position = " + playing_clock + " / " + total_playLength );
//            } else {
//                playing_clock = System.currentTimeMillis() - mGameStartClock;
            }
            if (endofSong <= playing_clock ) {
                Log.d("ukulele", "End of this song." + playing_clock + "("+endofSong+")" );
                finish();
                break;          // break for while.
            }
            if (mPlayingView != null ) {
                mPlayingView.setPlayPosition( playing_clock );

                mRecording.parseSpectrum();
                mPlayingView.setPlayedNote(mRecording.notes_detected);
                mPlayingView.setSpectruData(mRecording.spectrum);
            }

            if ( mRecording.isStroked() ) {
                // 스트로크 입력된 시점의 time stamp 를 일단 저장.
                stroked_clock = playing_clock;
                // searching for nearest note from 악보.
            }

            // 메트로놈 소리.
            if (metronom_on) {
                mMetronom.running(System.currentTimeMillis());
            }
            mPlayingView.invalidate();
        }
    }

    private int search_nearest_note( long clock, int start_index ) {
        long minimum_timediff = 999999;
        long timediff = 0;
        if (mSongData.timeStamp[start_index] > clock) { // start 로 지정한 index 의 clock 이 아직 미래의 것 이라면, 최근의 과거의 것으로 거슬러 올라가서 시작하기로 한다.
            while(start_index > 0) {
                if (mSongData.timeStamp[start_index] < clock)
                    break;
                start_index--;
            }
        }
        // 지정한 index 부터 검색해서 가장 가까운 timestamp 의 index 를 찾아 리턴하자.
        int  ret_index = 0;
        for (int i = start_index; i<mSongData.timeStamp.length; i++) {
            timediff = mSongData.timeStamp[i] - clock;
            if (timediff < 0) timediff = -1*timediff;   // 절대값 계산
            if (minimum_timediff > timediff) {
                minimum_timediff = timediff;
                ret_index = i;
            }
        }
        return ret_index;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
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

    private void changeSpeed(float speed) {
        btnPlay0_5.setImageResource(R.drawable.off_slow0_5);
        btnPlay0_75.setImageResource(R.drawable.off_slow0_75);
        btnPlay1_0.setImageResource(R.drawable.off_ff1_0);
        btnPlay1_25.setImageResource(R.drawable.off_ff1_25);
        btnPlay1_5.setImageResource(R.drawable.off_ff1_5);
        btnPlay2_0.setImageResource(R.drawable.off_ff2_0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this, "Playing.." + speed+"x", Toast.LENGTH_SHORT).show();
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));
        } else {
            Toast.makeText(this, "Need to upgrade to M or over", Toast.LENGTH_SHORT).show();
        }
    }
}
