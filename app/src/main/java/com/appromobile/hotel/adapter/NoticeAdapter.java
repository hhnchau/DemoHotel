package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.NoticeForm;

import java.util.List;

/**
 * Created by xuan on 7/19/2016.
 */
public class NoticeAdapter extends BaseAdapter {
    private final List<NoticeForm> data;
    private final Context context;

    public NoticeAdapter(Context context, List<NoticeForm> data) {
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
        if(convertView==null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.notice_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvName =  convertView.findViewById(R.id.tvName);
            viewHolder.line = convertView.findViewById(R.id.line);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final NoticeForm noticeForm = data.get(position);
        viewHolder.tvName.setText(noticeForm.getTitle());
        if(position!=data.size()-1){
            viewHolder.line.setVisibility(View.VISIBLE);
        }else{
            viewHolder.line.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
        View line;
    }
}
