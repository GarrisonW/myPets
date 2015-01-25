package com.garrison.mypets;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.garrison.mypets.util.ImageHandler;
import com.garrison.mypets.data.MyPetsContract.PetTable;

/**
 * Created by Garrison on 10/4/2014.
 */
public class PetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PetFragment.class.getSimpleName();

    public static String PET_ID_KEY = "pid";
    public static String PET_POS_KEY = "pos";
    public static int PET_FRAGMENT_LOADER = 2;

    private static String BUNDLE_NAME = "holdName";
    private static String BUNDLE_DIET = "holdDiet";
    private static String BUNDLE_MICROCHIP = "holdMicrochip";
    private static String BUNDLE_AVATAR_URI = "holdAvatarURI";
    private static String BUNDLE_SPECIES_POS = "holdSpeciesPos";
    private static String BUNDLE_FREQUENCY_POS = "holdFrequencyPos";

    private static int savedOrientation = -1;

    ViewHolder viewHolder = null;
    boolean returnFromPetImageLoad = false;
    boolean petActuallyEntered = false;
    boolean deviceNotRotated = true;
    boolean loadedFromBundle = false;

    private int _ID = 0;

    Context mContext = null;
    Uri mImageUri = null;
    String mAvatarUriString = null;
    int mSpeciesPos = -1;
    int mFrequencyPos = -1;
    Bitmap mSizedBitmap = null;

    String mPetName = null;
    String mPetDiet = null;
    String mPetMicrochipID = null;

    public PetFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_pet) {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setTargetFragment(this, 1);
            confirmDialog.show(getFragmentManager(), "ConfirmDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PET_FRAGMENT_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_AVATAR_URI, mAvatarUriString);
        outState.putString(BUNDLE_NAME, mPetName);
        outState.putInt(BUNDLE_SPECIES_POS, mSpeciesPos);
        outState.putInt(BUNDLE_FREQUENCY_POS, mFrequencyPos);
        outState.putString(BUNDLE_DIET, mPetDiet);
        outState.putString(BUNDLE_MICROCHIP, mPetMicrochipID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pet_fragment_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        View rootView = inflater.inflate(R.layout.pet_fragment, container, false);
        viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            _ID = getActivity().getIntent().getIntExtra(Intent.EXTRA_TEXT, -1);
        }

        Spinner speciesSpinner = viewHolder.petSpeciesSpinner;
        ArrayAdapter<CharSequence> speciesCharAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.species_array, R.layout.spinner_item);
        speciesCharAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(speciesCharAdapter);

        Spinner frequencySpinner = viewHolder.petFrequencySpinner;
        ArrayAdapter<CharSequence> charFrequencyAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.frequency_array, R.layout.spinner_item);
        charFrequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(charFrequencyAdapter);

        ImageView petAvatar = viewHolder.petAvatarImageView;
        petAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnFromPetImageLoad = true;

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        if (savedInstanceState != null) {
           mAvatarUriString = savedInstanceState.getString(BUNDLE_AVATAR_URI);

           if (mAvatarUriString != null) {
               mSizedBitmap  = ImageHandler.resizeImage(mContext, mAvatarUriString, 40, 40);
               petAvatar.setImageBitmap(mSizedBitmap);
               Log.v(LOG_TAG, "BITMAP SIZE: W: " + mSizedBitmap.getWidth() + " H: " + mSizedBitmap.getHeight());
           }

           mSpeciesPos = savedInstanceState.getInt(BUNDLE_SPECIES_POS);
           if (mSpeciesPos >= 0)
               speciesSpinner.setSelection(mSpeciesPos);

           mFrequencyPos = savedInstanceState.getInt(BUNDLE_FREQUENCY_POS);
           if (mFrequencyPos >= 0)
               frequencySpinner.setSelection(mFrequencyPos);

           mPetName = savedInstanceState.getString(BUNDLE_NAME);
           viewHolder.petNameEditText.setText(mPetName);

           mPetDiet = savedInstanceState.getString(BUNDLE_DIET);
           viewHolder.petDietEditText.setText(mPetDiet);

           mPetMicrochipID = savedInstanceState.getString(BUNDLE_MICROCHIP);
           viewHolder.petMicrochipEditText.setText(mPetMicrochipID);

            loadedFromBundle = true;

        }
        savedOrientation = getResources().getConfiguration().orientation;

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        Configuration config = getResources().getConfiguration();

        if (config.orientation != savedOrientation) {
            deviceNotRotated = false;
            //mAvatarUriString
            mPetName = viewHolder.petNameEditText.getText().toString();
            mPetDiet = viewHolder.petDietEditText.getText().toString();
            mFrequencyPos = viewHolder.petFrequencySpinner.getSelectedItemPosition();
            mSpeciesPos = viewHolder.petSpeciesSpinner.getSelectedItemPosition();
            mPetMicrochipID = viewHolder.petMicrochipEditText.getText().toString();
        }

        if (!returnFromPetImageLoad && deviceNotRotated)
            storePet();
        returnFromPetImageLoad = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            mImageUri = data.getData();
            mAvatarUriString = mImageUri.toString();

            mSizedBitmap  = ImageHandler.resizeImage(mContext, mAvatarUriString, 40, 40);
            ImageView petAvatarImageView = (ImageView) viewHolder.petAvatarImageView;
            petAvatarImageView.setImageBitmap(mSizedBitmap);

        }
    }

    private void storePet() {

        String petName = viewHolder.petNameEditText.getText().toString();
        long species_pos = viewHolder.petSpeciesSpinner.getSelectedItemId();
        String species_text = (String) viewHolder.petSpeciesSpinner.getSelectedItem();
        String dietText = viewHolder.petDietEditText.getText().toString();
        long frequency_pos = viewHolder.petFrequencySpinner.getSelectedItemId();
        String microchipID = viewHolder.petMicrochipEditText.getText().toString();

        String holdUriString = mAvatarUriString;
        if (mImageUri != null)
            mAvatarUriString = mImageUri.toString();

        if (petName.length() == 0 &&
                species_pos == 0 &&
                dietText.length() == 0 &&
                frequency_pos == 0 &&
                microchipID.length() == 0 &&
                mAvatarUriString == holdUriString)
            petActuallyEntered = false;
        else
            petActuallyEntered = true;

        // Set pet data for update
        if (petActuallyEntered) {
            ContentValues petValues = new ContentValues();
            petValues.put(PetTable.COLUMN_PET_NAME, petName);
            petValues.put(PetTable.COLUMN_SPECIES_POS, species_pos);
            petValues.put(PetTable.COLUMN_SPECIES_TEXT, species_text);
            petValues.put(PetTable.COLUMN_DIET, dietText);
            petValues.put(PetTable.COLUMN_DIET_FREQUENCY_POS, frequency_pos);
            petValues.put(PetTable.COLUMN_MICROCHIP, microchipID);
            petValues.put(PetTable.COLUMN_AVATAR_URI, mAvatarUriString);

            //  If _ID = -1, then user selected "new pet"
            if (_ID > 0) {
                String updateStmt = PetTable._ID + " = " + _ID;
                int numRows = mContext.getContentResolver().update(PetTable.buildPetUri(_ID), petValues, updateStmt, null);
            } else {
                Uri petInsertUri = mContext.getContentResolver().insert(PetTable.CONTENT_URI, petValues);
            }
        }
    }

    public void removePet() {
        Uri petUri = PetTable.buildPetsUri();
        String selection = PetTable._ID + " = " + _ID;
        int rowsDeleted = getActivity().getContentResolver().delete(petUri, selection, null);
        if (rowsDeleted > 0) {
            Log.e(LOG_TAG, "Deleted Pet " + _ID + " Name: " + viewHolder.petNameEditText.getText());
        } else
            Log.e(LOG_TAG, "ERROR Deleting Pet. ID: " + _ID + " Name: " + viewHolder.petNameEditText.getText());
        Toast.makeText(getActivity(),"Pet has been removed", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] columns = new String[]{
                PetTable.TABLE_NAME + "." + PetTable._ID,
                PetTable.COLUMN_PET_NAME,
                PetTable.COLUMN_SPECIES_POS,
                PetTable.COLUMN_SPECIES_TEXT,
                PetTable.COLUMN_DIET,
                PetTable.COLUMN_DIET_FREQUENCY_POS,
                PetTable.COLUMN_MICROCHIP,
                PetTable.COLUMN_AVATAR_URI
        };

        String queryString = PetTable._ID + " = " + _ID;

        if (_ID >= 0) {
            Uri petUri = PetTable.buildPetUri(_ID);

            // Create the CursorLoader
            return new CursorLoader(
                    getActivity(),
                    petUri,
                    columns,  /// projection
                    queryString,
                    null,
                    null
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loadedFromBundle)
            data = null;   //  FYI....Saves on storage
        if (data != null) {
            data.moveToFirst();
            String petName = data.getString(data.getColumnIndex(PetTable.COLUMN_PET_NAME));
            int petSpeciesPos = data.getInt(data.getColumnIndex(PetTable.COLUMN_SPECIES_POS));
            String petDiet = data.getString(data.getColumnIndex(PetTable.COLUMN_DIET));
            int petFrequencyPos = data.getInt(data.getColumnIndex(PetTable.COLUMN_DIET_FREQUENCY_POS));
            String petMicrochip = data.getString(data.getColumnIndex(PetTable.COLUMN_MICROCHIP));
            mAvatarUriString = data.getString(data.getColumnIndex(PetTable.COLUMN_AVATAR_URI));

            if (mAvatarUriString != null) {
                mSizedBitmap = ImageHandler.resizeImage(mContext, mAvatarUriString, 40, 40);
                viewHolder.petAvatarImageView.setImageBitmap(mSizedBitmap);
            }

            viewHolder.petNameEditText.setText(petName);
            viewHolder.petSpeciesSpinner.setSelection(petSpeciesPos);
            viewHolder.petDietEditText.setText(petDiet);
            viewHolder.petFrequencySpinner.setSelection(petFrequencyPos);
            viewHolder.petMicrochipEditText.setText(petMicrochip);
        }
    }

    @Override
    public void onLoaderReset (Loader < Cursor > loader) {}

    //  Helper to reduce mapping
    public static class ViewHolder {
        public final ImageView petAvatarImageView;
        public final EditText petNameEditText;
        public final EditText petDietEditText;
        public final EditText petMicrochipEditText;
        public final Spinner petSpeciesSpinner;
        public final Spinner petFrequencySpinner;

        public ViewHolder(View view) {
            petAvatarImageView = (ImageView) view.findViewById(R.id.imageview_pet_avatar);
            petNameEditText = (EditText) view.findViewById(R.id.edittext_name);
            petDietEditText = (EditText) view.findViewById(R.id.edittext_diet);
            petMicrochipEditText = (EditText) view.findViewById(R.id.edittext_microchip);
            petSpeciesSpinner = (Spinner) view.findViewById(R.id.spinner_species);
            petFrequencySpinner = (Spinner) view.findViewById(R.id.spinner_frequency);
        }
    }
}
