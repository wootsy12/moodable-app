package com.example.alex.datascraper;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
Activity that displays at the end of the application
It presents the user with a code they can use to redeem on Amazon Mechanical Turk
 */

public class ResultsActivity extends AppCompatActivity {
    //ConstraintLayout rLayout = (ConstraintLayout) findViewById(R.id.resultsLayout);
    //SwipeActivity onSwipeListen;
    private static Button nextScreenButton;
    TextView depv;
    TextView deps;
    ImageView img;
    // Build activity screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ConstraintLayout resultsL = (ConstraintLayout) findViewById(R.id.resultsLayout);

        resultsL.setOnTouchListener(new SwipeActivity(this){

            @Override
            public void onSwipeLeft(){
                startActivity(new Intent(ResultsActivity.this,ClinicActivity.class));
            }
            public void onSwipeRight(){
                startActivity(new Intent(ResultsActivity.this,InstaActivity.class));
            }
        });
        String res = ServerHook.getMLResult();
        Integer results = Integer.valueOf(res);
        depv = findViewById(R.id.depressionEstimate);
        deps = findViewById(R.id.depressionString);
        img = findViewById(R.id.emoji_image_view);
        //ImageView img = new ImageView(this);  // or (ImageView) findViewById(R.id.myImageView);

        if (results > 50){
            String  happy = "you are happy";
            //depv.setText(res);
            img.setImageResource(R.drawable.happy_emoji);
            deps.setText(happy);
        }
        else
            if (results < 49){
                String  sad = "you are sad";
                img.setImageResource(R.drawable.sad_emoji);
                //depv.setText(res);
                deps.setText(sad);
            }



        /*
        // create next button
        nextScreenButton = (Button) findViewById(R.id.nextMap);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultsActivity.this,ClinicActivity.class));

            }
        });*/


    }

}
