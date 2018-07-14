package com.appromobile.hotel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.callback.CallBackListenerPopupCenter;
import com.appromobile.hotel.model.view.PopupForm;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.bumptech.glide.Glide;

import java.util.Calendar;

/**
 * Created by appro on 17/05/2017.
 */
public class PromotionPopup {
    private static PromotionPopup Instance = null;

    public static PromotionPopup getInstance() {
        if (Instance == null) {
            Instance = new PromotionPopup();
        }
        return Instance;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showNew(final Context context, final PopupForm popup, int statusButtonGetCoupon, int statusButtonSeeDetail, final CallBackListenerPopupCenter listener) {
        /*
        * isShowButton
        * 1: show
        * 2: hide
        * 3: gone
        * */
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.promotion_center_popup_new);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                ImageView imgBanner =  dialog.findViewById(R.id.imgBanner);
                TextViewSFBold tvTitle = dialog.findViewById(R.id.tvTitle);
                WebView webView =  dialog.findViewById(R.id.wvContent);
                webView.getSettings().setJavaScriptEnabled(true);
                Button btnGetCoupon =  dialog.findViewById(R.id.btnGetCoupon);
                Button btnSeeDetail =  dialog.findViewById(R.id.btnViewDetail);

                /*
                / DISPLAY BUTTON GET COUPON OR NOT
                 */
                if (statusButtonGetCoupon == ParamConstants.BUTTON_INVISIBLE) {
                    btnGetCoupon.setTextColor(ContextCompat.getColor(context, R.color.bk_50p));
                    btnGetCoupon.setEnabled(false);
                    btnSeeDetail.setTextColor(ContextCompat.getColor(context, R.color.org));
                } else if (statusButtonGetCoupon == ParamConstants.BUTTON_HIDE) {
                    btnGetCoupon.setVisibility(View.GONE);
                    btnSeeDetail.setTextColor(ContextCompat.getColor(context, R.color.org));
                }

                /*
                * CHECK BUTTON SEE DETAIL
                */
                if (statusButtonSeeDetail == ParamConstants.BUTTON_SHOW) {
                    btnSeeDetail.setVisibility(View.VISIBLE);
                } else if (statusButtonSeeDetail == ParamConstants.BUTTON_HIDE) {
                    btnSeeDetail.setVisibility(View.GONE);
                }

                String url = UrlParams.MAIN_URL + "/hotelapi/popup/download/downloadPopupImage?popupSn=" + popup.getSn();
                try {
                    GlideApp
                            .with(imgBanner.getContext())
                            .load(url)
                            .placeholder(R.drawable.loading_small)
                            .error(R.drawable.loading_small)
                            .into(imgBanner);
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.writeLog("showPromotionPopup----------------------->" + e);
                }
                if (popup.getTitle() != null)
                    tvTitle.setText(popup.getTitle());
                if (popup.getContent() != null)
                    webView.loadDataWithBaseURL("", popup.getContent(), "text/html", "UTF-8", "");

                //Get Coupon Click
                btnGetCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onGetCoupon(popup.getTargetSn(), dialog);
                    }
                });

                //See Detail Click
                btnSeeDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSeeDetail(popup.getTargetInfo());
                        dialog.dismiss();
                    }
                });


                dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        }

    }
}
