package com.app.kiranpuppala.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

public class EventDescriptionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "EVENT_DESCR_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().getStringExtra("inputPayload") != null) {
            Log.e(LOG_TAG, getIntent().getStringExtra("inputPayload"));
            inflateView(getIntent().getStringExtra("inputPayload"));
        }
    }

    private void inflateView(String inputPayload){
        try{
            JSONObject payload = new JSONObject(inputPayload);
            Log.e(LOG_TAG,payload.getString("graphic"));
            Glide.with((findViewById(R.id.image))).load(payload.optString("graphic",""));
            ((TextView)(findViewById(R.id.name))).setText(payload.optString("name",""));
            ((TextView)(findViewById(R.id.description))).setText(payload.optString("description",""));
            String timings = payload.optString("from_date","")+" , "+payload.optString("from_time","")+" to "+
                    payload.optString("to_date",""+payload.optString("to_time",""));
            ((TextView)(findViewById(R.id.timings))).setText(timings);
            ((TextView)(findViewById(R.id.venue))).setText("venue");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
