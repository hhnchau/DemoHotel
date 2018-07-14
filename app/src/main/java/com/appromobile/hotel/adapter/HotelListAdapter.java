package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.RoomAvailableType;
import com.appromobile.hotel.enums.SortType;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.Utils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by xuan on 7/4/2016.
 */
public class HotelListAdapter extends BaseAdapter {
    private List<HotelForm> data;
    private Context context;
    private int typeSearch;

    private ViewHolder viewHolder;

    public HotelListAdapter(final Context context, List<HotelForm> data, int typeSearch) {
        this.data = data;
        this.context = context;
        this.typeSearch = typeSearch;
        PictureUtils.getInstance().clearCache(context);
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

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.hotel_list_item, parent, false);
            bindViewNormal(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data != null && data.size() > 0) {

            final HotelForm hotelForm = data.get(position);

            if (hotelForm != null) {

                if (typeSearch != SortType.ALPHABET.getType()) {
                    if (hotelForm.isCategory()) {
                        viewHolder.tvCategory.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvCategory.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.tvCategory.setVisibility(View.GONE);
                }

                float distance = calculateDistance(hotelForm);

                String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + hotelForm.getHomeImageSn() + "&fileName=" + hotelForm.getHomeImageName();

                        /*
                        *   Set Hotel Contact or Trial
                        */

                if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                    viewHolder.tvCategory.setText(context.getString(R.string.go2joy_hotel));
                    viewHolder.imgNew.setVisibility(View.GONE);
                    viewHolder.imgHot.setVisibility(View.GONE);
                    viewHolder.imgPromotion.setVisibility(View.GONE);

                    //Set Icon Promotion
                    if (hotelForm.getNewHotel() == 1) {
                        viewHolder.imgNew.setVisibility(View.VISIBLE);
                    }
                    if (hotelForm.getHasPromotion() == 1) {
                        viewHolder.imgPromotion.setVisibility(View.VISIBLE);
                    }
                    if (hotelForm.getHotHotel() == 1) {
                        viewHolder.imgHot.setVisibility(View.VISIBLE);
                    }
                    if (hotelForm.getRoomAvailable() == RoomAvailableType.Available.getType()) {
                        viewHolder.imgRoomAvailable.setImageResource(R.drawable.room);
                    } else {
                        viewHolder.imgRoomAvailable.setImageResource(R.drawable.no_room);
                    }

                    viewHolder.boxVipHotel.setVisibility(View.VISIBLE);
                    viewHolder.boxNonVipHotel.setVisibility(View.GONE);

                    //Set Name Hotel
                    viewHolder.tvNameVip.setText(hotelForm.getName());

                    //Set Distance
                    viewHolder.tvDistanceVip.setText(Utils.meterToKm(distance));

                    //Set Rating
                    int rate = ((int) hotelForm.getAverageMark()) * 2;
                    if (rate <= 0) {
                        viewHolder.tvReview.setVisibility(View.GONE);
                    } else {
                        viewHolder.tvReview.setVisibility(View.VISIBLE);
                        viewHolder.tvReview.setText(String.valueOf(rate));
                    }

                    //--------------Set Price------------
                    int[] discount = Utils.getPromotionInfoForm(hotelForm.getSn());

                    if (discount[0] > 0 || discount[1] > 0) {

                        //Set Price Status
                        viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
                        viewHolder.tvPriceStatus.setText(context.getString(R.string.txt_2_coupon_applied));

                        if (discount[0] > 0) {
                            int priceHourlyDiscount = hotelForm.getLowestPrice() - discount[0];

                            if (priceHourlyDiscount < 0) {
                                priceHourlyDiscount = 0;
                            }

                            //Set Price Hourly Normal
                            viewHolder.tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                            viewHolder.tvPriceHourlyNormal.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));
                            //StrikeThrough
                            viewHolder.tvPriceHourlyNormal.setPaintFlags(viewHolder.tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            //Set Price Hourly Discount
                            viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                        } else {

                            //Set Price Hourly Normal
                            viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                            //Set Price Hourly Discount
                            viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));

                        }

                        //Overnight
                        if (discount[1] > 0) {
                            int priceOvernightDiscount = hotelForm.getLowestPriceOvernight() - discount[1];

                            if (priceOvernightDiscount < 0) {
                                priceOvernightDiscount = 0;
                            }
                            //Set Price Overnight Normal
                            viewHolder.tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                            viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
                            //StrikeThrough
                            viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            //Set Price Overnight Discount
                            viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                        } else {
                            //Set Price Overnight Normal
                            viewHolder.tvPriceOvernightNormal.setVisibility(View.GONE);

                            //Set Price Overnight Discount
                            viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
                        }

                    } else {

                        //Set Price Status
                        viewHolder.tvPriceStatus.setVisibility(View.GONE);

                        //Set Price Hourly Normal
                        viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                        //Set Price Hourly Discount
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));

                        //Set Price Overnight Normal
                        viewHolder.tvPriceOvernightNormal.setVisibility(View.GONE);

                        //Set Price Overnight Discount
                        viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));

                    }


                    //Set Image Hotel
                    PictureUtils.getInstance().load(
                            url,
                            context.getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                            context.getResources().getDimensionPixelSize(R.dimen.hotel_list_height),
                            R.drawable.loading_big,
                            viewHolder.imgHotelVip
                    );


                } else if (hotelForm.getHotelStatus() == ContractType.GENERAL.getType() || hotelForm.getHotelStatus() == ContractType.TERMINAL.getType()) {

                            /*
                            * Set none contact
                            */

                    viewHolder.tvCategory.setText(context.getString(R.string.list));
                    viewHolder.boxVipHotel.setVisibility(View.GONE);
                    viewHolder.boxNonVipHotel.setVisibility(View.VISIBLE);
                    viewHolder.tvNameNonVip.setText(hotelForm.getName());
                    viewHolder.tvDistanceNonVip.setText(Utils.meterToKm(distance));

                    PictureUtils.getInstance().load(
                            url,
                            context.getResources().getDimensionPixelSize(R.dimen.hotel_list_height),
                            context.getResources().getDimensionPixelSize(R.dimen.hotel_list_height),
                            R.drawable.loading_small,
                            viewHolder.imgHotelNonVip
                    );
                }

                        /*
                        /Set Icon 360
                        */
                if (hotelForm.getCountExifImage() > 0) {
                    viewHolder.icon360.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.icon360.setVisibility(View.GONE);
                }


                /*
                / Set Icon Stamp
                */
                if (hotelForm.getNumToRedeem() > 0){
                    viewHolder.iconStamp.setVisibility(View.VISIBLE);
                    viewHolder.tvNumStamp.setText(hotelForm.getActiveStamp() + "/" + hotelForm.getNumToRedeem());
                }else {
                    viewHolder.iconStamp.setVisibility(View.GONE);
                }

                /*
                / OnClick Image 360
                */
