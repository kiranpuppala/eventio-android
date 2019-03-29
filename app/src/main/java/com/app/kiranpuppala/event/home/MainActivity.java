package com.app.kiranpuppala.event.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.createevent.CreateEventActivity;
import com.app.kiranpuppala.event.menu.Menu_Activity;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.onboard.GetInActivity;
import com.app.kiranpuppala.event.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.kiranpuppala.event.network.AuthUtils.isTokenValid;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private AccountManager am;
    private String authToken = "";
    private LinearLayout infoContainer;
    private ImageView infoIcon;
    private TextView infoMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Events");
        setSupportActionBar(toolbar);

        authenticate();
    }

    private void renderContent(JSONArray jsonArray, final String authToken) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        infoContainer = findViewById(R.id.infoContainer);
        infoIcon = findViewById(R.id.infoIcon);
        infoMessage = findViewById(R.id.infoMessage);
        ArrayList<JSONObject> values = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {
                if(jsonArray.length() == 0) inflateInfoContainer(Constants.NO_EVENTS);

                for (int i = 0; i < jsonArray.length(); i++) {
                    values.add(jsonArray.getJSONObject(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(), values, authToken);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

    }

    private void inflateInfoContainer(String msg){
        infoContainer.setVisibility(View.VISIBLE);
        switch (msg){
            case Constants.NO_INTERNET : break;
            case Constants.NO_EVENTS :
                infoIcon.setImageDrawable(getResources().getDrawable(R.drawable.add_event));
                infoMessage.setText("Add Event");
                infoContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                        intent.putExtra(Constants.KEY_AUTH_TOKEN,authToken);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    private void authenticate() {
        am = AccountManager.get(this);
        final Account account[] = (am.getAccountsByType(GetInActivity.ARG_ACCOUNT_TYPE));
        if (account.length != 0) {
            final AccountManagerFuture<Bundle> future = am.getAuthToken(account[0], GetInActivity.ARG_AUTH_TYPE, null, this, null, null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bundle bnd = future.getResult();
                        authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        if (isTokenValid(getBaseContext(), authToken)) {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("authorization", authToken);

                            ApiClient.makeRequest(MainActivity.this, null, headers, Request.Method.POST, ApiClient.LIST_EVENTS_PATH, new ResponseCallback() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        JSONArray eventsObj = jsonObject.getJSONArray("response");
                                        renderContent(eventsObj, authToken);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            am.invalidateAuthToken(GetInActivity.ARG_ACCOUNT_TYPE, authToken);
                            authenticate();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            addNewAccount(GetInActivity.ARG_ACCOUNT_TYPE, GetInActivity.ARG_AUTH_TYPE);
        }

    }


    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = am.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    authenticate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                final View view = findViewById(R.id.menu_overflow);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Menu_Activity.class);
                        intent.putExtra(Constants.KEY_AUTH_TOKEN, authToken);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }
                });
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
