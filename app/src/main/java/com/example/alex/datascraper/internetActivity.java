package com.example.alex.datascraper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class internetActivity extends AppCompatActivity {

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);

        serverHook.start();
        Log.d("MYAPP", "OBTAINED ID: " + serverHook.identifier);
        if(!serverHook.identifier.equals("")){
            startActivity(new Intent(internetActivity.this, phqActivity.class));
        }



        nextButton = (Button) findViewById(R.id.internetButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                
                Toast toast=Toast.makeText(getApplicationContext(),"Checking for connection....",Toast.LENGTH_LONG);
                toast.show();
                serverHook.start();
                Log.d("MYAPP", "OBTAINED ID: " + serverHook.identifier);
                if(!serverHook.identifier.equals("")){
                    startActivity(new Intent(internetActivity.this, phqActivity.class));
                }
                else{
                    toast = Toast.makeText(getApplicationContext(),"No Wifi connection detected.",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
    }
}
