package com.app.kiranpuppala.event;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);


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
