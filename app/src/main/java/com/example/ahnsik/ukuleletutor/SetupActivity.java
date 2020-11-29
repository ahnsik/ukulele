package com.example.ahnsik.ukuleletutor;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SetupActivity extends AppCompatActivity {

    private int     hiddenTouchCount = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );

        CheckBox chkPlayingMetronomOnOff = (CheckBox)findViewById(R.id.chkPlayingMetronomOnOff);
        chkPlayingMetronomOnOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                if ( ((CheckBox)view).isChecked() ) {
                    // TODO : CheckBox is checked.
                    Log.d("ukulele", "Playing Metronom ON !");
                    editor.putBoolean("playing_metronom_onoff", true);
                } else {
                    // TODO : CheckBox is unchecked.
                    Log.d("ukulele", "Playing Metronom OFF !");
                    editor.putBoolean("playing_metronom_onoff", false);
                }
            }
        });
        if (preferences.getBoolean("playing_metronom_onoff", false )) {
            chkPlayingMetronomOnOff.setChecked(true);
        } else {
            chkPlayingMetronomOnOff.setChecked(false);
        }

        CheckBox chkTraingMetronomOnOff = (CheckBox)findViewById(R.id.chkTraingMetronomOnOff);
        chkTraingMetronomOnOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                if ( ((CheckBox)view).isChecked() ) {
                    // TODO : CheckBox is checked.
                    Log.d("ukulele", "Training Metronom ON !");
                    editor.putBoolean("training_metronom_onoff", true);
                } else {
                    // TODO : CheckBox is unchecked.
                    Log.d("ukulele", "Training Metronom OFF !");
                    editor.putBoolean("training_metronom_onoff", false);
                }
            }
        });
        if (preferences.getBoolean("training_metronom_onoff", true )) {
            chkTraingMetronomOnOff.setChecked(true);
        } else {
            chkTraingMetronomOnOff.setChecked(false);
        }

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
