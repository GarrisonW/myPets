package com.garrison.mypets.sync;
/**
 * Created by Garrison on 9/21/2014.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VetFinderSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static VetFinderSyncAdapter sVetFinderSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sVetFinderSyncAdapter == null) {
                sVetFinderSyncAdapter = new VetFinderSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sVetFinderSyncAdapter.getSyncAdapterBinder();
    }
}
