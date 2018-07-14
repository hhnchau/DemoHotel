package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.clock.NumberClock;

import java.util.List;


/**
 * Created by appro on 09/03/2018.
 */

public class AdapterClock extends BaseAdapter {
    private Context context;
    private List<NumberClock> numberClockList;
    private int type;

    public interface OnItemClickListener {
        void onItemListener(int type, int p);
    }

    private OnItemClickListener onItemClickListener;

    public AdapterClock(Context context, List<NumberClock> numberClockList, int type, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.numberClockList = numberClockList;
        this.type = type;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return numberClockList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            if (type == NumberClock.FROM) {
                convertView = inflater.inflate(R.layout.item_clock_from, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_clock_to, parent, false);
            }

            viewHolder = new ViewHolder();

            viewHolder.tvNumber = convertView.findViewById(R.id.tvNumber);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Update View
        NumberClock numberClock = numberClockList.get(position);
        if (numberClock.isEnable()) {
            viewHolder.tvNumber.setEnabled(true);
            viewHolder.tvNumber.setTextColor(context.getResources().getColor(R.color.wh));
        } else {
            viewHolder.tvNumber.setEnabled(false);
            viewHolder.tvNumber.setTextColor(context.getResources().getColor(R.color.bk_15p));
        }
        viewHolder.tvNumber.setText(numberClock.getNumber());

        viewHolder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemListener(type, position);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        TextView tvNumber;
    }

}
