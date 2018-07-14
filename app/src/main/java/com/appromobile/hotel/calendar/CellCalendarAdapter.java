package com.appromobile.hotel.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by appro on 27/03/2018.
 */

public class CellCalendarAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private CallbackCalendar callbackCalendar;
    private String select;

    public CellCalendarAdapter(Context context, int month, int year, String select, CallbackCalendar callbackCalendar) {
        this.context = context;
        this.select = select;
        this.callbackCalendar = callbackCalendar;
        this.list = HelperCalendar.createCellList(month, year);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.screen_gridcell, null);
                viewHolder.tvDay = convertView.findViewById(R.id.tvDay);
                viewHolder.imgCellBg = convertView.findViewById(R.id.imgBg);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String[] item = list.get(position).split("-");

        Date parseDate = Calendar.getInstance().getTime();
        try {
            parseDate = HelperCalendar.ddmmmyyyy().parse(item[0] + "-" + item[2] + "-" + item[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.tvDay.setText(item[0]);

        if (item[1].equals("GREY")) {
            viewHolder.tvDay.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tvDay.setVisibility(View.VISIBLE);

            //Current Calendar
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTimeInMillis(parseDate.getTime());
            currentCal.set(Calendar.HOUR_OF_DAY, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.SECOND, 1);

            //Select Calendar
            Calendar selectCal = Calendar.getInstance();
            try {
                Date date = HelperCalendar.ddmmyyyy().parse(select);
                selectCal.setTimeInMillis(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (HelperCalendar.yyyymmdd().format(currentCal.getTime()).equals(HelperCalendar.yyyymmdd().format(selectCal.getTime()))) {
                viewHolder.tvDay.setTextColor(context.getResources().getColor(R.color.wh));
                viewHolder.imgCellBg.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvDay.setTextColor(context.getResources().getColor(R.color.bk));
                viewHolder.imgCellBg.setVisibility(View.GONE);
            }

        }

        //OnClick
        final Date finalParseDate = parseDate;
        viewHolder.tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalParseDate != null)
                    callbackCalendar.onDate(HelperCalendar.ddmmyyyy().format(finalParseDate));
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView tvDay;
        private ImageView imgCellBg;
    }
}
