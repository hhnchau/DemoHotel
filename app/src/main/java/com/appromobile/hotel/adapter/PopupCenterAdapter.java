package com.appromobile.hotel.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.picture.PicturePicasso;

import java.util.List;

/**
 * Created by appro on 22/03/2018.
 */

public class PopupCenterAdapter extends PagerAdapter {
    private List<String> mList;

    public PopupCenterAdapter(List<String> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpager_popup_center, container, false);

        ImageView img = view.findViewById(R.id.imgBanner);

        //String url = UrlParams.MAIN_URL + "hotelapi/popup/download/downloadPopupImage?popupSn=" + mList.get(position);
        String url = UrlParams.MAIN_URL + "/hotelapi/popup/download/popupImage/" + mList.get(position);

        PictureGlide.getInstance().show(url, img);
        //PicturePicasso.getInstance().show(img, url);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
