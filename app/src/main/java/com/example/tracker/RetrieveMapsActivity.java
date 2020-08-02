package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class RetrieveMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_maps);
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
        mMap = googleMap;

        DatabaseReference base = FirebaseDatabase.getInstance().getReference().child("Current Location");

        base.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Double  longitude = dataSnapshot.child("longitude").getValue(Double.class);
              Double  latitude = dataSnapshot.child("latitude").getValue(Double.class);

              LatLng location = new LatLng(longitude,latitude);


                Toast.makeText(RetrieveMapsActivity.this, "fetching location in a bit.....  ", Toast.LENGTH_SHORT).show();
                mMap.addMarker(new MarkerOptions().position(location).title(getCompleteAdress(latitude,longitude)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location ,14));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private String getCompleteAdress (double Latitude ,double Longitude){
        String address = "";
        Geocoder geocoder = new Geocoder( RetrieveMapsActivity.this , Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(Latitude,Longitude, 1);

            if (address != null){
                Address returnAdress = addresses.get(0);
                StringBuilder stringBuilderReturnAdress = new StringBuilder( "");

                for(int i= 0;i <=returnAdress.getMaxAddressLineIndex();i++){
                    stringBuilderReturnAdress.append(returnAdress.getAddressLine(i)).append("\n");
                }
                address = stringBuilderReturnAdress.toString();
            }
            else{
                Toast.makeText(this, "addresss not found", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_SHORT).show();

        }

        return address;
    }
}
