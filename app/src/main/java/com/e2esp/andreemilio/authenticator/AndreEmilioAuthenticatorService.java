package com.e2esp.andreemilio.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Zain on 2/17/2017.
 */

public class AndreEmilioAuthenticatorService extends Service {

    private AndreEmilioAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new AndreEmilioAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
