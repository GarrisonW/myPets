package com.garrison.mypets;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Garrison on 2/5/2015.
 */
public class VetsListViewAdapter  extends CursorAdapter {
    private final String LOG_TAG = VetsListViewAdapter.class.getSimpleName();

    public GoogleMap googleMap;

    public VetsListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private Uri mAvatarUri = null;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        googleMap = VetsMapActivity.getGoogleMap();

        View view = LayoutInflater.from(context).inflate(R.layout.vet_list_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String lastLatString = context.getString(R.string.pref_latitude);
        String lastLongString = context.getString(R.string.pref_longitude);
        double latitude = Double.longBitsToDouble(sharedPreference.getLong(lastLatString, 0));
        double longitude = Double.longBitsToDouble(sharedPreference.getLong(lastLongString, 0));
        LatLng currentLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("I am here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_lightblue)));

        float zoomLevel = 12;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel);
        googleMap.moveCamera(cameraUpdate);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String vetName = cursor.getString(VetsListFragment.ADAPTER_BINDER_COL_VET_NAME);
        viewHolder.vetNameView.setText(vetName);

        String vetAddress = cursor.getString(VetsListFragment.ADAPTER_BINDER_COL_VET_ADDRESS);
        viewHolder.vetAddressView.setText(vetAddress);

        double latitude = cursor.getDouble(VetsListFragment.ADAPTER_BINDER_COL_VET_LATITUDE);
        double longitude = cursor.getDouble(VetsListFragment.ADAPTER_BINDER_COL_VET_LONGITUDE);
        int open = cursor.getInt(VetsListFragment.ADAPTER_BINDER_COL_VET_OPEN);

        if (open == 0)
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(vetName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)));
        else if (open == 1)
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(vetName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)));
        else if (open == 2)
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(vetName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_yellow)));
;
    }

    //  Helper to reduce mapping
    public static class ViewHolder {

        public final TextView vetNameView;
        public final TextView vetAddressView;

        public ViewHolder(View view) {
            vetNameView = (TextView) view.findViewById(R.id.list_vet_name);
            vetAddressView = (TextView) view.findViewById(R.id.list_vet_address);
        }
    }

}
