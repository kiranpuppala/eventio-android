package com.app.kiranpuppala.event.onboard;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.home.MainActivity;

import java.util.HashMap;

public class OnBoardingActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;

    private HashMap<Integer, LinearLayout> map = new HashMap<>();

    private LinearLayout firstDot,secondDot,thirdDot;
    TextView next,prev;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        mPager =  findViewById(R.id.pager);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        firstDot = findViewById(R.id.firstDot);
        secondDot = findViewById(R.id.secondDot);
        thirdDot = findViewById(R.id.thirdDot);

        map.put(0,firstDot);
        map.put(1,secondDot);
        map.put(2,thirdDot);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem()!=2){
                    mPager.setCurrentItem(mPager.getCurrentItem()+1);
                    changeCurrentDot(mPager.getCurrentItem());
                }else{
                    Intent intent = new Intent(OnBoardingActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem()!=0){
                    mPager.setCurrentItem(mPager.getCurrentItem()-1);
                    changeCurrentDot(mPager.getCurrentItem());
                }
            }
        });
    }

    int toDp(int pix){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pix, getResources().getDisplayMetrics());
    }

    public void changeCurrentDot (int pos){

        switch (pos){
            case 0 :
                prev.setVisibility(View.INVISIBLE);
                next.setText("NEXT");
                break;
            case 1 :
                prev.setVisibility(View.VISIBLE);
                next.setText("NEXT");
                break;
            case 2 :
                next.setText("FINISH");
                break;
        }

            for(int i=0;i<3;i++){
                if(i==pos){
                    map.get(i).setBackgroundDrawable(getResources().getDrawable( R.drawable.circular_dark ));
                    map.get(i).getLayoutParams().height = map.get(i).getLayoutParams().width = toDp(10);
                    map.get(i).requestLayout();
                }
                else{
                    map.get(i).setBackgroundDrawable(getResources().getDrawable( R.drawable.circular ));
                    map.get(i).getLayoutParams().height = map.get(i).getLayoutParams().width = toDp(8);
                    map.get(i).requestLayout();
                }
            }

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);

        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new OnBoardFragmentOne();
                case 1 :
                    return new OnBoardFragmentTwo();
                case 2 :
                    return new OnBoardFragmentThree();
            }
            return  new OnBoardFragmentOne();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
