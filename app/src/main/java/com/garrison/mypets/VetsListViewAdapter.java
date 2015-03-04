package com.garrison.mypets;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

/**
 * Created by Garrison on 2/5/2015.
 */
public class VetsListViewAdapter extends CursorAdapter {
    private final String LOG_TAG = VetsListViewAdapter.class.getSimpleName();

    int i= -1;

    public VetsListFragment mVetListFragment = null;

    public ListView mVetsListView = null;
    public GoogleMap googleMap;

    private Marker previousMarker = null;
    private Cursor previousCursor = null;

    Cursor mCursor = null;
    Marker mMarker = null;

    public VetsListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mCursor = c;
    }

    private HashMap<Integer, Marker> posToMarkerMap = new HashMap<Integer, Marker>();
    private HashMap<Marker, Integer> markerToPosMap = new HashMap<Marker, Integer>();

    private Uri mAvatarUri = null;

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup viewGroup) {

        googleMap = VetsMapActivity.getGoogleMap();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMarker = marker;
                if (!marker.getTitle().equalsIgnoreCase(context.getString(R.string.text_here)))
                    isolateVet(marker, -1);
                return false;
            }
        });

        View view = LayoutInflater.from(context).inflate(R.layout.vet_list_view, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);


        TextView vetName = viewHolder.vetNameView;
        vetName.setFocusable(false);

        TextView vetAddress = viewHolder.vetAddressView;
        vetAddress.setFocusable(false);

        Button vetCall = viewHolder.vetCallButton;

        vetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mVetsListView.getPositionForView(view);
                Cursor cursor = (Cursor)mVetsListView.getItemAtPosition(pos);
                isolateVet(null, pos);
            }
        });

        vetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mVetsListView.getPositionForView(view);
                Cursor cursor = (Cursor)mVetsListView.getItemAtPosition(pos);
                isolateVet(null, pos);
            }
        });

        vetCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mVetsListView.getPositionForView(view);
                Cursor cursor = (Cursor)mVetsListView.getItemAtPosition(pos);
                String phoneNo = cursor.getString(VetsListFragment.ADAPTER_BINDER_COL_VET_PHONE);
                ((Callback)mVetListFragment).phoneVet(phoneNo);
            }
        });

        vetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mVetsListView.getPositionForView(view);
                Cursor cursor = (Cursor)mVetsListView.getItemAtPosition(pos);
                isolateVet(null, pos);
            }
        });

        return view;
    }

    public void setFragment(VetsListFragment vlf) {
        mVetListFragment = vlf;
    }
    public void setListView(ListView lv) {
        mVetsListView = lv;
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

        int markerBitmapResource = 0;
        int vetItemBitmapResource = 0;

        if (open == 0) {
            markerBitmapResource = R.drawable.marker_red;
            vetItemBitmapResource = R.drawable.circle_red;
        }
        else if (open == 1) {
            markerBitmapResource = R.drawable.marker_green;
            vetItemBitmapResource = R.drawable.circle_green;
        }
        else {
            markerBitmapResource = R.drawable.marker_yellow;
            vetItemBitmapResource = R.drawable.circle_yellow;
        }

        viewHolder.vetStatusImage.setImageResource(vetItemBitmapResource);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(vetName)
                .icon(BitmapDescriptorFactory.fromResource(markerBitmapResource));

        Marker marker = googleMap.addMarker(markerOptions);

        int vet_ID =  cursor.getInt(VetsListFragment.ADAPTER_BINDER_COL_VETS_ID);

        int position = cursor.getPosition();
        posToMarkerMap.put(position, marker);
        markerToPosMap.put(marker, position);

    }

    //  Helper to reduce mapping
    public static class ViewHolder {

        public final ImageView vetStatusImage;
        public final TextView vetNameView;
        public final Button vetCallButton;
        public final TextView vetAddressView;

        public ViewHolder(View view) {
            vetStatusImage = (ImageView) view.findViewById(R.id.list_vet_status_image);
            vetNameView = (TextView) view.findViewById(R.id.list_vet_name);
            vetCallButton = (Button) view.findViewById(R.id.list_vet_call);
            vetAddressView = (TextView) view.findViewById(R.id.list_vet_address);
        }
    }

    public void isolateVet(Marker marker, int position) {

        Cursor cursor = null;

        if (marker == null)
            marker = posToMarkerMap.get(position);
        else
            position = markerToPosMap.get(marker);

        cursor = (Cursor)mVetsListView.getItemAtPosition(position);

        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_purple_dot));
        if ((previousMarker != null) && (previousMarker != marker)) {
            int open = previousCursor.getInt(VetsListFragment.ADAPTER_BINDER_COL_VET_OPEN);
            int bitmapResource = 0;

            if (open == 0)
                bitmapResource = R.drawable.marker_red;
            else if (open == 1)
                bitmapResource = R.drawable.marker_green;
            else
                bitmapResource = R.drawable.marker_yellow;
            previousMarker.setIcon(BitmapDescriptorFactory.fromResource(bitmapResource));
        }

        mVetsListView.setSelection(position);

        marker.showInfoWindow();

        double latitude = cursor.getDouble(VetsListFragment.ADAPTER_BINDER_COL_VET_LATITUDE);
        double longitude = cursor.getDouble(VetsListFragment.ADAPTER_BINDER_COL_VET_LONGITUDE);
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        googleMap.animateCamera(cameraUpdate, 2000, null);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        previousMarker = marker;
        previousCursor = cursor;
    }

    public interface Callback {
        public void phoneVet(String vetNumber);
    }

}
