package com.example.ahnsik.ukuleletutor;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TuningActivity extends AppCompatActivity implements Runnable {

    public float mDetectedFreq = 0;
    public double mDetectedIntensity = 0;
    private TunerMessageHander mHandler;
    private TuneIndicatorView   tuneIndicatorView;

//    private TuneIndicatorView   tuneIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        Toast toast = Toast.makeText(getApplicationContext(),"튜닝 기능은 아직 많이 부족합니다. 사용이 좀 불편하며 불안정 합니다.", Toast.LENGTH_SHORT);
//        toast.show();

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tuneIndicatorView = (TuneIndicatorView) findViewById(R.id.imgTunedNote);

        mHandler = new TunerMessageHander();
        startTuningTask();
    }


    /////////////////////// 튜닝 하면서 필요한 메세지를 핸들링 - UI 업데이트를 위함. /////////////

    @SuppressLint("HandlerLeak")
    public class TunerMessageHander extends Handler {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message m) {
            Log.d("ukulele", "Message Handler !! m="+m );
            String detectedNote;

            TextView freqText = (TextView) findViewById(R.id.txtTunedFreq);
            float freq = m.getData().getFloat("Freq");
            freqText.setText(":"+freq);
            tuneIndicatorView.setDetectFreq(mDetectedFreq);
            tuneIndicatorView.invalidate();
            Log.d("ukulele", "[][][][][][][][] Draw CustomView [][][][][][]" + mDetectedFreq + "Hz");

            detectedNote = FindNote.NoteName(freq);

            TextView statusText = (TextView) findViewById(R.id.txtWhatToDo);
            statusText.setText( "center:"+FindNote.getCenterFreq(detectedNote) );

            ImageView nob_image = (ImageView) findViewById(R.id.imgTune);
            switch(detectedNote) {
                case "G4" :
                case "G3" :
                    nob_image.setImageResource( R.drawable.ic_ukulele_tune_g);
                    break;
                case "C4" :
                    nob_image.setImageResource( R.drawable.ic_ukulele_tune_c);
                    break;
                case "E4" :
                    nob_image.setImageResource( R.drawable.ic_ukulele_tune_e);
                    break;
                case "A4" :
                    nob_image.setImageResource( R.drawable.ic_ukulele_tune_a);
                    break;
                default:
                    nob_image.setImageResource( R.drawable.ic_ukulele_tune_none);
                    break;
            }
        }
    }



    /////////////////////// Audio Record 그리고 주파수 검출에 사용되는 함수들. /////////////

    private AudioRecord mAudioRecord;
    private static final int[] SAMPLE_RATES = {44100, 22050, 16000, 11025, 8000};
    private Thread mThread = null;
    private boolean mThreadRunning = false;

    private static final int SAMPLERATE_DIVIDER = 4;        // 버퍼 크기를 조정하에 따른 샘플레이트 분배값. - 2의 승수로 할 것. 이 값이 커질 수록 버퍼의 크기를 작게 할 수 있으므로, 화면 Update가 빨라짐.
    private static final int BUFFSIZE_DIVIDER = (SAMPLERATE_DIVIDER*2);     // Audio녹음버퍼의 크기를 줄임으로써 화면 Refresh 속도가 빨라짐. - 튜닝에 유리하다.

    private void startTuningTask() {

        // 각 주파수 별로 오디오 버퍼 크기를 계산하여 AudioRecord 객체를 생성하는 함수.
        int bufSize = 16384;
        int avalaibleSampleRates = SAMPLE_RATES.length;
        int i = 0;
        do {
            int sampleRate = SAMPLE_RATES[i] / SAMPLERATE_DIVIDER;
            int minBufSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (minBufSize != AudioRecord.ERROR_BAD_VALUE && minBufSize != AudioRecord.ERROR) {
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, Math.max(bufSize, minBufSize * 4));
            }
            i++;
        }
        while (i < avalaibleSampleRates && (mAudioRecord == null || mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED));

        // AudioRecord 객체를 생성했다면, 녹음을 곧바로 시작.
        mAudioRecord.startRecording();
        Log.d("ukulele", "StartRecording.");
        if ( mThread == null ) {
            mThread = new Thread(this);
            mThreadRunning = true;
            mThread.start();
            Log.d("ukulele", "Thread Start.");
        }
    }

    private void stopTuningTask() {
        mThreadRunning = false;
        mAudioRecord.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ukulele", "$$$$$$$$$$ Tuning Activity is paused.");
        stopTuningTask();
        this.finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }



    public void run() {

        int bufSize = 8192 / BUFFSIZE_DIVIDER;
        final int sampleRate = mAudioRecord.getSampleRate();
        final short[] buffer = new short[bufSize];

        if (mAudioRecord == null)
            return;

        Log.d("ukulele", "Detected : Record is OK, anyway.");

        while (mThreadRunning) {
            // 녹음된 데이터를 버퍼로 읽어 들이고
            final int readLength = mAudioRecord.read(buffer, 0, bufSize);

            if (readLength > 0) {            // 읽어 낸 데이터가 존재한다면.
//                Log.d("ukulele", "readLength : " + readLength );
                final double intensity = averageIntensity(buffer, readLength);       // 읽어 낸 데이터들의 평균값 (평균 음량)을 계산.
                int maxZeroCrossing = (int) (380 * (readLength / (8192/BUFFSIZE_DIVIDER) ) * (sampleRate / (44100/SAMPLERATE_DIVIDER) ));   // 650을 기준으로 샘플링 주파수와 버퍼 설정에 맞게 비례하여 최대 값을 지정.

                if (intensity >= 6 && zeroCrossingCount(buffer) <= maxZeroCrossing) {      // 음량이 최소 20 이상, 주파수(?)가 너무 노이즈가 심하지 않은 데이터에 대해서만 확인
                    float freq = getPitch(buffer, readLength / 4, readLength, sampleRate, 150, 650);
                    mDetectedFreq = freq;
                    mDetectedIntensity = intensity;

                    // 검출한 주파수를 String 으로 해서 문자열로 메세지를 전송 --> UI 화면 표시를 고치도록 함.
                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putFloat("Freq", freq );      // 주파수 값을 넣어서 메세지 전송함.
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                    Log.d("ukulele", "Detected Freq: " + mDetectedFreq + ",  Intensity: " + mDetectedIntensity );

                } else {
//                    Log.d("ukulele", "intensity : " + intensity +",  maxZeroCrossing: "+ maxZeroCrossing );
                }
            }
        }
    }

    private double averageIntensity(short[] data, int frames) {
        double sum = 0;
        for (int i = 0; i < frames; i++) {
            sum += Math.abs(data[i]);
        }
        return sum / frames;
    }

    private int zeroCrossingCount(short[] data) {       // 음량데이터가 음수/양수가 교차되는 부분의 갯수를 카운트 함.
        int len = data.length;
        int count = 0;
        boolean prevValPositive = data[0] >= 0;
        for (int i = 1; i < len; i++) {
            boolean positive = data[i] >= 0;
            if (prevValPositive == !positive)
                count++;

            prevValPositive = positive;
        }
        return count;
    }

            // 드디어 주파수(?)를 계산하는 함수.
    private float getPitch(short[] data, int windowSize, int frames, float sampleRate, float minFreq, float maxFreq) {

        float maxOffset = sampleRate / minFreq;
        float minOffset = sampleRate / maxFreq;

        int minSum = Integer.MAX_VALUE;
        int minSumLag = 0;
        int[] sums = new int[Math.round(maxOffset) + 2];

        for (int lag = (int) minOffset; lag <= maxOffset; lag++) {
            int sum = 0;
            for (int i = 0; i < windowSize; i++) {
                int oldIndex = i - lag;
                int sample = ((oldIndex < 0) ? data[frames + oldIndex] : data[oldIndex]);
                sum += Math.abs(sample - data[i]);
            }
            sums[lag] = sum;
            if (sum < minSum) {
                minSum = sum;
                minSumLag = lag;
            }
        }

        // quadratic interpolation
        float delta = (float) (sums[minSumLag + 1] - sums[minSumLag - 1]) / ((float)
                (2 * (2 * sums[minSumLag] - sums[minSumLag + 1] - sums[minSumLag - 1])));
        return sampleRate / (minSumLag + delta);
    }

}
