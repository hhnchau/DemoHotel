package com.appromobile.hotel.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.CalendarAdapter;
import com.appromobile.hotel.adapter.CellDayClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by appro on 20/03/2018.
 */

public class MyCalendar {
    private static MyCalendar Instance = null;

    public static MyCalendar getInstance() {
        if (Instance == null) {
            Instance = new MyCalendar();
        }
        return Instance;
    }

    public void show(Context context, String dateFrom, String select, String dateTo, final CallbackCalendar callbackCalendar) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.calendar_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                dialog.findViewById(R.id.linear_calendar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                ViewPager vpCalendar = dialog.findViewById(R.id.vpCalendar);
                MyCalendarAdapter calendarAdapter = new MyCalendarAdapter(context, dateFrom, select, dateTo, new CallbackCalendar() {
                    @Override
                    public void onDate(String date) {
                        callbackCalendar.onDate(date);
                        dialog.dismiss();
                    }
                });
                vpCalendar.setAdapter(calendarAdapter);
                vpCalendar.setCurrentItem(HelperCalendar.getTotalMonths(context, dateFrom, select) - 1);

            }
        }
    }

}
