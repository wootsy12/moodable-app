package com.example.alex.datascraper;

import android.app.Application;

/*
Class for tracking how much of the application the user has completed and calculation
the financial compensation they will receive
The more the user complete, the more compensation they will receive
The code the user receives at the end contains extra numbers corresponding to which modules they
completed
When they submit the code on Amazon Turk, we can see how much they completed and reward them
 */
public class MyApplication extends Application {

    private double someVariable=0.40;
    private boolean recordingComplete=false;
    private boolean googleComplete=false;
    private boolean twitterComplete=false;
    private boolean instaComplete=false;

    // functions for altering variables
    public double getComepnsation() {
        return someVariable;
    }

    public void addCompensation(double add) {
        someVariable+=add;
    }

    public void completeRecording() {
        recordingComplete=true;
    }

    public void completeGoogle() {
        googleComplete=true;
    }

    public void completetwitter() {
        twitterComplete=true;
    }

    public void completeInsta() {
        instaComplete=true;
    }

    // Builds the portion of the code the user receives at the end that allows us to see how much
    //they completed
    public String getCompletion() {
        String str = "";
        if(recordingComplete){
            str+="0";
        }
        if(googleComplete){
            str+="3";
        }
        if(twitterComplete) {
            str+="1";
        }
        if(instaComplete) {
            str+="2";
        }
        return str;
    }

    public void setCompensation(double someVariable) {
        this.someVariable = someVariable;
    }
}
