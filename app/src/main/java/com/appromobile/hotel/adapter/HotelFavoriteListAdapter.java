package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
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
import com.appromobile.hotel.model.request.UserFavoriteDto;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/4/2016.
 */
public class HotelFavoriteListAdapter extends BaseAdapter {
    private List<HotelForm> data;
    private Context context;
    private Location newCurrLocation;
    private ViewHolder viewHolder;

    public HotelFavoriteListAdapter(Context context, List<HotelForm> data) {
        this.data = data;
        this.context = context;
        newCurrLocation = new Location("gps");
        if (PreferenceUtils.getLatLocation(context).equals("")) {
            newCurrLocation.setLongitude(Double.parseDouble(context.getString(R.string.longitude_default)));
            newCurrLocation.setLatitude(Double.parseDouble(context.getString(R.string.latitude_default)));

        } else {
            newCurrLocation.setLatitude(Double.parseDouble(PreferenceUtils.getLatLocation(context)));
            newCurrLocation.setLongitude(Double.parseDouble(PreferenceUtils.getLongLocation(context)));
        }
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

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.hotel_favorite_list_item, parent, false);

            bindViewNormal(convertView);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final HotelForm hotelForm = data.get(position);
        Location location = new Location("gps");

        location.setLatitude(hotelForm.getLatitude());
        location.setLongitude(hotelForm.getLongitude());
        float distance = location.distanceTo(newCurrLocation);

        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + hotelForm.getHomeImageSn() + "&fileName=" + hotelForm.getHomeImageName();

        if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelForm.getHotelStatus() == ContractType.TRIAL.getType()) {
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

            //Set Hotel Name
            viewHolder.tvNameVip.setText(hotelForm.getName());

            //Set Distance
            viewHolder.tvDistanceVip.setText(Utils.meterToKm(distance));

            //Set Rating
            int rate = (int) hotelForm.getAverageMark() * 2;
            if (rate <= 0) {
                viewHolder.tvReview.setVisibility(View.GONE);
            } else {
                viewHolder.tvReview.setText(String.valueOf(rate));
            }


            //--------------Set Price------------
            int[] discount = Utils.getPromotionInfoForm(hotelForm.getSn());

            if (discount[0] > 0 || discount[1] > 0) {

                //Set Price Status
                viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
                viewHolder.tvPriceStatus.setText(context.getString(R.string.txt_2_coupon_applied));

                //Hourly
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


            PictureUtils.getInstance().load(
                    url,
                    context.getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                    context.getResources().getDimensionPixelSize(R.dimen.hotel_list_height),
                    R.drawable.loading_big,
                    viewHolder.imgHotelVip
            );

            /*
            /Set Icon 360
            */
            if (hotelForm.getCountExifImage() > 0) {
                viewHolder.icon360.setVisibility(View.VISIBLE);
            } else {
                viewHolder.icon360.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void updateData(List<HotelForm> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView tvNameVip, tvReview, tvDistanceVip;
        private ImageView imgHotelVip;
        private ImageView imgHot, imgPromotion, imgNew, imgRoomAvailable;
        private TextView tvOtherPromotion;
        private ImageView icon360;
        private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount;

        private ImageView imgIconPromotion1, imgIconPromotion2, imgIconPromotion3, imgIconPromotion4;

        private RelativeLayout boxVipHotel;
    }

    private void bindViewNormal(View convertView) {
        viewHolder = new ViewHolder();

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

        viewHolder.imgIconPromotion1 = convertView.findViewById(R.id.imgIconPromotion1);
        viewHolder.imgIconPromotion2 = convertView.findViewById(R.id.imgIconPromotion2);
        viewHolder.imgIconPromotion3 = convertView.findViewById(R.id.imgIconPromotion3);
        viewHolder.imgIconPromotion4 = convertView.findViewById(R.id.imgIconPromotion4);

        viewHolder.boxVipHotel = convertView.findViewById(R.id.boxVipHotel);

        convertView.setTag(viewHolder);

    }
}
