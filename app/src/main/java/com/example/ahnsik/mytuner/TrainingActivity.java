package com.example.ahnsik.mytuner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TrainingActivity extends AppCompatActivity implements Runnable {

    private PlayView  mGameView;
    private NoteData  mSongData;

    private Thread    mThread = null;
    private long      mGameStartClock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameView = new PlayView(this);
        setContentView(mGameView);

        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("filename");

        mSongData = loadDataInfo(fileName);
        mGameView.setSongData(mSongData);

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mGameView.setPlayPosition(mGameStartClock);
    }

    private NoteData loadDataInfo(String fileName) {
        NoteData data;
        String   UkeDataRead;
        UkeDataRead = readTextFile("/storage/sdcard0/" + fileName );
        data = new NoteData(UkeDataRead);
        return  data;
    }

    public String readTextFile(String path) {
        String  datafile = null;
        File file = new File(path);
        String  line;
        try {
            FileReader fr = new FileReader(file);
            if (fr==null) {
                Log.d("ukulele", "File Reader Error:" + fr);
                return null;
            }
            BufferedReader buffrd = new BufferedReader(fr);
            if (buffrd==null) {
                Log.d("ukulele", "File Buffered Read Error:" + buffrd);
                return null;
            }
            datafile = "";
            Log.d("TEST", "Readey to vote !!");
            while ( (line=buffrd.readLine() ) != null) {
                if (line == null || line.trim().length() <= 0) {
                    Log.d("TEST", "Skip Empty line. !!");
                } else if ( (line.charAt(0)=='#') && (line.charAt(1)=='#') ) {     // 처음 시작하는게 ##로 시작하는 라인은 comment 로 처리 함.
                    Log.d("TEST", "This Line is comments. !!" );
                } else {
                    datafile += line;
                }
            }
            Log.d("TEST", "buffrd.close !!");
            buffrd.close();
            fr.close();
            Log.d("TEST", "fullText="+datafile);
        } catch(Exception e) {
            Log.d("TEST", "Exceptions ");
            e.printStackTrace();
        }
        return datafile;
    }

    public void run() {
        long game_clock;

        while(true) {
            game_clock = System.currentTimeMillis() - mGameStartClock;
            mGameView.setPlayPosition( game_clock );
        }
    }

    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
//        mGameView.pause();
//        mRecording.end();
//        mp.pause();
        this.finish();
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
//        mGameView.resume();
//        mRecording.start();
//        mp.start();
    }
}
