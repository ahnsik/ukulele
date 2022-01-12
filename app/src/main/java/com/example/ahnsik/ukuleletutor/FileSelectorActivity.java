package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileSelectorActivity extends AppCompatActivity {

    private String[] songfiles;         // 파일명
    private String[] songTitles;        // 곡목
    private String[] songComments;      // 곡목에 대한 설명
    private String[] thumbPath;      // 곡목에 대한 thumbnail 이미지의 path(파일명)
    private Bitmap[] thumbnailBitmap;    // 곡에 대한 thmubnail 이미지
    private String[] songBpm;      // 곡목에 대한 설명
    private String[] songTypes;         // 멜로디 / 코드 / 핑거스타일
    private String nextActivity;
    private Bitmap defaultThumbnail;    // 곡에 대한 thmubnail 이미지


    public static final String INDEXFILENAME="ftpsonglist.json";  //"ccash.iptime.org";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_file_selector);
        // Lock orientation into landscape.

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

    /*--------- INDEXFILENAME 이란 파일에서 List Object를 구성할 데이터를 읽어 오기 ---------------------- */
        try
        {
            String listOfJSON = readTextFile( getFilesDir() + "/" + INDEXFILENAME);
            JSONObject jsonFile = new JSONObject(listOfJSON);
            int numUkeFiles = jsonFile.getInt("num_of_songs");

            songfiles = new String[numUkeFiles];
            songTitles = new String[numUkeFiles];        // 곡목
            songComments = new String[numUkeFiles];      // 곡목에 대한 설명
            thumbPath = new String[numUkeFiles];          // 곡의 thumbnail 파일명
            thumbnailBitmap = new Bitmap[numUkeFiles];    // 곡의 Thumbnail 이미지
            songBpm = new String[numUkeFiles];      // BPM
            songTypes = new String[numUkeFiles];         // 멜로디 / 코드 / 핑거스타일
            defaultThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.ukulele_icon);

            JSONArray songList = jsonFile.getJSONArray("songList" );
            for (int i=0; i<numUkeFiles; i++ ) {
                JSONObject  info = songList.getJSONObject(i);
                songfiles[i] = info.getString("filename");;
                songTitles[i] = info.getString("title");        // 곡목
                songComments[i] = info.getString("comment");      // 곡목에 대한 설명
                thumbPath[i] = info.getString("thumbnail");          // 곡의 thumbnail 파일명
                Log.d("ukulele", "index="+i+", thumbnail:"+thumbPath[i] );
                if ( thumbPath[i]==null || thumbPath[i].equals("null") ) {
                    thumbnailBitmap[i] = null;
                } else {
                    thumbnailBitmap[i] = BitmapFactory.decodeFile( getFilesDir() + "/" + thumbPath[i] );
                    Log.d("ukulele", "index="+i+", thumbnailBitmap = "+thumbnailBitmap[i] );
                }
                songBpm[i] = info.getString("bpm");
//                songTypes[i] = info.getString("type");         // 멜로디 / 코드 / 핑거스타일
            }
        } catch (Exception e) {
            Log.d("ukulele", "-xxxxxxxxxxxx Error to parse Index file xxxxxxxxxxxx-");
            e.printStackTrace();
            songfiles = null;
            songTitles = null;
            songComments = null;
            thumbnailBitmap = null;
            songBpm = null;
            songTypes = null;
        }

        // 본격적으로 리스트를 만들기
        // 첫번째로 리스트뷰 리소스를 가져옴.
        ListView fileListView = (ListView)findViewById(R.id.fileList);
        // 리스트에 아이템을 부착할 어댑터를 생성하고, 리스트 객체에 연결
        CustomAdaptor customAdaptor=new CustomAdaptor();
        if (customAdaptor != null)
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

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

//    public void onConfigurationChanged() {
//        Toast errmsg = Toast.makeText(this.getApplicationContext(),"가로모드에서만 동작합니다.", Toast.LENGTH_SHORT);
//        errmsg.show();
////        finish();
//    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            if (songfiles != null)
                return songfiles.length;
            else
                return  0;
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
            TextView filenameTextview = (TextView) view.findViewById(R.id.filenameText);
            filenameTextview.setText(songfiles[i]);
            TextView songTitleTextview = (TextView) view.findViewById(R.id.songTitleText);
            songTitleTextview.setText(songTitles[i]);
            TextView songCommentTextview = (TextView) view.findViewById(R.id.descriptionText);
            songCommentTextview.setText(songComments[i]);
            ImageView thumbnailImageview = (ImageView) view.findViewById(R.id.fileImageView);
            if ((thumbnailBitmap == null) || (thumbnailBitmap[i] == null)) {
                thumbnailImageview.setImageBitmap(defaultThumbnail);
            } else {
                thumbnailImageview.setImageBitmap(thumbnailBitmap[i]);
            }
            return view;
        }
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

/*    // *** 하단 네비게이션 바 숨기기
    public void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility( // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                // Hide the nav bar and status bar
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }
    //출처: https://ddunnimlabs.tistory.com/3 [뚠님 연구소]
*/
}
