package com.example.ahnsik.ukuleletutor;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoTestClassActivity extends AppCompatActivity {

    private View mContentView;
    private VideoView   videoView;
    private GameView    surfaceView;
    private static boolean toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test_class);

        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        mContentView = findViewById(R.id.fullscreen_video);
        videoView = (VideoView)findViewById(R.id.fullscreen_video);

        videoView.setVideoURI(Uri.parse("http://ccash.iptime.org/videoviewdemo.mp4"));
//        videoView.setMediaController(new MediaController(VideoTestClassActivity.this));
        videoView.requestFocus();
        videoView.start();

        Button btnJustBtn = (Button)findViewById(R.id.btnJustBtn);
        btnJustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "시험용 Toast. VideoView 위에 OSD 그리기 - 메뉴를 위해", Toast.LENGTH_SHORT);
                toast.show();

                if (toggle) {
                    videoView.start();
                } else {
                    videoView.pause();
                }
                toggle = !toggle;
            }
        });

//        surfaceView = (GameView)findViewById(R.id.gameview_overlay);
    }
}
