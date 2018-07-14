package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.MySettingAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.model.request.LogoutDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserSettingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by appro on 13/04/2018.
 */

public class MySettingsActivity extends BaseActivity {
    private String[] stringList;
    private ListView list;
    private MySettingAdapter mySettingAdapter;
    private UserSettingForm userSettingForm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_setting_activity);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        findApi();
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView imgBack = findViewById(R.id.btnClose);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        list = findViewById(R.id.listCheckbox);
        stringList = getResources().getStringArray(R.array.list_my_setting);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnItemClick(position);
            }
        });
    }

    private void findApi(){
        ControllerApi.getmInstance().findUserSettingViaAppUserSn(this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                userSettingForm = (UserSettingForm) object;
                //Update View

                mySettingAdapter = new MySettingAdapter(stringList, userSettingForm);
                list.setAdapter(mySettingAdapter);
                
            }
        });
    }

    private void handleOnItemClick(int p) {
        String s = stringList[p];

        if (s.equals(getString(R.string.account_setting))) {
            gotoAccountSetting();
        } else if (s.equals(getString(R.string.txt_6_14_notification_setting))) {
            gotoNotificationSetting();
        } else if (s.equals(getString(R.string.txt_6_14_language_setting))) {
            gotoLangugeSetting();
        } else if (s.equals(getString(R.string.txt_6_1_logout))) {
            gotoLogout();
        }
    }

    private void gotoAccountSetting() {
        Intent intent = new Intent(this, AccountSettingActivity.class);
        AppUserForm appUserForm = PreferenceUtils.getAppUser(this);
        intent.putExtra("AppUserForm", appUserForm);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoNotificationSetting() {
        Intent notify = new Intent(this, NotificationSettingActivity.class);
        notify.putExtra("UserSettingForm", userSettingForm);
        startActivity(notify);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoLangugeSetting() {
        Intent lang = new Intent(this, LanguageSettingActivity.class);
        lang.putExtra("UserSettingForm", userSettingForm);
        startActivity(lang);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoLogout() {
        LogoutDto logoutDto = new LogoutDto(HotelApplication.DEVICE_ID);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.logout(logoutDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                SignupType type = PreferenceUtils.getLoginType(MySettingsActivity.this);
                // 2 = facebook
                HotelApplication.userAreaFavoriteForms = null;
                if (type.getType() == 2) {
                    revokeAccessFromFacebook();
                }
                // 3 = google plus
                else if (type.getType() == 3) {
                    revokeAccessFromGooglePlus();
                } else {
                    deleteDataOnPref();
                }
            }


            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void revokeAccessFromGooglePlus() {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestIdToken("170640652110-tc01mpf2ucq65o8dejksatq699ofnfc0.apps.googleusercontent.com")
                .requestEmail().requestProfile()
                .build();
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        mGoogleApiClient.disconnect();
                    }
                }
            });
        }
        deleteDataOnPref();
    }

    private void revokeAccessFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
        }
        deleteDataOnPref();
    }

    private void deleteDataOnPref() {
        PreferenceUtils.setToken(MySettingsActivity.this, "");
        PreferenceUtils.setPassword(MySettingsActivity.this, "");
        PreferenceUtils.setUserId(MySettingsActivity.this, "");
        PreferenceUtils.setPasscode(MySettingsActivity.this, "");
        PreferenceUtils.setIsPasscode(MySettingsActivity.this, false);
        PreferenceUtils.setAutoLogin(MySettingsActivity.this, false);
        Intent intent = new Intent(MySettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {

    }
}
