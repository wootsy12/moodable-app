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
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "44fa875f13844f5f8401fef309ccfc97";
    private static final String CALLBACK = "http://depressionmqp.wpi.edu:8080/instagram";

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    private static final String[] permissions = new String[]{
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
    };

    // constants for telling which boolean in the send array pertains to which modality
    private static final int TEXT = 0;
    private static final int CALLS = 1;
    private static final int CALENDAR = 2;
    private static final int STORAGE = 3;
    private static final int CONTACTS = 4;

    // holds which modalities are waiting for permissions to be granted
    private ArrayList<Integer> waiting = new ArrayList<Integer>();
    // boolean array for modalities, true indicates that the modality send thread has been launched
    private boolean[] send = {false, false, false, false, false};

    // booleans for tracking send thread dispatching, true when done dispatching
    private boolean mainDataDispatchingFinished = false; // true when done dispatching granted permissions
    private boolean permissionsDataDispatchingFinished = false; // true when done attempting to dispatch new permissions

    MediaRecorder mediaRecorder = new MediaRecorder();
    private static String audioFilePath;

    private static Button submitButton;
    private static Button nextScreenButton;
    private static WebView instaView;

    private static EditText twitterText;
    private boolean isRecording = false;

    private static boolean dataSent = false;

    modalityHabits mhabits = new modalityHabits();

    public MainActivity(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        serverHook.start();

        // send all available data in a seperate thread from the UI, as long as it hasnt already been started
        if(!dataSent){
            dataSent = true;

            Thread t = new Thread(){
                public void run() {
                    sendAllAvailableData();
                }
            };
            t.start();
        }


        // set up code for Twitter username submission
        twitterText = (EditText) findViewById(R.id.twitterText);
        // send twitter username to server on submit
        submitButton = (Button) findViewById(R.id.submitSM);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String twitter = twitterText.getText().toString();
                Log.d("MYAPP", twitter);
                twitterText.setText("");
                serverHook.sendToServer("twitterUsername", twitter);
            }
        });


        // set up webview for Instagram login
        CookieManager.getInstance().setAcceptCookie(false); // for testing purposes, lets me always see login screen
        // load instagram authentication website
        instaView = (WebView) findViewById(R.id.instagramWebview);
        instaView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url){
                v.loadUrl(url);
                return true;
            }
        });
        // url of instagram authentication page
        String url = "https://api.instagram.com/oauth/authorize/?client_id="
                + CLIENT_ID // my API id
                + "&redirect_uri="
                + CALLBACK // server page to redirect to
                + "&state="
                + serverHook.identifier // ID to send to server alongside the auth token
                +"&response_type=code";
        // set up webview and load page
        instaView.getSettings().setJavaScriptEnabled(true);
        instaView.setInitialScale(200);
        instaView.loadUrl(url);
        instaView.setVisibility(View.VISIBLE);

        // button for switching to next screen
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
        serverHook.sendToServer("debug","START");
        Log.d("MYAPP", "GO");

        // list holding modalities for which the app is waiting for permissions to be granted
        waiting = new ArrayList<Integer>();

        // check if the app has permissions for each modality
        // if yes, mark as ready to send
        // if no, add to list of awaited permissions

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

        // ask for permission for all needed data types. If the app already has any they will be ignored
        ActivityCompat.requestPermissions(this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

        // send all data types that were marked as having permissions already
        if(send[TEXT]){
            mhabits.getHabit(mContext, "texts");
        }
        if(send[CALLS]){
            mhabits.getHabit(mContext, "calls");
        }
        if(send[CALENDAR]){
            mhabits.getHabit(mContext, "calendar");
        }
        if(send[STORAGE]){
            mhabits.getHabit(mContext, "files");
        }
        if(send[CONTACTS]){
            mhabits.getHabit(mContext, "contacts");
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
            mhabits.dispatchDone(); // tell modalityHabits that the mainactivity is done dispatching
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
