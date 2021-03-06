package com.example.alex.datascraper;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;

/**
 * Created by Dpl on 2/18/2018.
 */


public class ClinicActivity  extends AppCompatActivity {
    TableLayout t1;
    private static final int REQUEST_MULTIPLE = 1;
    double latitude,longitude;
    gpsTracker gps;
    private boolean permissionAccepted = false;
    protected void onCreate(Bundle savedInstanceState) {
        //ScrollView cLayout = (ScrollView) findViewById(R.id.clinicLayout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int res = this.checkCallingOrSelfPermission(permission);
        if(res == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_MULTIPLE);
        }

    }
    void getLocation(){
        gps = new gpsTracker(this);

        latitude = gps.getLatitude(); // returns latitude
        longitude = gps.getLongitude();
        createTable();
        gps.stopUsingGPS();
    }

    void createTable(){
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(this);

        tr_head.setBackgroundColor(0xFF00876C);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        TextView label_name = new TextView(this);
        label_name.setText("Name");
        label_name.setTextColor(Color.WHITE);
        label_name.setPadding(5, 20, 5, 20);
        label_name.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        label_name.setLayoutParams(new TableRow.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        tr_head.addView(label_name);

        TextView labelAddress = new TextView(this);
        labelAddress.setText("Address");
        labelAddress.setTextColor(Color.WHITE);
        labelAddress.setPadding(5, 20, 5, 20);
        labelAddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        labelAddress.setLayoutParams(new TableRow.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        tr_head.addView(labelAddress);

        //add heading
        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        JSONObject clinic;
        int count = 0;
            try {
                clinic = getJSONObjectFromURL(latitude,longitude);
                JSONArray result = clinic.getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject place = result.getJSONObject(i);
                    String name = place.getString("name");
                    String address = place.getString("vicinity");
                    TableRow tr = new TableRow(this);
                    if (count%2==0) tr.setBackgroundColor(0xFF00AF8C);
                    else tr.setBackgroundColor(0xFF00846A);
                    tr.setId(100+count);
                    tr.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    tr.setWeightSum(3);
                    TextView labelName = new TextView(this);
                    labelName.setId(200+count);
                    labelName.setText(name);
                    labelName.setPadding(2, 0, 5, 0);
                    labelName.setTextColor(Color.WHITE);
                    labelName.setLayoutParams(new TableRow.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 2));
                    tr.addView(labelName);
                    labelAddress = new TextView(this);
                    labelAddress.setId(200+count);
                    labelAddress.setText(address.toString());
                    labelAddress.setTextColor(Color.WHITE);
                    labelAddress.setLayoutParams(new TableRow.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    tr.addView(labelAddress);
                    JSONObject location =  place.getJSONObject("geometry").getJSONObject("location");
                    final double locationLat = location.getDouble("lat");
                    final double locationLng = location.getDouble("lng");
                    tr.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            Intent openMap = new Intent(ClinicActivity.this,MapsActivity.class);
                            openMap.putExtra("latitude",latitude);
                            openMap.putExtra("longitude",longitude);
                            openMap.putExtra("clinicLat",locationLat);
                            openMap.putExtra("clinicLng",locationLng);
                            startActivity(openMap);
                        }
                    });

                    tl.addView(tr, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    count++;
                }
            }

            catch(IOException e){

            }
            catch(JSONException e){

            }
    }

    public static JSONObject getJSONObjectFromURL(double latitude, double longitude) throws IOException, JSONException {
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append("5000");
        googlePlacesUrl.append("&keyword=").append("mental");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCUSaNANfi2DS7LWRDCXVWiZ1Qo-sIMytU");
        String urlString = googlePlacesUrl.toString();
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        return new JSONObject(jsonString);
    }

    /*
   Handles response from permissions request, just checks to see if all were granted or not
    */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_MULTIPLE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    permissionAccepted = true;
                    getLocation();
                }
                else{
                    Toast.makeText(this, "GPS is disabled",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

}
