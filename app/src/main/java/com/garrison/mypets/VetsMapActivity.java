package com.garrison.mypets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Garrison on 2/2/2015.
 */
public class VetsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String LOG_TAG = VetsMapActivity.class.getSimpleName();

    public static GoogleMap googleMap = null;

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String lastLatString = getString(R.string.pref_latitude);
        String lastLongString = getString(R.string.pref_longitude);
        double latitude = Double.longBitsToDouble(sharedPreference.getLong(lastLatString, 0));
        double longitude = Double.longBitsToDouble(sharedPreference.getLong(lastLongString, 0));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("I am here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_lightblue)));

        CameraPosition cameraPosition = googleMap.getCameraPosition();
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        googleMap.moveCamera(cameraUpdate);

    }

    public static GoogleMap getGoogleMap() {
        return googleMap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vet_map_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

}


