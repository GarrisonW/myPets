package com.garrison.mypets.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.garrison.mypets.R;

/**
 * Created by Garrison on 3/27/2015.
 */
public class PetSelectorDialog extends DialogFragment {

    private final String LOG_TAG = PetSelectorDialog.class.getSimpleName();

    public CharSequence[] mPetNames;

    public PetSelectorDialog() {};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pet_selection);
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })

                .setMultiChoiceItems(mPetNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        Log.v(LOG_TAG, "Multiselection has occured.");
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setPetNames(CharSequence[] names) {
        mPetNames = names;
    }
}
