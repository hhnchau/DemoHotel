package com.appromobile.hotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.UserStampForm;

import java.util.List;

/**
 * Created by appro on 25/12/2017.
 */

public class AdapterStamp extends BaseAdapter {
    private Context myContext;
    private List<UserStampForm> list;

    public AdapterStamp(Context myContext, List<UserStampForm> list) {
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
        private TextView txtHotelName, txtValue;
        private ImageView imgStamp;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();

        if (inflater != null && rowView == null) {
            rowView = inflater.inflate(R.layout.item_stamp, null);
            //Init
            viewHolder.txtHotelName = rowView.findViewById(R.id.tvHotelName);
            viewHolder.txtValue = rowView.findViewById(R.id.tvStampValue);
            viewHolder.imgStamp = rowView.findViewById(R.id.imgStamp);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        UserStampForm userStampForm = list.get(i);

        viewHolder.txtHotelName.setText(userStampForm.getHotelName());

        int numActive = userStampForm.getNumStampActive();
        int numRedeem = userStampForm.getNumToRedeem();
        viewHolder.txtValue.setText(String.valueOf(numActive) + "/" + String.valueOf(numRedeem));

        if (numActive >= numRedeem) {
            viewHolder.imgStamp.setImageResource(R.drawable.icon_stamp_active);
        } else {
            viewHolder.imgStamp.setImageResource(R.drawable.icon_stamp_inactive);
        }

        //Lock Stamp
        if (userStampForm.getNumStampLocked() > 0){
            viewHolder.imgStamp.setImageResource(R.drawable.icon_stamp_inactive);
        }

        return rowView;
    }
}
