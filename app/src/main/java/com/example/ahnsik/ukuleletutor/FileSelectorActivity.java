package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class FileSelectorActivity extends AppCompatActivity {

    private String[] songfiles;         // 파일명
    private String[] songTitles;        // 곡목
    private String[] songComments;      // 곡목에 대한 설명
    private String[] songBpm;      // 곡목에 대한 설명
    private String[] songTypes;         // 멜로디 / 코드 / 핑거스타일
    private String nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String playingMode = getIntent().getExtras().getString("mode");
        if (playingMode.equals("TrainingMode")) {
            nextActivity = "com.example.ahnsik.ukuleletutor.TrainingActivity";
        } else {
            nextActivity = "com.example.ahnsik.ukuleletutor.PlayingActivity";
        }

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String filteringName;
        // 맨 처음 모든 파일의 목록을 가져와서 *.uke 파일만 골라서 리스트를 만들 것임.
//        File directory = new File("/storage/sdcard0");
        File directory = getFilesDir();
        File[] allfiles = directory.listFiles();
        int numFiles = allfiles.length;
        // 우선 *.uke 파일의 갯수만 확인.  - 배열의 크기를 결정하기 위해서 임.
        int numUkeFiles = 0;
        for (int i = 0; i < numFiles; i++) {
            filteringName = allfiles[i].getName();
            if (filteringName.toLowerCase().endsWith("uke")) {
                numUkeFiles++;
            }
        }

        if (numUkeFiles <= 0) {     // *.uke 파일이 하나도 없다면..?
            Log.d("ukulele", "ERROR: No *.uke files.");
            return;
        }

        // *.uke 파일이름만 골라 저장할 배열을 생성
        songfiles = new String[numUkeFiles];
        songTitles = new String[numUkeFiles];        // 곡목
        songComments = new String[numUkeFiles];      // 곡목에 대한 설명
        songBpm = new String[numUkeFiles];      // BPM
        songTypes = new String[numUkeFiles];         // 멜로디 / 코드 / 핑거스타일

        // 다시 한 번 *.uke 파일만 골라서 배열에 저장함.
        numUkeFiles = 0;
        for (int i = 0; i < allfiles.length; i++) {
            filteringName = allfiles[i].getName();
            if (filteringName.toLowerCase().endsWith("uke")) {
                songfiles[numUkeFiles] = allfiles[i].getName();

                NoteData temp = new NoteData();
                boolean jsonResult = false;

                jsonResult = temp.loadFromFile( getFilesDir(), filteringName );
                if ( !jsonResult) {
//                    Log.d("ukulele", "FTP: Could not get music-file info." );
                    return;
                }
                songTitles[numUkeFiles] = temp.mSongTitle;
                songComments[numUkeFiles] = "설명:"+temp.mCommentary;
                songBpm[numUkeFiles] = "BPM:"+temp.mBpm;
                songTypes[numUkeFiles] = temp.mCategory;

                Log.d("ukulele", "---> " + songfiles[numUkeFiles]+", Title:"+ songTitles[numUkeFiles] +
                                                    ", Comments:" + songComments[numUkeFiles] );
                numUkeFiles++;
            }
        }

        // 본격적으로 리스트를 만들기
        // 첫번째로 리스트뷰 리소스를 가져옴.
        ListView fileListView = (ListView)findViewById(R.id.fileList);
        // 리스트에 아이템을 부착할 어댑터를 생성하고, 리스트 객체에 연결
        CustomAdaptor customAdaptor=new CustomAdaptor();
        fileListView.setAdapter(customAdaptor);

        // 리스트의 아이템을 선택(클릭) 했을 때의 동작.
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String file=String.valueOf(adapterView.getItemAtPosition(i));
                // 우선은 그냥 선택된 파일 이름만 선택 - 간단히.
                System.out.println(songfiles[i]);

                // 플레이(연습용) 액티비티를 기동한다.
                Intent intent = new Intent();
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TrainingActivity");
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", nextActivity );
                intent.setComponent(name);
                intent.putExtra("filename", songfiles[i] );
                startActivity(intent);
            }
        });

    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            return songfiles.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.filelistitem, null);
            TextView filenameTextview = (TextView)view.findViewById(R.id.filenameText);
            filenameTextview.setText(songfiles[i] );
            TextView songTitleTextview = (TextView)view.findViewById(R.id.songTitleText);
            songTitleTextview.setText(songTitles[i] );
            TextView songCommentTextview = (TextView)view.findViewById(R.id.descriptionText);
            songCommentTextview.setText(songComments[i] );
            return view;
        }
    }

}