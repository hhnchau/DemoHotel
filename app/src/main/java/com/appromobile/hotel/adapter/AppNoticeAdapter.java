package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.AppNoticeForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 9/5/2016.
 */
public class AppNoticeAdapter extends BaseAdapter {
    private List<AppNoticeForm> data;
    private Context context;
    private SimpleDateFormat apiFormat, viewFormat;

    public AppNoticeAdapter(Context context, List<AppNoticeForm> data) {
        this.context = context;
        this.data = data;
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
            convertView = inflater.inflate(R.layout.notice_app_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDate =  convertView.findViewById(R.id.tvDate);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppNoticeForm noticeForm = data.get(position);
        viewHolder.tvTitle.setText(noticeForm.getTitle());

        try{
            Date date = apiFormat.parse(noticeForm.getLastUpdate());
            viewHolder.tvDate.setText(viewFormat.format(date));
        }catch (Exception e){}


        return convertView;
    }

    public void updateData(List<AppNoticeForm> appNoticeForms) {
        this.data = appNoticeForms;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextViewSFRegular tvTitle;
        TextViewSFRegular tvDate;
    }
}
