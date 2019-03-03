package com.app.kiranpuppala.event.management;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.kiranpuppala.event.onboard.GetInActivity;
import com.app.kiranpuppala.event.menu.Menu_Activity;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.utils.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.kiranpuppala.event.network.AuthUtils.isTokenValid;

public class EventManagementActivity extends AppCompatActivity {

    private static final String LOG_TAG = "EVENT_MANAGEMENT_ACTIVITY";
    AccountManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        ((TextView)(findViewById(R.id.pageTitle))).setText("MANAGE");
        authenticate();
    }

    private void renderContent(JSONArray jsonArray) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ArrayList<JSONObject> values = new ArrayList<JSONObject>();

        try{
            if (jsonArray != null) {
                for (int i=0;i<jsonArray.length();i++){
                    values.add(jsonArray.getJSONObject(i));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(), values);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.menu_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(EventManagementActivity.this, android.support.v4.util.Pair.create(view, "transition"));
                int revealX = (int) (view.getX() + view.getWidth() / 2);
                int revealY = (int) (view.getY() + view.getHeight() / 2);

                Intent intent = new Intent(EventManagementActivity.this, Menu_Activity.class);
                intent.putExtra(Menu_Activity.EXTRA_CIRCULAR_REVEAL_X, revealX);
                intent.putExtra(Menu_Activity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

                ActivityCompat.startActivity(EventManagementActivity.this, intent, options.toBundle());

                startActivity(intent);
            }
        });
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
                        final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        if(isTokenValid(getBaseContext(),authtoken)){
                            Map<String,String> headers = new HashMap<>();
                            headers.put("authorization",authtoken);

                            JSONObject request = new JSONObject();
                            request.put("email",Session.get(getBaseContext(),"user_email"));

                            ApiClient.makeRequest(EventManagementActivity.this, request, headers, Request.Method.POST, ApiClient.LIST_EVENTS_PATH, new ResponseCallback() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try{
                                        JSONArray eventsObj = jsonObject.getJSONArray("response");
                                        renderContent(eventsObj);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }else{
                            am.invalidateAuthToken(GetInActivity.ARG_ACCOUNT_TYPE,authtoken);
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
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a:
                //Write your code
                return true;
            case R.id.b:
                //Write your code
                return true;
            case R.id.c:
                //Write your code
                return true;
            case R.id.d:
                //Write your code
                return true;
            case R.id.e:
                //Write your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
