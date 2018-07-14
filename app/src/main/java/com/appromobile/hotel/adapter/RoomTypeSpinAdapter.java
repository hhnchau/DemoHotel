package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.RoomTypeForm;

import java.util.List;

/**
 * Created by xuan on 8/10/2016.
 */
public class RoomTypeSpinAdapter extends BaseAdapter {
    private Context context;
    private List<RoomTypeForm> data;

    public RoomTypeSpinAdapter(Context context, List<RoomTypeForm> data){
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
        try {
            return data.get(position).getSn();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.roomtype_spin_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvRoomName = convertView.findViewById(R.id.tvRoomName);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvRoomName.setText(data.get(position).getName());
        return convertView;
    }

    private class ViewHolder {
        TextView tvRoomName;
    }
}
