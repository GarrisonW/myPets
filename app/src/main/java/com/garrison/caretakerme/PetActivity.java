package com.garrison.caretakerme;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuInflater;

/**
 * Created by Garrison on 10/4/2014.
 */
public class PetActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_activity);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

}
