package com.example.ahnsik.ukuleletutor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Recording extends Thread {

    private static final int RECORDER_SAMPLERATE = 8000;    //11025;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final float PEAK_MINIMUM_DB=0.3f;        // 소리를 인식하는 최소 데시벨.     1.0f 으로 하면 화음에서 놓치는 음이 너무 많은 듯.,


    //  locally used variables.
    private boolean     isRecording = false;
    private int         bufferSize;
    private AudioRecord recorder = null;
    private double      prev_amplitude = 0;      // index of note data (next position what it will be played.)
    private boolean     vol_increment = false;
    private double      detected_volume = 0.0f;

    public  double[] spectrum;
    public  double  center_freq = 0.0f;

    private final static int NUM_OF_NOTE_UKE=36;                // G3 ~ F6 까지..
    public  boolean notes_detected[];
    //  locally referenced tables.
    private final static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };
    private final static int base_minimum_freq_table[] = {      // 음계를 찾기 위한 주파수 범위의 최소값
            190, 201, 213, 226, 239, 254, 269, 285, 302, 320,
            339, 359, 381, 404, 428, 453, 479, 508, 538, 570,
            604, 640, 678, 718, 761, 806, 855, 906, 959, 1016,
            1077,1141,1209,1281,1357,1437,
    };


    public  Recording() {
        super();
        notes_detected = new boolean[NUM_OF_NOTE_UKE];
        Log.d ("ukulele", "Recording constructed.. " );
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

        vol_increment = false;
        prev_amplitude = 0.0;

        while (isRecording) {
            recorder.read(buffer, 0, bufferSize );
//            Log.d ("ukulele", "recorder.read.. " + bufferSize );

            Spectrum s = new Spectrum(buffer, RECORDER_SAMPLERATE);
            spectrum = s.getSpectrum();
            center_freq = s.getFrequency();
            detected_volume = s.getVolume();
//            Log.d ("ukulele", "center_freq is ..." + center_freq  );
        }

    }

    ////////////////////////////////
    // Recognise notes play.
    public void parseSpectrum() {
        // 우선 스펙트럼 데이터를 살짝 뭉개서 주변값들을 통합할 필요가 있다.
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        // 그 주파수를 기준으로 음계를 찾아 플래그 설정.
        if (spectrum==null)   return;
        if (notes_detected==null)   return;

        int length = spectrum.length;
        double[] blur = new double[length];
        blur[0] = spectrum[0];
        for (int i = 1; i<length-1; i++) {
            //blur[i] = (spectrum[i-1]+spectrum[i]+spectrum[i+1]) / 3.0f;
            blur[i] = spectrum[i] / 3.0f;
        }
        // 그런 다음에 peak 값을 찾아 주파수를 계산하고,
        for (int i=0; i<notes_detected.length; i++)
            notes_detected[i] = false;
        for (int i = 1; i< (length/2)-1; i++) {
            if ( (blur[i-1]<blur[i])&&(blur[i]>blur[i+1]) && (magnitude(i) > PEAK_MINIMUM_DB) ) {   // PEAK 값
                double frequency = frequency(i);
                // 주파수에 해당하는 음계를 찾아 플래그 설정
                notes_detected[ findIndex(frequency(i)) ]=true;
            }
        }
    }

    private int findIndex(double freq) {
        int min=0, max=base_minimum_freq_table.length;
        int index = max/2;
        while( (max-min)>0 ) {       // binary search.
            if (base_minimum_freq_table[index] > freq ) {
                max = index;
                index = min+(max-min)/2;
            } else {
                min = index;
                index = min+(max-min)/2;
            }
            if ((max-min) <= 1) break;
        }
        return index;       //주파수 테이블 내에서 주어진 주파수값에 해당하는 범위의 index 를 리턴 함.
    }

    // 문자열로 받아오는 음계 데이터가 연주되고 있는지(녹음되었는지) 확인하는 함수.
    public boolean isPlayed(String note) {
        int  k;

        if (notes_detected==null) {
            return false;
        }

        // 우선 입력 파라메터 note 의 음계에 대한 index (k)를 찾는다.
        for (k=0; k<NUM_OF_NOTE_UKE; k++) {     // 계이름 문자열을 비교 하여.
            if ( note_name[k].equals( note ) ) {    // index를 찾았다면 루프는 종료하고.
                break;
            }
        }

        if (k>=NUM_OF_NOTE_UKE) {       // 루프 끝까지 갔는데 못 찾았다면? - 계이름 데이터가 이상하다. Not found ??  weird.
            Log.d("ukulele", "Weird data: '" + note + "' was not found.");
            return false;
        }

        // 해당 인덱스(계이름=k)가 녹음된 주파수에 들어 있는지(검출 되었는지) 플래그 확인.
        if ( notes_detected[k] ) {
            return true;
        }
        return false;       // 검출 안됐으면 그냥 종료.
    }

    public boolean isStroked() {
        boolean  stroked = false;
//            Log.d("ukulele", "  check peak- , prev:"+ (int)prev_amplitude + ", now:" + (int)mRecording.detected_volume + ", inc:"+vol_increment );
        if ( vol_increment && (prev_amplitude > detected_volume )) {     // prev_amplitude 의 값이 peak 이어야 함.
            Log.d("ukulele", "  Stroke detected !!   vol:"+ detected_volume + ", freq:" + center_freq );
            if (detected_volume > 2.0f) {      // 그 중에서도 volume 이 2 이상으로 좀 음량이 있는 것으로 한정함.
                stroked = true;
            }
        }

        if ( prev_amplitude > detected_volume)
            vol_increment = false;
        if ( prev_amplitude < detected_volume)
            vol_increment = true;
        // 과거의 음량을 갱신 기억.
        prev_amplitude = detected_volume;
        return stroked;
    }
}
