package com.example.alex.datascraper;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class resultsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        String comp = ((MyApplication) getApplication()).getCompletion();



        setContentView(R.layout.activity_results);

        TextView results = findViewById(R.id.resultsText);
        results.setText("Thanks for participating! \nYour code is: " + serverHook.identifier+comp);


        String fuckyou = String.format("%.1f",  ((MyApplication) getApplication()).getComepnsation());
        fuckyou = fuckyou + "0";

        TextView Compensation = findViewById(R.id.comp);
        Compensation.setText("Final Reward: $"+fuckyou);

    }

}
