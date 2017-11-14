package com.example.alex.datascraper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class phqActivity extends AppCompatActivity {

    private static Button phqSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PHQ-9 Questionnaire");
        setContentView(R.layout.activity_phq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        phqSubmit = (Button) findViewById(R.id.phqSub);
        phqSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(phqActivity.this,resultsActivity.class));

            }
        });

    }
}
