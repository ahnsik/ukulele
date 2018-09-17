package com.example.ahnsik.mytuner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class NoteData {

    public  String  mMusicURL;          // 연주할 음악 MP3 주소 또는 YouTube 주소..
    public  String  mSongTitle;         // 곡의 제목
    public  String  mCategory;          // 연주방법 : Chord / 밴드(단음)연주 / 핑거스타일 / 아르페지오, etc..
    public  String  mAuthor;            // 악보 제작자
    public  String  mAuthorNote;        // 제작자의 코멘트
    public  String  mAuthorComment;     // 제작자가 하고 싶은 말.. 설명
    public  String  mDateCreated;       // 제작한 날짜/시간
    public  String  mCommentary;        // 그 외에 이러 저러한 코멘트.

    public  int     mStartOffset;       // 처음 시작할 위치의 오프셋
    public  int     mBpm;
    public  int     numNotes;
    public  long    playtime;

    // 여기 아래의 배열들은 진짜 연주해야 할 데이터 들..
    public  long[]       timeStamp;
    public  String[]    chordName;
    public  String[][]  tab;
    public  String[][]  note;
    public  boolean[][] note_played;
    public  String[]    lyric;

    public  int[]  score;        // time diff what with played.


    public  NoteData() {
        mMusicURL = null;
        mSongTitle = null;
        mStartOffset = 0;
        mBpm = 0;
        numNotes = 0;
        // 여기 아래의 배열들은 진짜 연주해야 할 데이터 들..
        timeStamp = null;
        chordName = null;
        tab = null;
        note = null;
        note_played = null;
        score = null;
    }

    public  NoteData(String dataFileString) {
        Log.d("ukulele", "-=========== DataFile Dump ===========-");
        Log.d("ukulele", dataFileString );
        Log.d("ukulele", "-=========== DataFile Dump END ===========-");
        try {
            Log.d("ukulele", "start parse" );
            JSONObject  ukeData = new JSONObject(dataFileString);

            mMusicURL = ukeData.getString("source");
            mSongTitle = ukeData.getString("title");

            mStartOffset = ukeData.getInt("start_offset");
            mBpm = ukeData.getInt("bpm");

            JSONArray noteData = ukeData.getJSONArray("notes" );
            numNotes = noteData.length();
            playtime = ukeData.getLong("playtime");

            Log.d("ukulele", "Title: " + mSongTitle + ", BPM: "+ mBpm );
            Log.d("ukulele", "notes.length= " + numNotes );

            timeStamp = new long[numNotes];
            score = new int[numNotes];
            chordName = new String[numNotes];
            tab = new String[numNotes][];
            note = new String[numNotes][];
            note_played = new boolean[numNotes][];
            lyric = new String[numNotes];

            for (int i = 0; i<numNotes; i++) {
                JSONObject  a_note = noteData.getJSONObject(i);
                score[i] = 99999999;
                timeStamp[i] = a_note.getLong("timestamp");
                try {
                    chordName[i] = a_note.getString("chord");
                } catch (Exception e) {
                    chordName[i] = null;
                }
                JSONArray   temp1 = a_note.getJSONArray("tab");
                tab[i] = new String[temp1.length()];
                for (int j=0; j<temp1.length(); j++) {
                    tab[i][j] = temp1.getString(j);
                }
                JSONArray   temp2 = a_note.getJSONArray("note");
                note[i] = new String[temp2.length()];
                note_played[i] = new boolean[temp2.length()];
                for (int j=0; j<temp2.length(); j++) {
                    note[i][j] = temp2.getString(j);
                    note_played[i][j] = false;
                }
                try {
                    lyric[i] = a_note.getString("lyric");
//                    Log.d("ukulele", "lyric : " + lyric[i] );
                } catch (Exception e) {
                    lyric[i] = null;
                }
            }

            try {
                mCategory = ukeData.getString("category");
                mAuthor = ukeData.getString("auther");
                mAuthorNote = ukeData.getString("auther_note");
                mAuthorComment = ukeData.getString("auther_comment");
                mDateCreated = ukeData.getString("create_date");
                mCommentary = ukeData.getString("comment");
            } catch (Exception e) {
                Log.d("ukulele", "[][][][][][] Parsing Error for sub-informations [][][][][][] ");
                Log.d("ukulele", "mCategory :"+mCategory );
                Log.d("ukulele", "mAuthor :"+mAuthor );
                Log.d("ukulele", "mAuthorNote :"+mAuthorNote );
                Log.d("ukulele", "mAuthorComment :"+mAuthorComment );
                Log.d("ukulele", "mDateCreated :"+mDateCreated );
                Log.d("ukulele", "mCommentary :"+mCommentary );
            }

        } catch (Exception e) {
            Log.d("ukulele", "-xxxxxxxxxxxx Error to parse JSON xxxxxxxxxxxx-");
            e.printStackTrace();
        }
    }

    public JSONObject makeJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("source", mMusicURL );
            json.put("title", mSongTitle);
            json.put("category", mCategory);
            json.put("author", mAuthor );
            json.put("author_note", mAuthorNote);
            json.put("author_comment", mAuthorComment);
            json.put("create_date", mDateCreated );
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

}
