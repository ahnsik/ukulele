package com.example.ahnsik.ukuleletutor;

import static com.example.ahnsik.ukuleletutor.R.id.patternList;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StrumListActivity extends AppCompatActivity {

    private String[] patternList = {
        "Pattern#1",
        "Pattern#2",
        "Pattern#3",
        "Pattern#4",
        "Pattern#5",
        "Pattern#6",
        "Pattern#7",
        "Pattern#8",
        "Pattern#8",
        "Pattern#10",
        "Pattern#11",
        "Pattern#12",
        "Pattern#13",
        "Pattern#14",
        "Pattern#15",
        "Pattern#16",
        "Pattern#17",
        "Pattern#18",
        "Pattern#19",
        "Pattern#20",
    };
    private int[] patternImgFileName = {
        R.drawable.strumming_pattern_1,
        R.drawable.strumming_pattern_2,
        R.drawable.strumming_pattern_3,
        R.drawable.strumming_pattern_4,
        R.drawable.strumming_pattern_5,
        R.drawable.strumming_pattern_6,
        R.drawable.strumming_pattern_7,
        R.drawable.strumming_pattern_8,
        R.drawable.strumming_pattern_8,
        R.drawable.strumming_pattern_10,
        R.drawable.strumming_pattern_11,
        R.drawable.strumming_pattern_12,
        R.drawable.strumming_pattern_13,
        R.drawable.strumming_pattern_14,
        R.drawable.strumming_pattern_15,
        R.drawable.strumming_pattern_16,
        R.drawable.strumming_pattern_17,
        R.drawable.strumming_pattern_18,
        R.drawable.strumming_pattern_19,
        R.drawable.strumming_pattern_20,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strum_list);
        // Lock orientation into landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView patternListView = (ListView)findViewById(R.id.patternList);
        // 리스트에 아이템을 부착할 어댑터를 생성하고, 리스트 객체에 연결
        StrumListActivity.CustomAdaptor customAdaptor = new StrumListActivity.CustomAdaptor();
        if (customAdaptor != null)
            patternListView.setAdapter(customAdaptor);

        // 리스트의 아이템을 선택(클릭) 했을 때의 동작.
        patternListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                String file=String.valueOf(adapterView.getItemAtPosition(i));
//                // 우선은 그냥 선택된 파일 이름만 선택 - 간단히.
//                System.out.println(songfiles[i]);

                // 플레이(연습용) 액티비티를 기동한다.
//                Intent intent = new Intent();
////                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TrainingActivity");
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", nextActivity );
//                intent.setComponent(name);
//                intent.putExtra("filename", songfiles[i] );
//                startActivity(intent);
            }
        });

    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            if (patternList != null)
                return patternList.length;
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
            view = getLayoutInflater().inflate(R.layout.strumlistitem, null);
            TextView filenameTextview = (TextView) view.findViewById(R.id.strumPatternName);
            filenameTextview.setText(patternList[i]);
//            TextView songTitleTextview = (TextView) view.findViewById(R.id.songTitleText);
//            songTitleTextview.setText(songTitles[i]);
//            TextView songCommentTextview = (TextView) view.findViewById(R.id.descriptionText);
//            songCommentTextview.setText(songComments[i]);
            ImageView thumbnailImageview = (ImageView) view.findViewById(R.id.patternImageView);
            Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), patternImgFileName[i] );
            thumbnailImageview.setImageBitmap( bmp );
            return view;
        }
    }

}

