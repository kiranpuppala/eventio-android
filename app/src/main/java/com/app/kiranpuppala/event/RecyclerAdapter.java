package com.app.kiranpuppala.event;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    ArrayList<String> adapterValues;
    int times=0;


    public RecyclerAdapter(Context context,ArrayList <String> adapterValues){
        this.context = context;
        this.adapterValues=adapterValues;
    }


    private int dptopx(int pix){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                pix,
                r.getDisplayMetrics()
        );
        return (int)px;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout parent;

        public ViewHolder(View v) {
            super(v);
            parent =  v.findViewById(R.id.item_parent);


            parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            ObjectAnimator animator = ObjectAnimator.ofFloat(parent,"elevation",0f);
                            animator.setInterpolator(new DecelerateInterpolator());
                            animator.setDuration(100);
                            animator.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            parent.setElevation(15f);
                            return false;
                    }


                    return false;
                }
            });


            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(context,EventDescriptionActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }
    }

//    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, src);
//            dr.setCornerRadius(cornerRadius);
//            imageView.setImageDrawable(dr);


    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.event_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

//        holder.simpleSwitch.setChecked(alarmAtPos.getStatus().equals("on")?true:false);

    }


    @Override
    public int getItemCount() {
        return adapterValues.size();
    }

}