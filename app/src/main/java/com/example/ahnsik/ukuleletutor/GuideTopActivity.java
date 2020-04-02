package com.example.ahnsik.ukuleletutor;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GuideTopActivity extends AppCompatActivity {

    private int     hiddenTouchCount = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_top);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        TextView txtAppTitle = (TextView) findViewById(R.id.txtAppTitle);
        txtAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenTouchCount++;
                Log.d("ukulele", "touch 7th will show the management activity : count=" + hiddenTouchCount);
                if (hiddenTouchCount >= 7) {
                    Intent i = new Intent();
                    ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TerminalActivity");
                    i.setComponent(name);
                    startActivity(i);
                    hiddenTouchCount = 0;
                }
            }       // end of onClick
        });
        hiddenTouchCount = 0;
    }

}
