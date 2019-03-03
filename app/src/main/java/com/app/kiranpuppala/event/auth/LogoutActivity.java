package com.app.kiranpuppala.event.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.kiranpuppala.event.onboard.GetInActivity;

import static com.app.kiranpuppala.event.network.AuthUtils.isTokenValid;

public class LogoutActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Log.e("SEEFEW","F");
            String authtoken = getIntent().getStringExtra("authToken");
            AccountManager am = AccountManager.get(this);
            final Account account[] = (am.getAccountsByType(GetInActivity.ARG_ACCOUNT_TYPE));
            if(account.length>0){
                am.removeAccountExplicitly(account[0]);
                am.invalidateAuthToken(GetInActivity.ARG_ACCOUNT_TYPE,authtoken);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
