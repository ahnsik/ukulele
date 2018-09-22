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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ftpClient.setControlEncoding("euc-kr");
                            ftpClient.connect(FTP_ADDRESS, 21);
                            ftpClient.login(FTP_ACCOUNT, FTP_PASSWORD);
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 바이너리 파일
                            Log.d("ukulele", "FTP: 로그인 완료");

                            ftpClient.changeWorkingDirectory("ukulele");

                            FTPFile[] ftpfiles = ftpClient.listFiles();
                            int length = ftpfiles.length;

                            for (int i = 0; i < length; i++) {
                                String name = ftpfiles[i].getName();
                                boolean isFile = ftpfiles[i].isFile();
                                if (isFile) {
//                                    Log.d("ukulele", "FTP: File : " + name);
                                    if (name.toLowerCase().endsWith(".uke")) {
                                        Log.d("ukulele", "FTP: File : " + name);

                                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                        ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                                        boolean result = false;
                                        FileOutputStream fos = new FileOutputStream(getFilesDir() );
                                        result = ftpClient.retrieveFile(name, fos);
                                        Log.d("ukuelele", "Access result: "+ result );
                                        fos.close();
                                    }
                                }
                                else {
                                    Log.d("ukulele", "FTP: Directory : " + name);
                                }
                            }
                            Log.d("ukulele","FTP: "+ftpClient.getReplyString());
                            //--------------------------
                            Toast t = Toast.makeText(getApplicationContext(), "FTP file list OK.", Toast.LENGTH_SHORT);
                            t.show();



                        } catch (Exception e) {
//                            Toast t = Toast.makeText(getApplicationContext(), "FTP Connection Failed.", Toast.LENGTH_SHORT);
//                            t.show();
                            e.printStackTrace();
                        }

                    }   // end of run()
                });      // end of new Thread()
                thread.start();

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
//
                Log.d("ukulele", "Internal Storage: " + dir);

                for (int i = 0; i < numFiles; i++) {
                    fileName = allfiles[i].getName();
                    Log.d("ukulele", "File: " + fileName );
                }

//                        String inputData = mEtInput.getText().toString();
//                        switch(view.getId()) {
//                            case R.id.bt_internal:
//                                FileOutputStream fos = null;
//                                try {
//                                    fos = openFileOutput("internal.txt", Context.MODE_PRIVATE);
//                                    fos.write(inputData.getBytes());
//                                    fos.close();;
//
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//
                        }
        });

    }
}
