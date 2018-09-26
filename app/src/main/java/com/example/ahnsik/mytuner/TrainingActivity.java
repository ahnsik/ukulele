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

    private final static float PEAK_MINIMUM_DB=0.6f;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,

    private PlayView  mGameView;
    private NoteData  mSongData = new NoteData();
    private Recording mRecording;

    //  locally used variables.
    private boolean   display_notes[];
    private double    decibel;
    private final static int NUM_OF_NOTE_UKE=36;                // G3 ~ F6 까지..

    private Thread    mThread = null;
    private long      mGameStartClock = 0;

    //  locally referenced tables.
    private final static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };
    private final static int base_minimum_freq_table[] = {      // 음계를 찾기 위한 주파수 범위의 최소값
            190, 201, 213, 226, 239, 253, 269, 285, 302, 320,
            339, 359, 380, 403, 427, 453, 479, 508, 538, 570,
            604, 640, 678, 718, 761, 806, 855, 906, 959, 1016,
            1077,1141,1209,1281,1357,1437,
    };


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
        display_notes = new boolean[NUM_OF_NOTE_UKE];
        decibel = 0.0;
        // 녹음 시작,
        mRecording = new Recording();

        mThread = new Thread(this);
        mThread.start();
        mGameStartClock = System.currentTimeMillis();     // 시작 싯점의 시스템 클럭을 저장.

        mGameView.setPlayPosition(mGameStartClock);
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
        long game_clock;

        while(true) {
            game_clock = System.currentTimeMillis() - mGameStartClock;
            mGameView.setPlayPosition( game_clock );
            parseSpectrum();
        }
    }

    public void parseSpectrum() {
        // 우선 스펙트럼 데이터를 살짝 뭉개서 주변값들을 통합할 필요가 있다.
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        // 그 주파수를 기준으로 음계를 찾아 플래그 설정.
        if (mRecording==null)   return;
        if (mRecording.spectrum==null)   return;
        int length = mRecording.spectrum.length;
        double[] blur = new double[length];
        blur[0] = mRecording.spectrum[0];
        for (int i = 1; i<length-1; i++) {
            blur[i] = (mRecording.spectrum[i-1]+mRecording.spectrum[i]+mRecording.spectrum[i+1]) / 3.0f;
        }
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        for (int i=0; i<display_notes.length; i++)
            display_notes[i] = false;
        for (int i = 1; i< (length/2)-1; i++) {
            if ( (blur[i-1]<blur[i])&&(blur[i]>blur[i+1]) && (mRecording.magnitude(i) > PEAK_MINIMUM_DB) ) {   // PEAK 값
                double frequency = mRecording.frequency(i);
                // 주파수에 해당하는 음계를 찾아 플래그 설정
                display_notes[ findIndex(mRecording.frequency(i)) ]=true;
            }
        }
        mGameView.setPlayedNote(display_notes);
        mGameView.setSpectruData(mRecording.spectrum);
    }

    private int findIndex(double freq) {
        int min=0, max=base_minimum_freq_table.length;
        int index = max/2;
        while( (max-min)>0 ) {       // binary search.
            if (base_minimum_freq_table[index] > freq ) {
                max = index;
                index = min+(max-min)/2;
            } else {
                min = index;
                index = min+(max-min)/2;
            }
            if ((max-min) <= 1) break;
        }
        return index;       //주파수 테이블 내에서 주어진 주파수값에 해당하는 범위의 index 를 리턴 함.
    }

}
