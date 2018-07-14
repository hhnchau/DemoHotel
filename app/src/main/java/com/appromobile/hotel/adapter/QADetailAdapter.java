package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.CounselingDetailForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 9/5/2016.
 */
public class QADetailAdapter extends BaseAdapter {
    private List<CounselingDetailForm> data;
    private Context context;
    private int userSn;
    private SimpleDateFormat apiFormat, viewFormat;
    public QADetailAdapter(Context context, List<CounselingDetailForm> data, int userSn) {
        this.context = context;
        this.data = data;
        this.userSn = userSn;
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
            convertView = inflater.inflate(R.layout.qa_detail_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvContentMy =convertView.findViewById(R.id.tvContentMy);
            viewHolder.tvDateMy = convertView.findViewById(R.id.tvDateMy);
            viewHolder.tvContentYour =convertView.findViewById(R.id.tvContentYour);
            viewHolder.tvDateYour =  convertView.findViewById(R.id.tvDateYour);
            viewHolder.tvNickname =convertView.findViewById(R.id.tvNickname);
            viewHolder.boxMyPost =  convertView.findViewById(R.id.boxMyPost);
            viewHolder.boxYourPost =  convertView.findViewById(R.id.boxYourPost);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CounselingDetailForm noticeForm = data.get(position);

        if(noticeForm.getReplyStaffSn()==0){
            viewHolder.boxMyPost.setVisibility(View.VISIBLE);
            viewHolder.boxYourPost.setVisibility(View.GONE);
            viewHolder.tvContentMy.setText(noticeForm.getContent());
            try{
                Date date = apiFormat.parse(noticeForm.getLastUpdate());
                viewHolder.tvDateMy.setText(viewFormat.format(date));
            }catch (Exception e){}
        }else{
            viewHolder.boxMyPost.setVisibility(View.GONE);
            viewHolder.boxYourPost.setVisibility(View.VISIBLE);
            viewHolder.tvContentYour.setText(noticeForm.getContent());
            try{
                Date date = apiFormat.parse(noticeForm.getLastUpdate());
                viewHolder.tvDateYour.setText(viewFormat.format(date));
            }catch (Exception e){}
            viewHolder.tvNickname.setText(noticeForm.getReplyStaffName());
        }

        return convertView;
    }

    public void updateData(List<CounselingDetailForm> appNoticeForms) {
        this.data = appNoticeForms;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextViewSFRegular tvContentMy;
        TextViewSFRegular tvDateMy;
        TextViewSFRegular tvContentYour;
        TextViewSFRegular tvDateYour;
        TextViewSFRegular tvNickname;
        LinearLayout boxYourPost;
        LinearLayout boxMyPost;
    }
}
