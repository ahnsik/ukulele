package com.example.ahnsik.mytuner;

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
import android.widget.Toast;

/*import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
*/

public class TuningActivity extends AppCompatActivity implements Runnable {

    public float mDetectedFreq = 0;
    public double mDetectedIntensity = 0;
    private TunerMessageHander mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Toast toast = Toast.makeText(getApplicationContext(),"튜닝 기능은 아직 많이 부족합니다. 사용이 좀 불편하며 불안정 합니다.", Toast.LENGTH_SHORT);
        toast.show();

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mHandler = new TunerMessageHander();
        startTuningTask();
    }



    /////////////////////// 튜닝 하면서 필요한 메세지를 핸들링 - UI 업데이트를 위함. /////////////

    public class TunerMessageHander extends Handler {
        public void handleMessage(Message m) {
            Log.d("ukulele", "Message Handler !! m="+m );
            String detectedNote;

            String result_msg = m.getData().getString("result_msg");
            if ( !result_msg.isEmpty() ) {
                Toast toast = Toast.makeText(getApplicationContext(),result_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
//            TextView statusText = (TextView) findViewById(R.id.txtWhatToDo);
//            statusText.setText("주파수가 검출 되었습니다.");

            TextView freqText = (TextView) findViewById(R.id.txtTunedFreq);
            Float freq = m.getData().getFloat("Freq");
            freqText.setText(":"+freq);

            detectedNote = FindNote.NoteName(freq);
            TextView txtTunedNote = (TextView) findViewById(R.id.txtTunedNote);
            txtTunedNote.setText( detectedNote );

            TextView statusText = (TextView) findViewById(R.id.txtWhatToDo);
            statusText.setText( "center:"+FindNote.getCenterFreq(detectedNote) );

            ImageView nob_image = (ImageView) findViewById(R.id.imgTune);
            switch(detectedNote) {
                case "G4" :
                case "G3" :
                    nob_image.setImageResource( R.drawable.tune_g);
                    break;
                case "C4" :
                    nob_image.setImageResource( R.drawable.tune_c);
                    break;
                case "E4" :
                    nob_image.setImageResource( R.drawable.tune_e);
                    break;
                case "A4" :
                    nob_image.setImageResource( R.drawable.tune_a);
                    break;
                default:
                    nob_image.setImageResource( R.drawable.tuner_none);
                    break;
            }
        }
    }



    /////////////////////// Audio Record 그리고 주파수 검출에 사용되는 함수들. /////////////

    private AudioRecord mAudioRecord;
    private static final int[] SAMPLE_RATES = {44100, 22050, 16000, 11025, 8000};
    private Thread mThread = null;
    private boolean mThreadRunning = false;
    private float mLastComputedFreq = 0;

    private void startTuningTask() {

        // 각 주파수 별로 오디오 버퍼 크기를 계산하여 AudioRecord 객체를 생성하는 함수.
        int bufSize = 16384;
        int avalaibleSampleRates = SAMPLE_RATES.length;
        int i = 0;
        do {
            int sampleRate = SAMPLE_RATES[i];
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

        int bufSize = 8192;
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
                int maxZeroCrossing = (int) (650 * (readLength / 8192) * (sampleRate / 44100.0));   // 650을 기준으로 샘플링 주파수와 버퍼 설정에 맞게 비례하여 최대 값을 지정.

                if (intensity >= 50 && zeroCrossingCount(buffer) <= maxZeroCrossing) {      // 음량이 최소 50 이상, 주파수(?)가 너무 노이즈가 심하지 않은 데이터에 대해서만 확인

                    float freq = getPitch(buffer, readLength / 4, readLength, sampleRate, 150, 650);

                    if (Math.abs(freq - mLastComputedFreq) <= 5f) {     // 새로 계산한 주파수와, 지난번 마지막으로 계산한 주파수의 차이 값이 5f 미만인 경우에만 의미가 있다고 판단. 검출된 주파수를 리턴.
                        mDetectedFreq = freq;
                        mDetectedIntensity = intensity;
                    }
                    mLastComputedFreq = freq;

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

/*
    /////////////////////// FTP 파일 가져오는데 사용되는 함수들. /////////////

    private void openAndGetListFromFtp() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            boolean success =true;
            // 우선 FTP에 접속 & 로그인 시도.
            try {
                ftpClient.setControlEncoding("euc-kr");
                ftpClient.connect(FTP_ADDRESS, 21);
                ftpClient.login(FTP_ACCOUNT, FTP_PASSWORD);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 바이너리 파일
                Log.d("ukulele", "FTP: 로그인 완료.");
            } catch (Exception e) {
                e.printStackTrace();
                success =false;
            }

            // 문제 없으면, 데이터 파일이 있는 '우쿨렐레' 폴더로 이동.
            if (success) {
                try {
                    ftpClient.changeWorkingDirectory(FTP_DATA_DIRECTORY);
                    Log.d("ukulele", "FTP: 우쿨렐레 폴더로 이동 완료.");
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }

            // 문제 없으면, 모든 파일목록을 가져와서 *.uke 파일만 골라 로컬 폴더에 복사.
            if (success) {
                try {
                    FTPFile[] ftpfiles = ftpClient.listFiles();
                    int length = ftpfiles.length;

                    for (int i = 0; i < length; i++) {
                        String name = ftpfiles[i].getName();
                        boolean isFile = ftpfiles[i].isFile();
                        if (isFile) {
                            if (name.toLowerCase().endsWith(".uke")) {
                                Log.d("ukulele", "FTP: File : " + name);

                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                ftpClient.enterLocalPassiveMode();
                                boolean result = false;
//                                Log.d("ukulele", "Local file name: " + getFilesDir() + "/" + name );
                                FileOutputStream fos = new FileOutputStream(getFilesDir() + "/" + name );
                                result = ftpClient.retrieveFile(name, fos);
//                                Log.d("ukulele", "Retrieve " + name + "- result: " + result);
                                fos.close();

                                // *.uke 파일 전송이 완료되면, 해당 파일을 읽어서 mp3 음악소스를 확인하고 copy 해야 한다.
                                if (result) {
                                    String musicUrl = getMusicSourceFile(name);
                                    if ( ! musicUrl.isEmpty() ) {
                                        FileOutputStream musicfos = new FileOutputStream(getFilesDir() + "/" + musicUrl );
                                        result = ftpClient.retrieveFile(musicUrl, musicfos );
//                                        Log.d("ukulele", "Retrieve " + musicUrl + "- result: " + result);
                                        musicfos.close();
                                    } else {
                                    Log.d("ukulele", "?????? music file name: "+ musicUrl );
                                    }
                                }
                            }
                        } else {
                            Log.d("ukulele", "FTP: Directory : " + name);
                        }
                    }
                    Log.d("ukulele", "FTP: " + ftpClient.getReplyString());

                    // Toast 를 대신 표시하도록 메세지를 던진다.
                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("result_msg", ftpClient.getReplyString() );      // 주파수 값을 넣어서 메세지 전송함.
                    msg.setData(b);
                    mHandler.sendMessage(msg);

                    ftpClient.logout();
                    Log.d("ukulele", "FTP: Logged out." );
                    ftpClient.disconnect();
                    Log.d("ukulele", "FTP: Disconnected." );

                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                //--------------------------
            }   // end of if

            }   // end of run()
            });      // end of new Thread()
        thread.start();
    }
*/
    private String getMusicSourceFile(String name) {
        boolean jsonResult = false;

        NoteData temp = new NoteData();
        jsonResult = temp.loadFromFile( getFilesDir(), name );
        if ( !jsonResult) {
            Log.d("ukulele", "FTP: Could not get music-file info." );
            return null;
        }
        return temp.mMusicURL;
    }

}
