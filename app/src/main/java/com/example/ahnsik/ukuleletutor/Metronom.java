package com.example.ahnsik.ukuleletutor;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import static android.media.AudioManager.STREAM_MUSIC;

public class Metronom {

    private int       mInterval;    // delay (wating) time
    private int       mBeat;        // 박자   2 = 2/4,  3 = 3/4,  4 = 4/4,  6 = 6/8
    private int       mBeatCount;   // 박자 세기, 강박자를 구별하기 위함.
    private SoundPool pool;         // 사운드 풀
    private int       tik, tok;     // 강조박자, 보통박자
    private long      sysClock, last_clock;     // 시스템 클럭.

    public Metronom(Context context)   {
        pool = new SoundPool(2, STREAM_MUSIC, 0);
        tik = pool.load(context, R.raw.metronom_tik, 1);
        tok = pool.load(context, R.raw.metronom_tok, 1);
        mBeatCount = 0;
        mBeat = 4;          // default = 4/4박자
        mInterval = (int)(60000.0f / 80);   // default = 80 bpm.
        sysClock = -1;      // 아직 시작 안했음
    }

    public void setBeat(int beat) {      // 박자   2 = 2/4,  3 = 3/4,  4 = 4/4,  6 = 6/8
        mBeat = beat;
        mBeatCount = 0;
    }

    public void setBpm(float bpm) {
        mInterval = (int)(60000.0f / bpm);        // 1분= 60초 * milliseconds.
    }

    public void start(long starting_clock) {
        sysClock = last_clock = starting_clock;
    }

    public void stop() {
        sysClock = -1;
    }

    public void running(long system_clock) {
        if (sysClock == -1)  {      // 아직 메트로놈 시작 안했음.
            return;
        }
        sysClock = system_clock;
        if ( sysClock >= last_clock+mInterval ) {
            if ( (mBeatCount % mBeat) == 0) {
                pool.play(tik, 0.4f, 0.4f, 1, 0, 1.0f);
                Log.d("ukulele", " tik..");
            }
            else {
                pool.play(tok, 0.4f, 0.4f, 1, 0, 1.0f);
                Log.d("ukulele", " tok..");
            }
            last_clock = last_clock+mInterval;
            mBeatCount++;
        }
    }
}
