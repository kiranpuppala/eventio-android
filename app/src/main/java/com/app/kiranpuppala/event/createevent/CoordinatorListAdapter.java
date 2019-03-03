package com.app.kiranpuppala.event.createevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.kiranpuppala.event.R;

import java.util.ArrayList;


public class CoordinatorListAdapter extends RecyclerView.Adapter<CoordinatorListAdapter.ViewHolder> {
    ArrayList<String> adapterValues;
    int currPos;


    public CoordinatorListAdapter(Context context, ArrayList <String> adapterValues){
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
    public CoordinatorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {

        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.coordinator_chip, parent, false);
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

    public void refreshEvents( ArrayList <String> values) {
        this.adapterValues.clear();
        this.adapterValues.addAll(values);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return adapterValues.size();
    }

}