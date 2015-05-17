package com.garrison.caretakerme.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Garrison on 9/21/2014.
 */
public class VetFinderAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private VetFinderAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new VetFinderAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
