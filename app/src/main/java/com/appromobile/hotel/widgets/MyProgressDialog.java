package com.appromobile.hotel.widgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.appromobile.hotel.R;



/**
 * Created by xuan on 01/02/2015.
 */
public class MyProgressDialog extends ProgressDialog {
    Context context;

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

    }
}
