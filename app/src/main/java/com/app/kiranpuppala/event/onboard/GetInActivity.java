package com.app.kiranpuppala.event.onboard;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.home.MainActivity;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;


public class GetInActivity extends AccountAuthenticatorActivity {

    private static final String LOG_TAG= "GET_IN_ACTIVITY";
    TextInputLayout email,username,password,confirmpassword;
    TextView action_button;
    TextView footer_text,footer_text_ext;
    String mode = "LOGIN";

    public static String KEY_AUTH_TYPE = "authType";
    public static String KEY_IS_ADDING_NEW_ACCOUNT = "isNewAccount";

    public static String ARG_ACCOUNT_TYPE = "com.app.kiranpuppala.event";
    public static String ARG_AUTH_TYPE = "USER_LOGIN";


    AccountManager mAccountManager ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getin_layout);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        username=findViewById(R.id.username);
        confirmpassword=findViewById(R.id.confirmpassword);
        action_button=findViewById(R.id.action_button);
        footer_text=findViewById(R.id.footer_text);
        footer_text_ext=findViewById(R.id.footer_text_ext);

        action_button.setOnClickListener(onClickListener);
        footer_text_ext.setOnClickListener(onClickListener);

        String user_id = Session.get(getApplicationContext(),"user_id");
        if(user_id!=null){
            Intent intent = new Intent(GetInActivity.this, MainActivity.class);
            startActivity(intent);
        }else
        renderAccordingToMode();

        mAccountManager = AccountManager.get(this);
    }

    private void renderAccordingToMode(){
        int visibility;
        String text,text_ext;
        if (mode.equals("LOGIN")) {
            visibility = View.GONE;
            text = "Don't have an account?";
            text_ext = "Signup";
        }
        else {
            visibility = View.VISIBLE;
            text="Already a member?";
            text_ext="Login";
        }
        username.setVisibility(visibility);
        confirmpassword.setVisibility(visibility);
        footer_text.setText(text);
        footer_text_ext.setText(text_ext);
        action_button.setText(mode);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_button: handleAction(); break;
                case R.id.footer_text_ext:
                    mode = mode == "LOGIN" ? "SIGNUP" : "LOGIN";
                    renderAccordingToMode();
                    break;
            }
        }
    };

    private void handleAction(){
        String path = mode=="LOGIN" ? ApiClient.LOGIN_PATH : ApiClient.SIGNUP_PATH;
        try{
            JSONObject request = new JSONObject();
            request.put("email",email.getEditText().getText().toString());
            request.put("password",password.getEditText().getText().toString());
            if(mode.equals("SIGNUP")){
                request.put("user_name",username.getEditText().getText().toString());
            }

            ApiClient.makeRequest(GetInActivity.this, request,null,Request.Method.POST,path,new ResponseCallback(){
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d("JSON RESPONSE",jsonObject.getInt("code")+"");
                        if(jsonObject.getInt("code")==200){

                            JSONObject res = (JSONObject) jsonObject.get("response");

                            final Intent intent = new Intent();
                            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email.getEditText().getText().toString());
                            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ARG_ACCOUNT_TYPE);
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN, res.getString("token"));
                            intent.putExtra(AccountManager.KEY_PASSWORD, password.getEditText().getText().toString());

                            Session.set(getApplicationContext(),"user_id",res.optString("id",""));
                            Session.set(getApplicationContext(),"user_email", email.getEditText().getText().toString());
                            finishLogin(intent);

//                            Intent intent = new Intent(GetInActivity.this,MainActivity.class);
//                            startActivity(intent);
                        }else{
                            Toast.makeText(GetInActivity.this,"wrong email or password",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(KEY_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = ARG_AUTH_TYPE;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}
