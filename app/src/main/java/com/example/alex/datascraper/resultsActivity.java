package com.example.alex.datascraper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class resultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Results Pending");


        setContentView(R.layout.activity_results);
        TextView results = (TextView) findViewById(R.id.resultsText);
        results.setText("Thanks for participating! Your code is:\n" + serverHook.identifier);
    }
}
