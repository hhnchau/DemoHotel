package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.List;

/**
 * Created by xuan on 7/27/2016.
 */
public class HomeFavoriteAdapter extends BaseAdapter {
    private Context context;
    private List<UserAreaFavoriteForm> data;

    public HomeFavoriteAdapter(Context context, List<UserAreaFavoriteForm> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.home_favorite_item, parent, false);
        TextViewSFRegular tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(data.get(position).getDistrictName() + ", " + data.get(position).getProvinceName());
        return convertView;
    }
}
