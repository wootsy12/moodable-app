package com.example.alex.datascraper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/*
Activity class for the page that displays when no internet connection could be established to obtain
a user ID
Returns to the PHQ activity once internet connection is established
 */

public class InternetActivity extends AppCompatActivity {

    private Button nextButton;

    // function that fires at the start of this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);

        // Attempt to get an ID from the server, and move on to PHQ screen if successful
        ServerHook.start();
        Log.d("MYAPP", "OBTAINED ID: " + ServerHook.identifier);
        if(!ServerHook.identifier.equals("")){
            startActivity(new Intent(InternetActivity.this, RecordActivity.class));
        }


        // Set up the next page button
        nextButton = (Button) findViewById(R.id.internetButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // Attempt to connect to internet and obtain an ID from the server
                Toast toast=Toast.makeText(getApplicationContext(),"Checking for connection....",Toast.LENGTH_LONG);
                toast.show();
                ServerHook.start();
                Log.d("MYAPP", "OBTAINED ID: " + ServerHook.identifier);

                // If successful, move on to PHQ screen
                if(!ServerHook.identifier.equals("")){
                    startActivity(new Intent(InternetActivity.this, RecordActivity.class));
                }
                // If failed, tell the user that no internet connection is detected and stay on this screen
                else{
                    toast = Toast.makeText(getApplicationContext(),"No internet connection detected.",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
    }
}
