package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.PromotionForm;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.Utils;

import java.util.List;

/**
 * Created by xuan on 9/5/2016.
 */
public class PromotionAdapter extends BaseAdapter {
    private List<PromotionForm> data;
    private Context context;

    public PromotionAdapter(final Context context, List<PromotionForm> data) {
        this.context = context;
        this.data = data;
        PictureGlide.getInstance().clearCache(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.promotion_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.img =convertView.findViewById(R.id.img);
            viewHolder.frame = convertView.findViewById(R.id.frame_promotion);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PromotionForm noticeForm = data.get(position);
        try {
            String imageName = Utils.md5(noticeForm.getLastUpdate());
            int promotionImageSn = 0;
            String imageKey = "";
            try {
                if (noticeForm.getPromotionImageFormList() != null) {
                    for (int i = 0; i < noticeForm.getPromotionImageFormList().size(); i++) {
                        if (noticeForm.getPromotionImageFormList().get(i).getTypeDisplay() == 1) {
                            promotionImageSn = noticeForm.getPromotionImageFormList().get(i).getSn();
                            imageKey = noticeForm.getPromotionImageFormList().get(i).getImageKey();
                        }
                    }
                }
            } catch (Exception e) {
            }

            //String url = UrlParams.MAIN_URL + "/hotelapi/promotion/download/downloadPromotionImage?promotionImageSn=" + promotionImageSn + "&fileName=" + imageName;
            String url = UrlParams.MAIN_URL + "/hotelapi/promotion/download/promotionImage/" + imageKey;

            PictureGlide.getInstance().show(
                    url,
                    context.getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                    context.getResources().getDimensionPixelSize(R.dimen.promotion_item_height),
                    R.drawable.loading_big,
                    viewHolder.img
            );

            //Check Expired
            if (noticeForm.getStatus() == 2) {
                viewHolder.frame.setVisibility(View.VISIBLE);
            } else {
                viewHolder.frame.setVisibility(View.GONE);
            }

        } catch (Exception e) {
        }
        return convertView;
    }

    public void updateData(List<PromotionForm> appNoticeForms) {
        this.data = appNoticeForms;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView img;
        View frame;
    }
}
