package com.appromobile.hotel.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.callback.CallbackApiFail;
import com.appromobile.hotel.widgets.MyProgressDialog;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;


/**
 * Created by xuan on 31/01/2015.
 */
public class DialogUtils {
    private static ProgressDialog progress;

    public static void showLoadingProgress(Context context, boolean isCancel) {
        try {
            if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
                if (progress == null) {
                    progress = new MyProgressDialog(context, R.style.dialog_full_transparent_background);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setProgress(50);
                    progress.setIndeterminate(true);
                    progress.setCancelable(isCancel);
                }
                if (progress != null) {
                    if (!progress.isShowing()) {
                        progress.show();
                    }
                }
            }
        } catch (Exception e) {
            MyLog.writeLog("showLoadingProgress------------------------->" + e);
        }
    }

    public static void hideLoadingProgress() {
        try {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        } catch (Exception e) {
            MyLog.writeLog("hideLoadingProgress------------------------->" + e);
        }
    }

    public static void showMessageDialog(Context context, String message) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.message_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            TextViewSFBold btnOK = dialog.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    public static void showExpiredDialog(Context context, final DialogCallback callback) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            MyLog.writeLog("------------------>Expired Dialod------>" + context);
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.message_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(context.getString(R.string.token_invalid));
            TextViewSFBold btnOK = dialog.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callback.finished();
                }
            });
        }
    }

    public static void showDialogReservationSuccessful(Context context, String msg, final DialogCallback dialogCallback) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog1 = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.message_dialog);
            Window window = dialog1.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog1.show();
            }
            TextViewSFRegular tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText(msg);
            TextViewSFBold btnOK =  dialog1.findViewById(R.id.btnOK);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCallback.finished();
                    dialog1.dismiss();
                }
            });
        }
    }

    public static void apiFail(final Context context, String content, String close, String retry, final CallbackApiFail callbackApiFail) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_api);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextView tvMessage = dialog.findViewById(R.id.api_content);
            TextView btnRetry =  dialog.findViewById(R.id.api_retry);
            TextView btnQuit =  dialog.findViewById(R.id.api_close);

            if (content != null) {
                tvMessage.setText(content);
            }

            if (close == null) {
                View space = dialog.findViewById(R.id.api_space);
                space.setVisibility(View.GONE);
                btnQuit.setVisibility(View.GONE);
            } else {
                btnQuit.setText(close);
            }

            if (retry == null) {
                btnRetry.setVisibility(View.GONE);
            } else {
                btnRetry.setText(retry);
            }

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callbackApiFail.onPress(true);

                }
            });

            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callbackApiFail.onPress(false);

                }
            });
        }
    }


    public static void showNetworkConfirm(final Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            final Dialog dialog = new Dialog(activity, R.style.dialog_full_transparent_background);
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
            tvMessage.setText(activity.getString(R.string.app_need_to_connect_internet_please_turn_on_the_internet));
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);
            TextView btnOK =  dialog.findViewById(R.id.btnOK);

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
        }
    }

    public static void showNetworkError(final Activity activity, final DialogCallback callback) {
        if (activity != null && !activity.isFinishing()) {
            final Dialog dialog = new Dialog(activity, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.network_error_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }

            TextView tvMessage =  dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(activity.getString(R.string.can_not_access_internet));
            TextView btnRetry =  dialog.findViewById(R.id.btnRetry);
            TextView btnQuit =  dialog.findViewById(R.id.btnQuit);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callback.finished();

                }
            });

            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    activity.finish();

                }
            });
        }
    }

}
