package com.example.ahnsik.ukuleletutor;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BpmToolActivity extends AppCompatActivity {

    private TextView txtLastBpm, txtAverageBpm, txtTime;
    private Long firstTouched, latestTouch ;
    private Long lastBpm, avBpm, touchCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm_tool);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtLastBpm = (TextView)findViewById(R.id.txtLastBpm);
        txtAverageBpm = (TextView)findViewById(R.id.txtAverageBpm);
        txtTime = (TextView)findViewById(R.id.txtTime);

        firstTouched = 0L;
        latestTouch = 0L;
        touchCount = 0L;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if ( e.getAction() != MotionEvent.ACTION_DOWN ) {
            return  false;
        }

        if (latestTouch != null) {
            Long    temp_timeDiff = System.currentTimeMillis()-latestTouch;
            latestTouch = System.currentTimeMillis();
            // 1분은 60초=60000msec, 1000분의1 사이즈로 책임 지라고 했다.
            if (temp_timeDiff != 0) {
                lastBpm = 60000L / temp_timeDiff;
                txtLastBpm.setText("Last BPM:"+ lastBpm );
                Log.d("ukulele", "Last BPM:"+ lastBpm +", touchCount=" + touchCount);
            }
        }

        if (touchCount == 0) {
            firstTouched = System.currentTimeMillis();
        } else {
            Long    timeDiff = System.currentTimeMillis()-firstTouched;
            avBpm = touchCount*60000L / timeDiff ;
            txtAverageBpm.setText("Average BPM:"+ avBpm );
            Log.d("ukulele", "Last BPM:"+ lastBpm +"Average BPM:"+ avBpm +", touchCount=" + touchCount);

            txtTime.setText("Time from first touch:" + timeDiff );
        }

        touchCount ++;
        return true;
    }

}
