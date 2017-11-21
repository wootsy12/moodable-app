package com.example.alex.datascraper;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class recordActivity extends AppCompatActivity {

    private static Button recordButton;
    private static Button stopButton;
    private static Button nextScreenButton;

    private static MediaRecorder mediaRecorder;

    private static String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";


        recordButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);
        recordButton.setEnabled(true);


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
                startActivity(new Intent(recordActivity.this,phqActivity.class));

            }
        });
    }


    public void recordAudio (View view) throws IOException
    {


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

    }
}
