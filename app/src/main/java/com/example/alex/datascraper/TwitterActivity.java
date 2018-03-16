package com.example.alex.datascraper;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TwitterActivity extends AppCompatActivity {

    private static EditText twitterText;
    private static boolean tritrd=false;
    private static Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        ConstraintLayout twitterL = (ConstraintLayout) findViewById(R.id.twitterLayout);




        // set up code for Twitter username submission
        twitterText = (EditText) findViewById(R.id.twitterText);
        // send twitter username to server on submit
        submitButton = (Button) findViewById(R.id.submitSM2);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String twitter = twitterText.getText().toString();
                Log.d("MYAPP", twitter);
                twitterText.setText("");
                ServerHook.sendToServer("twitterUsername", twitter);
                if(!tritrd) {
                    ((MyApplication) getApplication()).completetwitter();
                }
                //startActivity(new Intent(TwitterActivity.this, InstaActivity.class));
                tritrd=true;

            }
        });

        twitterL.setOnTouchListener(new SwipeActivity(this){

            @Override
            public void onSwipeLeft(){
                startActivity(new Intent(TwitterActivity.this, InstaActivity.class));
                tritrd=true;            }
            @Override
            public void onSwipeRight(){
                startActivity(new Intent(TwitterActivity.this, SocialMediaActivity.class));
                tritrd=true;            }
        });
    }
}
