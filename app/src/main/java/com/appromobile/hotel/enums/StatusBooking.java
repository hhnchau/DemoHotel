package com.appromobile.hotel.enums;

import android.content.Context;

import com.appromobile.hotel.R;

/**
 * Created by appro on 14/03/2017.
 */
public class StatusBooking {
    //1: Booked, 2: Check-in. 3: Canceled

    public static String getStatusBooking(Context context, int status) {
        String result = "";
        switch (status) {
            case 1:
                result = context.getString(R.string.txt_6_3_1_booking_status_booked);
                break;
            case 2:
                result = context.getString(R.string.txt_6_3_1_booking_checked_in);
                break;
            case 3:
                result = context.getString(R.string.txt_6_3_1_booking_status_cancel);
                break;
            case 4:
                result = "";
                break;
        }
        return result;
    }
}