//            viewHolder.icon360.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, Panorama.class);
//                    intent.setAction("HotelListAdapter");
//                    intent.putExtra("hotelSn", hotelForm.getSn());
//                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
//                }
//            });

            }

        }
        return convertView;
    }


    public void updateData(List<HotelForm> data, int typeSearch) {
        this.typeSearch = typeSearch;
        this.data = data;
        notifyDataSetChanged();
    }


    private class ViewHolder {
        private TextView tvCategory;

        private TextView tvNameVip, tvReview, tvDistanceVip;
        private ImageView imgHotelVip;
        private ImageView imgHot, imgPromotion, imgNew, imgRoomAvailable;
        private TextView tvOtherPromotion;
        private ImageView icon360;
        private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount;

        private TextView tvNameNonVip, tvDistanceNonVip;
        private ImageView imgHotelNonVip;

        private ImageView imgIconPromotion1, imgIconPromotion2, imgIconPromotion3, imgIconPromotion4;

        private LinearLayout boxHourly, boxOvernight;

        private LinearLayout boxNonVipHotel;
        private RelativeLayout boxVipHotel;
        private RelativeLayout iconStamp;
        private TextView tvNumStamp;
    }


    private void bindViewNormal(View convertView) {
        viewHolder = new ViewHolder();

        viewHolder.tvCategory = convertView.findViewById(R.id.tvCategory);

        viewHolder.tvNameVip = convertView.findViewById(R.id.tvNameVip);
        viewHolder.tvReview = convertView.findViewById(R.id.txtReview);
        viewHolder.tvDistanceVip = convertView.findViewById(R.id.tvDistanceVip);

        viewHolder.imgHotelVip = convertView.findViewById(R.id.imgHotelVip);

        viewHolder.imgHot = convertView.findViewById(R.id.imgHot);
        viewHolder.imgPromotion = convertView.findViewById(R.id.imgPromotion);
        viewHolder.imgNew = convertView.findViewById(R.id.imgNew);
        viewHolder.imgRoomAvailable = convertView.findViewById(R.id.imgRoomAvailable);

        viewHolder.tvOtherPromotion = convertView.findViewById(R.id.tvOtherPromotion);

        viewHolder.icon360 = convertView.findViewById(R.id.item_hotel_icon_360);

        viewHolder.tvPriceStatus = convertView.findViewById(R.id.tvPriceStatus);
        viewHolder.tvPriceHourlyNormal = convertView.findViewById(R.id.tvPriceHourlyNormal);
        viewHolder.tvPriceHourlyDiscount = convertView.findViewById(R.id.tvPriceHourlyDiscount);
        viewHolder.tvPriceOvernightNormal = convertView.findViewById(R.id.tvPriceOvernightNormal);
        viewHolder.tvPriceOvernightDiscount = convertView.findViewById(R.id.tvPriceOvernightDiscount);

        viewHolder.tvNameNonVip = convertView.findViewById(R.id.tvNameNonVip);
        viewHolder.tvDistanceNonVip = convertView.findViewById(R.id.tvDistanceNonVip);
        viewHolder.imgHotelNonVip = convertView.findViewById(R.id.imgHotelNonVip);

        viewHolder.imgIconPromotion1 = convertView.findViewById(R.id.imgIconPromotion1);
        viewHolder.imgIconPromotion2 = convertView.findViewById(R.id.imgIconPromotion2);
        viewHolder.imgIconPromotion3 = convertView.findViewById(R.id.imgIconPromotion3);
        viewHolder.imgIconPromotion4 = convertView.findViewById(R.id.imgIconPromotion4);

        viewHolder.boxHourly = convertView.findViewById(R.id.boxHourly);
        viewHolder.boxOvernight = convertView.findViewById(R.id.boxOvernight);

        viewHolder.boxNonVipHotel = convertView.findViewById(R.id.boxGeneralHotel);
        viewHolder.boxVipHotel = convertView.findViewById(R.id.boxVipHotel);
        viewHolder.iconStamp = convertView.findViewById(R.id.iconStamp);
        viewHolder.tvNumStamp = convertView.findViewById(R.id.tvNumStamp);


        convertView.setTag(viewHolder);

    }

    private float calculateDistance(HotelForm hotel) {
        float distance = 0;
        if (hotel != null) {
            //get hotel location
            Location hotelLocation = new Location("gps");
            hotelLocation.setLatitude(hotel.getLatitude());
            hotelLocation.setLongitude(hotel.getLongitude());
            //get current location
            Location currentLocation = Utils.getLocationFromPref(context);
            //calculate distance
            if (currentLocation != null) {
                distance = currentLocation.distanceTo(hotelLocation);
                MyLog.writeLog("Distance:------------------>" + distance + " m");
            }
        }
        return distance;
    }
}
