package com.example.alex.datascraper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class launchActivity extends AppCompatActivity {

    private static Button nextScreenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //display the logo during 5 seconds,
        new CountDownTimer(2500,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                launchActivity.this.setContentView(R.layout.activity_launch);

                nextScreenButton = (Button) findViewById(R.id.nextSocial);
                nextScreenButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        serverHook.start();
                        Log.d("MYAPP", "OBTAINED ID: " + serverHook.identifier);
                        if(!serverHook.identifier.equals("")){
                            startActivity(new Intent(launchActivity.this, phqActivity.class));
                        }
                        else{
                            startActivity(new Intent(launchActivity.this, internetActivity.class));
                        }

                    }
                });


            }
        }.start();



    }
}
