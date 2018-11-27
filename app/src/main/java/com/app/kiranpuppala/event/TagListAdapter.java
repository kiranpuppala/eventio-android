package com.app.kiranpuppala.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {
    ArrayList<String> adapterValues;
    int currPos;


    public TagListAdapter(Context context, ArrayList <String> adapterValues){
        this.adapterValues=adapterValues;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public  TextView tagText;
        public ImageView chipDelete;

        public ViewHolder(View v) {
            super(v);
            tagText = v.findViewById(R.id.tagText);
            chipDelete = v.findViewById(R.id.chipDelete);

        }
    }

    @Override
    public TagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.tag_chip, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tagText.setText(adapterValues.get(position));
        holder.chipDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterValues.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return adapterValues.size();
    }

}