package com.appromobile.hotel.calendar;

import android.content.Context;
import android.widget.Toast;

import com.appromobile.hotel.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by appro on 27/03/2018.
 */

class HelperCalendar {
    private static final int DAY_OFFSET = 1;
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private static final int[] DAY_OF_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    static List<String> createCellList(int month, int year) {
        List<String> list = new ArrayList<>();


        int prevMonth;
        int currentMonth = month - 1;
        int nextMonth;

        int prevYear;
        int nextYear;

        int dayOfMonth = DAY_OF_MONTH[currentMonth];
        int dayOfPrevMonth;

        if (month == 12) {

            prevMonth = currentMonth - 1;
            dayOfPrevMonth = DAY_OF_MONTH[prevMonth];
            nextMonth = 0;
            prevYear = year;
            nextYear = year + 1;

        } else if (month == 1) {

            prevMonth = 11;
            prevYear = year - 1;
            nextYear = year;
            dayOfPrevMonth = DAY_OF_MONTH[prevMonth];
            nextMonth = 1;

        } else {

            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = year;
            prevYear = year;
            dayOfPrevMonth = DAY_OF_MONTH[prevMonth];

        }

        GregorianCalendar cal = new GregorianCalendar(year, currentMonth, 1);
        int space = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (cal.isLeapYear(cal.get(Calendar.YEAR))) {
            if (month == 2) {
                ++dayOfMonth;
            } else if (month == 3) {
                ++dayOfPrevMonth;
            }
        }

        // Trailing Month days
        for (int i = 0; i < space; i++) {
            list.add(String.valueOf((dayOfPrevMonth - space + DAY_OFFSET) + i) + "-GREY" + "-" + MONTHS[prevMonth] + "-" + prevYear);
        }

        // Current Month Days
        for (int i = 1; i <= dayOfMonth; i++) {
            list.add(String.valueOf(i) + "-WHITE" + "-" + MONTHS[currentMonth] + "-" + year);
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + MONTHS[nextMonth] + "-" + nextYear);
        }

        return list;
    }

    static SimpleDateFormat ddmmyyyy() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    }

    static SimpleDateFormat ddmmmyyyy() {
        return new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    }

    static SimpleDateFormat yyyymmdd() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    static SimpleDateFormat mmyyyy() {
        return new SimpleDateFormat("MM.yyyy", Locale.ENGLISH);
    }

    static int getTotalMonths(Context context, String from, String to) {
        if (from == null || to == null) {
            Toast.makeText(context, "Data null", Toast.LENGTH_SHORT).show();
            return 0;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat(context.getString(R.string.date_format_view), Locale.ENGLISH);
        try {
            Date _from = dateFormatter.parse(from);
            Date _to = dateFormatter.parse(to);

            if (_from.after(_to)) {
                Toast.makeText(context, "Data error", Toast.LENGTH_SHORT).show();
                return 0;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(_from);
            int monthFrom = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);
            cal.setTime(_to);
            int monthTo = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);

            return monthTo - monthFrom + 1;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
