package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import java.util.List;

/**
 * Created by xuan on 10/6/2016.
 */

public class RecentHotelReviewAdapter extends BaseAdapter {
    private Context context;
    private List<HotelForm> hotelForms;

    public RecentHotelReviewAdapter(Context context, List<HotelForm> hotelForms){
        this.context = context;
        this.hotelForms = hotelForms;
    }

    @Override
    public int getCount() {
        if(hotelForms!=null){
            return hotelForms.size();
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
        ViewHolder viewHolder;
        if(convertView==null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.recent_hotel_review_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvHotelName =  convertView.findViewById(R.id.tvHotelName);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvHotelName.setText(hotelForms.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        TextViewSFRegular tvHotelName;
    }
}
