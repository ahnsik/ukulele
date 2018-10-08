package com.example.ahnsik.mytuner;

public class NoteFreq {

    public static String note_name[] = {
            "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4",
            "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5",
            "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6",
            "C#6", "D6", "D#6", "E6", "E#6", "F6" };
    public final static int base_minimum_freq_table[] = {      // 음계를 찾기 위한 주파수 범위의 최소값
            190, 201, 213, 226, 239, 253, 269, 285, 302, 320,
            339, 359, 380, 403, 427, 453, 479, 508, 538, 570,
            604, 640, 678, 718, 761, 806, 855, 906, 959, 1016,
            1077,1141,1209,1281,1357,1437,
    };

    private static int findIndex(double freq) {
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

}
