package com.garrison.mypets.Utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Garrison on 1/22/2015.
 */
public class LocationFinder {

    public static String getLocationLongLatString(Context context) {

        String locationString = null;

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
                // TODO: Notify that no service GPS or Network are available
            }
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();
            locationString = latitude + "," + longitude;
            System.out.println("LOCATION: " + locationString);

            if (networkEnabled)
                System.out.println("Network enabled");
        } catch (SecurityException se) {
            // TODO:  Launch permissions were not granted dialog
        }
        return locationString;
    }

}
