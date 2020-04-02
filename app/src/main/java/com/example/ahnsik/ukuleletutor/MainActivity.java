package com.example.ahnsik.ukuleletutor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 121106;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 17;
    private static final String BGVIDEO_PATH = "android.resource://com.example.ahnsik.ukuleletutor/"+R.raw.tropical_beach;       // or ""http://ccash.iptime.org/videoviewdemo.mp4" from Online-Server

    private VideoView   videoView;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Context thisActivity = this.getApplicationContext();
        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
//            ActivityCompat.requestPermissions((Activity) thisActivity, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_RECORD_AUDIO);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        videoView = (VideoView)findViewById(R.id.fullscreen_video);

        videoView.setVideoURI( Uri.parse(BGVIDEO_PATH) );
        //videoView.setMediaController(new MediaController(VideoTestClassActivity.this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {    // Video 무한 루프로 재생하도록 함.
                mp.setLooping(true);
                mp.setScreenOnWhilePlaying(false);      // Video 가 무한으로 재생되고 있더라도 Sleep 모드로 들어갈 수 있게 해 주려면 false 를 설정.
            }
        });
        videoView.start();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button btnGuideTop = (Button)findViewById(R.id.btnGuideTop);
        btnGuideTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.GuideTopActivity");
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
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
