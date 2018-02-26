package com.example.alex.datascraper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class InstaActivity extends AppCompatActivity {

    // Client ID for making Instagram API sign in requests
    private static final String CLIENT_ID = "44fa875f13844f5f8401fef309ccfc97";
    // URL to redirect to after Instagram sign in
    private static final String CALLBACK = "http://depressionmqp.wpi.edu:8080/instagram";

    private static WebView instaView;
    private static Button nextScreenButton;
    private static boolean instad =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta);

        /*
        nextScreenButton = (Button) findViewById(R.id.nextScreenButton);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InstaActivity.this, ResultsActivity.class));

            }
        });*/



        // set up webview for Instagram login
        CookieManager.getInstance().setAcceptCookie(true); // for testing purposes, lets me always see login screen
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
                + ServerHook.identifier // ID to send to server alongside the auth token
                +"&response_type=code";
        // set up webview and load page
        instaView.getSettings().setJavaScriptEnabled(true);
        instaView.setInitialScale(200);
        instaView.loadUrl(url);
        //instaView.setVisibility(View.VISIBLE);

        // build web page inside app screen
        instaView.setWebViewClient(new WebViewClient(){

            // on page started
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if(url.substring(0,43).equals("http://depressionmqp.wpi.edu:8080/instagram")) {
                    if(!instad) {
                        ((MyApplication) getApplication()).completeInsta();
                    }
                    instad=true;

                }
                Log.d("WebView", url);
            }

            public void onPageFinished(WebView view, String url) {
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url){
                v.loadUrl(url);
                return true;
            }
        });

        instaView.setOnTouchListener(new SwipeActivity(this){

            @Override
            public void onSwipeLeft(){
                startActivity(new Intent(InstaActivity.this, ResultsActivity.class));
            }
            @Override
            public void onSwipeRight(){
                startActivity(new Intent(InstaActivity.this, TwitterActivity.class));
            }
        });
    }
}
