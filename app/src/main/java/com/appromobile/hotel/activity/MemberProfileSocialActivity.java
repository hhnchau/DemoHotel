package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appromobile.hotel.BuildConfig;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.enums.Gender;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.gcm.FirebaseUtils;
import com.appromobile.hotel.model.request.AppUserSocialDto;
import com.appromobile.hotel.model.request.LogoutDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SendSmsDto;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DateTimeDialogUtils;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaredrummler.android.device.DeviceName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/5/2016.
 */
public class MemberProfileSocialActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private AppUserSocialDto appUserDto;
    private TextViewSFRegular tvFemale, tvMale, tvBirthday, btnOK, tvMessage;
    private ImageView imgValidateEmail;
    private TextViewSFBold btnCheckDuplicateNickName;
    private EditTextSFRegular txtNickname, txtAddress, txtMobile, txtEmail, txtVerifyCode;
    private ImageView btnClose;
    boolean isNicknameValid = false;
    //    boolean isEmailValid =false;
    private Gender gender;
    ImageView chkFemale, chkMale;
    TextViewSFBold btnGetCode;
    Timer timer = new Timer();
    private LinearLayout inviteCodeArea;
    private EditTextSFRegular txtInviteCode;

    @Override
    public void setScreenName() {
        this.screenName = "SLogProfile";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.member_profile_social_activity);

        appUserDto = getIntent().getParcelableExtra("AppUserSocialDto");

        txtInviteCode =  findViewById(R.id.txtInviteCodeSocial);
        inviteCodeArea = findViewById(R.id.viewInviteCodeSocial);
        btnGetCode =  findViewById(R.id.btnGetCode);
        txtVerifyCode =  findViewById(R.id.txtVerifyCode);
        chkFemale =  findViewById(R.id.chkFemale);
        chkMale =  findViewById(R.id.chkMale);
        btnOK =  findViewById(R.id.btnOK);
        imgValidateEmail =  findViewById(R.id.imgValidateEmail);
        btnCheckDuplicateNickName =  findViewById(R.id.btnCheckDuplicateNickName);
        tvFemale = findViewById(R.id.tvFemale);
        tvMale = findViewById(R.id.tvMale);
        tvBirthday =  findViewById(R.id.tvBirthday);
        btnClose =  findViewById(R.id.btnClose);
        tvMessage =  findViewById(R.id.tvMessage);
        txtNickname =  findViewById(R.id.txtNickname);
        txtAddress =  findViewById(R.id.txtAddress);
        txtMobile =  findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtEmail);

        btnCheckDuplicateNickName.setOnClickListener(this);
        chkFemale.setOnClickListener(this);
        chkMale.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        tvFemale.setOnClickListener(this);
        tvMale.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);
        Utils.showKeyboard(this);
        txtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isNicknameValid = false;
