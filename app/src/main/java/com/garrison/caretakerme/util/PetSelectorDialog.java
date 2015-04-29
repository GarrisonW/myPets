package com.garrison.caretakerme.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.garrison.caretakerme.R;

import java.util.ArrayList;

/**
 * Created by Garrison on 3/27/2015.
 */
public class PetSelectorDialog extends DialogFragment {

    private static String BUNDLE_PET_NAMES = "holdNames";
    private static String BUNDLE_PET_SELECTIONS = "holdSelections";

    private final String LOG_TAG = PetSelectorDialog.class.getSimpleName();

    public CharSequence[] mPetNames;
    public boolean[] mPetSelected;

    public ArrayList<Integer> mSelectedPets = new ArrayList<Integer>();

    public PetSelectorDialog() {}

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mPetNames = savedInstanceState.getCharSequenceArray(BUNDLE_PET_NAMES);
            mPetSelected = savedInstanceState.getBooleanArray(BUNDLE_PET_SELECTIONS);
        }
        else {
            mPetSelected = new boolean[mPetNames.length];
            for (int i = 0; i < mPetSelected.length; i++) {
                mPetSelected[i] = true;
                mSelectedPets.add(i);

            }
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pet_selection);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((Callback) getTargetFragment()).getSelectedPets(mSelectedPets);
                }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .setMultiChoiceItems(mPetNames, mPetSelected, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int whichOne, boolean isChecked) {
                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        mSelectedPets.add(whichOne);
                    } else if (mSelectedPets.contains(whichOne)) {
                        // Else, if the item is already in the array, remove it
                        mSelectedPets.remove(Integer.valueOf(whichOne));
                    }
                    Log.v(LOG_TAG, "THIS ONE SELECTED: " + whichOne + " is " + isChecked);
                }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequenceArray(BUNDLE_PET_NAMES, mPetNames);
        outState.putBooleanArray(BUNDLE_PET_SELECTIONS, mPetSelected);
    }

    public void setPetNames(CharSequence[] names) {
        mPetNames = names;
    }

    public interface Callback {
        public void getSelectedPets(ArrayList<Integer> petsSelected);
    }
}
