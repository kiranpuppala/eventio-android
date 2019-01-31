package com.app.kiranpuppala.event;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.app.kiranpuppala.event.ApiAuthenticator;

public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        ApiAuthenticator authenticator = new ApiAuthenticator(this);
        return authenticator.getIBinder();
    }
}