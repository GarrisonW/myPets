package com.garrison.mypets.sync;
/**
 * Created by Garrison on 9/21/2014.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class VetFinderSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static VetFinderSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new VetFinderSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}
