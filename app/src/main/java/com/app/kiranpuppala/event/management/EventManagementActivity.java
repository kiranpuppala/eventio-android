package com.app.kiranpuppala.event.management;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.onboard.GetInActivity;
import com.app.kiranpuppala.event.utils.Constants;
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
        setContentView(R.layout.manage_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Manage Events");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(), values,getIntent().getStringExtra(Constants.KEY_AUTH_TOKEN));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    private void authenticate() {


            Map<String,String> headers = new HashMap<>();
            headers.put("authorization",getIntent().getStringExtra(Constants.KEY_AUTH_TOKEN));
            JSONObject request = new JSONObject();

            try{
                request.put("email",Session.get(getBaseContext(),"user_email"));
            }catch (Exception e){
                e.printStackTrace();
            }

            ApiClient.makeRequest(EventManagementActivity.this, request, headers, Request.Method.POST, ApiClient.MANAGE_EVENTS_PATH, new ResponseCallback() {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
}
