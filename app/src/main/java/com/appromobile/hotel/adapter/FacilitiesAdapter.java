package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.FacilityForm;
import com.appromobile.hotel.picture.PictureGlide;


import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 */
public class FacilitiesAdapter extends BaseAdapter {
    private final List<FacilityForm> data;
    private final Context context;

    public FacilitiesAdapter(Context context, List<FacilityForm> data) {
        this.context = context;
        this.data = data;
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
            convertView = inflater.inflate(R.layout.facility_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.imgItem = convertView.findViewById(R.id.imgItem);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final FacilityForm facilityForm = data.get(position);
        if (facilityForm != null) {
            viewHolder.tvName.setText(facilityForm.getName());

            //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadFacilityImage?facilitySn=" + facilityForm.getSn() + "&fileName=" + facilityForm.getCustomizePath();
            String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/facilityImage/" + facilityForm.getImageKey();

            PictureGlide.getInstance().show(url, context.getResources().getDimensionPixelSize(R.dimen.facility_width), context.getResources().getDimensionPixelSize(R.dimen.facility_height), R.drawable.loading_small, viewHolder.imgItem);

        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        ImageView imgItem;
    }
}
