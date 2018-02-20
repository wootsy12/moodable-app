package com.example.alex.datascraper;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static Button nextScreenButton;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent myIntent = getIntent(); // gets the previously created intent
        double latitude = myIntent.getDoubleExtra("latitude",1);
        double secondKeyName= myIntent.getDoubleExtra("longitude",1);
        double clinicLat = myIntent.getDoubleExtra("clinicLat",1);
        double clinicLong= myIntent.getDoubleExtra("clinicLng",1);
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng clinic = new LatLng(clinicLat, clinicLong);
        mMap.addMarker(new MarkerOptions().position(clinic).title("Mental Health Clinic"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(clinic));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+clinicLat+","+clinicLong);
        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        nextScreenButton = (Button) findViewById(R.id.navigation);
        nextScreenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(mapIntent);

            }
        });

    }
}
