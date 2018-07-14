package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xuan on 7/4/2016.
 */
public class ChooseCouponAdapter extends BaseAdapter {
    private List<CouponIssuedForm> data;
    private Context context;
    SimpleDateFormat formatApi;
    SimpleDateFormat formatView;
    private int selectedIndex = ParamConstants.NOTHING;

    public ChooseCouponAdapter(Context context, List<CouponIssuedForm> data, int selectedIndex) {
        this.data = data;
        this.context = context;
        formatApi = new SimpleDateFormat(context.getString(R.string.date_format_request));
        formatView = new SimpleDateFormat(context.getString(R.string.date_format_view));
        this.selectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        if (data != null) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.choose_coupon_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvTimeValid =  convertView.findViewById(R.id.tvTimeValid);
            viewHolder.tvDiscount =  convertView.findViewById(R.id.tvDiscount);
            viewHolder.tvCouponName =  convertView.findViewById(R.id.tvCouponName);
            viewHolder.imgChecked =  convertView.findViewById(R.id.imgChecked);
            viewHolder.txtCanUse =  convertView.findViewById(R.id.textView_coupon_can_use);
            viewHolder.tvCondition =  convertView.findViewById(R.id.tvCouponCondition);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CouponIssuedForm couponIssuedForm = data.get(position);

        //Set Time Coupon
        viewHolder.tvTimeValid.setText(couponIssuedForm.getStart() + "~" + couponIssuedForm.getEnd());

        try {
            Date start = formatApi.parse(couponIssuedForm.getStart());
            Date end = formatApi.parse(couponIssuedForm.getEnd());
            viewHolder.tvTimeValid.setText(formatView.format(start) + "~" + formatView.format(end));
        } catch (Exception e) {
            MyLog.writeLog("ChooseCouponAdapter " + e);
        }

        //Check Discount type
        if (couponIssuedForm.getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            viewHolder.tvDiscount.setText(context.getString(R.string.discount) + ": " + couponIssuedForm.getDiscount() + " " + context.getString(R.string.percent));
        } else {
            viewHolder.tvDiscount.setText(Utils.formatCurrency(couponIssuedForm.getDiscount()) + " " + context.getString(R.string.currency));
        }

        //Set Coupon Name
        viewHolder.tvCouponName.setText(couponIssuedForm.getPromotionName());

        /*
        / Set Coupon Select
        */
        if (selectedIndex == ParamConstants.NOTHING) {
            viewHolder.imgChecked.setImageResource(R.drawable.checkbox);
        } else {
            if (position == selectedIndex) {
                viewHolder.imgChecked.setImageResource(R.drawable.checkbox_selected);
            } else {
                viewHolder.imgChecked.setImageResource(R.drawable.checkbox);
            }
        }

        /*
        / Set IsCanUse
        */
        if (couponIssuedForm.getCanUse() == ParamConstants.HOTEL_NOT_ACCEPT) {
            viewHolder.txtCanUse.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtCanUse.setVisibility(View.GONE);
        }
        // Set Coupon Conditional
        handleCouponConditionDisplay(position, viewHolder);

        return convertView;
    }

    private void handleCouponConditionDisplay(int position, ViewHolder holder) {

        CouponIssuedForm coupon = data.get(position);

        if (coupon.getCanUse() == ParamConstants.NOT_ENOUGH_CONDITION) {
            holder.tvCondition.setVisibility(View.VISIBLE);
            String message= "";
            if (coupon.getCouponMemo().equals("")){
                message = context.getString(R.string.msg_3_9_promotion_time_frame_4);
            }else {
                if (HotelApplication.isEnglish) {
                    message = context.getResources().getString(R.string.txt_3_9_coupon_not_enough_condition);
                    message = message.replace("key_time", coupon.getCouponMemo().split("\\|")[1]);
                } else {
                    message = context.getResources().getString(R.string.txt_3_9_coupon_not_enough_condition);
                    message = message.replace("key_time", coupon.getCouponMemo().split("\\|")[0]);
                }
            }

            holder.tvCondition.setText(message);
        } else {
            holder.tvCondition.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        TextViewSFRegular tvDiscount;
        TextViewSFRegular tvTimeValid;
        TextViewSFRegular tvCouponName;
        ImageView imgChecked;
        TextView txtCanUse;
        TextView tvCondition;
    }
}
