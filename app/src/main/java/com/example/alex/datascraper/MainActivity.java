package com.example.alex.datascraper;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int TEXT = 0;
    private static final int CALLS = 1;
    private static final int CALENDAR = 2;
    private static final int STORAGE = 3;
    private static final int CONTACTS = 4;

    private static final String CLIENT_ID = "44fa875f13844f5f8401fef309ccfc97";
    private static final String CALLBACK = "http://depressionmqp.wpi.edu:8080/instagram";

    private String accessToken = "";

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    private static final String[] permissions = new String[]{
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
    };
    
    // holds which modalities are waiting for permissions to be granted
    private ArrayList<Integer> waiting = new ArrayList<Integer>();

    MediaRecorder mediaRecorder = new MediaRecorder();
    private static String audioFilePath;

    private static Button submitButton;
    private static Button nextScreenButton;

    private static EditText twitterText;
    private boolean isRecording = false;

    private int identifier = 800; // testing

    private static boolean dataSent = false;

    serverHook hook;
    modalityText mtext = new modalityText();
    modalityHabits mhabits = new modalityHabits();

    public MainActivity(){
        super();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/myaudio.3gp";


        // log in to Instagram
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.instagram.com/oauth/authorize/?client_id="
                + CLIENT_ID
                + "&redirect_uri="
                + CALLBACK
                + "&response_type=code"));
        startActivity(browserIntent);

        if(!dataSent){
            dataSent = true;

             hook = new serverHook();
             hook.start();

            Thread t = new Thread(){
                public void run() {
                    sendAllAvailableData();
                }
            };
            t.start();

        }


        twitterText = (EditText) findViewById(R.id.twitterText);

        submitButton = (Button) findViewById(R.id.submitSM);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String twitter = twitterText.getText().toString();
                Log.d("MYAPP", twitter);
                twitterText.setText("");
                hook.sendToServer("twitterUsername", twitter);
            }
        });


        nextScreenButton = (Button) findViewById(R.id.nextRecord);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,recordActivity.class));

            }
        });

    }

    /**
     * Sends all data that the app can scrape to a server
     * Ignores data that permissions were denied for
     */
    public void sendAllAvailableData(){
        Context mContext = getApplicationContext();
        hook.sendToServer("debug","START");
        boolean[] send = {false, false, false, false, false};
        waiting = new ArrayList<Integer>();

        if(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            send[TEXT] = true;
        }
        else{
            waiting.add(TEXT);
        }
        if(checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            send[CALLS] = true;
        }
        else{
            waiting.add(CALLS);
        }
        //request calendar access if access not already available
        if(checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            send[CALENDAR] = true;
        }
        else{
            waiting.add(CALENDAR);
        }
        //request storage access if access not already available
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            send[STORAGE] = true;
        }
        else{
            waiting.add(STORAGE);
        }
        //request contacts access if access not already available
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            send[CONTACTS] = true;
        }
        else{
            waiting.add(CONTACTS);
        }

        ActivityCompat.requestPermissions(this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);


        if(send[TEXT]){
            mtext.getTexts(mContext,hook);
            Log.d("MYAPP", "text");
        }
        if(send[CALLS]){
            mhabits.getCalls(mContext,hook);
            Log.d("MYAPP", "Calls");
        }
        if(send[CALENDAR]){
            mhabits.getCalendar(mContext,hook);
            Log.d("MYAPP", "Calendar");
        }
        if(send[STORAGE]){
            mhabits.getStorage(mContext,hook);
            Log.d("MYAPP", "storage");
        }
        if(send[CONTACTS]){
            mhabits.getContacts(mContext,hook);
            Log.d("MYAPP", "contacts");
        }


        Log.d("MyAPP", "DONE");
        hook.sendToServer("debug","END");

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        boolean[] accepted = {false, false, false, false, false};
        Context mContext = getApplicationContext();
        Log.d("MYAPPP", "HERE");
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for(int i=0; i<waiting.size(); i++){
                        //Log.d("MYAPP", Integer.toString(i));
                        //Log.d("MYAPP", Integer.toString(grantResults[i]));
                        //Log.d("MYAPP", Integer.toString(waiting.get(i)));

                        if(grantResults[i] >= 0){
                            switch(waiting.get(i)){
                                case TEXT:
                                    Log.d("MYAPP", "text");
                                    mtext.getTexts(mContext,hook);
                                    break;
                                case CALLS:
                                    Log.d("MYAPP", "calls");
                                    mhabits.getCalls(mContext,hook);
                                    break;
                                case CALENDAR:
                                    Log.d("MYAPP", "Calendar");
                                    mhabits.getCalendar(mContext,hook);
                                    break;
                                case STORAGE:
                                    Log.d("MYAPP", "storage");
                                    mhabits.getStorage(mContext,hook);
                                    break;
                                case CONTACTS:
                                    Log.d("MYAPP", "contacts");
                                    mhabits.getContacts(mContext,hook);
                                    break;
                            }
                        }
                    }
                }
                else{
                    Log.d("MYAPP", "UH OH");
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            dataSent = false;
        }
    }



}
