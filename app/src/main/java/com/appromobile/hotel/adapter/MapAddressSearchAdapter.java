package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appromobile.hotel.R;

import java.util.List;

/**
 * Created by xuan on 9/5/2016.
 */
public class MapAddressSearchAdapter extends BaseAdapter {
    private List<Address> data;
    private Context context;

    public MapAddressSearchAdapter(Context context, List<Address> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data!=null){
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
        final ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.search_address_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvAddress = convertView.findViewById(R.id.tvAddress);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Address address = data.get(position);

        String locationAddress;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append(", ");
        }
        locationAddress = sb.toString();
        try {
            locationAddress = locationAddress.trim();
            locationAddress = locationAddress.substring(0, locationAddress.length() - 1);
        } catch (Exception e) {
        }

        viewHolder.tvAddress.setText(locationAddress);

//        String country = address.getCountryName();
//        String province = address.getAdminArea();
//        String district = address.getSubAdminArea();



        return convertView;
    }

    public void updateData(List<Address> addresses) {
        this.data = addresses;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView tvAddress;
    }
}
