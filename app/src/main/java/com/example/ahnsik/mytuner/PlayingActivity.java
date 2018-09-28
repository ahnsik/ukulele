package com.example.ahnsik.mytuner;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayingActivity extends AppCompatActivity {

    private View mContentView;
    private VideoView   videoView;
    private GameView    surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        mContentView = findViewById(R.id.fullscreen_video);
        videoView = (VideoView)findViewById(R.id.fullscreen_video);

        videoView.setVideoURI(Uri.parse("http://ccash.iptime.org/videoviewdemo.mp4"));
        videoView.setMediaController(new MediaController(PlayingActivity.this));
        videoView.requestFocus();
        videoView.start();

//        surfaceView = (GameView)findViewById(R.id.gameview_overlay);
    }

}
