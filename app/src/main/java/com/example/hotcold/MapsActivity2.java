package com.example.hotcold;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotcold.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hotcold.databinding.ActivityMaps2Binding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity2 extends FragmentActivity implements  OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    public LocationManager myLocationManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    TextView tv1,tv2;
    Button winButton;
    MediaPlayer mediaFirstPlayer,mediaSecondPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        winButton=findViewById(R.id.winButtonXml);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mediaFirstPlayer=MediaPlayer.create(this,R.raw.drum);
        mediaSecondPlayer=MediaPlayer.create(this,R.raw.done);



        mapFragment.getMapAsync(this);
        //Runtimes Permission
        if(ContextCompat.checkSelfPermission(MapsActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity2.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }else{
            getLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getLocation();

    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            myLocationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,1,MapsActivity2.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public float getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        String dist = distance + "M";

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + "KM";
        }
        return distance;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mMap.clear();

        mediaFirstPlayer.start();
        try {

            //Before  Display Marker Check The Distance Between Nodes
            float dis=getDistance(new LatLng(MainActivity.nLat,MainActivity.nLong),new LatLng(location.getLatitude(), location.getLongitude()));
            tv2.setText("Distance : "+dis+"");

            LatLng bau = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            if(dis<5){
                // Display User Marker


                mediaFirstPlayer.stop();

                mediaSecondPlayer.start();
                tv1.setText("Distance : "+dis);
                tv2.setText("Status : Very Closer");

                mMap.addMarker(new MarkerOptions().position(bau).title("My Current Position")
                        .icon(BitmapDescriptorFactory.
                                defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bau,17));
                if(dis<2){
                    winButton.setVisibility(View.VISIBLE);
                }else{
                    winButton.setVisibility(View.INVISIBLE);
                }
                winButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaSecondPlayer.stop();
                        Intent moveToWin=new Intent(MapsActivity2.this,MainActivity2.class);
                        startActivity(moveToWin);
                    }
                });
            }else{
                // Display User Marker

                mMap.addMarker(new MarkerOptions().position(bau).title("My Current Position")
                        .icon(BitmapDescriptorFactory.
                                defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bau,17));
                winButton.setVisibility(View.INVISIBLE);
                mediaSecondPlayer.stop();

                mediaFirstPlayer.start();
                tv1.setText("Distance : "+dis);
                tv2.setText("Status : Away");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


}