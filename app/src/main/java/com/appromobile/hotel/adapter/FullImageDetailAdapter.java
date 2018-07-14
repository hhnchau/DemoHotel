package com.appromobile.hotel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.picture.PictureGlide;

import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 */
public class FullImageDetailAdapter extends PagerAdapter {
    private Context context;
    private List<HotelImageForm> data;
    private List<RoomTypeForm> listRoomTypeForm;

    public FullImageDetailAdapter(Context context, List<HotelImageForm> data, List<RoomTypeForm> listRoomTypeForm) {
        this.context = context;
        this.data = data;
        this.listRoomTypeForm = listRoomTypeForm;
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
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_full_image_item, container, false);
        final ImageView imgItem = view.findViewById(R.id.imgItem);
        TextView imgLocked = view.findViewById(R.id.roomtype_locked);

        HotelImageForm hotelImageForm = data.get(position);

        //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?imageKey?hotelImageSn=" + hotelImageForm.getSn() + "&fileName=" + hotelImageForm.getCustomizeName();
        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + hotelImageForm.getImageKey();

        PictureGlide.getInstance().show(
                url,
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels,
                R.drawable.loading_big,
                imgItem
        );

        /*
        * Check Room type Locked.
        */
        int sn = hotelImageForm.getRoomTypeSn();
        for (int i = 0; i < listRoomTypeForm.size(); i++) {
            if (sn == listRoomTypeForm.get(i).getSn()) {

                /*
            * Check Room type Locked
            */
                if (listRoomTypeForm.get(i).getStatus() == ParamConstants.LOCK_TODAY) {
                    imgLocked.setVisibility(View.VISIBLE);
                } else {
                    imgLocked.setVisibility(View.GONE);
                }

            }
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    public void updateData(List<HotelImageForm> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
