package com.garrison.mypets;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrison.mypets.data.MyPetsContract.PetTable;
import com.garrison.mypets.sync.VetFinderSyncAdapter;
import com.garrison.mypets.util.LocationFinder;

import java.io.BufferedReader;

/**
 * Created by Garrison on 9/29/2014.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int PET_LOADER = 0;

    private int numberOfPets = -1;
    public ListView mPetListView = null;
    public PetsListAdapter mPetsListAdapter = null;

    public Button mFindVetButton = null;
    public Button mEmergencyContactsButton = null;
    public ProgressBar mProgressSpinner = null;
    public TextView mPetCountTextView = null;

    double latitude = 0.0;
    double longitude = 0.0;

    BufferedReader reader = null;

    private static final String[] PETS_TABLE_COLUMNS = {
            PetTable.TABLE_NAME + "." + PetTable._ID,
            PetTable.COLUMN_PET_NAME,
            PetTable.COLUMN_SPECIES_POS,
            PetTable.COLUMN_SPECIES_TEXT,
            PetTable.COLUMN_BREED,
            PetTable.COLUMN_COLOR_POS,
            PetTable.COLUMN_COLOR_TEXT,
            PetTable.COLUMN_MICROCHIP,
            PetTable.COLUMN_MEDS_FREE_TEXT,
            PetTable.COLUMN_OTHER_FREE_TEXT,
            PetTable.COLUMN_AVATAR_URI
    };

    public static final int ADAPTER_BINDER_COL_PET_ID = 0;
    public static final int ADAPTER_BINDER_COL_PET_NAME = 1;
    public static final int ADAPTER_BINDER_COL_SPECIES_POS = 2;
    public static final int ADAPTER_BINDER_COL_SPECIES_TEXT = 3;
    public static final int ADAPTER_BINDER_COL_BREED = 4;
    public static final int ADAPTER_BINDER_COL_COLOR_POS = 5;
    public static final int ADAPTER_BINDER_COL_COLOR_TEXT = 6;
    public static final int ADAPTER_BINDER_COL_MICROCHIP = 7;
    public static final int ADAPTER_BINDER_COL_MEDS_FREE_TEXT = 8;
    public static final int ADAPTER_BINDER_COL_OTHER_FREE_TEXT = 9;
    public static final int ADAPTER_BINDER_COL_AVATAR = 10;

    public MainFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();
        LocalBroadcastManager.getInstance(context).registerReceiver(mVetDataLoadReceiver,
                new IntentFilter(context.getString(R.string.broadcast_vet_data)));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        mProgressSpinner = (ProgressBar) rootView.findViewById(R.id.progress_vet_data);
        mProgressSpinner.setVisibility(View.GONE);

        mPetsListAdapter = new PetsListAdapter(getActivity(), null, 0);

        mPetCountTextView = (TextView) rootView.findViewById(R.id.num_pets_summary);

        mEmergencyContactsButton = (Button) rootView.findViewById(R.id.button_emergency_contacts);
        mEmergencyContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
            }

        });

        mPetListView = (ListView) rootView.findViewById(R.id.listview_pets);
        mPetListView.setAdapter(mPetsListAdapter);

        mPetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = mPetsListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(i)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getInt(ADAPTER_BINDER_COL_PET_ID));
                }
            }
        });

        mFindVetButton = (Button) rootView.findViewById(R.id.button_find_vet);
        mFindVetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = getActivity();

                int result = LocationFinder.setLocation(context);

                if (result == LocationFinder.LOCATION_FINDER_OK) {
                    mProgressSpinner.setVisibility(View.VISIBLE);
                    mFindVetButton.setVisibility(View.GONE);
                    mPetCountTextView.setVisibility(View.GONE);
                    mPetListView.setVisibility(View.GONE);
                    mEmergencyContactsButton.setVisibility(View.GONE);

                    VetFinderSyncAdapter.syncImmediately(context);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.service_not_available)
                            .setNeutralButton(R.string.okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                }
            }

        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add_pet) {
            ((Callback) getActivity()).onItemSelected(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressSpinner.setVisibility(View.GONE);
        mFindVetButton.setVisibility(View.VISIBLE);
        mPetCountTextView.setVisibility(View.VISIBLE);
        mPetListView.setVisibility(View.VISIBLE);
        mEmergencyContactsButton.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(PET_LOADER, null, this);

    }

    @Override
    public void onDestroy() {
        Context context = getActivity();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mVetDataLoadReceiver);
        super.onDestroy();
    }

    /**
     * *******************************************************************************************
     * Cursor Loader for List
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri petsUri = PetTable.buildPetsUri();

        return new CursorLoader(
                getActivity(),
                petsUri,
                PETS_TABLE_COLUMNS,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int numberOfPets = data.getCount();
        Context context = getView().getContext();
        String formattedPetCount = null;

        if (numberOfPets == 0)
            formattedPetCount = context.getString(R.string.startup_text);
        else if (numberOfPets == 1)
            formattedPetCount = context.getString(R.string.formatted_pet_one);
        else
            formattedPetCount = context.getString(R.string.formatted_pet_count, numberOfPets);
        mPetCountTextView.setText(formattedPetCount);
        mPetsListAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPetsListAdapter.swapCursor(null);
    }

    public interface Callback {

        public void onItemSelected(int _ID);
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mVetDataLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra(context.getString(R.string.broadcast_vet_message));
            Intent intentMap = new Intent(context, VetsMapActivity.class);
            startActivity(intentMap);

        }
    };
}
