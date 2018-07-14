package com.appromobile.hotel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appromobile.hotel.R;

/**
 * Created by appro on 29/05/2017.
 */
public class RequestNetwork {
    private static RequestNetwork Instance = null;
    private static boolean isShowDialog;

    public static RequestNetwork getInstance() {
        if (Instance == null) {
            Instance = new RequestNetwork();
        }
        return Instance;
    }

    public static void show(final Context context) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirm_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextView tvMessage =  dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(context.getString(R.string.app_need_to_connect_internet_please_turn_on_the_internet));
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);
            TextView btnOK =  dialog.findViewById(R.id.btnOK);

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });
        }
    }

    public static void showDialogOneButton(final Context context) {
        if (!isShowDialog) {
            if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
                final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirm_dialog);
                Window window = dialog.getWindow();
                if (window!= null) {
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    window.setAttributes(wlp);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.show();
                }
                isShowDialog = true;
                TextView tvMessage = dialog.findViewById(R.id.tvMessage);
                tvMessage.setText(context.getString(R.string.app_need_to_connect_internet_please_turn_on_the_internet));
                TextView btnCancel =  dialog.findViewById(R.id.btnCancel);
                btnCancel.setVisibility(View.GONE);
                TextView btnOK =  dialog.findViewById(R.id.btnOK);

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        isShowDialog = false;
                        context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }
}
