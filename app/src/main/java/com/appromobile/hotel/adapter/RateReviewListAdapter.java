package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.UpdateRateReviewActivity;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 7/4/2016.
 */
public class RateReviewListAdapter extends BaseAdapter {
    private List<UserReviewForm> data;
    private Context context;
    private HotelDetailForm hotelDetailForm;
    private SimpleDateFormat formatView;
    private SimpleDateFormat formatApi;
    public RateReviewListAdapter(Context context, List<UserReviewForm> data, HotelDetailForm hotelDetailForm){
        this.data = data;
        this.context = context;
        this.hotelDetailForm = hotelDetailForm;
        formatApi = new SimpleDateFormat(context.getString(R.string.date_format_request));
        formatView = new SimpleDateFormat(context.getString(R.string.date_format_view));

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
        if(data!=null) {
            return data.get(position);
        }else{
            return null;
        }
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
            convertView = inflater.inflate(R.layout.rate_review_list_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvNickname =  convertView.findViewById(R.id.tvNickname);
            viewHolder.tvRoomName =  convertView.findViewById(R.id.tvRoomName);
            viewHolder.tvComment =  convertView.findViewById(R.id.tvComment);
            viewHolder.tvDate =  convertView.findViewById(R.id.tvDate);
            viewHolder.btnEdit =  convertView.findViewById(R.id.btnEdit);
            viewHolder.boxMyReview =  convertView.findViewById(R.id.boxMyReview);

            viewHolder.stars[0] =  convertView.findViewById(R.id.btnStar1);
            viewHolder.stars[1] =  convertView.findViewById(R.id.btnStar2);
            viewHolder.stars[2] =  convertView.findViewById(R.id.btnStar3);
            viewHolder.stars[3] =  convertView.findViewById(R.id.btnStar4);
            viewHolder.stars[4] =  convertView.findViewById(R.id.btnStar5);


            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final UserReviewForm userReviewForm = data.get(position);
        if(hotelDetailForm.getHotelStatus()== ContractType.CONTRACT.getType()) {
            viewHolder.tvRoomName.setText(userReviewForm.getRoomTypeName() + "/" + userReviewForm.getRoomName());
        }
        viewHolder.tvComment.setText(userReviewForm.getComment());


        try {
            Date date = formatApi.parse(userReviewForm.getCreateTime());
            viewHolder.tvDate.setText(formatView.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            viewHolder.tvNickname.setText(userReviewForm.getUserNickName());
        }catch (Exception e){}

        for (int i=0;i<viewHolder.stars.length;i++){
            viewHolder.stars[i].setImageResource(R.drawable.review_star);
        }
        if(userReviewForm.getMark()>0){
            for (int i = 1; i<= userReviewForm.getMark(); i++){
                viewHolder.stars[i-1].setImageResource(R.drawable.review_star_fill);
            }
        }

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, UpdateRateReviewActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("UserReviewForm", userReviewForm);
                ((Activity)context).startActivityForResult(intent, 1001);
                ((Activity)context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        if(userReviewForm.isAuthor()){
            viewHolder.boxMyReview.setVisibility(View.VISIBLE);
        }else{
            viewHolder.boxMyReview.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void updateData(List<UserReviewForm> userReviewForms) {
        data = userReviewForms;
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextViewSFRegular tvNickname;
        TextViewSFRegular tvRoomName;
        TextViewSFRegular tvDate;
        TextViewSFRegular tvComment;
        TextViewSFBold btnEdit;
        ImageView [] stars = new ImageView[5];
        LinearLayout boxMyReview;
    }
}
