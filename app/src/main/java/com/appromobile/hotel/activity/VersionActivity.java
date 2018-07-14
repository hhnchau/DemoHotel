package com.appromobile.hotel.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.BuildConfig;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 10/13/2016.
 */

public class VersionActivity extends BaseActivity {
    private TextView tvLastVersionMessage, btnUpdate, tvLastVersion, tvCurrentInstall;
    LinearLayout boxInstalVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.version_activity);
        tvLastVersionMessage = findViewById(R.id.tvLastVersionMessage);
        btnUpdate =  findViewById(R.id.btnUpdate);
        tvLastVersion =  findViewById(R.id.tvLastVersion);
        tvCurrentInstall = findViewById(R.id.tvCurrentInstall);
        boxInstalVersion =  findViewById(R.id.boxInstalVersion);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        DialogUtils.showLoadingProgress(this, false);
        initData();
        /*String lastAppVersion;
        if (HotelApplication.apiSettingForm != null) {
            lastAppVersion = HotelApplication.apiSettingForm.getLastAndroidAppVersion();

        } else {
            lastAppVersion = "1.2";
        }*/


    }

    private void initData() {
        HotelApplication.serviceApi.findApiSetting(PreferenceUtils.getToken(this),HotelApplication.DEVICE_ID).enqueue(new Callback<ApiSettingForm>() {
            @Override
            public void onResponse(Call<ApiSettingForm> call, Response<ApiSettingForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    ApiSettingForm form = response.body();
                    String versionName = BuildConfig.VERSION_NAME;            //from local
                    String lastAppVersion = form.getLastAndroidAppVersion(); //from server

                    int[] appBuild = changeVersionToInt(versionName);
                    int[] svBuild = changeVersionToInt(lastAppVersion);

                    //App0 > Server0
                    if (appBuild[0] > svBuild[0]) {
                        //Continue
                        btnUpdate.setVisibility(View.GONE);
                        boxInstalVersion.setVisibility(View.GONE);
                        tvLastVersionMessage.setVisibility(View.VISIBLE);
                        tvLastVersionMessage.setText(getString(R.string.you_already_used_last_version) + " " + versionName);
                    } else {
                        //App < Server
                        if (appBuild[0] < svBuild[0]) {
                            //Force
                            tvCurrentInstall.setText(getString(R.string.installed_version) + ": " + versionName);
                            tvLastVersion.setText(getString(R.string.lastest_version) + ": " + lastAppVersion);
                            tvLastVersionMessage.setVisibility(View.GONE);
                        }
                        //App0 = Server0
                        else {
                            //App1 > Server1
                            if (appBuild[1] >= svBuild[1]) {
                                //Check number 3
                                if (appBuild[2] >= svBuild[2]) {
                                    //Continue
                                    btnUpdate.setVisibility(View.GONE);
                                    boxInstalVersion.setVisibility(View.GONE);
                                    tvLastVersionMessage.setVisibility(View.VISIBLE);
                                    tvLastVersionMessage.setText(getString(R.string.you_already_used_last_version) + " " + versionName);
                                } else {
                                    //Update
                                    tvCurrentInstall.setText(getString(R.string.installed_version) + ": " + versionName);
                                    tvLastVersion.setText(getString(R.string.lastest_version) + ": " + lastAppVersion);
                                    tvLastVersionMessage.setVisibility(View.GONE);
                                }
                            } else {
                                if (appBuild[1] < svBuild[1]) {
                                    //Update
                                    tvCurrentInstall.setText(getString(R.string.installed_version) + ": " + versionName);
                                    tvLastVersion.setText(getString(R.string.lastest_version) + ": " + lastAppVersion);
                                    tvLastVersionMessage.setVisibility(View.GONE);
                                }
                            }
                        }

                    }

//                    if (lastAppVersion.equals(versionName)) {
//                        btnUpdate.setVisibility(View.GONE);
//                        boxInstalVersion.setVisibility(View.GONE);
//                        tvLastVersionMessage.setVisibility(View.VISIBLE);
//                        tvLastVersionMessage.setText(getString(R.string.you_already_used_last_version) + " " + versionName);
//                    } else {
//                        tvCurrentInstall.setText(getString(R.string.installed_version) + ": " + versionName);
//                        tvLastVersion.setText(getString(R.string.lastest_version) + ": " + lastAppVersion);
//                        tvLastVersionMessage.setVisibility(View.GONE);
//                    }

                }
            }

            @Override
            public void onFailure(Call<ApiSettingForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(VersionActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int[] changeVersionToInt(String version) {
        String[] temp = version.split("\\.");
        int[] intVer = new int[3];
        for (int i = 0; i < intVer.length; i++) {
            if (i == 2 & temp.length != 3) {
                intVer[2] = 0;
            } else {
                intVer[i] = Integer.parseInt(temp[i]);
            }
        }
        return intVer;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetVersion";
    }
}
