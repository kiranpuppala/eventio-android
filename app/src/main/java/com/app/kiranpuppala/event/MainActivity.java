package com.app.kiranpuppala.event;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MAIN_ACTIVITY";
    AccountManager am;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        authenticate();
    }

    private void renderContent(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ArrayList<String> values = new ArrayList<>(Arrays.asList("a", "b","c"));
        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),values);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);



        findViewById(R.id.menu_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, android.support.v4.util.Pair.create (view, "transition"));
                int revealX = (int) (view.getX() + view.getWidth() / 2);
                int revealY = (int) (view.getY() + view.getHeight() / 2);

                Intent intent = new Intent(MainActivity.this, Menu_Activity.class);
                intent.putExtra(Menu_Activity.EXTRA_CIRCULAR_REVEAL_X, revealX);
                intent.putExtra(Menu_Activity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());

                startActivity(intent);

            }
        });
    }

    private void authenticate(){
        am = AccountManager.get(this);
        Account account[] = (am.getAccountsByType(GetInActivity.ARG_ACCOUNT_TYPE));
        if(account.length!=0){
            final AccountManagerFuture<Bundle> future = am.getAuthToken(account[0], GetInActivity.ARG_AUTH_TYPE, null, this, null, null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bundle bnd = future.getResult();
                        final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        Log.e(LOG_TAG,"AUTH_TOKEN " + authtoken);
                        renderContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            addNewAccount(GetInActivity.ARG_ACCOUNT_TYPE,GetInActivity.ARG_AUTH_TYPE);
        }

    }

    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = am.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);
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