//                btnOK.setEnabled(false);
//                btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
            }
        });

        tvBirthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMessage.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().equals("")) {
                    if (!UtilityValidate.isEmailValid(txtEmail.getText().toString())) {
                        tvMessage.setText(getString(R.string.email_format_not_valid));
                        tvMessage.setVisibility(View.VISIBLE);
                        imgValidateEmail.setImageResource(R.drawable.cancel);
                    } else {
                        tvMessage.setVisibility(View.GONE);
                        imgValidateEmail.setImageResource(R.drawable.check);
                    }
                } else {
                    imgValidateEmail.setImageResource(R.drawable.cancel);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initData();
    }

    private void logout() {
        LogoutDto logoutDto = new LogoutDto(HotelApplication.DEVICE_ID);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.logout(logoutDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    onBackPressed();
                }

            }


            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }


    private void initData() {
        txtNickname.setText(appUserDto.getNickName());
        txtEmail.setText(appUserDto.getEmail());
        if (!txtEmail.getText().toString().equals("")) {
            txtEmail.setEnabled(false);
//            isEmailValid = true;
            txtNickname.requestFocus();
        } else {
            txtEmail.requestFocus();
        }
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request));
            calendar.setTimeInMillis(simpleDateFormat.parse(appUserDto.getBirthday()).getTime());
        } catch (Exception e) {
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view));
        tvBirthday.setText(simpleDateFormat.format(calendar.getTime()));
        gender = Gender.toType(appUserDto.getGender());
        setupGenderView();
        if (appUserDto.getMobile() == null || appUserDto.getMobile().equals("")) {
            inviteCodeArea.setVisibility(View.VISIBLE);
        } else {
            inviteCodeArea.setVisibility(View.GONE);
        }
    }

    private void setupGenderView() {
        if (gender == Gender.Male) {
            chkMale.setImageResource(R.drawable.checkbox_selected);
            chkFemale.setImageResource(R.drawable.checkbox);
        } else {
            chkFemale.setImageResource(R.drawable.checkbox_selected);
            chkMale.setImageResource(R.drawable.checkbox);
        }
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.btnClose: {
                logout();

                break;
            }
            case R.id.btnOK:
                sendSignup();
                break;
            case R.id.btnCheckDuplicateNickName:
                checkDuplicateNickname();
                break;
            case R.id.chkFemale:
            case R.id.tvFemale:
                gender = Gender.Female;
                setupGenderView();
                break;
            case R.id.chkMale:
            case R.id.tvMale:
                gender = Gender.Male;
                setupGenderView();
                break;
            case R.id.tvBirthday:
                showDateTimeChoose();
                break;
            case R.id.btnGetCode:
                sendPhoneToVerify();
                break;
        }
    }

    private void sendPhoneToVerify() {
        if (txtMobile.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_phone_number));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        SendSmsDto sendSmsDto = new SendSmsDto();
        sendSmsDto.setMobile(txtMobile.getText().toString());
        sendSmsDto.setMobileUserId(HotelApplication.DEVICE_ID);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.sendVerifyCode(sendSmsDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            btnGetCode.setVisibility(View.GONE);
                            int timeIntervalGetCode = 60;
                            try {
                                timeIntervalGetCode = HotelApplication.apiSettingForm.getTimeIntervalGetCode();
                            } catch (Exception e) {
                                timeIntervalGetCode = 60;
                            }
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.verify_code_button, timeIntervalGetCode), Toast.LENGTH_LONG).show();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    btnGetCode.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnGetCode.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }, 1000 * timeIntervalGetCode);

                        } else if (restResult.getResult() == 12) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.number_is_wrong_format), Toast.LENGTH_LONG).show();
                        } else if (restResult.getResult() == 13) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.server_cannot_send_sms), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MemberProfileSocialActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkDuplicateEmail() {
        if (txtEmail.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (!UtilityValidate.isEmailValid(txtEmail.getText().toString())) {
            tvMessage.setText(getString(R.string.email_format_not_valid));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", txtEmail.getText().toString());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.checkUserIdInSytem(params, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getOtherInfo().trim().equals("0")) {
//                        isEmailValid = true;
                        tvMessage.setText("");
                        tvMessage.setVisibility(View.GONE);
                        Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.the_email_is_available), Toast.LENGTH_LONG).show();
                    } else {
//                        isEmailValid = false;
                        tvMessage.setVisibility(View.VISIBLE);
                        tvMessage.setText(getString(R.string.email_already_exists));
                    }
                }
                checkFormValidate();
            }


            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
