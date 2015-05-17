package com.garrison.caretakerme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Garrison on 2/2/2015.
 */
public class VetsMapActivity extends ActionBarActivity implements OnMapReadyCallback  {

    public static GoogleMap googleMap = null;

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        initializeMap();
    }

    public static GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleMap != null)
            initializeMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vet_map_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.vets_map);
        mapFragment.getMapAsync(this);
    }

    public void initializeMap() {

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String lastLatString = getString(R.string.pref_latitude);
        String lastLongString = getString(R.string.pref_longitude);
        double latitude = Double.longBitsToDouble(sharedPreference.getLong(lastLatString, 0));
        double longitude = Double.longBitsToDouble(sharedPreference.getLong(lastLongString, 0));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(getApplicationContext().getString(R.string.text_here))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_lightblue_dot)));

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        googleMap.moveCamera(cameraUpdate);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

    }

}


