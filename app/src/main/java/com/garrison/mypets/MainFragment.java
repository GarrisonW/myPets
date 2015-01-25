package com.garrison.mypets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.garrison.mypets.data.MyPetsContract.PetTable;
import com.garrison.mypets.sync.VetFinderSyncAdapter;
import com.garrison.mypets.utility.LocationFinder;

/**
 * Created by Garrison on 9/29/2014.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>   {

    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final int PET_LOADER = 0;

    private int numberOfPets = -1;
    private ListView petListView = null;
    private MainPetsListAdapter mMainPetsListAdapter = null;

    TextView mPetCountTextView = null;

    private static final String[] PETS_TABLE_COLUMNS = {
            PetTable.TABLE_NAME + "." + PetTable._ID,
            PetTable.COLUMN_PET_NAME,
            PetTable.COLUMN_SPECIES_POS,
            PetTable.COLUMN_SPECIES_TEXT,
            PetTable.COLUMN_MICROCHIP,
            PetTable.COLUMN_AVATAR_URI
    };

    public static final int ADAPTER_BINDER_COL_PET_ID = 0;
    public static final int ADAPTER_BINDER_COL_PET_NAME = 1;
    public static final int ADAPTER_BINDER_COL_SPECIES_POS = 2;
    public static final int ADAPTER_BINDER_COL_SPECIES_TEXT = 3;
    public static final int ADAPTER_BINDER_COL_MICROCHIP = 4;
    public static final int ADAPTER_BINDER_COL_AVATAR = 5;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        mMainPetsListAdapter = new MainPetsListAdapter(getActivity(), null, 0);

        mPetCountTextView = (TextView) rootView.findViewById(R.id.num_pets_summary);

        Button emergencyContactsPicker = (Button) rootView.findViewById(R.id.button_emergency_contacts);
        emergencyContactsPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
            }

        });

        petListView = (ListView) rootView.findViewById(R.id.listview_pets);
        petListView.setAdapter(mMainPetsListAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = mMainPetsListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(i)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getInt(ADAPTER_BINDER_COL_PET_ID));
                }
            }
        });

        Button findVetButton = (Button) rootView.findViewById(R.id.button_find_vet);
        findVetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadVetMap();
            }

        });

        return rootView;
    }

    public void loadVetMap() {
        // Finding location in Synch Adapter causes proble with "Looper"
        String locationString = LocationFinder.getLocationLongLatString(getActivity());
        VetFinderSyncAdapter.setLocation(locationString);

        VetFinderSyncAdapter.syncImmediately(getActivity());
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
            ((Callback)getActivity()).onItemSelected(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PET_LOADER, null, this);
    }

    /**********************************************************************************************
     *  Cursor Loader for List
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
        else
            if (numberOfPets == 1)
                formattedPetCount = context.getString(R.string.formatted_pet_one);
            else
                formattedPetCount = context.getString(R.string.formatted_pet_count, numberOfPets);
        mPetCountTextView.setText(formattedPetCount);
        mMainPetsListAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainPetsListAdapter.swapCursor(null);
    }

    public interface Callback {

        public void onItemSelected(int _ID);
    }
}
