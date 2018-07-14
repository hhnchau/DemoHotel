package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.Province;

import java.util.List;

/**
 * Created by xuan on 7/12/2016.
 */
public class ProvinceSpinAdapter extends BaseAdapter {
    private Context context;
    private List<Province> data;
    public ProvinceSpinAdapter(Context context, List<Province> data){
        this.data = data;
        this.context = context;
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
        return data.get(position).getSn();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.province_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.provinceName =  convertView.findViewById(R.id.provinceName);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.provinceName.setText(data.get(position).getName());
        return convertView;
    }

    private class ViewHolder{
        TextView provinceName;
    }
}
