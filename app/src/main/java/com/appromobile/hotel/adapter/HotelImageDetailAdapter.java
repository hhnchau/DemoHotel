package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.HotelPhotoDetailActivity;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.picture.PictureGlide;

import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 * Kingpes
 */
public class HotelImageDetailAdapter extends PagerAdapter {
    private Context context;
    private List<HotelImageForm> data;
    private HotelDetailForm hotelDetailForm;


    public HotelImageDetailAdapter(Context context, List<HotelImageForm> data, HotelDetailForm hotelDetailForm) {
        this.context = context;
        this.data = data;
        this.hotelDetailForm = hotelDetailForm;
        PictureGlide.getInstance().clearCache(context);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
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
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_image_item, container, false);
        final ImageView imgViewNormal =  view.findViewById(R.id.imgItem);

        //final String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + data.get(position).getSn() + "&fileName=" + data.get(position).getCustomizeName();
        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + data.get(position).getImageKey();


        /*
        / Set Image Normal
        */
        PictureGlide.getInstance().show(
                url,
                context.getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                context.getResources().getDimensionPixelSize(R.dimen.hotel_detail_image_height),
                R.drawable.loading_big,
                imgViewNormal
        );


        imgViewNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, HotelPhotoDetailActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("SelectedRoomType", 0);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        try {
            container.addView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

}
