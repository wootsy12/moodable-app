package com.example.alex.datascraper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class phqActivity extends AppCompatActivity {

    private static Button phqSubmit;

    private RadioGroup[] questions;
    /*Q1;
    private RadioGroup Q2;
    private RadioGroup Q3;
    private RadioGroup Q4;
    private RadioGroup Q5;
    private RadioGroup Q6;
    private RadioGroup Q7;
    private RadioGroup Q8;
    private RadioGroup Q9;*/

    private static Button rb1;
    private static Button rb2;
    private static Button rb3;
    private static Button rb4;
    private static View scrollView2;
    private static LinearLayout scrollChild;
    private static ImageButton butty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PHQ-9 Questionnaire");
        setContentView(R.layout.activity_phq);


        rb1 = (Button) findViewById(R.id.radioButton);
        rb2 = (Button) findViewById(R.id.radioButton2);
        rb3 = (Button) findViewById(R.id.radioButton3);
        rb4 = (Button) findViewById(R.id.radioButton4);
        rb1.setClickable(false);
        rb2.setClickable(false);
        rb3.setClickable(false);
        rb4.setClickable(false);

        scrollView2 = findViewById(R.id.scrollView2);
        scrollChild = findViewById(R.id.scrollChild);
        butty = findViewById(R.id.arrowBoy);





        questions = new RadioGroup[9];
        questions[0] = (RadioGroup) findViewById(R.id.PHQ1);
        questions[1] = (RadioGroup) findViewById(R.id.PHQ2);
        questions[2] = (RadioGroup) findViewById(R.id.PHQ3);
        questions[3] = (RadioGroup) findViewById(R.id.PHQ4);
        questions[4] = (RadioGroup) findViewById(R.id.PHQ5);
        questions[5] = (RadioGroup) findViewById(R.id.PHQ6);
        questions[6] = (RadioGroup) findViewById(R.id.PHQ7);
        questions[7] = (RadioGroup) findViewById(R.id.PHQ8);
        questions[8] = (RadioGroup) findViewById(R.id.PHQ9);



        phqSubmit = (Button) findViewById(R.id.phqSub);
        phqSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boolean PHQcompleted = true;
                String phq = "{";
                RadioButton selected;

                // go through each question and grab the answer
                for(int i=0; i<questions.length; i++){
                    int checked = questions[i].getCheckedRadioButtonId();
                    // if a question is unanswered, stop gathering answers and dont send
                    if(checked == -1){
                        PHQcompleted = false;
                        break;
                    }
                    selected = (RadioButton) findViewById(checked);
                    phq += "\"Q" + i + "\":\"" + selected.getText().toString() + "\",";
                }
                // send PHQ answers and move to next window if all PHQ questions were answered
                if(PHQcompleted) {
                    phq = phq.substring(0, phq.length() - 1);
                    phq += "}";
                    serverHook.sendToServer("phq", phq);

                    startActivity(new Intent(phqActivity.this, resultsActivity.class));
                }
                else{
                    // say something about needing to complete
                }

            }
        });

        scrollView2.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView2.getScrollY(); // For ScrollView
                int scrollHeight = scrollChild.getHeight();
                int var = (scrollHeight-1100);
                float alph = (float)(var-scrollY)/(float)var;

                butty.setAlpha(alph);
            }
        });

    }

}
