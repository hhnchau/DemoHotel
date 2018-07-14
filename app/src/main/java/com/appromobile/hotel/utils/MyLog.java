package com.appromobile.hotel.utils;

import android.util.Log;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.model.request.WriteLogDto;
import com.appromobile.hotel.model.view.RestResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by appro on 04/05/2017.
 */
public class MyLog {
    private static final String TAG = "running";
    private static boolean log = false; //Change

    public static void writeLog(String s) {
        if (log) {
            Log.d(TAG, s);
            //writeLogFile(s);
        }
    }

    private static void writeLogFile(String content) {
        String idDevice = HotelApplication.DEVICE_ID;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());
        content = currentDateandTime + "---" + idDevice +":----->" + content;
        WriteLogDto writeLogDto = new WriteLogDto();
        writeLogDto.setContent(content);
        HotelApplication.serviceApi.writeLogFile(writeLogDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }
}
