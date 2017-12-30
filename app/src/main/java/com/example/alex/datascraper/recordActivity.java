package com.example.alex.datascraper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
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

/*
Activity class for the voice recording screen of the application
 */

public class recordActivity extends AppCompatActivity {

    // UI elements
    private static Button recordButton;
    private static Button stopButton;
    private static Button nextScreenButton;
    private static TextView thankYouText;

    // boolean for tracking whether or not the recorind was obtained
    private static boolean RECORDINGRECEIVED = false;

    // boolean seeing if all necessary permissions were obtained
    private boolean permissionAccepted = false;

    private static final int REQUEST_MULTIPLE = 1;

    // for recording from the mic
    private static MediaRecorder mediaRecorder;
    // file path to store recording at
    private static String audioFilePath;

    String formatter = "Voice Recording\t\t\t\t\t\t\t\t\t\t\t\t\tReward: $";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        String fuckyou = String.format("%.1f",  ((MyApplication) getApplication()).getComepnsation());
        fuckyou = fuckyou + "0";
        setTitle(formatter+fuckyou);


        // ask for permissions needed to get a recording (Access to record, permission to read and write files)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_MULTIPLE);

        // build audio file path
        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";


        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);


        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);



        stopButton.setEnabled(false);
        recordButton.setEnabled(true);

        // display text once the recording has been obtained
        thankYouText = (TextView) findViewById(R.id.thankyouText);
        if(RECORDINGRECEIVED){
            thankYouText.setVisibility(View.VISIBLE);
        }

    // create record button
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    recordAudio(view);
                } catch (Exception e) {

                }

            }
        });

        // create stop recording button
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    stopAudio(view);
                } catch (Exception e) {

                }

            }
        });

        // create next button
        nextScreenButton = (Button) findViewById(R.id.nextPHQ);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(recordActivity.this,SocialMediaActivity.class));

            }
        });
    }


    /*
    Records audio from the mic and saves it to a file
     */
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

    /*
    Stops recording audio and sends it to the server as base64 encoded
     */
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
            // read audio file as byte array
            File file = new File(audioFilePath);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            // convert byte array to base64 string so it can be sent to the server
            String recording = Base64.encodeToString(bytes, 0);
            // send recording to the server
            Log.d("MYAPP", recording);
            serverHook.sendToServer("audio", recording);

            if(!RECORDINGRECEIVED) {
                ((MyApplication) this.getApplication()).addCompensation(0.10);
                String fuckyou = String.format("%.1f",  ((MyApplication) getApplication()).getComepnsation());
                fuckyou = fuckyou + "0";
                setTitle(formatter+fuckyou);
                ((MyApplication) this.getApplication()).completeRecording();
            }
            RECORDINGRECEIVED = true;
            thankYouText.setVisibility(View.VISIBLE);

        }
        catch(Exception e){

        }

    }

    /*
    Handles response from permissions request, just checks to see if all were granted or not
     */
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

    public void onResume() {
        super.onResume();
        String fuckyou = String.format("%.1f",  ((MyApplication) getApplication()).getComepnsation());
        fuckyou = fuckyou + "0";
        setTitle(formatter+fuckyou);
    }
}
