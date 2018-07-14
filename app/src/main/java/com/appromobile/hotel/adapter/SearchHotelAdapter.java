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
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.List;

/**
 * Created by xuan on 8/29/2016.
 */
public class SearchHotelAdapter extends BaseAdapter {
    private Context context;

    public List<HotelForm> getHotelForms() {
        return hotelForms;
    }

    public SearchHotelAdapter setHotelForms(List<HotelForm> hotelForms) {
        this.hotelForms = hotelForms;
        return this;
    }

    private List<HotelForm> hotelForms;

    public SearchHotelAdapter(Context context, List<HotelForm> hotelForms) {
        this.context = context;
        this.hotelForms = hotelForms;
    }

    @Override
    public int getCount() {
        if (this.hotelForms != null) {
            return this.hotelForms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return this.hotelForms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_recycler_view_holder, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.img = convertView.findViewById(R.id.img);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.address = convertView.findViewById(R.id.address);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (hotelForms != null && position < hotelForms.size()) {
            viewHolder.name.setText(this.hotelForms.get(position).getName());
            viewHolder.address.setText(this.hotelForms.get(position).getAddress());

            if (this.hotelForms.get(position).checkFlashSale()) {
                viewHolder.img.setImageResource(R.drawable.ic_promotion_red);
//        }else if (this.hotelForms.get(position).isPromotion()){
//            viewHolder.img.setImageResource(R.drawable.ic_promotion_green);
            } else {
                viewHolder.img.setImageResource(R.drawable.ic_promotion_grey);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView img;
        private TextView name, address;
    }
}
