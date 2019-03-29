package com.app.kiranpuppala.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.profile.UserAccountActivity;
import com.app.kiranpuppala.event.utils.Session;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDescriptionActivity extends AppCompatActivity {

    String authToken="";
    JSONObject eventObj;
    Toolbar toolbar;
    private static final String LOG_TAG = "EVENT_DESCR_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fetching...");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            if(getIntent().getStringExtra("authToken")!=null)
                authToken=getIntent().getStringExtra("authToken");

            if(getIntent().getStringExtra("inputPayload") != null){
                Log.e(LOG_TAG, getIntent().getStringExtra("inputPayload"));
                try{
                    eventObj = new JSONObject(getIntent().getStringExtra("inputPayload"));
                }catch (Exception e){
                    eventObj = new JSONObject();
                    e.printStackTrace();
                }
                inflateView();
            }
        }
    }

    private void inflateView(){
        try{
            Glide.with(getBaseContext()).load(eventObj.optString("graphic","")).into((ImageView)findViewById(R.id.image));
            ((TextView)(findViewById(R.id.description))).setText(eventObj.optString("description",""));
            String timings = eventObj.optString("from_date","")+" , "+eventObj.optString("from_time","")+" to "+
                    eventObj.optString("to_date",""+eventObj.optString("to_time",""));
            ((TextView)(findViewById(R.id.timings))).setText(timings);
            toolbar.setTitle(eventObj.optString("name",""));
            ((TextView)(findViewById(R.id.venue))).setText(eventObj.optString("venue",""));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onJoinClick(View view) {
        try {
            JSONObject request = new JSONObject();
            request.put("email", Session.get(getBaseContext(), "user_email"));
            request.put("id",eventObj.optInt("id",-1));
            Map<String, String> headers = new HashMap<>();
            headers.put("authorization", authToken);

            ApiClient.makeRequest(EventDescriptionActivity.this, request, headers, Request.Method.POST, ApiClient.JOIN_EVENT_PATH , new ResponseCallback() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("code") == 200) {
                            Toast.makeText(EventDescriptionActivity.this,"Joined Succesfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EventDescriptionActivity.this,"Couldn't Join",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
