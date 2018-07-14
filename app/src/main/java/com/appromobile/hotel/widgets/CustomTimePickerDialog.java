package com.appromobile.hotel.widgets;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by xuan on 9/23/2016.
 */

public class CustomTimePickerDialog extends TimePickerDialog {
    private int minHour = -1;
    private int minMinute = -1;

    private final static int TIME_PICKER_INTERVAL = 10;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;

    public CustomTimePickerDialog(Context context, OnTimeSetListener listener, int currentHour, int currentMinute, int minHour, int minMinute) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, currentHour,
                currentMinute / TIME_PICKER_INTERVAL, true);
        setMin(minHour, minMinute);
        mTimeSetListener = listener;
//        // Create a TextView programmatically.
        TextView tv = new TextView(context);
//
//        // Create a TextView programmatically
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setPadding(10, 10, 10, 10);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setText("Choose Time");

//        tv.setTextColor(Color.parseColor("#ff0000"));
//        tv.setBackgroundColor(Color.parseColor("#FFD2DAA7"));
//
//        // Set the newly created TextView as a custom tile of DatePickerDialog
        this.setCustomTitle(tv);
    }

    public void setMin(int hour, int minute) {
        minHour = hour;
        minMinute = minute;
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);

    }
//    private int currentHour = 0;
//    private int currentMinute = 0;
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//
        boolean validTime = true;
        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)){
            validTime = false;
        }

        if (validTime) {
//            currentHour = hourOfDay;
//            currentMinute = minute;
//            super.onTimeChanged(view, hourOfDay, minute);
            updateTime(hourOfDay, minute);
        }

//        updateTime(currentHour, currentMinute);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            mTimePicker =  findViewById(timePickerField.getInt(null));

            // minute
            Field field = classForid.getField("minute");
            NumberPicker minuteSpinner =  mTimePicker
                    .findViewById(field.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));


            Field fieldHour = classForid.getField("hour");
            NumberPicker hoursSpinner =  mTimePicker
                    .findViewById(fieldHour.getInt(null));
            hoursSpinner.setMinValue(minHour);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
