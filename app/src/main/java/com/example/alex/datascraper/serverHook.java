package com.example.alex.datascraper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Created by Alex on 10/22/2017.
 */

public class serverHook extends AppCompatActivity {

    private final static String request = "http://130.215.249.204:8080";//http://depressionmqp.wpi.edu:8080"; //"http://[insert ip]:8080";
    public static String identifier = "";

    public static String start(){

        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(request + "/initiateclient");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);


            //DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            //DataInputStream in = new DataInputStream(connection.getInputStream());

            BufferedReader in;
            String output;
            try{

                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                while ((output = in.readLine()) != null) {
                    output = output.replace("null", "");
                    identifier += output;
                }
                in.close();
            }catch(Exception e){
                return "";
            }
            connection.disconnect();

        } catch(Exception e) {
            e.printStackTrace();
            identifier = "";
        }
        return identifier;
    }

    // Attempts to send a message to the server
    // If it fails to it will try again a few times, then start checking only every 2 seconds
    public static void sendToServer(String type, String msg){
        int timeout = 0;
        while(timeout < 10){
            try{
                attemptToSend(type, msg);
                return;
            }
            catch(Exception e){
                try{
                    Thread.sleep(100);
                }
                catch(Exception ex){

                }
                timeout++;
            }
        }

        while(true){
            try{
                attemptToSend(type, msg);
                return;
            }
            catch(Exception e){
                try{
                    Thread.sleep(2000);
                }
                catch(Exception ex){

                }
            }
        }

    }

    private static void attemptToSend(String type, String msg) throws Exception{
        if(identifier == null){
            Log.d("MYAPP", "OHHHHH NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            return;
        }

        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String toSend = msg.replace("&", "%26");

            String urlParameters = type + "=" + toSend + "&ID=" + identifier;
            URL url = new URL(request);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            //connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);


            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            connection.getInputStream();

            wr.close();
            connection.disconnect();

        } catch(Exception e) {
            e.printStackTrace();

            throw e;
        }
    }


}
