package com.example.alex.datascraper;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SocialMediaActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "44fa875f13844f5f8401fef309ccfc97";
    private static final String CALLBACK = "http://depressionmqp.wpi.edu:8080/instagram";

    private static Button submitButton;
    private static Button nextScreenButton;
    private static WebView instaView;

    private static EditText twitterText;

    public SocialMediaActivity(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        serverHook.start();

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
                if(modalityHabits.DONE) {
                    startActivity(new Intent(SocialMediaActivity.this, resultsActivity.class));
                }
                else{
                    Toast toast=Toast.makeText(getApplicationContext(),"Please wait for data sending to finish.",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

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

}
