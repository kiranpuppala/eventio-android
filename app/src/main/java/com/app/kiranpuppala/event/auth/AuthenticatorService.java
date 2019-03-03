package com.app.kiranpuppala.event.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.app.kiranpuppala.event.auth.ApiAuthenticator;

public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        ApiAuthenticator authenticator = new ApiAuthenticator(this);
        return authenticator.getIBinder();
    }
}