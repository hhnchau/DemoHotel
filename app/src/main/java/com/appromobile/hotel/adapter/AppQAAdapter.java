package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.QAScope;
import com.appromobile.hotel.model.view.CounselingForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 9/5/2016.
 */
public class AppQAAdapter extends BaseAdapter {
    private List<CounselingForm> data;
    private Context context;
    private SimpleDateFormat apiFormat, viewFormat;
    public AppQAAdapter(Context context, List<CounselingForm> data) {
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
            convertView = inflater.inflate(R.layout.notice_qa_app_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDate =  convertView.findViewById(R.id.tvDate);
            viewHolder.imgLock = convertView.findViewById(R.id.imgLock);
            viewHolder.tvUserName = convertView.findViewById(R.id.tvUserName);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CounselingForm noticeForm = data.get(position);
        viewHolder.tvTitle.setText(noticeForm.getTitle());
        try{
            Date date = apiFormat.parse(noticeForm.getLastUpdate());
            viewHolder.tvDate.setText(viewFormat.format(date));
        }catch (Exception e){}

        viewHolder.tvUserName.setText(noticeForm.getAppUserNickName());
        if(noticeForm.getScope() == QAScope.Private.getType()){
            viewHolder.imgLock.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imgLock.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void updateData(List<CounselingForm> appNoticeForms) {
        this.data = appNoticeForms;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextViewSFRegular tvTitle;
        TextViewSFRegular tvDate;
        TextViewSFRegular tvUserName;
        ImageView imgLock;
    }
}
