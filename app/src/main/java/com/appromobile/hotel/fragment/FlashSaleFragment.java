package com.appromobile.hotel.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.HotelDetailActivity;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.enums.RoomAvailableType;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.Utils;


/**
 * Created by appro on 22/08/2017.
 */
public class FlashSaleFragment extends Fragment implements View.OnClickListener {
    private TextView tvNameVip, tvReview, tvDistanceVip;
    private ImageView imgHotelVip;
    private ImageView imgHot, imgPromotion, imgNew, imgRoomAvailable;
    private TextView tvOtherPromotion;
    private ImageView icon360;
    private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount;

    private ImageView imgIconPromotion1, imgIconPromotion2, imgIconPromotion3, imgIconPromotion4;

    private RelativeLayout boxVipHotel;

    private LinearLayout boxHourly;

    private RelativeLayout iconStamp;
    private TextView tvNumStamp;

    private HotelForm hotelForm;
    private View view;

    public static FlashSaleFragment newInstance(HotelForm hotelForm) {
        FlashSaleFragment fragment = new FlashSaleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("HotelForm", hotelForm);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hotelForm = getArguments().getParcelable("HotelForm");
        }
        //Clear Cache Glide
        PictureGlide.getInstance().clearCache(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.flash_sale_item, container, false);
        init();
        setView();
        return view;
    }

    private void setView() {
        setInfoHotel(hotelForm);
        //setImageHotel(hotelForm.getHomeImageSn(), hotelForm.getHomeImageName());
        setImageHotel(hotelForm.getImageKey(), hotelForm.getHomeImageName());

        /*
        /Set Icon 360
        */
        if (hotelForm.getCountExifImage() > 0) {
            icon360.setVisibility(View.VISIBLE);
        } else {
            icon360.setVisibility(View.GONE);
        }

    }

    private void init() {
        tvNameVip = view.findViewById(R.id.tvNameVip);
        tvReview = view.findViewById(R.id.txtReview);
        tvDistanceVip = view.findViewById(R.id.tvDistanceVip);

        imgHotelVip = view.findViewById(R.id.imgHotelVip);

        imgHot = view.findViewById(R.id.imgHot);
        imgPromotion = view.findViewById(R.id.imgPromotion);
        imgNew = view.findViewById(R.id.imgNew);
        imgRoomAvailable = view.findViewById(R.id.imgRoomAvailable);

        tvOtherPromotion = view.findViewById(R.id.tvOtherPromotion);

        icon360 = view.findViewById(R.id.item_hotel_icon_360);

        tvPriceStatus = view.findViewById(R.id.tvPriceStatus);
        tvPriceHourlyNormal = view.findViewById(R.id.tvPriceHourlyNormal);
        tvPriceHourlyDiscount = view.findViewById(R.id.tvPriceHourlyDiscount);
        tvPriceOvernightNormal = view.findViewById(R.id.tvPriceOvernightNormal);
        tvPriceOvernightDiscount = view.findViewById(R.id.tvPriceOvernightDiscount);

        imgIconPromotion1 = view.findViewById(R.id.imgIconPromotion1);
        imgIconPromotion2 = view.findViewById(R.id.imgIconPromotion2);
        imgIconPromotion3 = view.findViewById(R.id.imgIconPromotion3);
        imgIconPromotion4 = view.findViewById(R.id.imgIconPromotion4);

        boxVipHotel = view.findViewById(R.id.boxVipHotel);
        boxVipHotel.setOnClickListener(this);

        boxHourly = view.findViewById(R.id.boxHourly);
        boxHourly.setVisibility(View.GONE);

        tvNumStamp = view.findViewById(R.id.tvNumStamp);
        iconStamp = view.findViewById(R.id.iconStamp);
    }

    private void setInfoHotel(HotelForm hotelForm) {
        imgNew.setVisibility(View.GONE);
        imgHot.setVisibility(View.GONE);
        imgPromotion.setVisibility(View.GONE);

        //Set Icon Promotion
        if (hotelForm.getNewHotel() == 1) {
            imgNew.setVisibility(View.VISIBLE);
        }
        if (hotelForm.getHasPromotion() == 1) {
            imgPromotion.setVisibility(View.VISIBLE);
        }
        if (hotelForm.getHotHotel() == 1) {
            imgHot.setVisibility(View.VISIBLE);
        }
        if (hotelForm.getRoomAvailable() == RoomAvailableType.Available.getType()) {
            imgRoomAvailable.setImageResource(R.drawable.room);
        } else {
            imgRoomAvailable.setImageResource(R.drawable.no_room);
        }

        //Set Label Flash Sale
        imgIconPromotion1.setVisibility(View.VISIBLE);
        imgIconPromotion1.setImageResource(R.drawable.icon_sale);


        //Set Stamp
        if (hotelForm.getNumToRedeem() > 0) {
            iconStamp.setVisibility(View.VISIBLE);
            tvNumStamp.setText(hotelForm.getActiveStamp() + "/" + hotelForm.getNumToRedeem());
        } else {
            iconStamp.setVisibility(View.GONE);
        }

        //Set Hotel Name
        tvNameVip.setText(hotelForm.getName());

        //Set Distance
        float distance = calculateDistance(hotelForm);
        tvDistanceVip.setText(Utils.meterToKm(distance));

        //Set Review
        double rate = hotelForm.getAverageMark();
        if (rate <= 0) {
            tvReview.setVisibility(View.GONE);
        } else {
            tvReview.setText(String.valueOf(rate));
        }

        RoomTypeForm roomTypeForm = hotelForm.getFlashSaleRoomTypeForm();
        if (roomTypeForm != null) {
            String s = "";
            int rooms = roomTypeForm.getAvailableRoom();
            int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
            int priceOvernightDiscount = roomTypeForm.getPriceOvernight();
            if (superSale > 0) {
                priceOvernightDiscount = priceOvernightDiscount - superSale;

                if (priceOvernightDiscount < 0)
                    priceOvernightDiscount = 0;

                if (rooms > 0) {
                    //if (rooms <= 5)
                    s = getString(R.string.txt_2_super_flashsale_room_left, String.valueOf(rooms));
                } else {
                    s = getString(R.string.txt_2_super_flashsale_sold_out);
                }
            } else { // normal
                if (rooms > 0) {
                    if (rooms <= 5) {
                        s = String.format(getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                    }
                } else {
                    s = getString(R.string.txt_2_flashsale_sold_out);
                }
            }
            //Set Price Status
            tvPriceStatus.setText(s);


            //Set Price Hourly Normal
            //tvPriceHourlyNormal.setText("100000");

            //Set Price Hourly Discount
            //tvPriceHourlyDiscount.setText("80000");

            //Set Price Overnight Normal
            tvPriceOvernightNormal.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
            //StrikeThrough
            tvPriceOvernightNormal.setPaintFlags(tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            //Set Price Overnight Discount
            tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

            //Feature92
            if (superSale > 0){
                TextView tvSupperSaleNormal = view.findViewById(R.id.tvSupperSaleNormal);
                tvSupperSaleNormal.setVisibility(View.VISIBLE);
                tvSupperSaleNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                tvSupperSaleNormal.setPaintFlags(tvSupperSaleNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                TextView tvSupperSaleDiscount = view.findViewById(R.id.tvSupperSaleDiscount);
                tvSupperSaleDiscount.setVisibility(View.VISIBLE);
                tvSupperSaleDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

                tvPriceOvernightDiscount.setVisibility(View.GONE);

            }

        }
    }

    private void setImageHotel(String homeImageSn, String homeImageName) {
        if (getActivity() != null && homeImageName != null) {
            //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + homeImageSn + "&fileName=" + homeImageName;
            String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + homeImageSn;

            PictureGlide.getInstance().show(
                    url,
                    getActivity().getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                    getActivity().getResources().getDimensionPixelSize(R.dimen.hotel_list_height),
                    R.drawable.loading_big,
                    imgHotelVip
            );
        }
    }

    private float calculateDistance(HotelForm hotel) {
        float distance = 0;
        if (hotel != null) {
            //get hotel location
            Location hotelLocation = new Location("gps");
            hotelLocation.setLatitude(hotel.getLatitude());
            hotelLocation.setLongitude(hotel.getLongitude());
            //get current location
            Location currentLocation = Utils.getLocationFromPref(getActivity());
            if (currentLocation != null) {
                //calculate distance
                distance = currentLocation.distanceTo(hotelLocation);
            }
        }
        return distance;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() != null) {
            if (v.getId() == R.id.boxVipHotel) {
                Intent intent = new Intent(getActivity(), HotelDetailActivity.class);
                intent.putExtra("sn", hotelForm.getSn());
                intent.putExtra("RoomAvailable", hotelForm.getRoomAvailable());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }

        }
    }
}