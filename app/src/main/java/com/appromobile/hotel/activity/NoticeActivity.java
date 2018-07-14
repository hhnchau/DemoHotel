package com.appromobile.hotel.activity;

import android.os.Bundle;

import com.appromobile.hotel.R;

/**
 * Created by xuan on 7/12/2016.
 */
public class NoticeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticie_activity);
        setScreenName();
    }

    @Override
    public void setScreenName() {
        this.screenName="notice";
    }
}
