package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.HotelDetailActivity;
import com.appromobile.hotel.activity.IntentTemp;
import com.appromobile.hotel.activity.InviteFriendActivity;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.activity.NoticeAppDetailActivity;
import com.appromobile.hotel.activity.PromotionDetailActivity;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.enums.InviteFriendType;
import com.appromobile.hotel.model.view.BannerForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.picture.PicturePicasso;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;


import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 */
public class AdvertAdapter extends PagerAdapter {
    private Context context;
    private int[] sn;
    private List<BannerForm> data;

    public AdvertAdapter(final Context context, List<BannerForm> data) {

        this.context = context;
        this.data = data;
        sn = new int[data.size()];
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(context).clearDiskCache();
//            }
//        });
//        thread.start();
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
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        ViewPager viewPager = (ViewPager) container;
        View view = LayoutInflater.from(context).inflate(R.layout.banner_image_item, container, false);
        final ImageView imgItem = view.findViewById(R.id.imgItem);
        BannerForm bannerForm = data.get(position);
        if (bannerForm != null) {
            sn[position] = bannerForm.getSn();
            //String url = UrlParams.MAIN_URL + "hotelapi/banner/download/downloadBannerImage?bannerSn=" + String.valueOf(sn[position]);
            String url = UrlParams.MAIN_URL + "/hotelapi/banner/download/bannerImage/" + bannerForm.getImageKey();

            PictureGlide.getInstance().show(url, imgItem);
            //PicturePicasso.getInstance().show(imgItem, url);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int action = data.get(position).getAction();
                    if (action == BannerForm.ACTION_PROMOTION) {
                        if (data.get(position).getTargetType() == 1) { //Promotion type 1
                            Intent intent = new Intent(context, PromotionDetailActivity.class);
                            intent.putExtra("promotionSn", data.get(position).getTargetSn());
                            intent.putExtra("actionPopup", data.get(position).getAction());
                            ((Activity) context).startActivityForResult(intent, ParamConstants.EVENT_PROMOTION_REQUEST);
                            ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                        }

                    } else if (action == BannerForm.ACTION_EVENT) {

                        Intent intent = new Intent(context, PromotionDetailActivity.class);
                        intent.putExtra("promotionSn", data.get(position).getTargetSn());
                        intent.putExtra("actionPopup", data.get(position).getAction());
                        ((Activity) context).startActivityForResult(intent, ParamConstants.EVENT_PROMOTION_REQUEST);
                        ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);

                    } else if (action == BannerForm.ACTION_INVITE) {
                        if (!PreferenceUtils.getToken(context).equals("")) {
                            Intent intent = new Intent(context, InviteFriendActivity.class);
                            intent.putExtra("InviteFriendType", InviteFriendType.BANNER.ordinal());
                            intent.putExtra("promotionDiscount", data.get(position).getDiscount());
                            context.startActivity(intent);
                        } else {
                            ((MainActivity) context).gotoLoginScreen(ParamConstants.INVITE_FRIEND_BANNER_REQUEST);
                        }
                    } else if (action == BannerForm.ACTION_HOTEL) {
                        gotoHotelDetailDeepLink(data.get(position).getTargetSn());
                    } else if (action == BannerForm.ACTION_NOTICE) {
                        gotoNoticeDetailFromPoup(data.get(position).getTargetSn());
                    } else if (action == BannerForm.ACTION_LINK) {

                    } else if (action == BannerForm.ACTION_DISTRICT) {

                        if (data.get(position).getTargetInfo() != null) {
                            Intent intent = new Intent(context, IntentTemp.class);
                            intent.setAction(ParamConstants.ACTION_CHANGE_AREA);
                            intent.putExtra(ParamConstants.ACTION_CHANGE_AREA, data.get(position).getTargetInfo());
                            ((Activity) context).startActivityForResult(intent, ParamConstants.REQUEST_CHANGE_AREA);
                        }
                    }
                }
            });
        }
        viewPager.addView(view);
        return view;


    }

    private void gotoNoticeDetailFromPoup(int targetSn) {
        Intent resultIntent = new Intent(context, NoticeAppDetailActivity.class);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        NotificationData notice = new NotificationData();
        notice.setSn(targetSn);
        resultIntent.putExtra("NotificationData", notice);
        context.startActivity(resultIntent);
        ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoHotelDetailDeepLink(int targetSn) {
        Intent intent = new Intent(context, HotelDetailActivity.class);
        intent.putExtra("sn", targetSn);
        intent.putExtra("RoomAvailable", true);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
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

    public void updateData(List<BannerForm> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
