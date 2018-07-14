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

import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.CouponStatus;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xuan on 7/12/2016.
 */
public class CouponAdapter extends BaseAdapter {
    private Context context;
    private List<CouponIssuedForm> data;
    SimpleDateFormat formatApi;
    SimpleDateFormat formatView;

    public CouponAdapter(Context context, List<CouponIssuedForm> data) {
        this.data = data;
        this.context = context;
        formatApi = new SimpleDateFormat(context.getString(R.string.date_format_request), Locale.ENGLISH);
        formatView = new SimpleDateFormat(context.getString(R.string.date_format_view), Locale.ENGLISH);
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
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.coupon_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvDiscount = convertView.findViewById(R.id.tvDiscount);
            viewHolder.tvTimeValid = convertView.findViewById(R.id.tvTimeValid);
            viewHolder.tvStatus = convertView.findViewById(R.id.tvStatus);
            viewHolder.imgTriangle = convertView.findViewById(R.id.imgTriangle);
            viewHolder.boxItem = convertView.findViewById(R.id.boxItem);
            viewHolder.boxCoupon = convertView.findViewById(R.id.boxCoupon);
            viewHolder.tvCurrency = convertView.findViewById(R.id.tvCurrency);
            viewHolder.boxDiscount = convertView.findViewById(R.id.boxDiscount);
            viewHolder.tvCouponName = convertView.findViewById(R.id.tvCouponName);
            viewHolder.tvCineDiscount = convertView.findViewById(R.id.tvCineDiscount);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CouponIssuedForm couponIssuedForm = data.get(position);

        viewHolder.tvTimeValid.setText(couponIssuedForm.getStart() + "~" + couponIssuedForm.getEnd());
        try {
            Date start = formatApi.parse(couponIssuedForm.getStart());
            Date end = formatApi.parse(couponIssuedForm.getEnd());
            viewHolder.tvTimeValid.setText(formatView.format(start) + "~" + formatView.format(end));

        } catch (Exception e) {
        }

        viewHolder.tvCouponName.setText(couponIssuedForm.getPromotionName());

        //Check Discount type
        if (couponIssuedForm.getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            viewHolder.tvCineDiscount.setVisibility(View.GONE);
            viewHolder.tvDiscount.setText(context.getString(R.string.discount) + ": " + couponIssuedForm.getDiscount() + " " + context.getString(R.string.percent));
        } else {
            viewHolder.tvDiscount.setText(Utils.formatCurrency(couponIssuedForm.getDiscount()) + " " + context.getString(R.string.currency));
            if (couponIssuedForm.getDiscount() > 0)
                viewHolder.boxDiscount.setVisibility(View.VISIBLE);
            else
                viewHolder.boxDiscount.setVisibility(View.GONE);


            //Discount CineJoy
            if (couponIssuedForm.getCineDiscount() > 0) {
                viewHolder.tvCineDiscount.setVisibility(View.VISIBLE);
                viewHolder.tvCineDiscount.setText(Utils.formatCurrency(couponIssuedForm.getCineDiscount()) + " " + context.getString(R.string.currency) + "(" + context.getString(R.string.txt_3_9_cinejoy_room_only) + ")");
            }
        }

        if (couponIssuedForm.getUsed() == CouponStatus.Valid.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.WHITE);
            viewHolder.imgTriangle.setVisibility(View.GONE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.gr));
            viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_white_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.org));
            viewHolder.tvStatus.setText(context.getString(R.string.valid));
        } else if (couponIssuedForm.getUsed() == CouponStatus.Used.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.BLACK);
            viewHolder.imgTriangle.setVisibility(View.VISIBLE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.dg));
            viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_grey_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.used));
        } else if (couponIssuedForm.getUsed() == CouponStatus.Expired.ordinal()) {
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.BLACK);
            viewHolder.imgTriangle.setVisibility(View.VISIBLE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.dg));
            viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_grey_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.expired));
        }else if (couponIssuedForm.getUsed() == CouponStatus.Temp.ordinal()){
            viewHolder.tvDiscount.setTextColor(Color.BLACK);
            viewHolder.tvTimeValid.setTextColor(Color.BLACK);
            viewHolder.tvCurrency.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.WHITE);
            viewHolder.imgTriangle.setVisibility(View.GONE);
            viewHolder.boxItem.setBackgroundColor(context.getResources().getColor(R.color.red));
            viewHolder.boxCoupon.setBackgroundResource(R.drawable.box_coupon_white_bg);
            viewHolder.tvCouponName.setTextColor(context.getResources().getColor(R.color.bk));
            viewHolder.tvStatus.setText(context.getString(R.string.txt_3_9_coming_soon));
        }


        return convertView;
    }

    private class ViewHolder {
        TextViewSFRegular tvDiscount;
        TextViewSFRegular tvTimeValid;
        TextViewSFRegular tvCurrency;
        TextViewSFRegular tvCouponName;
        TextView tvCineDiscount;
        TextViewSFBold tvStatus;
        LinearLayout boxItem;
        LinearLayout boxCoupon;
        ImageView imgTriangle;
        LinearLayout boxDiscount;
    }
}
