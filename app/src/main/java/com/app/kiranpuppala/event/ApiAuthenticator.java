package com.app.kiranpuppala.event;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.AuthUtils;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.utils.Session;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class ApiAuthenticator extends AbstractAccountAuthenticator {
    public Context mContext;
    private String authToken;
    private static final String LOG_TAG= "API_AUTHENTICATOR";

    public ApiAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, GetInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.e(LOG_TAG,"KEY_ACCOUNT_TYPE " + accountType);

        intent.putExtra(GetInActivity.KEY_AUTH_TYPE, authTokenType);
        intent.putExtra(GetInActivity.KEY_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, final Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        Log.e(LOG_TAG,"GET_AUTH_TOKEN ");

        authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)||!AuthUtils.isTokenValid(mContext,authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                authToken = getAuthToken(account,password);
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, GetInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        intent.putExtra(GetInActivity.KEY_AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;

    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }


    public String getAuthToken(Account account,String password){
        try {
            final Map<String,String> req = new HashMap<>();
            req.put("email",account.name);
            req.put("password",password);

            FutureTask apiTask = new FutureTask(new Callable() {
                @Override
                public Object call() throws Exception {
                    return new ApiClient().makeSyncRequest(mContext,req,null,ApiClient.LOGIN_PATH);
                }
            });

            new Thread(apiTask).start();

            JsonObject resObj = new Gson().fromJson((String)apiTask.get(), JsonObject.class);
            if(Integer.parseInt(resObj.get("code")+"")==200){
                 return ((JsonObject)resObj.get("response")).get("token").getAsString();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
