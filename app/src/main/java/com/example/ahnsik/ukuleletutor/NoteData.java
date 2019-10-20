package com.example.ahnsik.ukuleletutor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class NoteData {

    public  String  mMusicURL;          // 연주할 음악 MP3 주소 또는 YouTube 주소..
    public  String  mSongTitle;         // 곡의 제목
    public  String  mCategory;          // 연주방법 : Chord / 밴드(단음)연주 / 핑거스타일 / 아르페지오, etc..
    public  String  mAuthor;            // 악보 제작자
//    public  String  mAuthorNote;        // 제작자의 코멘트
//    public  String  mAuthorComment;     // 제작자가 하고 싶은 말.. 설명
//    public  String  mDateCreated;       // 제작한 날짜/시간
    public  String  mCommentary;        // 그 외에 이러 저러한 코멘트.

    public  int     mStartOffset;       // 처음 시작할 위치의 오프셋
    public  float   mBpm;
    public  int     numNotes;
//    public  long    playtime;

    // 여기 아래의 배열들은 진짜 연주해야 할 데이터 들..
    public  long[]       timeStamp;
    public  String[]    chordName;
    public  String[]    stroke;
    public  String[][]  tab;
    public  String[][]  note;
    public  boolean[][] note_played;
    public  String[]    lyric;

    public  int[]  score;        // time diff what with played.


    public  NoteData() {
        mMusicURL = null;
        mSongTitle = null;
        mStartOffset = 0;
        mBpm = 0.0f;
        numNotes = 0;
        // 여기 아래의 배열들은 진짜 연주해야 할 데이터 들..
        timeStamp = null;
        chordName = null;
        stroke = null;
        tab = null;
        note = null;
        note_played = null;
        score = null;
    }

//    public  NoteData(String dataFileString) {
//
//        setData(dataFileString);
//    }

    public boolean loadFromFile(File dir, String fileName) {
        String   UkeDataRead;
        Log.d("ukulele", "loadFromFile : "+dir+"/"+fileName );
        UkeDataRead = readTextFile(dir+"/"+fileName );
        return setData(UkeDataRead);
    }

    private String readTextFile(String path) {
        String  datafile = null;
        File file = new File(path);
        String  line;
        try {
            FileReader fr = new FileReader(file);
            if (fr==null) {
                Log.d("ukulele", "File Reader Error:" + fr);
                return null;
            }
            BufferedReader buffrd = new BufferedReader(fr);
            if (buffrd==null) {
                Log.d("ukulele", "File Buffered Read Error:" + buffrd);
                return null;
            }
            datafile = "";
            Log.d("TEST", "Readey to vote !!");
            while ( (line=buffrd.readLine() ) != null) {
                if (line == null || line.trim().length() <= 0) {
                    Log.d("TEST", "Skip Empty line. !!");
                } else if ( (line.charAt(0)=='#') && (line.charAt(1)=='#') ) {     // 처음 시작하는게 ##로 시작하는 라인은 comment 로 처리 함.
                    Log.d("TEST", "This Line is comments. !!" );
                } else {
                    datafile += line;
                }
            }
            Log.d("TEST", "buffrd.close !!");
            buffrd.close();
            fr.close();
            Log.d("TEST", "fullText="+datafile);
        } catch(Exception e) {
            Log.d("TEST", "Exceptions ");
            e.printStackTrace();
        }
        return datafile;
    }



    public JSONObject makeJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("source", mMusicURL );
            json.put("title", mSongTitle);
            json.put("category", mCategory);
            json.put("author", mAuthor );
//            json.put("author_note", mAuthorNote);
//            json.put("author_comment", mAuthorComment);
//            json.put("create_date", mDateCreated );
            json.put("comment", mCommentary);

            json.put("start_offset", mStartOffset);
            json.put("bpm", mBpm);

            JSONArray notes = new JSONArray();
            for (int i=0; i< numNotes; i++) {
                JSONObject oneChord = new JSONObject();
                oneChord.put("timestamp", timeStamp[i] );
                if ( (chordName[i] !=null) && ( ! chordName[i].isEmpty() ) ) {
                    oneChord.put("chord", chordName[i] );
                }
                if ( (stroke[i] !=null) && ( ! stroke[i].isEmpty() ) ) {
                    oneChord.put("stroke", stroke[i] );
                }
                JSONArray tabJ= new JSONArray();
                for (int j=0; j<tab[i].length; j++) {
                    tabJ.put(tab[i][j]);
                }
                oneChord.put("tab", tabJ);
                JSONArray noteJ= new JSONArray();
                for (int j=0; j<note[i].length; j++) {
                    noteJ.put(note[i][j]);
                }
                oneChord.put("note", noteJ);
                oneChord.put("lyric", lyric[i]);

                notes.put(oneChord);
            }
            json.put("notes", notes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    public boolean  setData(String dataFileString) {

        Log.d("ukulele", "-=========== DataFile Dump ===========-");
        Log.d("ukulele", dataFileString );
        Log.d("ukulele", "-=========== DataFile Dump END ===========-");
        try {
            Log.d("ukulele", "start parse" );
            JSONObject  ukeData = new JSONObject(dataFileString);

            this.mMusicURL = ukeData.getString("source");
            Log.d("ukulele", " ** very important: source - " + this.mMusicURL );
            this.mSongTitle = ukeData.getString("title");

            this.mStartOffset = ukeData.getInt("start_offset");
            this.mBpm = (float) ukeData.getDouble("bpm");

            JSONArray noteData = ukeData.getJSONArray("notes" );
            this.numNotes = noteData.length();
//            this.playtime = ukeData.getLong("playtime");

            Log.d("ukulele", "Title: " + this.mSongTitle + ", BPM: "+ this.mBpm );
            Log.d("ukulele", "notes.length= " + this.numNotes );

            this.timeStamp = new long[this.numNotes];
            this.score = new int[this.numNotes];
            this.chordName = new String[this.numNotes];
            this.stroke = new String[this.numNotes];
            this.tab = new String[this.numNotes][];
            this.note = new String[this.numNotes][];
            this.note_played = new boolean[this.numNotes][];
            this.lyric = new String[this.numNotes];

            for (int i = 0; i<this.numNotes; i++) {
                JSONObject  a_note = noteData.getJSONObject(i);
                this.score[i] = 99999999;
                this.timeStamp[i] = a_note.getLong("timestamp");
                try {
                    this.chordName[i] = a_note.getString("chord");
                } catch (Exception e) {
                    this.chordName[i] = null;
                }
                try {
                    this.stroke[i] = a_note.getString("stroke");
                } catch (Exception e) {
                    this.stroke[i] = null;
                }
                JSONArray   temp1 = a_note.getJSONArray("tab");
                this.tab[i] = new String[temp1.length()];
                for (int j=0; j<temp1.length(); j++) {
                    this.tab[i][j] = temp1.getString(j);
                }
                JSONArray   temp2 = a_note.getJSONArray("note");
                this.note[i] = new String[temp2.length()];
                this.note_played[i] = new boolean[temp2.length()];
                for (int j=0; j<temp2.length(); j++) {
                    this.note[i][j] = temp2.getString(j);
                    this.note_played[i][j] = false;
                }
                try {
                    this.lyric[i] = a_note.getString("lyric");
//                    Log.d("ukulele", "lyric : " + lyric[i] );
                } catch (Exception e) {
                    this.lyric[i] = null;
                }
            }

            try {
                this.mCommentary = ukeData.getString("comment");
                this.mCategory = ukeData.getString("category");
                this.mAuthor = ukeData.getString("author");
                Log.d("ukulele", "mCommentary :"+this.mCommentary );
//                this.mAuthorNote = ukeData.getString("auther_note");
//                this.mAuthorComment = ukeData.getString("auther_comment");
//                this.mDateCreated = ukeData.getString("create_date");
            } catch (Exception e) {
                Log.d("ukulele", "[][][][][][] Parsing Error for sub-informations [][][][][][] ");
                Log.d("ukulele", "mCategory :"+this.mCategory );
                Log.d("ukulele", "mAuthor :"+this.mAuthor );
//                Log.d("ukulele", "mAuthorNote :"+this.mAuthorNote );
//                Log.d("ukulele", "mAuthorComment :"+this.mAuthorComment );
//                Log.d("ukulele", "mDateCreated :"+this.mDateCreated );
                Log.d("ukulele", "mCommentary :"+this.mCommentary );
            }

        } catch (Exception e) {
            Log.d("ukulele", "-xxxxxxxxxxxx Error to parse JSON xxxxxxxxxxxx-");
            e.printStackTrace();
            return false;
        }
        return true;
    }   // end of setData();

}
