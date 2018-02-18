package com.example.alex.datascraper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
Activity class for requesting data from scoial media accounts.
Asks the user to sign in to Google, Instagram, and give their Twitter username
 */

public class SocialMediaActivity extends AppCompatActivity {



    // UI elements

    private static Button nextScreenButton;

    public static WebView googleView;
    private static boolean downloaded=false;


    private long mDownloadedFileID;

    // count of GPS files downloading
    private static int cnt=-1;

    // message for telling the user to wait for gps download to finish
    Toast downloadToast;


    private static List<String> urlList = new ArrayList<String>();
    private static List<String> fileList = new ArrayList<String>();


    public SocialMediaActivity(){
        super();
    }

    // converts a file to a string
    // inspired by http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm
    // INPUT - is - an input stream made from a file
    // OUTPUT - a string representing the file
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

    // function that fires when this Activity is created, overrides the default
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);








        Calendar c = Calendar.getInstance();

        // requests the GPS for the last 2 weeks (14 days)
        for(int i=0; i<14; i++) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            String turl = String.format("https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i%s!2i%s!3i%s!2m3!1i%s!2i%s!3i%s\n", year, month, day, year, month, day);

            urlList.add(turl);
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        System.out.println(urlList);

        // set up Google login webview
        googleView = (WebView) findViewById(R.id.googleWebview);
        googleView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);


                if(url.contains("https://myaccount.google.com/")) {
                    googleView.setVisibility(View.GONE);
                    TextView googComplete = (TextView) findViewById(R.id.googComplete);
                    googComplete.setVisibility(View.VISIBLE);
                    if(!downloaded) {
                        cnt=0;
                        googleView.loadUrl(urlList.get(cnt));
                    }
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

        //Handle google GPS downloads
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
                        else if(cnt==14) {
                            if(!downloaded) {
                                ((MyApplication) getApplication()).completeGoogle();
                            }
                            downloaded=true;
                        }
                        else{
                            downloadToast.cancel();
                            findViewById(R.id.GPSDoneText).setVisibility(View.VISIBLE);
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
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    try{
                        mDownloadedFileID = downloadManager.enqueue(request);
                    }
                    catch(Exception e){
                        Log.d("MYAPP", "Exception Caught");
                        cnt = -1;
                        return;
                    }

                }

                downloadToast = Toast.makeText(getApplicationContext(), "Downloading File... Please wait", Toast.LENGTH_SHORT);
                downloadToast.show();
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

                // originally would not let the user pass if background data sending isnt finished
                // we decided to remove that aspect for users with slow internet
                // instead it only makes the user wait for the GPS data to send
                if(/*(ModalityHabits.DONE) &&*/ ((cnt==14) || (cnt==-1))) {

                    // send downloaded GPS data
                    for(int i=0;i<14;i++) {
                        try {
                            FileInputStream Fin=new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileList.get(i)));
                            String sendGPS = convertStreamToString(Fin);
                            ServerHook.sendToServer("gps",sendGPS);
                        } catch (Exception e) {

                        }

                    }
                    ServerHook.sendToServer("debug", "END");
                    startActivity(new Intent(SocialMediaActivity.this, TwitterActivity.class));
                }
                // if in progress of GPS download, do not continue
                else{

                    Log.d("MYAPP", Integer.toString(cnt));
                    Toast toast=Toast.makeText(getApplicationContext(),"Please wait for data sending to finish.",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

    }

    // loads the next download URL in the Google webview
    // INPUT - url - url of next download page
    // INPUT - v - webview to open URL in
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
