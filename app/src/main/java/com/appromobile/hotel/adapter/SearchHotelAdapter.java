package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.List;

/**
 * Created by xuan on 8/29/2016.
 */
public class SearchHotelAdapter extends BaseAdapter {
    private Context context;
    private List<HotelForm> hotelForms;

    public SearchHotelAdapter(Context context, List<HotelForm> hotelForms) {
        this.context = context;
        this.hotelForms = hotelForms;
    }

    @Override
    public int getCount() {
        if (this.hotelForms != null) {
            return this.hotelForms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return this.hotelForms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.search_hotel_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvName =  convertView.findViewById(R.id.tvName);
            viewHolder.btnDelete = convertView.findViewById(R.id.btnDelete);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(this.hotelForms.get(position).getName());
        return convertView;
    }

    public void updateData(List<HotelForm> hotelForms) {
        this.hotelForms = hotelForms;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextViewSFRegular tvName;
        ImageView btnDelete;
    }
}
