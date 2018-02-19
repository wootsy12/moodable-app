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
import java.net.URLEncoder;
import java.nio.ByteBuffer;

/**
 * Static class used for sending data to our server. Pauses when internet connection is lost and
 * continues where it left off when internet returns.
 */

public class ServerHook extends AppCompatActivity {

    // URL of server to send data to
    private final static String request = "http://depressionmqp.wpi.edu:8080";
    // Unique ID that is the only identifier saved with the data obtained
    public static String identifier = "";

    // Initializes ServerHook by obtaining a unique ID
    // OUPUT - the string identifier received, or an empty string if nothing received
    public static String start(){
        identifier = "";

        // connect to server
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(request + "/initiateclient");

            // Set connection settings
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            // Attempt to read an ID from the server response
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
    // If it fails to it will try again a few times, then start checking for connection
    // only every 2 seconds
    // INPUT - type - the type of data being sent
    // INPUT - msg - the data to send, in string form
    public static void sendToServer(String type, String msg){
        // If error occurs connecting to server, try again 10 times
        int timeout = 0;
        while(timeout < 10){
            try{
                // send data to the server
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

        // if data send failed 10 times, start checking only every 2 seconds
        while(true){
            try{
                // if connection successful, return
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


    /*
    Sends data to the server in the body of an HTTP message
    INPUT - type - the data type being sent
    INPUT - msg  - the data
    THROWS - an exception if connection fails
     */
    private static void attemptToSend(String type, String msg) throws Exception{
        if(identifier == ""){
            Log.d("MYAPP", "Managed to get this far without an ID, not good.");
            return;
        }

        try {
            // connect to server
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String toSend = msg.replace("&", "%26");
            toSend = URLEncoder.encode(toSend, "utf-8");

            // build message as a URI for easy parsing
            // contains an ID, a Type, and the contents of the message
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

            // write the message
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            connection.getInputStream();

            wr.close();
            connection.disconnect();

        }
        // If an error was encountered, pass it up to the calling function
        catch(Exception e) {
            e.printStackTrace();

            throw e;
        }
    }


    // Initializes ServerHook by obtaining a unique ID
    // OUPUT - the string identifier received, or an empty string if nothing received
    public static String getMLResult(){
        identifier = "";

        // connect to server
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL("http://depressionmqp.wpi.edu:3232");

            // Set connection settings
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            // Attempt to read an ID from the server response
            BufferedReader in;
            String output;
            try{

                if (150 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
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


}
