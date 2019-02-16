package com.example.ahnsik.mytuner;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChordTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_table);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_C = (Button)findViewById(R.id.btnChord_C);
        btnChord_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordC_Activity");
            }
        });

        Button btnChord_C7 = (Button)findViewById(R.id.btnChord_C7);
        btnChord_C7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordC7_Activity");
            }
        });

        Button btnChord_Cm = (Button)findViewById(R.id.btnChord_Cm);
        btnChord_Cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordCm_Activity");
            }
        });

        Button btnChord_Cm7 = (Button)findViewById(R.id.btnChord_Cm7);
        btnChord_Cm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordCm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_D = (Button)findViewById(R.id.btnChord_D);
        btnChord_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordD_Activity");
            }
        });

        Button btnChord_D7 = (Button)findViewById(R.id.btnChord_D7);
        btnChord_D7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordD7_Activity");
            }
        });

        Button btnChord_Dm = (Button)findViewById(R.id.btnChord_Dm);
        btnChord_Dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordDm_Activity");
            }
        });

        Button btnChord_Dm7 = (Button)findViewById(R.id.btnChord_Dm7);
        btnChord_Dm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordDm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_E = (Button)findViewById(R.id.btnChord_E);
        btnChord_E.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordE_Activity");
            }
        });

        Button btnChord_E7 = (Button)findViewById(R.id.btnChord_E7);
        btnChord_E7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordE7_Activity");
            }
        });

        Button btnChord_Em = (Button)findViewById(R.id.btnChord_Em);
        btnChord_Em.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordEm_Activity");
            }
        });

        Button btnChord_Em7 = (Button)findViewById(R.id.btnChord_Em7);
        btnChord_Em7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordEm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_F = (Button)findViewById(R.id.btnChord_F);
        btnChord_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordF_Activity");
            }
        });

        Button btnChord_F7 = (Button)findViewById(R.id.btnChord_F7);
        btnChord_F7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordF7_Activity");
            }
        });

        Button btnChord_Fm = (Button)findViewById(R.id.btnChord_Fm);
        btnChord_Fm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordFm_Activity");
            }
        });

        Button btnChord_Fm7 = (Button)findViewById(R.id.btnChord_Fm7);
        btnChord_Fm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordFm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_G = (Button)findViewById(R.id.btnChord_G);
        btnChord_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordG_Activity");
            }
        });

        Button btnChord_G7 = (Button)findViewById(R.id.btnChord_G7);
        btnChord_G7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordG7_Activity");
            }
        });

        Button btnChord_Gm = (Button)findViewById(R.id.btnChord_Gm);
        btnChord_Gm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordGm_Activity");
            }
        });

        Button btnChord_Gm7 = (Button)findViewById(R.id.btnChord_Gm7);
        btnChord_Gm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordGm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_A = (Button)findViewById(R.id.btnChord_A);
        btnChord_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordA_Activity");
            }
        });

        Button btnChord_A7 = (Button)findViewById(R.id.btnChord_A7);
        btnChord_A7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordA7_Activity");
            }
        });

        Button btnChord_Am = (Button)findViewById(R.id.btnChord_Am);
        btnChord_Am.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordAm_Activity");
            }
        });

        Button btnChord_Am7 = (Button)findViewById(R.id.btnChord_Am7);
        btnChord_Am7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordAm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_B = (Button)findViewById(R.id.btnChord_B);
        btnChord_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordB_Activity");
            }
        });

        Button btnChord_B7 = (Button)findViewById(R.id.btnChord_B7);
        btnChord_B7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordB7_Activity");
            }
        });

        Button btnChord_Bm = (Button)findViewById(R.id.btnChord_Bm);
        btnChord_Bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordBm_Activity");
            }
        });

        Button btnChord_Bm7 = (Button)findViewById(R.id.btnChord_Bm7);
        btnChord_Bm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordBm7_Activity");
            }
        });

    }

    protected void gotoActivity(String activityName)
    {
        Intent i = new Intent();
        ComponentName name= new ComponentName("com.example.ahnsik.mytuner", "com.example.ahnsik.mytuner."+activityName);
        i.setComponent(name);
        startActivity(i);
    }
}
