package com.example.ahnsik.mytuner;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;

public class TuningActivity extends AppCompatActivity {

    public static final String FTP_ADDRESS="ccash.iptime.org";
    public static final String FTP_DATA_DIRECTORY="ukulele";
    public static final String FTP_ACCOUNT="ahnsik";
    public static final String FTP_PASSWORD="Ahnsik7@!";

    FTPClient ftpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);

        ftpClient = new FTPClient();


        Toast toast = Toast.makeText(getApplicationContext(),"튜닝 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT);
        toast.show();

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
                openAndGetListFromFtp();

            }       // end of onClick
        });

        Button btnExportToFTP = (Button)findViewById(R.id.btnExportToFTP);
        btnExportToFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = getFilesDir();
                File[] allfiles = dir.listFiles();
                int numFiles = allfiles.length;
                String fileName;

                Log.d("ukulele", "Internal Storage: " + dir + ", " + numFiles + " files exist.");

                for (int i = 0; i < numFiles; i++) {
                    fileName = allfiles[i].getName();
                    File delFile = new File(getFilesDir(),fileName);
                    delFile.delete();
                    Log.d("ukulele", "File: " + fileName + " was deleted." );
                }

            }
        });

    }

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
                Log.d("ukulele", "FTP: 로그인 완료.");
            } catch (Exception e) {
                e.printStackTrace();
                success =false;
            }

            // 문제 없으면, 데이터 파일이 있는 '우쿨렐레' 폴더로 이동.
            if (success) {
                try {
                    ftpClient.changeWorkingDirectory(FTP_DATA_DIRECTORY);
                    Log.d("ukulele", "FTP: 우쿨렐레 폴더로 이동 완료.");
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }

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
                                Log.d("ukulele", "FTP: File : " + name);

                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                ftpClient.enterLocalPassiveMode();
                                boolean result = false;
                                Log.d("ukulele", "Local file name: " + getFilesDir() + "/" + name );
                                FileOutputStream fos = new FileOutputStream(getFilesDir() + "/" + name );
                                result = ftpClient.retrieveFile(name, fos);
                                Log.d("ukulele", "Retrieve " + name + "- result: " + result);
                                fos.close();

                                // *.uke 파일 전송이 완료되면, 해당 파일을 읽어서 mp3 음악소스를 확인하고 copy 해야 한다.
                                if (result) {
                                    String musicUrl = getMusicSourceFile(name);
                                    Log.d("ukulele", "music file name: "+ musicUrl );

                                    FileOutputStream musicfos = new FileOutputStream(getFilesDir() + "/" + musicUrl );
                                    result = ftpClient.retrieveFile(musicUrl, musicfos );
                                    Log.d("ukulele", "Retrieve " + musicUrl + "- result: " + result);
                                    musicfos.close();
                                }

                            }
                        } else {
                            Log.d("ukulele", "FTP: Directory : " + name);
                        }
                    }
                    Log.d("ukulele", "FTP: " + ftpClient.getReplyString());
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                //--------------------------
            }   // end of if

            }   // end of run()
        });      // end of new Thread()
        thread.start();
    }

    private String getMusicSourceFile(String name) {

        NoteData temp = new NoteData();
        temp.loadFromFile( getFilesDir(), name );

        return temp.mMusicURL;
    }

}
