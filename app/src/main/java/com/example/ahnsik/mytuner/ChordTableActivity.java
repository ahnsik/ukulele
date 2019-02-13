package com.example.ahnsik.mytuner;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChordTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_table);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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

    }

    protected void gotoActivity(String activityName)
    {
        Intent i = new Intent();
        ComponentName name= new ComponentName("com.example.ahnsik.mytuner", "com.example.ahnsik.mytuner."+activityName);
        i.setComponent(name);
        startActivity(i);
    }
}
