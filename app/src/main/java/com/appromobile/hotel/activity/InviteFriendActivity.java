package com.appromobile.hotel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.InviteFriendType;
import com.appromobile.hotel.model.view.InviteFriendForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 11/22/2016.
 */

public class InviteFriendActivity extends BaseActivity {
    String url = "";
    TextViewSFBold tvRecommenderCode;
    WebView tvInviteFriendDescription;
    int promotionDiscount = 0;
    InviteFriendType inviteFriendType;
    String code;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }
        super.onCreate(savedInstanceState);
        inviteFriendType = InviteFriendType.values()[getIntent().getIntExtra("InviteFriendType", 0)];
        // Ấn nút ở banner
        if (inviteFriendType != InviteFriendType.POPUP) {
            setContentView(R.layout.invite_friend_activity);
            tvRecommenderCode =  findViewById(R.id.tvRecommenderCode);
            // Ấn nút ở popup
        } else {
            setContentView(R.layout.invite_friend_center_popup);
            HotelApplication.serviceApi.findInviteFriendInfo(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<InviteFriendForm>() {
                @Override
                public void onResponse(Call<InviteFriendForm> call, Response<InviteFriendForm> response) {
                    InviteFriendForm invite = response.body();
                    if (invite != null) {
                        promotionDiscount = invite.getDiscount();
                        code = String.valueOf(invite.getMemberId());
                        openPopupShare();
                    }
                }

                @Override
                public void onFailure(Call<InviteFriendForm> call, Throwable t) {

                }
            });
        }

        findViewById(R.id.btnInviteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupShare();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvInviteFriendDescription =  findViewById(R.id.tvInviteFriendDescription);
        tvInviteFriendDescription.getSettings().setJavaScriptEnabled(true);

        if (inviteFriendType != InviteFriendType.MY_PAGE) {
            promotionDiscount = getIntent().getIntExtra("promotionDiscount", 0);
            tvInviteFriendDescription.loadDataWithBaseURL("", getString(R.string.msg_10_1_share_content, Utils.formatCurrency(promotionDiscount)), "text/html", "UTF-8", "");

        }
        findInviteFriendInfo();
        findInviteFriendLink();
    }

    private void findInviteFriendInfo() {
        HotelApplication.serviceApi.findInviteFriendInfo(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<InviteFriendForm>() {
            @Override
            public void onResponse(Call<InviteFriendForm> call, Response<InviteFriendForm> response) {
                if (response.isSuccessful()) {
                    InviteFriendForm inviteFriendForm = response.body();
                    if (inviteFriendForm != null) {
                        if (inviteFriendType == InviteFriendType.MY_PAGE) {
                            promotionDiscount = inviteFriendForm.getDiscount();
                        }
                        tvInviteFriendDescription.loadDataWithBaseURL("", inviteFriendForm.getContent(), "text/html", "UTF-8", "");

                        if (tvRecommenderCode != null) {
                            code = String.valueOf(inviteFriendForm.getMemberId());
                            tvRecommenderCode.setText(code);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<InviteFriendForm> call, Throwable t) {
                Toast.makeText(InviteFriendActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void findInviteFriendLink() {
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.findInviteFriendLink(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    url = restResult.getOtherInfo();
                } else {
                    DialogUtils.showExpiredDialog(InviteFriendActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(InviteFriendActivity.this, LoginActivity.class);
                            startActivityForResult(intent, ParamConstants.LOGIN_USER_INFO_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(InviteFriendActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openPopupShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String sendMessage = getString(R.string.msg_10_1_share_content);
        if (promotionDiscount <= 100) {
            sendMessage = sendMessage.replace("myDiscount", String.valueOf(promotionDiscount) + "%");
        } else {
            sendMessage = sendMessage.replace("myDiscount", String.valueOf(promotionDiscount) + " VNĐ");
        }
        sendMessage = sendMessage + "\n" + url;
        sendIntent.putExtra(Intent.EXTRA_TEXT, sendMessage);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetInvite";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }
}
