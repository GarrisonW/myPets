package com.garrison.mypets;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.garrison.mypets.data.MyPetsContract.VetsTable;

/**
 * Created by Garrison on 2/5/2015.
 */
public class VetsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, VetsListViewAdapter.Callback {

    private final String LOG_TAG = VetsListFragment.class.getSimpleName();
    private static final int VETS_LOADER = 5;

    private ListView mVetsListView = null;
    private VetsListViewAdapter mVetListViewAdapter = null;

    // For the pets view - columns to be displayed
    private static final String[] VETS_TABLE_COLUMNS = {
            // ID is fully qualified because of later joins
            VetsTable.TABLE_NAME + "." + VetsTable._ID,
            VetsTable.COLUMN_VET_PLACE_ID,
            VetsTable.COLUMN_VET_NAME,
            VetsTable.COLUMN_VET_ADDRESS,
            VetsTable.COLUMN_VET_PHONE,
            VetsTable.COLUMN_VET_LATITUDE,
            VetsTable.COLUMN_VET_LONGITUDE,
            VetsTable.COLUMN_VET_OPEN,
            VetsTable.COLUMN_VET_MY_VET
    };

    // These indices are to be bound (in the adapter) to PET_COLUMNS above.
    public static final int ADAPTER_BINDER_COL_VETS_ID = 0;
    public static final int ADAPTER_BINDER_COL_VETS_PLACE_ID = 1;
    public static final int ADAPTER_BINDER_COL_VET_NAME = 2;
    public static final int ADAPTER_BINDER_COL_VET_ADDRESS = 3;
    public static final int ADAPTER_BINDER_COL_VET_PHONE = 4;
    public static final int ADAPTER_BINDER_COL_VET_LATITUDE = 5;
    public static final int ADAPTER_BINDER_COL_VET_LONGITUDE = 6;
    public static final int ADAPTER_BINDER_COL_VET_OPEN = 7;
    public static final int ADAPTER_BINDER_COL_VET_MY_VET = 8;

    public VetsListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(VETS_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.vet_list_fragment, container, false);


        mVetListViewAdapter = new VetsListViewAdapter(getActivity(), null, 0);
        mVetsListView = (ListView) rootView.findViewById(android.R.id.list);
        mVetsListView.setAdapter(mVetListViewAdapter);
        mVetListViewAdapter.setFragment(this);
        mVetListViewAdapter.setListView(mVetsListView);


        PaintDrawable div = new PaintDrawable(R.drawable.divider);
        mVetsListView.setDivider(div);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(VETS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri vetsUri = VetsTable.buildVetsUri();

        // Loads Cursor for list
        return new CursorLoader(
                getActivity(),
                vetsUri,
                VETS_TABLE_COLUMNS,
                null,
                null,
                VetsTable.COLUMN_VET_DISTANCE_VALUE
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        mVetListViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mVetListViewAdapter.swapCursor(null);
    }

    @Override
    public void phoneVet(String vetNumber) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + vetNumber));
        startActivity(intent);
    }
}
