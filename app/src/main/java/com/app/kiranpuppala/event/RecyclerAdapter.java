package com.app.kiranpuppala.event;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private Typeface weekTypeFace;
    ArrayList<String> adapterValues;


    public RecyclerAdapter(Context context,ArrayList <String> adapterValues){
        this.context = context;
        this.adapterValues=adapterValues;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//       public TextView timeText;
//       public TextView weekText;
//       public TextView repeatText;
//       public Switch simpleSwitch;
        public RelativeLayout parent;

        public ViewHolder(View v) {
            super(v);
//            timeText = (TextView) v.findViewById(R.id.timeText);
            parent = v.findViewById(R.id.item_parent);
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