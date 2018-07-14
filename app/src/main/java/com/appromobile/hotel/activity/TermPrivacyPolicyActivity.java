package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.model.request.UserCommonInfoDto;
import com.appromobile.hotel.model.view.CommonInfoForm;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 8/1/2016.
 */
public class TermPrivacyPolicyActivity extends BaseActivity implements View.OnClickListener {
    TextViewSFBold tvTab1, tvTab2;
    LinearLayout tabServiceAgreement, tabPrivacyPolicy;
    WebView wvContent;
    CommonInfoForm commonInfoForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.term_privacy_policy_activity);
        wvContent =  findViewById(R.id.wvContent);
        tabServiceAgreement =  findViewById(R.id.tabServiceAgreement);
        tabPrivacyPolicy =  findViewById(R.id.tabPrivacyPolicy);
        tvTab1 =  findViewById(R.id.tvTab1);
        tvTab2 =  findViewById(R.id.tvTab2);

        tabPrivacyPolicy.setOnClickListener(this);
        tabServiceAgreement.setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        wvContent.loadData(getString(R.string.service_agreement_content), "text/html", "UTF-8");
        getCommonInfoFromAPI();

    }

    private void getCommonInfoFromAPI() {
        ControllerApi.getmInstance().findCommonInfo(this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                if (object != null) {
                    commonInfoForm = (CommonInfoForm) object;
                    setupTab(0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        boolean status = PreferenceUtils.getReadStatusPolicy(TermPrivacyPolicyActivity.this);
        if (!status) {
            // hiện popup
            Dialag.getInstance().show(TermPrivacyPolicyActivity.this, false, false, true, null,
                    getString(R.string.msg_6_9_3_popup_exit_agreement_page),
                    null,
                    null, getString(R.string.ok), Dialag.BTN_RIGHT,
                    new CallbackDialag() {
                        @Override
                        public void button1() {

                        }

                        @Override
                        public void button2() {

                        }

                        @Override
                        public void button3(final Dialog dialog) {
                            // update status in local memory
                            PreferenceUtils.setReadStatusPolicy(TermPrivacyPolicyActivity.this, true);
                            UserCommonInfoDto commonInfoDto = new UserCommonInfoDto();
                            commonInfoDto.setRead(true);
                            ControllerApi.getmInstance().updateReadStatusCommonInfo(TermPrivacyPolicyActivity.this, commonInfoDto, new ResultApi() {
                                @Override
                                public void resultApi(Object object) {
                                    // không cần xử lý
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            });

                        }
                    });
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.stable, R.anim.left_to_right);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabPrivacyPolicy:
                setupTab(1);
                break;
            case R.id.tabServiceAgreement:
                setupTab(0);
                break;
        }
    }

    public void setupTab(int tab) {

        switch (tab) {
            case 0:
                tabPrivacyPolicy.setBackgroundColor(getResources().getColor(R.color.transparent));
                tabServiceAgreement.setBackgroundResource(R.drawable.box_tab0_white_bg);
                tvTab1.setTextColor(getResources().getColor(R.color.org));
                tvTab2.setTextColor(getResources().getColor(R.color.bk));
                if (commonInfoForm != null && commonInfoForm.getServiceAgreement() != null) {
                    wvContent.loadDataWithBaseURL("", commonInfoForm.getServiceAgreement(), "text/html", "UTF-8", "");
                }
                break;
            case 1:
                tabServiceAgreement.setBackgroundColor(getResources().getColor(R.color.transparent));
                tabPrivacyPolicy.setBackgroundResource(R.drawable.box_tab1_white_bg);
                tvTab2.setTextColor(getResources().getColor(R.color.org));
                tvTab1.setTextColor(getResources().getColor(R.color.bk));
                if (commonInfoForm != null && commonInfoForm.getPrivatePolicy() != null) {
                    wvContent.loadDataWithBaseURL("", commonInfoForm.getPrivatePolicy(), "text/html", "UTF-8", "");
                }
                break;
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetService";
    }
}
