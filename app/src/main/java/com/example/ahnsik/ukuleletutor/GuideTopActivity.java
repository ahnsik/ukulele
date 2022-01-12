package com.example.ahnsik.ukuleletutor;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GuideTopActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_top);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnExplainUkulele = (Button)findViewById(R.id.btnExplainUkulele);
        btnExplainUkulele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ukulele", "Show major chord tables. ");
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.AboutUkuleleActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnTuning = (Button)findViewById(R.id.btnTuning);
        btnTuning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TuningActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnHowtoReadTAB = (Button)findViewById(R.id.btnHowtoReadTAB);
        btnHowtoReadTAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.HelpReadTabActivity");
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

        Button btnSetup = (Button)findViewById(R.id.btnSetup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("ukulele", "Setup Activity");
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.SetupActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });
    }

/*    // *** 하단 네비게이션 바 숨기기
    public void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility( // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                // Hide the nav bar and status bar
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }
    //출처: https://ddunnimlabs.tistory.com/3 [뚠님 연구소]
*/
}
