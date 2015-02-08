package com.garrison.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.garrison.mypets.services.ContactsNotificationService;
import com.garrison.mypets.sync.VetFinderSyncAdapter;
import com.garrison.mypets.util.LocationFinder;


public class MainActivity extends ActionBarActivity implements MainFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent mServiceIntent = new Intent(this, ContactsNotificationService.class);
        startService(mServiceIntent);

        String locationString = LocationFinder.getLocationLongLatString(this);
        VetFinderSyncAdapter.setLocationString(locationString);
        VetFinderSyncAdapter.syncImmediately(this);

        MainFragment mainPetsFragment = (MainFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment_main);
    }

    // Implementation of Call back from MainFragment
    @Override
    public void onItemSelected(int _ID) {

        Intent intent = new Intent(this, PetActivity.class).putExtra(Intent.EXTRA_TEXT, _ID);
        startActivity(intent);
    }
}
