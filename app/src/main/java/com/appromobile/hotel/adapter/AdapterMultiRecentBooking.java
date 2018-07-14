package com.appromobile.hotel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.StatusBooking;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.RecentBookingForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by appro on 09/03/2017.
 */
public class AdapterMultiRecentBooking extends PagerAdapter {
    private Context context;
    private List<RecentBookingForm> recentBookingForms;
    private int minPrice;

    public interface Callback_Multi_Recent_Booking {
        void statusBooking(int status);

        void paynow();
    }

    private Callback_Multi_Recent_Booking callback_multi_recent_booking;

    public static final int SHOW = 1;
    public static final int HIDE = 0;


    public AdapterMultiRecentBooking(Context context, List<RecentBookingForm> recentBookingForms, int minPrice, Callback_Multi_Recent_Booking callback_multi_recent_booking) {
        this.context = context;
        this.recentBookingForms = recentBookingForms;
        this.callback_multi_recent_booking = callback_multi_recent_booking;
        this.minPrice = minPrice;
    }

    @Override
    public int getCount() {
        if (recentBookingForms != null) {
            return recentBookingForms.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_booking_activity, container, false);

        TextView tvCheckinCode = (TextViewSFRegular) view.findViewById(R.id.tvcheckinCode);
        TextView tvBookingId = (TextViewSFRegular) view.findViewById(R.id.tvBookingId);
        TextView tvMemberUid = (TextViewSFRegular) view.findViewById(R.id.tvMemberUid);
        TextView tvDate = (TextViewSFRegular) view.findViewById(R.id.tvDate);
        TextView tvTime = (TextViewSFRegular) view.findViewById(R.id.tvTime);
        TextView tvHotelName = (TextViewSFRegular) view.findViewById(R.id.tvHotelName);
        TextView tvRoomType = (TextViewSFRegular) view.findViewById(R.id.tvRoomType);
        TextView tvPrice = (TextViewSFRegular) view.findViewById(R.id.tvPrice);
        TextView tvCouponDiscount = (TextViewSFRegular) view.findViewById(R.id.tvCouponDiscount);
        TextView tvStampDiscount = (TextViewSFRegular) view.findViewById(R.id.tvStampDiscount);
        TextView tvTotalPayment = (TextViewSFRegular) view.findViewById(R.id.tvTotalPayment);
        TextView tvPaymentStatus = (TextViewSFRegular) view.findViewById(R.id.tvPaymentStatus);
        TextView btnPaynow = (TextViewSFBold) view.findViewById(R.id.btnPaynow);
        btnPaynow.setVisibility(View.GONE);
        TextView tvReservationStatus = (TextViewSFRegular) view.findViewById(R.id.textView_item_recent_booking_reservation_status);

        final RecentBookingForm recent = recentBookingForms.get(position);

        //Checkin Code
        String checkinCode = recent.getCheckinCode();
        if (checkinCode != null && !checkinCode.equals("")) {
            tvCheckinCode.setTextColor(ContextCompat.getColor(context,R.color.org));
            tvCheckinCode.setText(context.getString(R.string.txt_6_3_1_checkin_code) + " " +checkinCode); //Server 78
        } else {
            tvCheckinCode.setTextColor(ContextCompat.getColor(context,R.color.wh));
            tvCheckinCode.setText(context.getString(R.string.txt_6_3_1_checkin_code) + " " +context.getString(R.string.txt_6_3_1_checkin_code_message)); //Server 78
        }

        tvBookingId.setText(context.getString(R.string.txt_1_4_booking_id) + ": " + recent.getBookingNo());
        tvMemberUid.setText(context.getString(R.string.txt_1_4_member_unique_id) + ": " + recent.getMemberId());

        SimpleDateFormat apiFormat = new SimpleDateFormat(context.getString(R.string.date_format_request));
        SimpleDateFormat viewFormat = new SimpleDateFormat(context.getString(R.string.date_format_view));

        try {
            Date date = apiFormat.parse(recent.getCheckInDatePlan());
            String endDate = "";
            // check if booking type = daily
            if (recent.getType() == 3) {
                if (recent.getEndDate() != null && !recent.getEndDate().equals("")) {
                    endDate = "~ " + viewFormat.format(apiFormat.parse(recent.getEndDate()));
                }
            }
            tvDate.setText(context.getString(R.string.txt_1_4_date) + ": " + viewFormat.format(date) + endDate);
        } catch (Exception e) {
            MyLog.writeLog("Multi_Booking" + e);
        }

        tvTime.setText(context.getString(R.string.txt_1_4_time) + ": " + recent.getStartTime() + "~" + recent.getEndTime());
        tvHotelName.setText(context.getString(R.string.txt_1_4_hotel_name) + ": " + recent.getHotelName());
        tvRoomType.setText(context.getString(R.string.txt_1_4_room_type) + ": " + recent.getRoomTypeName());
        tvPrice.setText(context.getString(R.string.txt_1_4_price) + ": " + Utils.formatCurrency(recent.getTotalAmount()) + context.getString(R.string.currency));
        //Check Coupon
        if (recent.getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            tvCouponDiscount.setText(context.getString(R.string.txt_1_4_coupon_discount) + ": " + recent.getDiscount() + context.getString(R.string.percent));
        } else {
            tvCouponDiscount.setText(context.getString(R.string.txt_1_4_coupon_discount) + ": " + Utils.formatCurrency(recent.getDiscount()) + context.getString(R.string.currency));
        }

        //Set Stamp
        tvStampDiscount.setText(context.getString(R.string.txt_6_12_stamp_value) + ": " + Utils.formatCurrency(recent.getRedeemValue()) + context.getString(R.string.currency));

        tvTotalPayment.setText(context.getString(R.string.txt_1_4_total_payment) + ": " + Utils.formatCurrency(recent.getAmountFromUser()) + context.getString(R.string.currency));
        tvReservationStatus.setText(context.getString(R.string.txt_1_4_booking_status) + ": " + StatusBooking.getStatusBooking(context, recent.getBookingStatus()));


//        Check paynow button

        if (recent.getPrepayAmount() > 0) { //Server 78
            //Payment Online
            tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));  //Server 78
            btnPaynow.setVisibility(View.GONE);
        } else {
            if (!recent.isInPast()) {

                /*
                * Check minPrice and trial hotel
                */

                if (recent.getAmountFromUser() >= minPrice) {
                    //Check trial
                    if (recent.getHotelStatus() == ContractType.TRIAL.getType()) {
                        // Hide btn payment, status: unpaid
                        tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
                        btnPaynow.setVisibility(View.GONE);
                    } else {
                        //Show btn payment, enable: pyment
                        if (recent.getPaymentOption() == 2) {
                            //Hide btn Pay Now
                            tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
                            btnPaynow.setVisibility(View.GONE);
                        } else {
                            //Show btn PayNow
                            tvPaymentStatus.setText(context.getString(R.string.txt_1_4_payment_status) + ": ");
                            btnPaynow.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // Hide btn payment, status: unpaid
                    tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
                    btnPaynow.setVisibility(View.GONE);
                }

            } else {
                tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
                btnPaynow.setVisibility(View.GONE);
            }
        }

        /*
        / Check Price = 0
        */
        if (recent.getAmountFromUser() <= 0) {
            btnPaynow.setVisibility(View.GONE);
            tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
        }


//        Check Cancel Booking and QR Scan

        if (recent.getBookingStatus() == 1) {
            callback_multi_recent_booking.statusBooking(SHOW);

        } else if (recent.getBookingStatus() == 3) {
            tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
            btnPaynow.setVisibility(View.GONE);

            callback_multi_recent_booking.statusBooking(HIDE);

            if (!recent.isPrepay()) {
                tvPaymentStatus.setText(context.getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(recent.getPrepayAmount()) + context.getString(R.string.currency));
                btnPaynow.setVisibility(View.GONE);
            }
        }
        if (recent.isInPast()) {
            callback_multi_recent_booking.statusBooking(HIDE);
        }

//        Pay now Click
        btnPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback_multi_recent_booking.paynow();
            }
        });


        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void addData(List<RecentBookingForm> data) {
        recentBookingForms = data;
        notifyDataSetChanged();
    }

}
