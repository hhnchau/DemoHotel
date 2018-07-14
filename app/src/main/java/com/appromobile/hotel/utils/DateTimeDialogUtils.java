package com.appromobile.hotel.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appromobile.hotel.R;
import com.appromobile.hotel.widgets.CustomTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by xuan on 8/5/2016.
 */
public class DateTimeDialogUtils {
    public static void showTimePickerDialogEdit(Context context, final TextView tvTime, String timeLimit) {
        String time = tvTime.getText().toString();
        String[] timeArray = time.split(":");
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        newCalendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));

        String[] timeLimitArray = timeLimit.split(":");
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeLimitArray[0]));
        currentTime.set(Calendar.MINUTE, Integer.parseInt(timeLimitArray[1]));

        CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));

        customTimePickerDialog.show();
    }

    public static void showDatePickerDialog(Context context, final TextView tvTime, Calendar minYear, Calendar maxYear) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(context.getString(R.string.date_format_view), Locale.ENGLISH);

        String[] d;

        if (tvTime.getText().toString().equals("")) {
            d = context.getString(R.string.default_birthday).split("/");
        } else {
            d = tvTime.getText().toString().split("/");
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvTime.setText(dateFormatter.format(newDate.getTime()));
            }


        }, Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0]));

        datePickerDialog.getDatePicker().setMinDate(minYear.getTimeInMillis());

        datePickerDialog.getDatePicker().setMaxDate(maxYear.getTimeInMillis());

        datePickerDialog.show();
    }
}
