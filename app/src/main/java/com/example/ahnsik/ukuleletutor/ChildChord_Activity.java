package com.example.ahnsik.ukuleletutor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChildChord_Activity extends AppCompatActivity {

    private String  chord_name;
    private String title_resource;
    private int     bmp_resource;
    private int     audio_resource;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = this.getIntent();
        String activityName = intent.getStringExtra("childActivity");
        Log.d("ukulele", "[][][][] Intent Parameter : "+activityName );

        switch(activityName) {
            //case "ChordC_Activity":       // default 값으로 옮겼음.
            case "ChordC7_Activity":
                chord_name = "C7";
                title_resource = "코드C7: G4-C4-E4-A#4 도-미-솔-라#";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_c7;
                audio_resource = R.raw.chord_c7;
                break;
            case "ChordCm_Activity":
                chord_name = "Cm";
                title_resource = "코드Cm: G4-D4s-G4-C5 솔레#솔도";    // R.string.c_g4_d4s_g_c;
                bmp_resource = R.drawable.chord_cm;
                audio_resource = R.raw.chord_cm;
                break;
            case "ChordCm7_Activity":
                chord_name = "Cm7";
                title_resource = "코드 Cm7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_cm7;
                audio_resource = R.raw.chord_cm7;
                break;
            case "ChordD_Activity":
                chord_name = "D";
                title_resource = "코드 D";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_d;
                audio_resource = R.raw.chord_d;
                break;
            case "ChordD7_Activity":
                chord_name = "D7";
                title_resource = "코드 D7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_d7;
                audio_resource = R.raw.chord_d7;
                break;
            case "ChordDm_Activity":
                chord_name = "Dm";
                title_resource = "코드 Dm";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_dm;
                audio_resource = R.raw.chord_dm;
                break;
            case "ChordDm7_Activity":
                chord_name = "Dm7";
                title_resource = "코드 Dm7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_dm7;
                audio_resource = R.raw.chord_dm7;
                break;
            case "ChordE_Activity":
                chord_name = "E";
                title_resource = "코드 E";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_e;
                audio_resource = R.raw.chord_e;
                break;
            case "ChordE7_Activity":
                chord_name = "E7";
                title_resource = "코드 E7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_e7;
                audio_resource = R.raw.chord_e7;
                break;
            case "ChordEm_Activity":
                chord_name = "Em";
                title_resource = "코드 Em";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_em;
                audio_resource = R.raw.chord_em;
                break;
            case "ChordEm7_Activity":
                chord_name = "Em7";
                title_resource = "코드 Em7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_em7;
                audio_resource = R.raw.chord_em7;
                break;
            case "ChordF_Activity":
                chord_name = "F";
                title_resource = "코드 F";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_f;
                audio_resource = R.raw.chord_f;
                break;
            case "ChordF7_Activity":
                chord_name = "F7";
                title_resource = "코드 F7";    // R.string.c_g4_c4_e4_a4s;
                bmp_resource = R.drawable.chord_f7;
                audio_resource = R.raw.chord_f7;
                break;
            case "ChordFm_Activity":
                chord_name = "Fm";
                title_resource = "코드 Fm";
                bmp_resource = R.drawable.chord_fm;
                audio_resource = R.raw.chord_fm;
                break;
            case "ChordG_Activity":
                chord_name = "G";
                title_resource = "코드 G";    //
                bmp_resource = R.drawable.chord_g;
                audio_resource = R.raw.chord_g;
                break;
            case "ChordG7_Activity":
                chord_name = "G7";
                title_resource = "코드 G7";    //
                bmp_resource = R.drawable.chord_g7;
                audio_resource = R.raw.chord_g7;
                break;
            case "ChordGm_Activity":
                chord_name = "Gm";
                title_resource = "코드 Gm";    //
                bmp_resource = R.drawable.chord_gm;
                audio_resource = R.raw.chord_gm;
                break;
            case "ChordGm7_Activity":
                chord_name = "Gm7";
                title_resource = "코드 Gm7";    //
                bmp_resource = R.drawable.chord_gm7;
                audio_resource = R.raw.chord_gm7;
                break;
            case "ChordA_Activity":
                chord_name = "A";
                title_resource = "코드 A";    //
                bmp_resource = R.drawable.chord_a;
                audio_resource = R.raw.chord_a;
                break;
            case "ChordA7_Activity":
                chord_name = "A7";
                title_resource = "코드 A7";    //
                bmp_resource = R.drawable.chord_a7;
                audio_resource = R.raw.chord_a7;
                break;
            case "ChordAm_Activity":
                chord_name = "Am";
                title_resource = "코드 Am";    //
                bmp_resource = R.drawable.chord_am;
                audio_resource = R.raw.chord_am;
                break;
            case "ChordAm7_Activity":
                chord_name = "Am7";
                title_resource = "코드 Am7";    //
                bmp_resource = R.drawable.chord_am7;
                audio_resource = R.raw.chord_am7;
                break;
            case "ChordB_Activity":
                chord_name = "B";
                title_resource = "코드 B";    //
                bmp_resource = R.drawable.chord_b;
                audio_resource = R.raw.chord_b;
                break;
            case "ChordB7_Activity":
                chord_name = "B7";
                title_resource = "코드 B7";    //
                bmp_resource = R.drawable.chord_b7;
                audio_resource = R.raw.chord_b7;
                break;
            case "ChordBm_Activity":
                chord_name = "Bm";
                title_resource = "코드 Bm";    //
                bmp_resource = R.drawable.chord_bm;
                audio_resource = R.raw.chord_bm;
                break;
            case "ChordBm7_Activity":
                chord_name = "Bm7";
                title_resource = "코드 Bm7";    //
                bmp_resource = R.drawable.chord_bm7;
                audio_resource = R.raw.chord_bm7;
                break;
            default:
                chord_name = "C";
                title_resource = "코드C : G4-C4-E4-C5 (도미솔)기본 화음";
                bmp_resource = R.drawable.chord_c;
                audio_resource = R.raw.chord_c;
                break;
        }
        setContentView(R.layout.activity_childchord);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RelativeLayout bg = (RelativeLayout)findViewById(R.id.chord_bg);         // 배경 바꾸기.
        bg.setBackgroundResource(bmp_resource);

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(title_resource);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mp = MediaPlayer.create(this, audio_resource);

        final Button btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // https://developer88.tistory.com/58  여길 참고 했음
                if (mp != null) {
                    TogglePlaying();
                } else {
                    mp = MediaPlayer.create(getApplicationContext(), audio_resource);
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
