package com.example.ahnsik.mytuner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrainingActivity extends AppCompatActivity implements Runnable {

    private PlayView  mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameView = new PlayView(this);
        setContentView(mGameView);

    }

    public void run() {
        long game_clock;
        while(true) {

        }
    }
}
