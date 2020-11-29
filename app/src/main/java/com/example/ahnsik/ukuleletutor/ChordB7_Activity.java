package com.example.ahnsik.ukuleletutor;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class ChordB7_Activity extends AppCompatActivity {

    private MediaPlayer mp;
    private int mp3_resource = R.raw.chord_b7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_b7);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        mp = new MediaPlayer();
        mp = MediaPlayer.create(this, mp3_resource);

        final Button btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // https://developer88.tistory.com/58  여길 참고 했음
                if (mp != null) {
                    TogglePlaying();
                } else {
                    mp = MediaPlayer.create(getApplicationContext(), mp3_resource);
                    TogglePlaying();
                }
            }

            private void TogglePlaying() {
                if (!mp.isPlaying()) {
                    mp.start();
                    mp.setLooping(true);
                    btnPlay.setText(R.string.btntxt_stop);
                } else {
                    btnPlay.setText(R.string.btntxt_play);
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
}
