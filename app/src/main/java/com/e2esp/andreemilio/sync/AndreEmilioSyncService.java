package com.e2esp.andreemilio.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Zain on 2/17/2017.
 */

public class AndreEmilioSyncService extends Service {

    public final String LOG_TAG = AndreEmilioSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static AndreEmilioSyncAdapter sAndreEmilioSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sAndreEmilioSyncAdapter == null) {
                sAndreEmilioSyncAdapter = new AndreEmilioSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sAndreEmilioSyncAdapter.getSyncAdapterBinder();
    }

}
