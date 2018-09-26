package com.example.ahnsik.mytuner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Recording extends Thread {

    private static final int RECORDER_SAMPLERATE = 8000;    //11025;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private boolean isRecording = false;
    private int     bufferSize;
    private AudioRecord recorder = null;

    public  double[] spectrum;
    public  double  center_freq = 0.0f;

    public void Recording() {
        Log.d ("ukulele", "Recording construct.. " );
    }

    public void start(){
        Log.d ("ukulele", "Recording starts.. " );
        int     calcurated_bufSize = 0;
        calcurated_bufSize  = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);  // *2 를 해 주면 6Hz 단위로 주파수 분석 가능. *2를 안하면 12Hz 단위로 분석.
        bufferSize = 1;
        while(bufferSize < calcurated_bufSize )     // 버퍼 크기를 2의 승수로 만들기 위함
            bufferSize *= 2;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

        Log.d ("ukulele", "startRecording() : SAMPLERATE="+RECORDER_SAMPLERATE+", bufferSize="+bufferSize );
        recorder.startRecording();
        isRecording = true;
        super.start();
    }

    public void end(){
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public float frequency(int spectrum_index) {
        return ((float)spectrum_index * RECORDER_SAMPLERATE) / bufferSize ;
    }
    public float magnitude(int spectrum_index) {
        return (float)spectrum[spectrum_index];
    }

    @Override
    public void run() {

        Log.d ("ukulele", "run()" );
        byte buffer[] = new byte[bufferSize];
        double freqDomain[];
        while (isRecording) {
            recorder.read(buffer, 0, bufferSize );
//            Log.d ("ukulele", "recorder.read.. " + bufferSize );

            Spectrum s = new Spectrum(buffer, RECORDER_SAMPLERATE);
            spectrum = s.getSpectrum();
            center_freq = s.getFrequency();
//            Log.d ("ukulele", "center_freq is ..." + center_freq  );
        }

    }

}
