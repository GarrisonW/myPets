package com.garrison.mypets.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.garrison.mypets.R;

/**
 * Created by Garrison on 1/22/2015.
 */
public class LocationFinder {

    private static final String LOG_TAG = LocationFinder.class.getSimpleName();

    public static void setLocation(Context context) {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (networkEnabled)
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            else if (gpsEnabled)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            else {
                // TODO:  No networks
               Toast.makeText(context, "There is no connection available to retrieve vets", Toast.LENGTH_LONG).show();
            }
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putLong(context.getString(R.string.pref_latitude), Double.doubleToLongBits(latitude));
            prefsEditor.putLong(context.getString(R.string.pref_longitude), Double.doubleToLongBits(longitude));
            prefsEditor.commit();

        } catch (SecurityException se) {
            // TODO:  Launch permissions were not granted dialog
            Log.v(LOG_TAG, "Location services not available");
        }
    }
}
