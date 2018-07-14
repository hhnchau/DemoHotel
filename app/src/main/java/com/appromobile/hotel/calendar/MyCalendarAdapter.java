package com.appromobile.hotel.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.appromobile.hotel.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by appro on 28/03/2018.
 */

public class MyCalendarAdapter extends PagerAdapter {
    private Context context;
    private String from, select, to;
    private CallbackCalendar callbackCalendar;

    public MyCalendarAdapter(Context context, String from, String select, String to, CallbackCalendar callbackCalendar) {
        this.context = context;
        this.from = from;
        this.select = select;
        this.to = to;
        this.callbackCalendar = callbackCalendar;
    }

    @Override
    public int getCount() {
        return HelperCalendar.getTotalMonths(context, from, to);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.calendar_view, container, false);
        TextView tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        GridView calendarView = view.findViewById(R.id.calendar);


        Calendar calendarSelected = Calendar.getInstance();
        try {
            Date date = HelperCalendar.ddmmyyyy().parse(from);
            calendarSelected.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar currentMonth = Calendar.getInstance();
        currentMonth.setTimeInMillis(calendarSelected.getTimeInMillis());
        currentMonth.add(Calendar.MONTH, position);

        //Set Title
        tvCurrentMonth.setText(HelperCalendar.mmyyyy().format(currentMonth.getTime()));

        int month, year;
        month = currentMonth.get(Calendar.MONTH) + 1;
        year = currentMonth.get(Calendar.YEAR);
        CellCalendarAdapter adapter = new CellCalendarAdapter(container.getContext(), month, year, select, new CallbackCalendar() {
            @Override
            public void onDate(String date) {
                callbackCalendar.onDate(date);
            }
        });
        calendarView.setAdapter(adapter);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
