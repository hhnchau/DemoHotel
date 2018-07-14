package com.appromobile.hotel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appromobile.hotel.R;

/**
 * Created by xuan on 10/11/2016.
 */

public class PointViewAdapter extends RecyclerView.Adapter<PointViewAdapter.ViewHolder> {
    private int total;
    private int selected =0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imgItem;
        public ViewHolder(View view) {
            super(view);
            imgItem = view.findViewById(R.id.imgItem);
        }
    }

    public PointViewAdapter(int total){
        this.total = total;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.point_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == selected){
            holder.imgItem.setImageResource(R.drawable.banner_on);
        }else{
            holder.imgItem.setImageResource(R.drawable.banner);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return total;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }
}
