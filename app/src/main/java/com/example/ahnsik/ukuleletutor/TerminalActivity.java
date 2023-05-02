package com.example.ahnsik.ukuleletutor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TerminalActivity extends AppCompatActivity {

    public static final String FTP_ADDRESS="ccash.gonetis.com";  //"ccash.iptime.org";
    public static final String FTP_DATA_DIRECTORY="ukulele";
    public static final String FTP_ACCOUNT="ahnsik";
    public static final String FTP_PASSWORD="Ahnsik7@!";

    public static final String INDEXFILENAME="ftpsonglist.json";  //"ccash.iptime.org";
    public static final int MAX_NUM_OF_SONGS = 1024;

    private String[] songfiles;         // 파일명
    private String[] thumbfiles;         // 파일명
    private String[] songTitles;        // 곡목
    private String[] songComments;      // 곡목에 대한 설명
    private String[] songBpm;      // 곡목에 대한 설명
    private String[] songTypes;         // 멜로디 / 코드 / 핑거스타일
    private int      num_of_songs;         // 멜로디 / 코드 / 핑거스타일

    private FTPClient ftpClient;
    private FtpAccessMessageHander mHandler;
    private String  logString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_terminal);

        Button btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnImportFromFTP = (Button)findViewById(R.id.btnImportFromFTP);
        btnImportFromFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ukulele", "btnImportFromFTP  clicked.");
                log("btnImportFromFTP  clicked.\n");
                openAndGetListFromFtp();

            }       // end of onClick
        });
        btnImportFromFTP.requestFocus();

        Button btnExportToFTP = (Button)findViewById(R.id.btnClearUkeFile);
        btnExportToFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = getFilesDir();
                File[] allfiles = dir.listFiles();
                int numFiles = allfiles.length;
                String fileName;

                Log.d("ukulele", "Internal Storage: " + dir + ", " + numFiles + " files exist.");
                log( "Internal Storage: " + dir + ", " + numFiles + " files exist.\n");

                for (int i = 0; i < numFiles; i++) {
                    fileName = allfiles[i].getName();
                    File delFile = new File(getFilesDir(),fileName);
                    delFile.delete();
                    Log.d("ukulele", "File: " + fileName + " was deleted." );
                    log( "File: " + fileName + " was deleted.\n" );
                }
            }
        });

        // FTP에 접속해서 악보 데이터를 읽어 오기 위함.
        ftpClient = new FTPClient();
        mHandler = new FtpAccessMessageHander();

        logString = "";

    }

    /////////////////////// Terminal 에 로그를 기록하는 함수 /////////////
    private void log(String logMsg) {
        Log.d("ukulele", logMsg);

        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("logString", logMsg );      // 로그 출력할 문자열을 넣어서 메세지 전송
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /////////////////////// FTP 파일 가져오는데 사용되는 함수들. /////////////

    private void openAndGetListFromFtp() {

        // FTP에서 *.uke 데이터를 읽어서 index 파일을 만들기 위한 버퍼들을 준비.
        num_of_songs = 0;
        songfiles = new String[MAX_NUM_OF_SONGS];       // 파일이름
        thumbfiles = new String[MAX_NUM_OF_SONGS];      // 앨범사진(썸네일)파일 이름
        songTitles = new String[MAX_NUM_OF_SONGS];      // 곡목
        songComments = new String[MAX_NUM_OF_SONGS];    // 곡목에 대한 설명
        songBpm = new String[MAX_NUM_OF_SONGS];         // BPM
        songTypes = new String[MAX_NUM_OF_SONGS];       // 멜로디 / 코드 / 핑거스타일

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
                    log("FTP: 로그인 완료.\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "FTP 로그인 실패", Toast.LENGTH_LONG).show();
                    success =false;
                }

                // 문제 없으면, 데이터 파일이 있는 '우쿨렐레' 폴더로 이동.
                if (success) {
                    try {
                        ftpClient.changeWorkingDirectory(FTP_DATA_DIRECTORY);
                        log("FTP: 우쿨렐레 폴더로 이동 완료.\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "FTP에서 폴더를 찾지 못했습니다.", Toast.LENGTH_LONG).show();
                        success = false;
                    }
                }

                log("FTP: 진행상황 점검\n");

                // 문제 없으면, 모든 파일목록을 가져와서 *.uke 파일만 골라 로컬 폴더에 복사.
                if (success) {
                    success = copyingUkeFiles();
                }   // end of if

            }   // end of run()
        });      // end of new Thread()
        thread.start();
    }

    public class FtpAccessMessageHander extends Handler {
        public void handleMessage(Message m) {
//            Log.d("ukulele", "Message Handler !! m="+m );
            String detectedNote;

            String result_msg = m.getData().getString("result_msg");
            if ( (result_msg!=null)&& !result_msg.isEmpty() ) {
                Toast toast = Toast.makeText(getApplicationContext(),result_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            String log_msg = m.getData().getString("logString");
            if ( (log_msg!=null) && !log_msg.isEmpty() ) {
                logString = logString + "\n" + log_msg;
                TextView txtLogs = (TextView) findViewById(R.id.txtLogs);
                ScrollView logScroll = (ScrollView) findViewById(R.id.scrollLogs);
//                txtLogs.setText(logString);
                txtLogs.append(log_msg);
                logScroll.fullScroll(View.FOCUS_DOWN);      // 맨 아래 위치로 자동 스크롤 시키기 위함. https://m.blog.naver.com/PostView.nhn?blogId=wotjd1005&logNo=110169020180&proxyReferer=https%3A%2F%2Fwww.google.com%2F
                return;
            }

        }
    }

    private boolean copyingUkeFiles() {
        String filename = "No Name";
        try {
            FTPFile[] ftpfiles = ftpClient.listFiles();
            int length = ftpfiles.length;

            for (int i = 0; i < length; i++) {
                String name = ftpfiles[i].getName();
                filename = name;
                boolean isFile = ftpfiles[i].isFile();
                if (isFile) {
                    if (name.toLowerCase().endsWith(".uke")) {
                        log( "FTP: File : " + name + '\n');

                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();
                        boolean result = false;
                        FileOutputStream fos = new FileOutputStream(getFilesDir() + "/" + name );
                        result = ftpClient.retrieveFile(name, fos);
                        fos.close();

                        // *.uke 파일 전송이 완료되면, 해당 파일을 읽어서 mp3 음악소스를 확인하고 copy 해야 한다.
                        if (result) {
                            String musicUrl = getMusicSourceFile(name, num_of_songs);
                            Log.d("ukulele", "musicUrl : " + musicUrl );
                            if ( ! musicUrl.isEmpty() ) {
                                if ( ! musicUrl.contains("https://") ) {  // URL 인 경우엔 파일 복사하지 않고 통과.
                                    Log.d("ukulele", "Copying.." + musicUrl);
                                    FileOutputStream musicfos = new FileOutputStream(getFilesDir() + "/" + musicUrl);
                                    result = ftpClient.retrieveFile(musicUrl, musicfos);
                                    Log.d("ukulele", "Retrieve " + musicUrl + "- result: " + result);
                                    musicfos.close();
                                } else {
                                    Log.d("ukulele", "Music file is on the web.." + musicUrl);
                                }
                                // 음악파일을 정상으로 가져 왔으면 Thumbnail 파일 이름도 가져 와서 FTP로 부터 다운로드..
                                if ( !thumbfiles[num_of_songs].isEmpty() ) {
                                    String thumbUrl = thumbfiles[num_of_songs];
                                    Log.d("ukulele", "Thumbnail.. " + thumbUrl );

                                    if ( thumbUrl.equals("null") || thumbUrl.contains("https://") ) {    // web 에 있는 파일이 아니라면,
                                        thumbfiles[num_of_songs] = null;
                                    } else {
                                        FileOutputStream thumbfos = new FileOutputStream(getFilesDir() + "/" + thumbUrl );
                                        result = ftpClient.retrieveFile(thumbUrl, thumbfos );
                                        Log.d("ukulele", "Thumbnail.. " + thumbUrl + "- result: " + result);
                                        thumbfos.close();
                                    }
                                }
                            } else {
                                log("?????? empty music file name: "+ musicUrl + '\n');
                            }
                        }
                        num_of_songs++;
                    }
                } else {
                    log("FTP: Directory : " + name + '\n');
                }
            }
            log("FTP: " + ftpClient.getReplyString() + '\n');

            // Toast 를 대신 표시하도록 메세지를 던진다.
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("result_msg", ftpClient.getReplyString() );      // 주파수 값을 넣어서 메세지 전송함.
            msg.setData(b);
            mHandler.sendMessage(msg);

            ftpClient.logout();
            log("FTP: Logged out.\n" );
            ftpClient.disconnect();
            log("FTP: Disconnected.\n" );

            makeFileIndexData();
            log("FTP: makeFileIndexData.\n" );

        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = "파일 " + filename + " 가져오기 실패.";
            Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
            log("FTP: Loading Failed. " + errMsg + "\n" );
            return  false;
        }
        return  true;
    }

    private String getMusicSourceFile(String name, int index) {
        boolean jsonResult = false;

        NoteData temp = new NoteData();
        jsonResult = temp.loadFromFile( getFilesDir(), name );
        if ( !jsonResult) {
            Log.d("ukulele", "FTP: Could not get music-file info." );
            return null;
        }
        songfiles[index] = name;                // 파일이름
        songTitles[index] = temp.mSongTitle;    // 곡목
        songComments[index] = temp.mCommentary; // 곡목에 대한 설명
        songBpm[index] = " "+temp.mBpm;             // BPM
        songTypes[index] = temp.mCategory;      // 멜로디 / 코드 / 핑거스타일
        thumbfiles[index] = temp.mThumbnailURL; // 앨범 사진파일
        Log.d("ukulele", "filename:"+name+", title:"+songTitles[index]+", bpm:"+songBpm[index]+", comments:"+songComments[index] );

        return temp.mMusicURL;
    }

    private JSONObject  makeFileIndexData() {
        JSONObject json = new JSONObject();

        try {
            json.put("num_of_songs", num_of_songs);
            Log.d("ukulele", "makeFileIndexData.." + num_of_songs );
            JSONArray tabJ= new JSONArray();
            for (int i=0; i<num_of_songs; i++) {
                JSONObject song = new JSONObject();
                song.put("filename", songfiles[i] );
                song.put("title", songTitles[i] );
                song.put("comment", songComments[i] );
                if ( thumbfiles[i]==null || thumbfiles[i].isEmpty() )
                    song.put("thumbnail", "null" );
                else
                    song.put("thumbnail", thumbfiles[i] );
                song.put("bpm", songBpm[i] );
                if (songTypes[i] != null) {
                    song.put("type", songTypes[i] );
                } else {
                    song.put("type", ".." );
                }

                tabJ.put(song);
                Log.d("ukulele", ".." + i + " songType:"+songTypes[i]+ " JSON: " + song );
            }
            json.put("songList", tabJ);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        File file = new File( getFilesDir() + "/" + INDEXFILENAME ) ;
        FileWriter fw = null ;
        BufferedWriter bufwr = null ;

        try {
            // open file.
            Log.d("ukulele", "\n.. Ready to write.." );
            fw = new FileWriter(file) ;
            bufwr = new BufferedWriter(fw);

            // write data to the file.
            bufwr.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close file.
        try {
            if (bufwr != null)
                bufwr.close();
            if (fw != null)
                fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ukulele", "\n..Indexing done." );
        log( "Done." );

        return json;
    }

}
