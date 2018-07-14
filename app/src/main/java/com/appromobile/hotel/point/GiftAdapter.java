package com.appromobile.hotel.point;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.MileageHistoryForm;

import java.util.List;

/**
 * Created by appro on 27/03/2018.
 */

public class GiftAdapter extends BaseAdapter {
    private Context myContext;
    private List<MileageHistoryForm> list;

    public GiftAdapter(Context myContext, List<MileageHistoryForm> list) {
        this.myContext = myContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        private TextView tvGiftName, tvPoint;
        private RelativeLayout boxChoose;
        private ImageView imgChoose;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();

        if (inflater != null && rowView == null) {
            rowView = inflater.inflate(R.layout.item_mileage_gift, null);
            //Init
            viewHolder.tvGiftName = rowView.findViewById(R.id.tvGiftName);
            viewHolder.tvPoint = rowView.findViewById(R.id.tvPoint);
            viewHolder.boxChoose = rowView.findViewById(R.id.boxChoose);
            viewHolder.imgChoose = rowView.findViewById(R.id.imgChoose);


            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        MileageHistoryForm mileageHistoryForm = list.get(i);

        if (mileageHistoryForm != null) {
            viewHolder.tvGiftName.setText(mileageHistoryForm.getExpiredDate());
            viewHolder.tvPoint.setText(mileageHistoryForm.getHotelName());
            //viewHolder.boxChoose.setText(mileageHistoryForm.getHotelName());
            //viewHolder.imgChoose.setText(String.valueOf(mileageHistoryForm.getValueAmount()));
        }

        return rowView;
    }
}
