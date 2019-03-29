package com.app.kiranpuppala.event.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.app.kiranpuppala.event.auth.LogoutActivity;
import com.app.kiranpuppala.event.management.EventManagementActivity;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.createevent.CreateEventActivity;
import com.app.kiranpuppala.event.home.MainActivity;
import com.app.kiranpuppala.event.profile.UserAccountActivity;
import com.app.kiranpuppala.event.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by kiran.puppala on 4/6/18.
 */

public class Menu_Activity extends AppCompatActivity {

    View rootLayout;
    private String authToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        final Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(intent!=null&&intent.hasExtra(Constants.KEY_AUTH_TOKEN)){
            authToken=intent.getStringExtra(Constants.KEY_AUTH_TOKEN);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void handleIntent (Class<?> cls){
        Intent i = new Intent(Menu_Activity.this,cls);
        i.putExtra(Constants.KEY_AUTH_TOKEN,authToken);
        startActivity(i);
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

    public void homeClick(View view){
        handleIntent(MainActivity.class);
    }

    public void addClick (View view){
        handleIntent(CreateEventActivity.class);
    }

    public void manageClick (View view){
        handleIntent(EventManagementActivity.class);
    }

    public void accountClick (View view){
        handleIntent(UserAccountActivity.class);
    }

    public void logoutClick(View view) {
        handleIntent(LogoutActivity.class);
    }
}
