package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.UserBookingForm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 7/12/2016.
 */
public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<UserBookingForm> data;
    private SimpleDateFormat apiFormat, viewFormat;
    public HistoryAdapter(Context context, List<UserBookingForm> data){
        this.data = data;
        this.context = context;
        apiFormat = new SimpleDateFormat(context.getString(R.string.date_format_request));
        viewFormat = new SimpleDateFormat(context.getString(R.string.date_format_view));
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
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.history_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            viewHolder.tvHotelName =  convertView.findViewById(R.id.tvHotelName);
            viewHolder.tvRoomType =  convertView.findViewById(R.id.tvRoomType);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try{
            Date date = apiFormat.parse(data.get(position).getCheckInDatePlan());
            viewHolder.tvDate.setText(viewFormat.format(date));
        }catch (Exception e){}

        viewHolder.tvHotelName.setText(data.get(position).getHotelName());
        viewHolder.tvRoomType.setText(data.get(position).getRoomTypeName());

        return convertView;
    }
    private class ViewHolder{
        TextView tvDate;
        TextView tvHotelName;
        TextView tvRoomType;
    }
}
