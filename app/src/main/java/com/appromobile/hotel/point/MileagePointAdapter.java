package com.appromobile.hotel.point;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.MileageHistoryForm;
import com.appromobile.hotel.utils.Utils;

import java.util.List;

/**
 * Created by appro on 26/03/2018.
 */

public class MileagePointAdapter extends BaseAdapter {
    private Context myContext;
    private List<MileageHistoryForm> list;

    public MileagePointAdapter(Context myContext, List<MileageHistoryForm> list) {
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
        private TextView txtDate, txtDesc, txtHotelName, txtValue;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();

        if (inflater != null && rowView == null) {
            rowView = inflater.inflate(R.layout.item_mileage_point, null);
            //Init
            viewHolder.txtDate = rowView.findViewById(R.id.tvDate);
            viewHolder.txtDesc = rowView.findViewById(R.id.tvDesc);
            viewHolder.txtHotelName = rowView.findViewById(R.id.tvHotelName);
            viewHolder.txtValue = rowView.findViewById(R.id.tvValue);


            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        MileageHistoryForm mileageHistoryForm = list.get(i);

        if (mileageHistoryForm != null) {
            int type = mileageHistoryForm.getType();
            if (type == 1) {
                viewHolder.txtDesc.setText(myContext.getString(R.string.txt_6_13_earned));
                viewHolder.txtDesc.setTextColor(myContext.getResources().getColor(R.color.rg));

                viewHolder.txtValue.setText("+" + String.valueOf(mileageHistoryForm.getNumOfPoint()));
                viewHolder.txtValue.setTextColor(myContext.getResources().getColor(R.color.rg));
            } else if (type == 2) {
                viewHolder.txtDesc.setText(mileageHistoryForm.getMileageRewardName());
                viewHolder.txtDesc.setTextColor(myContext.getResources().getColor(R.color.bk));

                viewHolder.txtValue.setText("+" + String.valueOf(mileageHistoryForm.getNumOfPoint()));
                viewHolder.txtValue.setTextColor(myContext.getResources().getColor(R.color.bk));
            } else if (type == 3) {
                viewHolder.txtDesc.setText(myContext.getString(R.string.txt_6_13_expired));
                viewHolder.txtDesc.setTextColor(myContext.getResources().getColor(R.color.red));

                viewHolder.txtValue.setText("-" + String.valueOf(mileageHistoryForm.getNumOfPoint()));
                viewHolder.txtValue.setTextColor(myContext.getResources().getColor(R.color.red));
            }

            viewHolder.txtDate.setText(Utils.formatDateddmmyyyy(myContext, mileageHistoryForm.getLastUpdate()));
            viewHolder.txtHotelName.setText(mileageHistoryForm.getHotelName());


        }

        return rowView;
    }
}
