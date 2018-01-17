package com.example.alex.datascraper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/*
Activity that displays at the end of the application
It presents the user with a code they can use to redeem on Amazon Mechanical Turk
 */

public class ResultsActivity extends AppCompatActivity {


    // Build activity screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        String comp = ((MyApplication) getApplication()).getCompletion();

        setContentView(R.layout.activity_results);

        TextView results = findViewById(R.id.resultsText);
        results.setText("Thanks for participating! \nYour code is: " + ServerHook.identifier+comp);


        String compString = String.format("%.1f",  ((MyApplication) getApplication()).getComepnsation());
        compString = compString + "0";

        TextView Compensation = findViewById(R.id.comp);
        Compensation.setText("Final Reward: $"+compString);

    }

}