//                isEmailValid = false;
                checkFormValidate();
                Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkFormValidate() {
        if (isNicknameValid) {
//            btnOK.setEnabled(true);
//            btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
        } else {
//            btnOK.setEnabled(false);
//            btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
        }
    }

    private void showDateTimeChoose() {

        Calendar minYear = Calendar.getInstance();
        minYear.set(Calendar.YEAR, 1950);

        Calendar maxYear = Calendar.getInstance();
        maxYear.set(Calendar.YEAR, maxYear.get(Calendar.YEAR) - 18);
        DateTimeDialogUtils.showDatePickerDialog(this, tvBirthday, minYear, maxYear);
    }

    private void checkDuplicateNickname() {
        if (txtNickname.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_nickname));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("nickName", txtNickname.getText().toString());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.checkNickNameInSytem(params, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getOtherInfo().trim().equals("0")) {
                        isNicknameValid = true;
                        tvMessage.setText("");
                        tvMessage.setVisibility(View.GONE);
                        Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.the_nickname_is_available), Toast.LENGTH_LONG).show();
                        checkFormValidate();
                    } else {
                        isNicknameValid = false;
                        tvMessage.setText(getString(R.string.nickname_already_exists));
                        tvMessage.setVisibility(View.VISIBLE);
                        checkFormValidate();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                isNicknameValid = false;
                Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendSignup() {
        if (txtEmail.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

//        if(!isEmailValid){
//            tvMessage.setText(getString(R.string.please_duplicate_check_email));
//            tvMessage.setVisibility(View.VISIBLE);
//            return;
//        }
        if (!UtilityValidate.isEmailValid(txtEmail.getText().toString())) {
            tvMessage.setText(getString(R.string.email_format_not_valid));
            tvMessage.setVisibility(View.VISIBLE);
            imgValidateEmail.setImageResource(R.drawable.cancel);
            return;
        }

        if (txtNickname.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_nickname));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (!isNicknameValid) {
            tvMessage.setText(getString(R.string.please_check_nickname_first));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (tvBirthday.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_choose_birthday));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (txtMobile.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_phone_number));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (txtVerifyCode.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_verify_code));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        appUserDto.setNickName(txtNickname.getText().toString());
        appUserDto.setGender(gender.getType());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view));
        try {
            Date date = simpleDateFormat.parse(tvBirthday.getText().toString());
            calendar.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request));
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));
        appUserDto.setEmail(txtEmail.getText().toString());
        appUserDto.setAddress(txtAddress.getText().toString());
        appUserDto.setMobile(txtMobile.getText().toString());
        appUserDto.setVerifyCode(txtVerifyCode.getText().toString());
        appUserDto.setInviteCode(txtInviteCode.getText().toString());
        DialogUtils.showLoadingProgress(this, false);

        HotelApplication.serviceApi.createNewAppUserViaSocialApp(appUserDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            loginAfterSignup();
                        } else if (response.body().getResult() == 19) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.verify_code_expired), Toast.LENGTH_LONG).show();
                        } else if (response.body().getResult() == 20) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.verify_code_not_match), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MemberProfileSocialActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                t.printStackTrace();
            }
        });
    }

    private void loginAfterSignup() {
        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setViaApp(appUserDto.getViaApp());
        loginDto.setSocialToken(appUserDto.getToken());
        loginDto.setCache(false);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getResult() == 1) {
                        PreferenceUtils.setLoginType(MemberProfileSocialActivity.this, SignupType.toType(appUserDto.getViaApp()));
                        PreferenceUtils.setToken(MemberProfileSocialActivity.this, restResult.getOtherInfo());
                        PreferenceUtils.setAutoLogin(MemberProfileSocialActivity.this, true);
                        updateTokenToServer();
                        getAppUserForm();
                    } else {
                        DialogUtils.showMessageDialog(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server));
                        //DialogUtils.showMessageDialog(MemberProfileSocialActivity.this, getString(R.string.incorrect_email_or_password_please_try_again));
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                DialogUtils.showMessageDialog(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server));
            }
        });
    }

    public void getAppUserForm() {
        DialogUtils.showLoadingProgress(this, false);
        ControllerApi.getmInstance().findApiSetting(MemberProfileSocialActivity.this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                if (object != null) {
                    ApiSettingForm form = (ApiSettingForm) object;
                    PreferenceUtils.setReadStatusPolicy(getApplicationContext(), form.isReadAgreementPolicy());
                }
            }
        });
        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    AppUserForm appUserForm = response.body();
                    if (appUserForm != null) {
                        PreferenceUtils.setAppUser(MemberProfileSocialActivity.this, appUserForm);
//                        Intent intentResult = new Intent();
//                        setResult(Activity.RESULT_OK, intentResult);
//                        finish();

                        Intent intent = new Intent(MemberProfileSocialActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppUserForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void updateTokenToServer() {
        MobileDeviceInput mobileDeviceInput = new MobileDeviceInput();
        mobileDeviceInput.setLanguage(2);
        mobileDeviceInput.setMobileUserId(HotelApplication.DEVICE_ID);
        mobileDeviceInput.setOs(2);
        mobileDeviceInput.setAppVersion(BuildConfig.VERSION_NAME);
        mobileDeviceInput.setPhoneModel(DeviceName.getDeviceName());
        mobileDeviceInput.setOsVersion(Build.VERSION.RELEASE);
        mobileDeviceInput.setTokenId(FirebaseUtils.getRegistrationId(this));
        HotelApplication.serviceApi.updateAppUserToken(mobileDeviceInput, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                if (response.isSuccessful()) {
//                    checkAutoLogin();
                } else {
                    System.out.println("updateAppUserToken: Fail: " + response.message());
//                    checkAutoLogin();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                System.out.println("updateAppUserToken: Fail:");
//                checkAutoLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            timer.cancel();
        } catch (Exception e) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        HotelApplication.googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (HotelApplication.googleApiClient != null) {
            HotelApplication.googleApiClient.connect();
        } else {
            HotelApplication.initializeGoogleSignIn();
            HotelApplication.googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (HotelApplication.googleApiClient != null) {
            HotelApplication.googleApiClient.disconnect();
        }
    }
}
