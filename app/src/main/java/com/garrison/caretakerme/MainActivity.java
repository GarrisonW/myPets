package com.garrison.caretakerme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.garrison.caretakerme.services.ContactsNotificationService;


public class MainActivity extends ActionBarActivity implements MainFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent mServiceIntent = new Intent(this, ContactsNotificationService.class);
        startService(mServiceIntent);
    }

    // Implementation of Callback from MainFragment
    @Override
    public void onItemSelected(int _ID) {
        Intent intent = new Intent(this, PetActivity.class).putExtra(Intent.EXTRA_TEXT, _ID);
        startActivity(intent);
    }
}
