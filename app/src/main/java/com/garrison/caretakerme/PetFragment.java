package com.garrison.caretakerme;

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

import com.garrison.caretakerme.data.CaretakerMeContract.PetTable;
import com.garrison.caretakerme.util.ImageHandler;

/**
 * Created by Garrison on 10/4/2014.
 */
public class PetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PetFragment.class.getSimpleName();

    public static int PET_FRAGMENT_LOADER = 2;

    private static String BUNDLE_NAME = "holdName";
    private static String BUNDLE_SPECIES_POS = "holdSpeciesPos";
    private static String BUNDLE_BREED = "holdBreed";
    private static String BUNDLE_COLOR_POS = "holdColorPos";
    private static String BUNDLE_DIET = "holdDiet";
    private static String BUNDLE_FREQUENCY_POS = "holdFrequencyPos";
    private static String BUNDLE_MEDS_FREE_TEXT = "holdMedsFreeText";
    private static String BUNDLE_MICROCHIP = "holdMicrochip";
    private static String BUNDLE_AVATAR_URI = "holdAvatarURI";
    private static String BUNDLE_OTHER_FREE_TEXT = "holdOtherFreeText";


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
    int mColorPos = -1;
    int mFrequencyPos = -1;
    Bitmap mSizedBitmap = null;

    String mPetName = null;
    String mPetBreed = null;
    String mPetMedsFreeText = null;
    String mPetDiet = null;
    String mPetMicrochipID = null;
    String mPetOtherFreeText = null;

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
        outState.putString(BUNDLE_BREED, mPetBreed);
        outState.putInt(BUNDLE_COLOR_POS, mColorPos);
        outState.putString(BUNDLE_DIET, mPetDiet);
        outState.putString(BUNDLE_MEDS_FREE_TEXT, mPetMedsFreeText);
        outState.putInt(BUNDLE_FREQUENCY_POS, mFrequencyPos);
        outState.putString(BUNDLE_OTHER_FREE_TEXT, mPetOtherFreeText);
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
        ArrayAdapter<CharSequence> speciesCharAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.array_species, R.layout.spinner_item);
        speciesCharAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(speciesCharAdapter);

        Spinner colorSpinner = viewHolder.petColorSpinner;
        ArrayAdapter<CharSequence> charColorAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.array_color, R.layout.spinner_item);
        charColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(charColorAdapter);

        Spinner frequencySpinner = viewHolder.petFrequencySpinner;
        ArrayAdapter<CharSequence> charFrequencyAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.array_frequency, R.layout.spinner_item);
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
           }

           mSpeciesPos = savedInstanceState.getInt(BUNDLE_SPECIES_POS);
           if (mSpeciesPos >= 0)
               speciesSpinner.setSelection(mSpeciesPos);

           mColorPos = savedInstanceState.getInt(BUNDLE_COLOR_POS);
           if (mColorPos >= 0)
               speciesSpinner.setSelection(mColorPos);

           mFrequencyPos = savedInstanceState.getInt(BUNDLE_FREQUENCY_POS);
           if (mFrequencyPos >= 0)
               frequencySpinner.setSelection(mFrequencyPos);

           mPetName = savedInstanceState.getString(BUNDLE_NAME);
           viewHolder.petNameEditText.setText(mPetName);

           mPetBreed = savedInstanceState.getString(BUNDLE_BREED);
           viewHolder.petNameEditText.setText(mPetName);

           mPetDiet = savedInstanceState.getString(BUNDLE_DIET);
           viewHolder.petDietEditText.setText(mPetDiet);

           mPetMedsFreeText = savedInstanceState.getString(BUNDLE_MEDS_FREE_TEXT);
           viewHolder.petMedsEditText.setText(mPetMedsFreeText);

           mPetMicrochipID = savedInstanceState.getString(BUNDLE_MICROCHIP);
           viewHolder.petMicrochipEditText.setText(mPetMicrochipID);

           mPetOtherFreeText = savedInstanceState.getString(BUNDLE_OTHER_FREE_TEXT);
           viewHolder.petOtherEditText.setText(mPetOtherFreeText);

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
            mSpeciesPos = viewHolder.petSpeciesSpinner.getSelectedItemPosition();
            mColorPos = viewHolder.petSpeciesSpinner.getSelectedItemPosition();
            mPetBreed = viewHolder.petBreedEditText.getText().toString();
            mPetMedsFreeText = viewHolder.petMedsEditText.getText().toString();
            mPetDiet = viewHolder.petDietEditText.getText().toString();
            mFrequencyPos = viewHolder.petFrequencySpinner.getSelectedItemPosition();
            mPetMicrochipID = viewHolder.petMicrochipEditText.getText().toString();
            mPetOtherFreeText = viewHolder.petOtherEditText.getText().toString();
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
            ImageView petAvatarImageView = viewHolder.petAvatarImageView;
            petAvatarImageView.setImageBitmap(mSizedBitmap);

        }
    }

    private void storePet() {

        String petName = viewHolder.petNameEditText.getText().toString();
        long species_pos = viewHolder.petSpeciesSpinner.getSelectedItemId();
        String species_text = (String) viewHolder.petSpeciesSpinner.getSelectedItem();
        long color_pos = viewHolder.petColorSpinner.getSelectedItemId();
        String color_text = (String) viewHolder.petColorSpinner.getSelectedItem();
        String petBreed = viewHolder.petBreedEditText.getText().toString();
        String dietText = viewHolder.petDietEditText.getText().toString();
        String medsFreeText = viewHolder.petMedsEditText.getText().toString();
        long frequency_pos = viewHolder.petFrequencySpinner.getSelectedItemId();
        String frequency_text = (String) viewHolder.petFrequencySpinner.getSelectedItem();
        String microchipID = viewHolder.petMicrochipEditText.getText().toString();
        String otherFreeText = viewHolder.petOtherEditText.getText().toString();

        String holdUriString = mAvatarUriString;
        if (mImageUri != null)
            mAvatarUriString = mImageUri.toString();

        if (petName.length() == 0 &&
                species_pos == 0 &&
                petBreed.length() == 0 &&
                color_pos == 0 &&
                dietText.length() == 0 &&
                frequency_pos == 0 &&
                medsFreeText.length() == 0 &&
                microchipID.length() == 0 &&
                otherFreeText.length() == 0 &&
                mAvatarUriString.equals(holdUriString))
            petActuallyEntered = false;
        else
            petActuallyEntered = true;

        // Set pet data for update
        if (petActuallyEntered) {
            ContentValues petValues = new ContentValues();
            petValues.put(PetTable.COLUMN_PET_NAME, petName);
            petValues.put(PetTable.COLUMN_SPECIES_POS, species_pos);
            petValues.put(PetTable.COLUMN_SPECIES_TEXT, species_text);
            petValues.put(PetTable.COLUMN_BREED, petBreed);
            petValues.put(PetTable.COLUMN_COLOR_POS, color_pos);
            petValues.put(PetTable.COLUMN_COLOR_TEXT, color_text);
            petValues.put(PetTable.COLUMN_DIET, dietText);
            petValues.put(PetTable.COLUMN_DIET_FREQUENCY_POS, frequency_pos);
            petValues.put(PetTable.COLUMN_DIET_FREQUENCY_TEXT, frequency_text);
            petValues.put(PetTable.COLUMN_MEDS_FREE_TEXT, medsFreeText);
            petValues.put(PetTable.COLUMN_MICROCHIP, microchipID);
            petValues.put(PetTable.COLUMN_AVATAR_URI, mAvatarUriString);
            petValues.put(PetTable.COLUMN_OTHER_FREE_TEXT, otherFreeText);

            //  If _ID = -1, then user selected "new pet"
            if (_ID > 0) {
                String updateStmt = PetTable._ID + " = " + _ID;
                mContext.getContentResolver().update(PetTable.buildPetUri(_ID), petValues, updateStmt, null);
            } else {
                mContext.getContentResolver().insert(PetTable.CONTENT_URI, petValues);
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
                PetTable.COLUMN_BREED,
                PetTable.COLUMN_COLOR_POS,
                PetTable.COLUMN_COLOR_TEXT,
                PetTable.COLUMN_DIET,
                PetTable.COLUMN_DIET_FREQUENCY_POS,
                PetTable.COLUMN_DIET_FREQUENCY_TEXT,
                PetTable.COLUMN_MEDS_FREE_TEXT,
                PetTable.COLUMN_MICROCHIP,
                PetTable.COLUMN_AVATAR_URI,
                PetTable.COLUMN_OTHER_FREE_TEXT
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
            getActivity().setTitle(petName);
            int petSpeciesPos = data.getInt(data.getColumnIndex(PetTable.COLUMN_SPECIES_POS));
            String petBreed = data.getString(data.getColumnIndex(PetTable.COLUMN_BREED));
            int petColorPos = data.getInt(data.getColumnIndex(PetTable.COLUMN_COLOR_POS));
            String petDiet = data.getString(data.getColumnIndex(PetTable.COLUMN_DIET));
            int petFrequencyPos = data.getInt(data.getColumnIndex(PetTable.COLUMN_DIET_FREQUENCY_POS));
            String petMedsFreeText = data.getString(data.getColumnIndex(PetTable.COLUMN_MEDS_FREE_TEXT));
            String petMicrochip = data.getString(data.getColumnIndex(PetTable.COLUMN_MICROCHIP));
            String petOtherFeeText = data.getString(data.getColumnIndex(PetTable.COLUMN_OTHER_FREE_TEXT));
            mAvatarUriString = data.getString(data.getColumnIndex(PetTable.COLUMN_AVATAR_URI));

            if (mAvatarUriString != null) {
                mSizedBitmap = ImageHandler.resizeImage(mContext, mAvatarUriString, 40, 40);
                viewHolder.petAvatarImageView.setImageBitmap(mSizedBitmap);
            }

            viewHolder.petNameEditText.setText(petName);
            viewHolder.petSpeciesSpinner.setSelection(petSpeciesPos);
            viewHolder.petBreedEditText.setText(petBreed);
            viewHolder.petColorSpinner.setSelection(petColorPos);
            viewHolder.petDietEditText.setText(petDiet);
            viewHolder.petMedsEditText.setText(petMedsFreeText);
            viewHolder.petFrequencySpinner.setSelection(petFrequencyPos);
            viewHolder.petMicrochipEditText.setText(petMicrochip);
            viewHolder.petOtherEditText.setText(petOtherFeeText);
        }
    }

    @Override
    public void onLoaderReset (Loader < Cursor > loader) {}

    //  Helper to reduce mapping
    public static class ViewHolder {
        public final ImageView petAvatarImageView;
        public final EditText petNameEditText;
        public final EditText petBreedEditText;
        public final EditText petMedsEditText;
        public final EditText petDietEditText;
        public final EditText petMicrochipEditText;
        public final EditText petOtherEditText;
        public final Spinner petSpeciesSpinner;
        public final Spinner petColorSpinner;
        public final Spinner petFrequencySpinner;

        public ViewHolder(View view) {
            petAvatarImageView = (ImageView) view.findViewById(R.id.imageview_pet_avatar);
            petNameEditText = (EditText) view.findViewById(R.id.edittext_name);
            petBreedEditText = (EditText) view.findViewById(R.id.edittext_breed);
            petDietEditText = (EditText) view.findViewById(R.id.edittext_diet);
            petMedsEditText = (EditText) view.findViewById(R.id.edittext_medsFreeText);
            petMicrochipEditText = (EditText) view.findViewById(R.id.edittext_microchip);
            petOtherEditText = (EditText) view.findViewById(R.id.edittext_otherFreeText);
            petSpeciesSpinner = (Spinner) view.findViewById(R.id.spinner_species);
            petColorSpinner = (Spinner) view.findViewById(R.id.spinner_color);
            petFrequencySpinner = (Spinner) view.findViewById(R.id.spinner_frequency);
        }
    }
}
