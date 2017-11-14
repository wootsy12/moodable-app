package com.example.alex.datascraper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class recordActivity extends AppCompatActivity {

    private static Button recordButton;
    private static Button stopButton;
    private static Button nextScreenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        nextScreenButton = (Button) findViewById(R.id.nextPHQ);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(recordActivity.this,phqActivity.class));

            }
        });
    }
}
