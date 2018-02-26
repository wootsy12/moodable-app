package com.example.alex.datascraper;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/*
Activity class that displays the first page of the App
This page simply contains text describing the application flow
 */

public class LaunchActivity extends AppCompatActivity {

    // UI elements
    //Intent i = new Intent(LaunchActivity.this, FingerprintActivity.class);

    //private static Button nextScreenButton;

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
                //set the new Content of your activity
                Intent i = new Intent(LaunchActivity.this, RecordActivity.class);
                startActivity(i);

                //startActivity(new Intent(LaunchActivity.this, FingerprintActivity.class));

            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_launch);

        //display the logo during 2.5 seconds,
        new CountDownTimer(2500,1000){
            @Override
            public void onTick(long millisUntilFinished){}
            @Override
            public void onFinish(){
                //set the new Content of your activity

                Intent i = new  Intent(LaunchActivity.this, RecordActivity.class);
                startActivity(i);


            }
        }.start();
    }
}
