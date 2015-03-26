package com.garrison.mypets.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.garrison.mypets.R;

/**
 * Created by Garrison on 1/22/2015.
 */
public class LocationFinder {

    private static final String LOG_TAG = LocationFinder.class.getSimpleName();

    public static final int LOCATION_FINDER_OK = 0;
    public static final int LOCATION_FINDER_NO_PERMISSIONS = 1;
    public static final int LOCATION_FINDER_NOT_AVAILABLE = 2;


    public static int setLocation(Context context) {
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

            Location loc = null;

            if (networkEnabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if (gpsEnabled) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else {
                // TODO:  No networks
                Log.v(LOG_TAG, "Location services not available");
            }

            if (loc == null) {
                return LOCATION_FINDER_NOT_AVAILABLE;
            }

            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putLong(context.getString(R.string.pref_latitude), Double.doubleToLongBits(latitude));
            prefsEditor.putLong(context.getString(R.string.pref_longitude), Double.doubleToLongBits(longitude));
            prefsEditor.commit();

        } catch (Exception se) {
            Log.v(LOG_TAG, "Location services require permission");
            return LOCATION_FINDER_NO_PERMISSIONS;
        }

        return LOCATION_FINDER_OK;
    }

}
