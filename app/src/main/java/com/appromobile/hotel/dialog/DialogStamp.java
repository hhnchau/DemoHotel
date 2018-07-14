package com.appromobile.hotel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.callback.CallBackListenerPopupCenter;
import com.appromobile.hotel.model.view.PopupForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;

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

                tvStampValue.setText(Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + context.getString(R.string.vnd));

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(context.getString(R.string.txt_6_12_stamp_policy_number)).append(": ").append(userStampForm.getNumToRedeem()).append("\n");
                stringBuilder.append(context.getString(R.string.txt_6_12_stamp_value)).append(": ").append(userStampForm.getRedeemValue()).append("\n");
                stringBuilder.append(context.getString(R.string.txt_6_12_stamp_policy_condision)).append(": ");
                stringBuilder.append(userStampForm.isRedeemHourly() ? context.getString(R.string.txt_2_flashsale_hourly_price) + ", " : "")
                        .append(userStampForm.isRedeemDaily() ? context.getString(R.string.txt_2_flashsale_overnight_price) + ", " : "")
                        .append(userStampForm.isRedeemOvernight() ? context.getString(R.string.txt_2_flashsale_daily_price) + ", " : "");
                tvTermsOfUse.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

            }

        }

    }
}
