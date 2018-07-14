package com.appromobile.hotel.utils;


import android.os.Handler;


/**
 * Created by thanh on 12/6/2017.
 */


public class TimerUtils {
    public static int TYPE_HOME_FRAGMENT = 1;

    public interface OnTimeListener {
        void setOnTimeListener(int type);
    }

    private OnTimeListener listener;

    private android.os.Handler handler;
    private int type;
    private Runnable runnable;
    private int interval;

    public TimerUtils(int interval, int type, OnTimeListener listener) {
        handler = new Handler();
        this.type = type;
        this.listener = listener;
        this.interval = interval;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (TimerUtils.this.listener != null) {
                    TimerUtils.this.listener.setOnTimeListener(TimerUtils.this.type);
                    startTimer();
                }
            }
        };
    }

    public void startTimer() {
        handler.postDelayed(runnable, interval);
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);
        listener = null;
    }

}
