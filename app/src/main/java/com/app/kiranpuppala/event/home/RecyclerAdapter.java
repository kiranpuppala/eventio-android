package com.app.kiranpuppala.event.home;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.kiranpuppala.event.EventDescriptionActivity;
import com.app.kiranpuppala.event.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final String LOG_TAG = "RECYCLER_ADAPTER";
    ArrayList<JSONObject> adapterValues;
    private Context context;
    String authToken;


    public RecyclerAdapter(Context context, ArrayList<JSONObject> adapterValues, String authToken) {
        this.context = context;
        this.adapterValues = adapterValues;
        this.authToken = authToken;
    }


    private int dptopx(int pix) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                pix,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

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
        try {
            JSONObject jsonObject = adapterValues.get(position);
            Glide.with(context).load(jsonObject.getString("graphic")).into(holder.event_image);
            holder.event_name.setText(jsonObject.getString("name"));
            holder.event_category.setText(jsonObject.getString("category"));
            holder.event_date.setText(jsonObject.getString("from_date"));
            holder.event_time.setText(jsonObject.getString("from_time"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return adapterValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout parent;
        TextView event_name, event_category, event_date, event_time;
        ImageView event_image;

        public ViewHolder(View v) {
            super(v);
            parent = v.findViewById(R.id.item_parent);

            event_name =  v.findViewById(R.id.event_name);
            event_category =  v.findViewById(R.id.event_category);
            event_date =  v.findViewById(R.id.event_date);
            event_time =  v.findViewById(R.id.event_time);
            event_image =  v.findViewById(R.id.event_image);


            parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            toggleAnimation(parent,0f);
                            return false;
                        case MotionEvent.ACTION_UP:
                            toggleAnimation(parent,15.0f);
                            return false;
                        case MotionEvent.ACTION_CANCEL:
                            toggleAnimation(parent,15.0f);
                            return false;
                    }

                    return false;
                }
            });

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context, EventDescriptionActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("inputPayload",adapterValues.get(getAdapterPosition()).toString());
                        intent.putExtra("authToken",authToken);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        public void toggleAnimation(View parent,Float value){
            ObjectAnimator animatorrr = ObjectAnimator.ofFloat(parent, "elevation", value);
            animatorrr.setInterpolator(new DecelerateInterpolator());
            animatorrr.setDuration(200);
            animatorrr.start();
        }

    }

}