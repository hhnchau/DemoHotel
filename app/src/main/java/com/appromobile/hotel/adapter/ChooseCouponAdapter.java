package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.CouponStatus;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        formatApi = new SimpleDateFormat(context.getString(R.string.date_format_request), Locale.ENGLISH);
        formatView = new SimpleDateFormat(context.getString(R.string.date_format_view), Locale.ENGLISH);
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
            viewHolder.tvTimeValid = convertView.findViewById(R.id.tvTimeValid);
            viewHolder.tvDiscount = convertView.findViewById(R.id.tvDiscount);
            viewHolder.tvCouponName = convertView.findViewById(R.id.tvCouponName);
            viewHolder.imgChecked = convertView.findViewById(R.id.imgChecked);
            viewHolder.txtCanUse = convertView.findViewById(R.id.textView_coupon_can_use);
            viewHolder.tvCondition = convertView.findViewById(R.id.tvCouponCondition);
            viewHolder.tvCineDiscount = convertView.findViewById(R.id.tvCineDiscount);

            viewHolder.tvStatus = convertView.findViewById(R.id.tvStatus);
            viewHolder.boxItem = convertView.findViewById(R.id.boxItem);

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
            viewHolder.tvCineDiscount.setVisibility(View.GONE);
            viewHolder.tvDiscount.setText(context.getString(R.string.discount) + ": " + couponIssuedForm.getDiscount() + " " + context.getString(R.string.percent));
            viewHolder.tvDiscount.append("\n" + context.getString(R.string.max_discount) + " " + Utils.formatCurrency(couponIssuedForm.getMaxDiscount()) + " " + context.getString(R.string.currency));
        } else {
            viewHolder.tvDiscount.setText(Utils.formatCurrency(couponIssuedForm.getDiscount()) + " " + context.getString(R.string.currency));
            if (couponIssuedForm.getDiscount() > 0)
                viewHolder.tvDiscount.setVisibility(View.VISIBLE);
            else
                viewHolder.tvDiscount.setVisibility(View.GONE);


            //Discount CineJoy
            if (couponIssuedForm.getCineDiscount() > 0) {
                viewHolder.tvCineDiscount.setVisibility(View.VISIBLE);
                viewHolder.tvCineDiscount.setText(Utils.formatCurrency(couponIssuedForm.getCineDiscount()) + " " + context.getString(R.string.currency) + "(" + context.getString(R.string.txt_3_9_cinejoy_room_only) + ")");
            }
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
        handleCouponConditionDisplay(couponIssuedForm, viewHolder);

        if (couponIssuedForm.getUsed() == CouponStatus.Valid.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            //viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.WHITE);
            //viewHolder.imgTriangle.setVisibility(View.GONE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.gr));
            //viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_white_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.org));
            viewHolder.tvStatus.setText(context.getString(R.string.valid));
        } else if (couponIssuedForm.getUsed() == CouponStatus.Used.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            //viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.BLACK);
            //viewHolder.imgTriangle.setVisibility(View.VISIBLE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.dg));
            //viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_grey_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.used));
        } else if (couponIssuedForm.getUsed() == CouponStatus.Expired.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            //viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.BLACK);
            //viewHolder.imgTriangle.setVisibility(View.VISIBLE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.dg));
            //viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_grey_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.expired));
        }else if (couponIssuedForm.getUsed() == CouponStatus.Temp.ordinal()){
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            //viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.WHITE);
            //viewHolder.imgTriangle.setVisibility(View.GONE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.red));
            //viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_white_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.txt_3_9_coming_soon));
        }else if (couponIssuedForm.getUsed() == CouponStatus.Unregister.ordinal()){
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            //viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.WHITE);
            //viewHolder.imgTriangle.setVisibility(View.GONE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.lb));
            //viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_white_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.txt_3_9_new));
        }


        return convertView;
    }

    private void handleCouponConditionDisplay(CouponIssuedForm coupon, ViewHolder holder) {

        if (coupon.getCanUse() == ParamConstants.NOT_ENOUGH_CONDITION) {
            holder.tvCondition.setVisibility(View.VISIBLE);
            String message = "";
            if (!coupon.getCouponMemo().equals("")) {
                String[] key = coupon.getCouponMemo().split("\\|");
                if (HotelApplication.isEnglish && key.length > 1) {
                    message = context.getResources().getString(R.string.txt_3_9_coupon_not_enough_condition);
                    message = message.replace("key_time", key[1]);
                } else {
                    message = context.getResources().getString(R.string.txt_3_9_coupon_not_enough_condition);
                    message = message.replace("key_time", key[0]);
                }
            }

            holder.tvCondition.setText(message);
        } else {
            holder.tvCondition.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        TextView tvDiscount;
        TextView tvTimeValid;
        TextView tvCouponName;
        TextView tvCineDiscount;
        ImageView imgChecked;
        TextView txtCanUse;
        TextView tvCondition;

        TextView tvStatus;
        LinearLayout boxItem;

    }
}
