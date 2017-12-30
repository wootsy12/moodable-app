package com.example.alex.datascraper;

import android.app.Application;

public class MyApplication extends Application {

    private double someVariable=0.40;
    private boolean recordingComplete=false;
    private boolean googleComplete=false;
    private boolean twitterComplete=false;
    private boolean instaComplete=false;

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

    public String getCompletion() {
        String str = "";
        if(recordingComplete){
            str+="1";
        }
        if(googleComplete){
            str+="2";
        }
        if(twitterComplete) {
            str+="3";
        }
        if(instaComplete) {
            str+="4";
        }
        return str;
    }

    public void setCompensation(double someVariable) {
        this.someVariable = someVariable;
    }
}
