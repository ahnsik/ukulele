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

import java.util.Objects;

public class SetupActivity extends AppCompatActivity {

    private int     hiddenTouchCount = 0;

    public  final String  PREFERENCE = "ukuleletutor";
    public  final String  prefkeyPlayingMetronom = "playing_metronom_onoff";
    public  final String  prefkeyTrainingMetronom = "training_metronom_onoff";

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

//        preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );       // PreferenceManager 를 이용해야 저장된다고 한다. - 그런데 이건 아예 기존 값도 유지하지 못하고 있다. 왜 ??
        preferences = getSharedPreferences(PREFERENCE, MODE_PRIVATE); //이건 왠지 파일(?)로 저장되지 않음. 그래서 App 을 재시작 하면 초기화 되어 있었다.  -->  코드 안에 warning 을 제거하고 나니 정상으로 돌아 왔다. 왜 ??

        boolean playingPref = Objects.equals(preferences.getString(prefkeyPlayingMetronom, ""), "on");
        boolean trainingPref = Objects.equals(preferences.getString(prefkeyTrainingMetronom, ""), "on");
        Log.d("ukulele", "playingPref="+playingPref+", trainingPref="+trainingPref );

        CheckBox chkPlayingMetronomOnOff = findViewById(R.id.chkPlayingMetronomOnOff);
        CheckBox chkTraingMetronomOnOff = findViewById(R.id.chkTraingMetronomOnOff);
        chkPlayingMetronomOnOff.setChecked(playingPref);
        chkTraingMetronomOnOff.setChecked(trainingPref);

        chkPlayingMetronomOnOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                if ( ((CheckBox)view).isChecked() ) {
                    Log.d("ukulele", "Playing Metronom ON !");
                    editor.putString( prefkeyPlayingMetronom, "on");
                } else {
                    Log.d("ukulele", "Playing Metronom OFF !");
                    editor.putString( prefkeyPlayingMetronom, "off");
                }
                editor.commit();
                Log.d("ukulele", "preference commit !");
            }
        });

        chkTraingMetronomOnOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                if ( ((CheckBox)view).isChecked() ) {
                    Log.d("ukulele", "Training Metronom ON !");
                    editor.putString( prefkeyTrainingMetronom, "on");
                } else {
                    // TODO : CheckBox is unchecked.
                    Log.d("ukulele", "Training Metronom OFF !");
                    editor.putString( prefkeyTrainingMetronom, "off");
                }
                editor.commit();
                Log.d("ukulele", "preference commit !");
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
