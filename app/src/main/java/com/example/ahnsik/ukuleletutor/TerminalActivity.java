package com.example.ahnsik.ukuleletutor;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;

public class TerminalActivity extends AppCompatActivity {

    public static final String FTP_ADDRESS="ccash.gonetis.com";  //"ccash.iptime.org";
    public static final String FTP_DATA_DIRECTORY="ukulele";
    public static final String FTP_ACCOUNT="ahnsik";
    public static final String FTP_PASSWORD="Ahnsik7@!";

    private FTPClient ftpClient;
    private FtpAccessMessageHander mHandler;
    private String  logString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                log("btnImportFromFTP  clicked.");
                openAndGetListFromFtp();

            }       // end of onClick
        });

        Button btnExportToFTP = (Button)findViewById(R.id.btnClearUkeFile);
        btnExportToFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = getFilesDir();
                File[] allfiles = dir.listFiles();
                int numFiles = allfiles.length;
                String fileName;

                Log.d("ukulele", "Internal Storage: " + dir + ", " + numFiles + " files exist.");
                log( "Internal Storage: " + dir + ", " + numFiles + " files exist.");

                for (int i = 0; i < numFiles; i++) {
                    fileName = allfiles[i].getName();
                    File delFile = new File(getFilesDir(),fileName);
                    delFile.delete();
                    Log.d("ukulele", "File: " + fileName + " was deleted." );
                    log( "File: " + fileName + " was deleted." );
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

//        logString = logString + "\n" + logMsg;
//        TextView txtLogs = (TextView) findViewById(R.id.txtLogs);
//        txtLogs.setText(logString);

        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("logString", logMsg );      // 로그 출력할 문자열을 넣어서 메세지 전송
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    /////////////////////// FTP 파일 가져오는데 사용되는 함수들. /////////////

    private void openAndGetListFromFtp() {
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
//                    Log.d("ukulele", "FTP: 로그인 완료.");
                    log("FTP: 로그인 완료.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ukulele", "Trace .. Error #3");
                    success =false;
                }

                // 문제 없으면, 데이터 파일이 있는 '우쿨렐레' 폴더로 이동.
                if (success) {
                    try {
                        ftpClient.changeWorkingDirectory(FTP_DATA_DIRECTORY);
//                        Log.d("ukulele", "FTP: 우쿨렐레 폴더로 이동 완료.");
                        log("FTP: 우쿨렐레 폴더로 이동 완료.");
                    } catch (Exception e) {
                        Log.d("ukulele", "Trace .. Error #1");
                        e.printStackTrace();
                        success = false;
                    }
                }

//                Log.d("ukulele", "FTP: 진행점검");
                log("FTP: 진행상황 점검");

                // 문제 없으면, 모든 파일목록을 가져와서 *.uke 파일만 골라 로컬 폴더에 복사.
                if (success) {
                    try {
                        FTPFile[] ftpfiles = ftpClient.listFiles();
                        int length = ftpfiles.length;

                        for (int i = 0; i < length; i++) {
                            String name = ftpfiles[i].getName();
                            boolean isFile = ftpfiles[i].isFile();
                            if (isFile) {
                                if (name.toLowerCase().endsWith(".uke")) {
                                    log( "FTP: File : " + name);

                                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                    ftpClient.enterLocalPassiveMode();
                                    boolean result = false;
//                                Log.d("ukulele", "Local file name: " + getFilesDir() + "/" + name );
                                    FileOutputStream fos = new FileOutputStream(getFilesDir() + "/" + name );
                                    result = ftpClient.retrieveFile(name, fos);
//                                Log.d("ukulele", "Retrieve " + name + "- result: " + result);
                                    fos.close();

                                    // *.uke 파일 전송이 완료되면, 해당 파일을 읽어서 mp3 음악소스를 확인하고 copy 해야 한다.
                                    if (result) {
                                        String musicUrl = getMusicSourceFile(name);
                                        if ( ! musicUrl.isEmpty() ) {
                                            FileOutputStream musicfos = new FileOutputStream(getFilesDir() + "/" + musicUrl );
                                            result = ftpClient.retrieveFile(musicUrl, musicfos );
//                                        Log.d("ukulele", "Retrieve " + musicUrl + "- result: " + result);
                                            musicfos.close();
                                        } else {
                                            log("?????? music file name: "+ musicUrl );
                                        }
                                    }
                                }
                            } else {
                                log("FTP: Directory : " + name);
                            }
                        }
                        log("FTP: " + ftpClient.getReplyString());

                        // Toast 를 대신 표시하도록 메세지를 던진다.
                        Message msg = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("result_msg", ftpClient.getReplyString() );      // 주파수 값을 넣어서 메세지 전송함.
                        msg.setData(b);
                        mHandler.sendMessage(msg);

                        ftpClient.logout();
                        log("FTP: Logged out." );
                        ftpClient.disconnect();
                        log("FTP: Disconnected." );

                    } catch (Exception e) {
                        log("Trace .. Error #2");
                        e.printStackTrace();
                        success = false;
                    }
                    //--------------------------
                }   // end of if

            }   // end of run()
        });      // end of new Thread()
        thread.start();
    }

    public class FtpAccessMessageHander extends Handler {
        public void handleMessage(Message m) {
            Log.d("ukulele", "Message Handler !! m="+m );
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
                txtLogs.setText(logString);
                return;
            }

        }
    }


    private String getMusicSourceFile(String name) {
        boolean jsonResult = false;

        NoteData temp = new NoteData();
        jsonResult = temp.loadFromFile( getFilesDir(), name );
        if ( !jsonResult) {
            Log.d("ukulele", "FTP: Could not get music-file info." );
            return null;
        }
        return temp.mMusicURL;
    }

}
