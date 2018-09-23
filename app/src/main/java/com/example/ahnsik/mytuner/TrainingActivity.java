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
    private NoteData  mSongData = new NoteData();

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

        mSongData.loadFromFile( getFilesDir(), fileName);
        mGameView.setSongData(mSongData);

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mGameView.setPlayPosition(mGameStartClock);
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
