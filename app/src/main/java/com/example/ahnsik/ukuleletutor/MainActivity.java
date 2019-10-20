package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final String BGVIDEO_PATH = "android.resource://com.example.ahnsik.ukuleletutor/"+R.raw.tropical_beach;       // or ""http://ccash.iptime.org/videoviewdemo.mp4" from Online-Server

    private VideoView   videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        videoView = (VideoView)findViewById(R.id.fullscreen_video);

        videoView.setVideoURI( Uri.parse(BGVIDEO_PATH) );
        //videoView.setMediaController(new MediaController(VideoTestClassActivity.this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {    // Video 무한 루프로 재생하도록 함.
                mp.setLooping(true);
            }
        });
        videoView.start();

        Button btnSetup = (Button)findViewById(R.id.btnSetup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.SetupMenuActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnChordTable = (Button)findViewById(R.id.btnChordTable);
        btnChordTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("ukulele", "Show major chord tables. ");
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.ChordTableActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnTraining = (Button)findViewById(R.id.btnTraining);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
                i.setComponent(name);
                i.putExtra("mode", "TrainingMode");
                startActivity(i);
            }
        });

        Button btnPlaying = (Button)findViewById(R.id.btnPlaying);
        btnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
                i.setComponent(name);
                i.putExtra("mode", "PlayingMode");
                startActivity(i);

//                Toast toast = Toast.makeText(getApplicationContext(), "연주하며 연습하는 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT);
//                toast.show();
            }
        });
    }


    @Override
    public void onPause() {
        Log.d("ukulele", "!@@@@@@@@@@ onPause() ------------- ");
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        Log.d("ukulele", "!@@@@@@@@@@ onResume() -------------");
        super.onResume();
        videoView.start();
    }

}
