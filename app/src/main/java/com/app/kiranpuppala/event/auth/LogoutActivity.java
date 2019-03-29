package com.app.kiranpuppala.event.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.kiranpuppala.event.home.MainActivity;
import com.app.kiranpuppala.event.onboard.GetInActivity;
import com.app.kiranpuppala.event.utils.Session;

import static com.app.kiranpuppala.event.network.AuthUtils.isTokenValid;

public class LogoutActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            String authtoken = getIntent().getStringExtra("authToken");
            AccountManager am = AccountManager.get(this);
            final Account account[] = (am.getAccountsByType(GetInActivity.ARG_ACCOUNT_TYPE));
            if(account.length>0){
                am.removeAccountExplicitly(account[0]);
                am.invalidateAuthToken(GetInActivity.ARG_ACCOUNT_TYPE,authtoken);
                Session.clear(getApplicationContext());
                Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
