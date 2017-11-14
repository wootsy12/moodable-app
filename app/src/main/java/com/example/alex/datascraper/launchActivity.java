package com.example.alex.datascraper;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class launchActivity extends AppCompatActivity {

    private static Button nextScreenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //display the logo during 5 seconds,
        new CountDownTimer(5000,1000){
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
                        startActivity(new Intent(launchActivity.this,MainActivity.class));

                    }
                });


            }
        }.start();



    }
}
