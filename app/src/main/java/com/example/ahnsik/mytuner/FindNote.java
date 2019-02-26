package com.example.ahnsik.mytuner;

public class FindNote {

    private static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3",
            "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
            "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };
    private final static int base_minimum_freq_table[] = {      // 음계를 찾기 위한 주파수 범위의 최소값
            190, 201, 213, 226, 239, 253, 269, 285, 302, 320,
            339, 359, 380, 403, 427, 453, 479, 508, 538, 570,
            604, 640, 678, 718, 761, 806, 855, 906, 959, 1016,
            1077,1141,1209,1281,1357,1437,
    };
    private final static float center_freq_table[] = {
            195.9977F, 207.6523F, 220.0F, 233.0819F, 246.9417F,
            261.6256F, 277.1826F, 293.6648F, 311.1270F, 329.6276F, 349.2282F, 369.9944F, 391.9954F, 415.3047F, 440.0F, 466.1638F, 493.8833F,
            523.2511F, 554.3653F, 587.3295F, 622.2540F, 659.2551F, 698.4565F, 739.9888F, 783.9909F, 830.6094F, 880.0F, 932.3275F, 987.7666F,
            1046.502F, 1108.731F, 1174.659F, 1244.508F, 1318.510F, 1396.913F, 1479.978F,
    };

    public static int findIndex(double freq) {
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

    public static String NoteName(double freq) {
        return  note_name[findIndex(freq)];
    }

    public static float getCenterFreq(String noteName) {
        int i;
        for ( i=0; i< note_name.length; i++) {
            if (noteName.equalsIgnoreCase(note_name[i]))
                break;
        }
        if (i < note_name.length) {
            return center_freq_table[i];
        } else {
            return 0.0F;
        }
    }
}
