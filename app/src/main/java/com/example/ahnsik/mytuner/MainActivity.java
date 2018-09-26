package com.example.ahnsik.mytuner;

import android.animation.ArgbEvaluator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button btnTuning = (Button)findViewById(R.id.btnTuning);
        btnTuning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.mytuner", "com.example.ahnsik.mytuner.TuningActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnTraining = (Button)findViewById(R.id.btnTraining);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.mytuner", "com.example.ahnsik.mytuner.FileSelectorActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnPlaying = (Button)findViewById(R.id.btnPlaying);
        btnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.mytuner", "com.example.ahnsik.mytuner.PlayingActivity");
                i.setComponent(name);
                startActivity(i);

                Toast toast = Toast.makeText(getApplicationContext(), "연주하며 연습하는 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
