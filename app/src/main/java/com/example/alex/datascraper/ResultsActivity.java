package com.example.alex.datascraper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
Activity that displays at the end of the application
It presents the user with a code they can use to redeem on Amazon Mechanical Turk
 */

public class ResultsActivity extends AppCompatActivity {

    private static Button nextScreenButton;
    TextView depv;
    // Build activity screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        String res = ServerHook.getMLResult();

        depv = findViewById(R.id.depressionEstimate);

        depv.setText(res);
        // create next button
        nextScreenButton = (Button) findViewById(R.id.nextMap);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultsActivity.this,ClinicActivity.class));

            }
        });
    }

}
