package com.appromobile.hotel.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanquach on 2/13/15.
 */
public class MyTimePicker extends TimePicker {
    private static final int TIME_PICKER_INTERVAL = 10;

    public MyTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyTimePicker(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public Integer getCurrentMinute() {
        return super.getCurrentMinute()*TIME_PICKER_INTERVAL;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
//            TimePicker timePicker = (TimePicker) findViewById(timePickerField
//                    .getInt(null));

            Field field = classForid.getField("minute");
            Field hours = classForid.getField("hour");

            NumberPicker mHourSpinner = (NumberPicker) findViewById(hours.getInt(null));
            mHourSpinner.setMinValue(9);

            NumberPicker mMinuteSpinner = (NumberPicker) findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL)
                displayedValues.add(String.format("%02d", i));
            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
