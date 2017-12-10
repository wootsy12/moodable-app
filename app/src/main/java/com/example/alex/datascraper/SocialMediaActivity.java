package com.example.alex.datascraper;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SocialMediaActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "44fa875f13844f5f8401fef309ccfc97";
    private static final String CALLBACK = "http://depressionmqp.wpi.edu:8080/instagram";

    private static Button submitButton;
    private static Button nextScreenButton;
    private static WebView instaView;
    public static WebView googleView;
    private long mDownloadedFileID;

    private static int cnt=-1;



    private static EditText twitterText;
    private static List<String> urlList = new ArrayList<String>();
    private static List<String> fileList = new ArrayList<String>();

    public SocialMediaActivity(){
        super();
    }

    // http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);


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
                + serverHook.identifier // ID to send to server alongside the auth token
                +"&response_type=code";
        // set up webview and load page
        instaView.getSettings().setJavaScriptEnabled(true);
        instaView.setInitialScale(200);
        instaView.loadUrl(url);
        //instaView.setVisibility(View.VISIBLE);

        Calendar c = Calendar.getInstance();

        for(int i=0; i<14; i++) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            String turl = String.format("https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i%s!2i%s!3i%s!2m3!1i%s!2i%s!3i%s\n", year, month, day, year, month, day);

            urlList.add(turl);
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        System.out.println(urlList);

        googleView = (WebView) findViewById(R.id.googleWebview);
        googleView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);


                if(url.contains("https://myaccount.google.com/")) {
                    googleView.setVisibility(View.VISIBLE);
                    cnt=0;
                    googleView.loadUrl(urlList.get(cnt));
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


        googleView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {


                // Function is called once download completes.
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Prevents the occasional unintentional call. I needed this.
                        if (mDownloadedFileID == -1)
                            return;
                        System.out.println("DOWNLOAD RECEIVED");


                        // Sets up the prevention of an unintentional call. I found it necessary. Maybe not for others.
                        mDownloadedFileID = -1;
                        cnt+=1;
                        if(cnt<14) {
                            downloadNext(urlList.get(cnt),googleView);
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                fileList.add(fileName);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("Cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading File");
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    mDownloadedFileID = downloadManager.enqueue(request);
                }
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }
        });
        // url of instagram authentication page
        String urlg = "https://accounts.google.com/ServiceLogin";
        // set up webview and load page
        googleView.getSettings().setJavaScriptEnabled(true);
        googleView.setInitialScale(200);
        googleView.loadUrl(urlg);
        googleView.setVisibility(View.VISIBLE);

        // button for switching to next screen
        nextScreenButton = (Button) findViewById(R.id.nextRecord);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if((modalityHabits.DONE) && ((cnt==14) || (cnt==-1))) {
                    for(int i=0;i<14;i++) {
                        try {
                            FileInputStream Fin=new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileList.get(i)));
                            String sendGPS = convertStreamToString(Fin);
                            serverHook.sendToServer("gps",sendGPS);
                        } catch (Exception e) {

                        }

                    }
                    startActivity(new Intent(SocialMediaActivity.this, resultsActivity.class));
                }
                else{

                    Log.d("MYAPP", Integer.toString(cnt));
                    Toast toast=Toast.makeText(getApplicationContext(),"Please wait for data sending to finish.",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

    }

    public void downloadNext(String url, WebView v) {
        v.loadUrl(url);
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
