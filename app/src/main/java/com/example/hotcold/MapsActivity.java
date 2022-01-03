package com.example.hotcold;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hotcold.databinding.ActivityMapsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public LocationManager myLocationManager;
    double nodeLat=0.0,nodeLong=0.0;
    String nodeName="";
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Runtimes Permission
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
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
        mMap = googleMap;
        //when the map is ready get the current location and put the marker in their

        getLocation();
        // Add a marker in Sydney and move the camera
        /*LatLng bau = new LatLng(32.0250, 35.7168);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.addMarker(new MarkerOptions().position(bau).title("Marker in BAU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bau,20));*/



    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            myLocationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,MapsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(MapsActivity.this, "Location Updates", Toast.LENGTH_SHORT).show();

        try {
            Geocoder myGeocoder=new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addressList=myGeocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String currentAddress=addressList.get(0).getAddressLine(0);
            LatLng bau = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mMap.addMarker(new MarkerOptions().position(bau).title(currentAddress));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bau,20));
            nodeLat=location.getLatitude();
            nodeLong=location.getLongitude();
            nodeName=currentAddress;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SaveNodeInFireBase(View view) {
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(MapsActivity.this);
        loadingDialog.setMessage("Please Wait .......... ");

        loadingDialog.show();
        if(nodeName!="" && nodeLat !=0.0 && nodeLong !=0.0){
            Nodes gameNode=new Nodes("Node",nodeLat,nodeLong);
            databaseReference.child("Nodes").push().setValue(gameNode);
            loadingDialog.dismiss();
            Toast.makeText(MapsActivity.this, "Done Successfully", Toast.LENGTH_LONG).show();
        }else{
            loadingDialog.dismiss();
            Toast.makeText(MapsActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onFlushComplete(int requestCode) {

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