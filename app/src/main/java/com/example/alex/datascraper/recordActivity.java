package com.example.alex.datascraper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class recordActivity extends AppCompatActivity {

    private static Button recordButton;
    private static Button stopButton;
    private static Button nextScreenButton;
    private static TextView thankYouText;

    private static boolean RECORDINGRECEIVED = false;

    private boolean permissionAccepted = false;

    private static final int REQUEST_MULTIPLE = 1;

    private static MediaRecorder mediaRecorder;

    private static String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_MULTIPLE);

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";


        recordButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);
        recordButton.setEnabled(true);

        thankYouText = (TextView) findViewById(R.id.thankyouText);
        if(RECORDINGRECEIVED){
            thankYouText.setVisibility(View.VISIBLE);
        }


        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    recordAudio(view);
                } catch (Exception e) {

                }

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    stopAudio(view);
                } catch (Exception e) {

                }

            }
        });


        nextScreenButton = (Button) findViewById(R.id.nextPHQ);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(recordActivity.this,SocialMediaActivity.class));

            }
        });
    }


    public void recordAudio (View view) throws IOException
    {

        if(!permissionAccepted){
            return;
        }

        stopButton.setEnabled(true);
        recordButton.setEnabled(false);
        stopButton.setVisibility(View.VISIBLE);
        recordButton.setVisibility(View.GONE);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    public void stopAudio (View view)
    {

        stopButton.setEnabled(false);
        recordButton.setEnabled(true);
        stopButton.setVisibility(View.GONE);
        recordButton.setVisibility(View.VISIBLE);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        try {

            File file = new File(audioFilePath);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            String recording = Base64.encodeToString(bytes, 0);
            recording = URLEncoder.encode(recording, "utf-8");

            Log.d("MYAPP", recording);
            serverHook.sendToServer("audio", recording);

            RECORDINGRECEIVED = true;
            thankYouText.setVisibility(View.VISIBLE);

        }
        catch(Exception e){

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_MULTIPLE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    permissionAccepted = true;
                break;
        }

    }
}
