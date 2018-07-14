package com.appromobile.hotel.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.TextView;

import com.appromobile.hotel.R;

import com.appromobile.hotel.model.view.UserStampForm;

import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;


/**
 * Created by appro on 26/12/2017.
 */

public class DialogStamp {
    private static DialogStamp Instance = null;

    public static DialogStamp getInstance() {
        if (Instance == null) {
            Instance = new DialogStamp();
        }
        return Instance;
    }

    public void show(final Context context, UserStampForm userStampForm) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_popup_stamp);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                dialog.findViewById(R.id.dialog_stamp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                TextView tvNumStamp = dialog.findViewById(R.id.tvNumStamp);
                TextView tvStampValue = dialog.findViewById(R.id.tvStampValue);
                TextView tvTermsOfUse = dialog.findViewById(R.id.tvTermsOfUse);

                tvNumStamp.setText(String.valueOf(userStampForm.getNumStampActive()) + "/" + String.valueOf(userStampForm.getNumToRedeem()));

                //Stamp V3
                //Check Stamp type
                String value;
                if (userStampForm.getRedeemType() == ParamConstants.DISCOUNT_PERCENT) {
                    //Set Value Redeem
                    value =  context.getString(R.string.discount) + " " + Utils.formatCurrency(userStampForm.getRedeemValue()) + context.getString(R.string.percent);
                    tvStampValue.setText(value);
                    value = context.getString(R.string.txt_6_12_stamp_value) + ": " + value + " - " +context.getString(R.string.max_discount) + " " + Utils.formatCurrency(userStampForm.getMaxRedeem()) + context.getString(R.string.vnd);
                } else {
                    //Set Value Redeem
                    value =  Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + context.getString(R.string.vnd);
                    tvStampValue.setText(value);
                    value = context.getString(R.string.txt_6_12_stamp_value) + ": " +value;
                }


                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("- ").append(context.getString(R.string.txt_6_12_stamp_policy_number)).append(": ").append(userStampForm.getNumToRedeem()).append("\n");
                stringBuilder.append("- ").append(value).append("\n");
                stringBuilder.append("- ").append(context.getString(R.string.txt_6_12_stamp_policy_condision)).append(": ");
                stringBuilder.append(userStampForm.isRedeemHourly() ? context.getString(R.string.txt_2_flashsale_hourly_price) + ", " : "")
                        .append(userStampForm.isRedeemDaily() ? context.getString(R.string.txt_2_flashsale_overnight_price) + ", " : "")
                        .append(userStampForm.isRedeemOvernight() ? context.getString(R.string.txt_2_flashsale_daily_price) + ", " : "");
                tvTermsOfUse.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

                tvTermsOfUse.append("\n" + "- " + context.getString(R.string.txt_6_12_policy_finish_stamp));

                tvTermsOfUse.setMovementMethod(new ScrollingMovementMethod());

            }

        }

    }
}
