package com.appromobile.hotel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.PopupCenterAdapter;
import com.appromobile.hotel.callback.CallBackListenerPopupCenter;
import com.appromobile.hotel.db.search.PopupCenterDao;
import com.appromobile.hotel.db.search.PopupSql;
import com.appromobile.hotel.model.view.PopupForm;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.TimerUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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

    private int dots_count;
    private ImageView[] dots;

    private int currentItem;
    private boolean isScroll;

    private Dialog dialog;

    @SuppressLint("SetJavaScriptEnabled")
    public void showNew(final Context context, final List<PopupForm> listPopup, final CallBackListenerPopupCenter listener) {
        /*
        * isShowButton
        * 1: show
        * 2: hide
        * 3: gone
        * */
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.promotion_center_popup_new);

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


                float w = Utils.getScreenWidthPixel();
                int widthImage = (int) w;
                int heightImage = (widthImage / 2) - 64; //Margin

//                CardView cw = dialog.findViewById(R.id.cw);
//                RelativeLayout.LayoutParams layoutParamsCv = new RelativeLayout.LayoutParams((int) w, (int) h);
//                layoutParamsCv.addRule(RelativeLayout.CENTER_VERTICAL);
//                layoutParamsCv.setMarginStart(32);
//                layoutParamsCv.setMarginEnd(32);
//                cw.setLayoutParams(layoutParamsCv);

                final RelativeLayout boxViewPager = dialog.findViewById(R.id.boxViewPager);
                RelativeLayout.LayoutParams layoutParamsVp = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, heightImage);
                boxViewPager.setLayoutParams(layoutParamsVp);

                dialog.show();

                // FIND VIEW BY ID
                final TextViewSFBold tvTitle = dialog.findViewById(R.id.tvTitle);
                final WebView webView = dialog.findViewById(R.id.wvContent);
                RelativeLayout.LayoutParams layoutParamsWv = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, heightImage);
                layoutParamsWv.addRule(RelativeLayout.BELOW, R.id.tvTitle);
                webView.setLayoutParams(layoutParamsWv);
                webView.getSettings().setJavaScriptEnabled(true);
                scrolling(webView);
                final View line1 = dialog.findViewById(R.id.line1);
                final View line2 = dialog.findViewById(R.id.line2);
                final TextView btnGetCoupon = dialog.findViewById(R.id.btnGetCoupon);
                final TextView btnSeeDetail = dialog.findViewById(R.id.btnViewDetail);
                final ViewPager viewPager = dialog.findViewById(R.id.item_viewPager);
                LinearLayout indicator = dialog.findViewById(R.id.indicator_item_viewpager);

                //Sync Database
                final List<PopupForm> listPopupAfterCheck = SyncDatabase(context, listPopup);

                if (listPopupAfterCheck.size() == 0) {
                    dialog.dismiss();
                    return;
                }

                List<String> list = new ArrayList<>();
                final List<Boolean> listAdd = new ArrayList<>();
                for (int i = 0; i < listPopupAfterCheck.size(); i++) {
                    //list.add(String.valueOf(listPopupAfterCheck.get(i).getSn()));
                    list.add(listPopupAfterCheck.get(i).getImageKey());
                    listAdd.add(false);
                }
                PopupCenterAdapter popupCenterAdapter = new PopupCenterAdapter(list);
                viewPager.setAdapter(popupCenterAdapter);

                currentItem = 0;

                initDot(context, indicator, popupCenterAdapter.getCount());

                updateView(context, listPopupAfterCheck.get(currentItem), btnGetCoupon, btnSeeDetail, line1, line2, tvTitle, webView);

                if (!listAdd.get(currentItem)) {
                    listAdd.set(currentItem, true);
                    PopupCenterDao.getInstance(context).update(listPopupAfterCheck.get(currentItem).getSn());
                }


                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        currentItem = position;

                        updateDot(context);

                        updateView(context, listPopupAfterCheck.get(currentItem), btnGetCoupon, btnSeeDetail, line1, line2, tvTitle, webView);

                        //Update SQL
                        if (!listAdd.get(currentItem)) {
                            listAdd.set(currentItem, true);
                            PopupCenterDao.getInstance(context).update(listPopupAfterCheck.get(currentItem).getSn());
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

//                final TimerUtils timer = new TimerUtils(DELAY_TIMER, TimerUtils.TYPE_POPUP_CENTER, new TimerUtils.OnTimeListener() {
//                    @Override
//                    public void setOnTimeListener(int type) {
//                        if (!isScroll && type == TimerUtils.TYPE_POPUP_CENTER) {
//                            autoScrollItem(context, viewPager, dots_count, currentItem);
//                        }
//                    }
//                });
//                timer.startTimer();


                final Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isScroll) {
                            autoScrollItem(context, viewPager, dots_count, currentItem);
                        }
                    }
                }, 0, 5000);

                //Get Coupon Click
                btnGetCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onGetCoupon(listPopupAfterCheck.get(currentItem).getTargetSn(), dialog);
                        //stopTimer(timer);
                        t.cancel();
                    }
                });

                //See Detail Click
                btnSeeDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSeeDetail(listPopupAfterCheck.get(currentItem).getTargetInfo(), listPopupAfterCheck.get(currentItem).getTargetSn(), listPopupAfterCheck.get(currentItem).getAction());
                        dialog.dismiss();
                        //stopTimer(timer);
                        t.cancel();
                    }
                });


                dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //stopTimer(timer);
                        t.cancel();
                    }
                });
            }

        }

    }

    public void dismiss(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    private void updateView(Context context, PopupForm popup, TextView btnGetCoupon, TextView btnSeeDetail, View line1, View line2, TextView tvTitle, WebView webView) {
        int statusButtonGetCoupon = ParamConstants.BUTTON_INVISIBLE; //Hide = 2
        int statusButtonSeeDetail = ParamConstants.BUTTON_SHOW;//Show = 1

        if (popup.getAction() == PopupForm.ACTION_PROMOTION) { //==1
            if (!popup.isApplied() && popup.isCanApply()) {
                statusButtonGetCoupon = ParamConstants.BUTTON_SHOW;
            } else if (!popup.isCanApply()) {
                statusButtonGetCoupon = ParamConstants.BUTTON_HIDE;

            }
        } else {
            // Hide Button Get Coupon
            statusButtonGetCoupon = ParamConstants.BUTTON_HIDE;
            if (popup.getAction() == PopupForm.ACTION_LINK && (popup.getTargetInfo() == null || popup.getTargetInfo().equals(""))) {
                // Hide Button See Detail
                statusButtonSeeDetail = ParamConstants.BUTTON_HIDE;
            }
        }

        /*
        / DISPLAY BUTTON GET COUPON OR NOT
        */
        if (statusButtonGetCoupon == ParamConstants.BUTTON_INVISIBLE) {
            btnGetCoupon.setTextColor(ContextCompat.getColor(context, R.color.bk_50p));
            btnGetCoupon.setEnabled(false);
            btnSeeDetail.setTextColor(ContextCompat.getColor(context, R.color.org));
        } else if (statusButtonGetCoupon == ParamConstants.BUTTON_HIDE) {
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            btnGetCoupon.setVisibility(View.GONE);
            btnSeeDetail.setTextColor(ContextCompat.getColor(context, R.color.org));
        } else if (statusButtonGetCoupon == ParamConstants.BUTTON_SHOW) {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            btnGetCoupon.setVisibility(View.VISIBLE);
            btnGetCoupon.setTextColor(ContextCompat.getColor(context, R.color.org));
            btnSeeDetail.setTextColor(ContextCompat.getColor(context, R.color.bk_50p));
        }

        /*
        * CHECK BUTTON SEE DETAIL
        */
        if (statusButtonSeeDetail == ParamConstants.BUTTON_SHOW) {
            btnSeeDetail.setVisibility(View.VISIBLE);
        } else if (statusButtonSeeDetail == ParamConstants.BUTTON_HIDE) {
            btnSeeDetail.setVisibility(View.GONE);
        }

        if (popup.getTitle() != null)
            tvTitle.setText(popup.getTitle());
        if (popup.getContent() != null)
            webView.loadDataWithBaseURL("", popup.getContent(), "text/html", "UTF-8", "");
    }

    private void initDot(Context context, LinearLayout indicator, int totalDot) {
        dots_count = totalDot;
        dots = new ImageView[dots_count];

        if (dots_count > 1) {
            for (int i = 0; i < dots_count; i++) {
                dots[i] = new ImageView(context);
                dots[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_non_active));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                indicator.addView(dots[i], params);
            }

            //Set Default
            dots[0].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_active));
        }
    }

    private void updateDot(Context context) {
        if (dots_count > 1 && currentItem < dots_count) {
            for (int i = 0; i < dots_count; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_non_active));
            }

            dots[currentItem].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_active));

        }
    }

    private void stopTimer(TimerUtils timer) {
        if (timer != null) {
            timer.stopTimer();
        }
    }

    private void autoScrollItem(Context context, final ViewPager viewPager, final int totalItem, final int position) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (viewPager != null && viewPager.getAdapter() != null) {
                    if (totalItem > 1) {
                        if (position < totalItem - 1) {
                            viewPager.setCurrentItem(position + 1, true);
                        } else {
                            viewPager.setCurrentItem(0, true);
                        }
                    }
                }
            }
        });
    }

    private List<PopupForm> SyncDatabase(Context context, List<PopupForm> listPopupForm) {
        int sn;
        int exist;
        List<PopupSql> listPopupSql = PopupCenterDao.getInstance(context).selectAll();
        //Add To DB
        for (int i = 0; i < listPopupForm.size(); i++) {
            exist = 0;
            sn = listPopupForm.get(i).getSn();
            for (int j = 0; j < listPopupSql.size(); j++) {
                if (sn == listPopupSql.get(j).getSn()) {
                    exist++;
                }
            }

            if (exist == 0 || listPopupSql.size() == 0) {
                //Add DB
                PopupCenterDao.getInstance(context).insert(new PopupSql(listPopupForm.get(i).getSn(), 0));
            }
        }

        List<PopupForm> listResult = new ArrayList<>();
        int view;

        listPopupSql = PopupCenterDao.getInstance(context).selectAll();
        //Remove DB
        for (int i = 0; i < listPopupSql.size(); i++) {
            exist = 0;
            sn = listPopupSql.get(i).getSn();
            view = listPopupSql.get(i).getView();
            for (int j = 0; j < listPopupForm.size(); j++) {
                if (sn == listPopupForm.get(j).getSn()) {
                    exist++;

                    //Add New List After Check MaxView
                    if (view < listPopupForm.get(j).getMaxView() || listPopupForm.get(j).getMaxView() == 0) {
                        listResult.add(listPopupForm.get(j));
                    }

                }
            }

            if (exist == 0) {
                //Delete DB
                PopupCenterDao.getInstance(context).delete(sn);
            }
        }

        return listResult;

    }


    private void scrolling(View v) {
        if (v instanceof WebView) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isScroll = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            v.performClick();
                            isScroll = false;
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        } else if (v instanceof ViewPager) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isScroll = true;
                            Log.d("running", "DOWN");
                            break;
                        case MotionEvent.ACTION_UP:
                            v.performClick();
                            isScroll = false;
                            Log.d("running", "UP");
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

}
