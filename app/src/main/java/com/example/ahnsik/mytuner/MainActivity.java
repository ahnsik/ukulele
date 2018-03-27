package com.example.ahnsik.mytuner;

import android.animation.ArgbEvaluator;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setButtonHandlers();
        enableButtons(false);
    }

    private void setButtonHandlers() {
        ((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart, !isRecording);
        enableButton(R.id.btnStop, isRecording);
    }

//    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
//    int BytesPerElement = 2; // 2 bytes in 16bit format
    int bufferSize;

    private void startRecording() {

Log.d("AudioRecording", "startRecording() Function.");

        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize);   //BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

/*  -- 이것은 음성데이터를 기록하기 위한 것일 뿐.  게임 및 튜닝을 위해서는 불필요 하다.
    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
*/

    // 출처 : https://stackoverflow.com/questions/12721254/how-to-calculate-sound-frequency-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    private static int maxAmplitude;
    private static int calculate(int sampleRate, short [] audioData){
        int numSamples = audioData.length;
        int numCrossing = 0;
        maxAmplitude = 0;
        double sumLevel = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0))        // 수평 기준점을 넘나드는 값을 확인. 음수에서 양수로, 양수에서 음수로 변경되는 기회를 카운트 하여 주파수 계산.
            {
                numCrossing++;
            }
            if ( maxAmplitude < audioData[p] )              // 최대 음량을 계산하기 위함.
                maxAmplitude = audioData[p];
        }

        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;
        return (int)frequency;
    }       // 꽤 쓸만한 함수 임.


    private void writeAudioDataToFile() {
        // Write the output audio in byte

//        String filePath = "/sdcard/voice44K16bitmono.pcm";
        short sData[] = new short[bufferSize/2];

/*  -- 이것은 음성데이터를 기록하기 위한 것일 뿐.  게임 및 튜닝을 위해서는 불필요 하다.
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
*/

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, bufferSize/2 );      //BufferElements2Rec);
//            System.out.println("Short wirting to file" + sData.toString());
            int freq = calculate(RECORDER_SAMPLERATE, sData);
//            int maxAmplitude = recorder.getMaxAmplitude();
            if ((freq < 1000) && (maxAmplitude > 400)) {
                Log.d("AudioRecording", "FREQUENCY : " + freq + ", amp : " + maxAmplitude );
                displayNote(freq);
            }

/*  -- 이것은 음성데이터를 기록하기 위한 것일 뿐.  게임 및 튜닝을 위해서는 불필요 하다.
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, bufferSize );        //BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
        }
/*  -- 이것은 음성데이터를 기록하기 위한 것일 뿐.  게임 및 튜닝을 위해서는 불필요 하다.
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    private void stopRecording() {
Log.d("AudioRecording", "stopRecording() Function.");
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
Log.d("AudioRecording", "onClick Function.");
            switch (v.getId()) {
                case R.id.btnStart: {
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.btnStop: {
                    enableButtons(false);
                    stopRecording();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void displayNote(int freq) {
//        TextView  txtDo = (TextView)findViewById(R.id.noteDo);
//        TextView  txtRe = (TextView)findViewById(R.id.noteRe);
//        TextView  txtMi = (TextView)findViewById(R.id.noteMi);
//        TextView  txtFa = (TextView)findViewById(R.id.noteFa);
//        TextView  txtSol = (TextView)findViewById(R.id.noteSol);
//        TextView  txtRa = (TextView)findViewById(R.id.noteRa);
//        TextView  txtSi = (TextView)findViewById(R.id.noteSi);
//        TextView  txtDo4 = (TextView)findViewById(R.id.noteDo4);
//
//        txtDo.setBackgroundColor(0xFFFFFFFF);
//        txtRe.setBackgroundColor(0xFFFFFFFF);
//        txtMi.setBackgroundColor(0xFFFFFFFF);
//        txtFa.setBackgroundColor(0xFFFFFFFF);
//        txtSol.setBackgroundColor(0xFFFFFFFF);
//        txtRa.setBackgroundColor(0xFFFFFFFF);
//        txtSi.setBackgroundColor(0xFFFFFFFF);
//        txtDo4.setBackgroundColor(0xFFFFFFFF);

        if ( (freq < 253) || (freq >= 539) ) {      // 3옥타브 '시' 이하 또는 5옥타브 '도' 초과
            return;
        }

        if (freq < 359) {   // '4옥타브 파' 보다 아래       261~369
            if (freq < 302) {   // 레 이하
                if (freq < 285) {   // 도# 이하
                    if (freq < 269) {
//                        txtDo.setBackgroundColor(0xFFFFFF00);         // 도.
                        Log.d("AudioRecording", "도");
                    } else {
//                        txtDo.setBackgroundColor(0xFFFF0000);         // 도#.
                        Log.d("AudioRecording", "도#");
                    }
                } else {
//                    txtRe.setBackgroundColor(0xFFFFFF00);         // 레
                    Log.d("AudioRecording", "레");
                }
            } else {
                if (freq < 339) {
                    if (freq < 320) {
//                        txtRe.setBackgroundColor(0xFFFF0000);         // 레#
                        Log.d("AudioRecording", "레#");
                    } else {
//                        txtMi.setBackgroundColor(0xFFFFFF00);         // 미
                        Log.d("AudioRecording", "미");
                    }
                } else {
//                    txtFa.setBackgroundColor(0xFFFFFF00);         // 파
                    Log.d("AudioRecording", "파");
                }
            }
        } else {            // '4옥타브 솔' 이상      369~493
            if (freq < 428) {
                if (freq < 403) {
                    if (freq < 380) {
//                        txtFa.setBackgroundColor(0xFFFF0000);         // 파#
                        Log.d("AudioRecording", "파#");
                    } else {
//                        txtSol.setBackgroundColor(0xFFFFFF00);         // 솔
                        Log.d("AudioRecording", "솔");
                    }
                } else {
//                    txtSol.setBackgroundColor(0xFFFF0000);         // 솔#
                    Log.d("AudioRecording", "솔#");
                }
            } else {        //440~
                if (freq < 453) {
//                    txtRa.setBackgroundColor(0xFFFFFF00);         // 라
                    Log.d("AudioRecording", "라");
                } else {
                    if (freq < 480) {
//                        txtRa.setBackgroundColor(0xFFFF0000);         // 라#
                        Log.d("AudioRecording", "라#");
                    } else {
//                        txtSi.setBackgroundColor(0xFFFF0000);         // 시
                        Log.d("AudioRecording", "시");
                    }
                }
            }
        }

    }


}
