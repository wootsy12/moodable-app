package com.example.alex.datascraper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/*
Activity that displays at the end of the application
It presents the user with a code they can use to redeem on Amazon Mechanical Turk
 */

public class ResultsActivity extends AppCompatActivity {


    TextView depv;
    // Build activity screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        String res = ServerHook.getMLResult();

        depv = findViewById(R.id.depressionEstimate);

        depv.setText(res);

    }

}
