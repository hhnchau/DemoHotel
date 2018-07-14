package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.BuildConfig;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackVerify;
import com.appromobile.hotel.dialog.DialogVerify;
import com.appromobile.hotel.enums.Gender;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.gcm.FirebaseUtils;
import com.appromobile.hotel.model.request.AppUserDto;
import com.appromobile.hotel.model.request.AppUserSocialDto;
import com.appromobile.hotel.model.request.LoginDto;
import com.appromobile.hotel.model.request.LogoutDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SendSmsDto;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.request.ViewCrmNotificationDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DateTimeDialogUtils;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/5/2016.
 */
public class MemberProfileSocialActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private AppUserSocialDto appUserDto;
    private TextView tvFemale, tvMale, btnOK;
    private EditText txtNickname, txtMobile, txtEmail, tvBirthday, txtInviteCode, focus;
    private ImageView btnClose;
    private Gender gender;
    private ImageView chkFemale, chkMale;

    private ImageView imgValidateMail, imgValidateNickname, imgValidateMobile, imgValidateBirthday, imgValidateInvite;
    private TextInputLayout inputLayoutMail, inputLayoutNickname, inputLayoutMobile, inputLayoutBirthday, inputLayoutInvite;
    private boolean isValidateEmail = false;
    private boolean isNicknameValid = false;
    private boolean isMobileValid = false;
    private boolean isBirthday = false;
    private boolean isInvite = false;

    private boolean isManual = false;
    private boolean isSend = false;


    @Override
    public void setScreenName() {
        this.screenName = "SLogProfile";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        setContentView(R.layout.member_profile_social_activity);

        String action = getIntent().getAction();
        if (action != null && action.equals("manual")) {
            isManual = true;
        }
        appUserDto = getIntent().getParcelableExtra("AppUserSocialDto");

        imgValidateMail = findViewById(R.id.imgValidateEmail);
        imgValidateNickname = findViewById(R.id.imgValidateNickName);
        imgValidateMobile = findViewById(R.id.imgValidateMobile);
        imgValidateBirthday = findViewById(R.id.imgValidateBirthday);
        imgValidateInvite = findViewById(R.id.imgValidateInvite);

        inputLayoutMail = findViewById(R.id.input_layout_mail);
        inputLayoutNickname = findViewById(R.id.input_layout_nickname);
        inputLayoutMobile = findViewById(R.id.input_layout_mobile);
        inputLayoutBirthday = findViewById(R.id.input_layout_birthday);
        inputLayoutInvite = findViewById(R.id.input_layout_invite);
        focus = findViewById(R.id.focus);


        txtInviteCode = findViewById(R.id.txtInviteCodeSocial);
        chkFemale = findViewById(R.id.chkFemale);
        chkMale = findViewById(R.id.chkMale);
        btnOK = findViewById(R.id.btnOK);

        tvFemale = findViewById(R.id.tvFemale);
        tvMale = findViewById(R.id.tvMale);
        tvBirthday = findViewById(R.id.tvBirthday);
        btnClose = findViewById(R.id.btnClose);
        txtNickname = findViewById(R.id.txtNickname);
        txtMobile = findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtEmail);
        chkFemale.setOnClickListener(this);
        chkMale.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        tvFemale.setOnClickListener(this);
        tvMale.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        Utils.showKeyboard(this);


        //Check Email
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidateMail.setVisibility(View.GONE);
                    if (!isManual) {
                        if (txtEmail.getText().toString().trim().equals("")) {
                            checkDuplicateEmail();
                        } else {
                            isValidateEmail = true;
                        }
                    }
                } else {
                    if (txtEmail.getText() != null && txtEmail.getText().toString().equals("")) {
                        imgValidateMail.setVisibility(View.VISIBLE);
                    }
                    inputLayoutMail.setErrorEnabled(false);
                }
            }
        });

        //Check Nickname
        txtNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidateNickname.setVisibility(View.GONE);
                    checkDuplicateNickname();
                } else {
                    imgValidateNickname.setVisibility(View.VISIBLE);
                    inputLayoutNickname.setErrorEnabled(false);
                }
            }
        });

        //Check Mobile
        txtMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidateMobile.setVisibility(View.GONE);
                    checkDuplicateMobile();
                } else {
                    imgValidateMobile.setVisibility(View.VISIBLE);
                    inputLayoutMobile.setErrorEnabled(false);
                }
            }
        });

        //Check Birthday

        //Check Invite

        initData(appUserDto);
    }

    //Check Email
    private void checkDuplicateEmail() {

        if (txtEmail.getText().toString().equals("")) {
            inputLayoutMail.setError(getString(R.string.please_input_email));
            isValidateEmail = false;
            return;
        }
        if (!UtilityValidate.isEmailValid(txtEmail.getText().toString())) {
            inputLayoutMail.setError(getString(R.string.email_format_not_valid));
            isValidateEmail = false;
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

                        //OK
                        isValidateEmail = true;

                    } else {

                        //Exist
                        isValidateEmail = false;
                        inputLayoutMail.setError(getString(R.string.email_already_exists));
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                isValidateEmail = false;
                Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Check Nickname
    private void checkDuplicateNickname() {
        final String nickName = txtNickname.getText().toString().trim();
        if (nickName.equals("")) {
            inputLayoutNickname.setError(getString(R.string.please_input_nickname));
            isNicknameValid = false;
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("nickName", nickName);

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.checkNickNameInSytem(params, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getOtherInfo().trim().equals("0")) {
                        isNicknameValid = true;
                    } else {

                        isNicknameValid = true;
                        txtNickname.setText(nickName + random());
                        //inputLayoutMail.setError(getString(R.string.nickname_already_exists));
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

    //Check Mobile
    private void checkDuplicateMobile() {
        if (txtMobile.getText().toString().equals("")) {
            inputLayoutMobile.setError(getString(R.string.please_input_mobile));
            isMobileValid = false;
            return;
        }

        String phone = txtMobile.getText().toString();
        DialogUtils.showLoadingProgress(this, false);
        ControllerApi.getmInstance().checkMobileInSystem(this, phone, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = (RestResult) object;
                if (restResult != null) {
                    if (restResult.getResult() == 1) {
                        //Ok
                        isMobileValid = true;

                        if (isSend)
                            sendSms(true);

                    } else if (restResult.getResult() == 0) {
                        //Exist
                        isMobileValid = false;
                        inputLayoutMobile.setError(getString(R.string.mobile_already_exists));
                    }
                } else {
                    isMobileValid = false;
                }
            }
        });
    }

    //Check Birthday
    private void checkBirthday() {

    }

    //Check invite
    private void checkInvite() {

    }

    private void initData(AppUserSocialDto _appUserSocialDto) {
        if (_appUserSocialDto != null) {
            String email = _appUserSocialDto.getEmail();
            if (email != null && !email.equals("")) {
                txtEmail.setText(email);
                txtEmail.setEnabled(false);
            } else {
                txtEmail.setEnabled(true);
            }

            String nickname = _appUserSocialDto.getNickName();
            if (nickname != null && !nickname.equals("")) {
                txtNickname.setText(nickname);
                checkDuplicateNickname();
            }

            String mobile = _appUserSocialDto.getMobile();
            if (mobile != null && !mobile.equals("")) {
                txtMobile.setText(mobile);
                checkDuplicateMobile();
            }


            String birthday = _appUserSocialDto.getBirthday();
            if (birthday != null) {
                Calendar calendar = Calendar.getInstance();
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);
                    calendar.setTimeInMillis(simpleDateFormat.parse(birthday).getTime());
                } catch (Exception e) {
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view), Locale.ENGLISH);

                tvBirthday.setText(simpleDateFormat.format(calendar.getTime()));
            }

            gender = Gender.toType(_appUserSocialDto.getGender());
            setupGenderView();
        }

    }

    private void setupGenderView() {
        if (gender == Gender.Male) {
            chkMale.setImageResource(R.drawable.checkbox_selected);
            chkFemale.setImageResource(R.drawable.checkbox);
        } else if (gender == Gender.Female) {
            chkFemale.setImageResource(R.drawable.checkbox_selected);
            chkMale.setImageResource(R.drawable.checkbox);
        }
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.btnClose:
                logout();
                break;
            case R.id.btnOK:
                if (!isSend) {
                    isSend = true;
                    requestFocus(focus);
                    handleBtnOk();
                } else {
                    DialogVerify.getInstance().show();
                }
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
        }
    }

    private void handleBtnOk() {
        if (!isManual) {
            if (txtEmail.getText().toString().trim().equals("")) {
                checkDuplicateEmail();
            } else {
                isValidateEmail = true;
            }
        } else {
            isValidateEmail = true;
        }
        checkDuplicateNickname();
        //checkDuplicateMobile();
    }

    private void showDialogVerify(String error) {
        Utils.showKeyboard(this);
        DialogVerify.getInstance().create(this, error, new CallbackVerify() {
            @Override
            public void onVerify(String s) {
                if (s != null) {
                    checkVerifyCode(txtMobile.getText().toString(), s);
                    Utils.hideKeyboard(MemberProfileSocialActivity.this);
                } else {
                    //Send sms Again
                    sendSms(false);
                }
            }
        });
    }

    private void sendSms(final boolean isNew) {
        if (isValidateEmail && isNicknameValid && isMobileValid) {

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

                                if (isNew)
                                    showDialogVerify(null);
                                else
                                    Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.txt_7_2_resend_sucessful), Toast.LENGTH_SHORT).show();

                            } else if (restResult.getResult() == 12) {
                                isMobileValid = false;
                                inputLayoutMobile.setError(getString(R.string.number_is_wrong_format));
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
    }

    private void checkVerifyCode(String _phone, final String _verify) {
        ControllerApi.getmInstance().checkVerifyCode(this, _phone, _verify, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                RestResult restResult = (RestResult) object;
                if (restResult != null) {
                    if (restResult.getResult() == 1) {
                        if (isManual) {
                            createNewAppUser(_verify);
                        } else {
                            createNewAppUserViaSocialApp(_verify);
                        }
                    } else {
                        //Not match
                        showDialogVerify(getString(R.string.msg_7_2_verify_code_not_match));
                    }
                }
            }
        });
    }

    private void showDateTimeChoose() {

        Calendar minYear = Calendar.getInstance();
        minYear.set(Calendar.YEAR, 1950);

        Calendar maxYear = Calendar.getInstance();
        maxYear.set(Calendar.YEAR, maxYear.get(Calendar.YEAR) - 18);
        DateTimeDialogUtils.showDatePickerDialog(this, tvBirthday, minYear, maxYear);
    }

    private void createNewAppUserViaSocialApp(String verify) {

        appUserDto.setNickName(txtNickname.getText().toString());
        appUserDto.setGender(gender.getType());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view), Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(tvBirthday.getText().toString());
            calendar.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));
        appUserDto.setEmail(txtEmail.getText().toString());
        appUserDto.setMobile(txtMobile.getText().toString());
        appUserDto.setVerifyCode(verify);
        appUserDto.setInviteCode(txtInviteCode.getText().toString());

        appUserDto.setViewCrmDto(new ViewCrmNotificationDto(
                (long) PreferenceUtils.getSnNotifyCrm(this),
                PreferenceUtils.getTypeCrm(this),
                2));

        DialogUtils.showLoadingProgress(this, false);

        HotelApplication.serviceApi.createNewAppUserViaSocialApp(appUserDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            loginAfterSignupSocail();
                        } else if (restResult.getResult() == 19) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.verify_code_expired), Toast.LENGTH_LONG).show();
                        } else if (restResult.getResult() == 20) {
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

    private void createNewAppUser(String verify) {

        AppUserDto _appUserDto = new AppUserDto();

        _appUserDto.setNickName(txtNickname.getText().toString());
        _appUserDto.setGender(gender.getType());
        _appUserDto.setMobileUserId(HotelApplication.DEVICE_ID);
        _appUserDto.setVerifyCode(verify);
        _appUserDto.setInviteCode(txtInviteCode.getText().toString());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view), Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(tvBirthday.getText().toString());
            calendar.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);
        _appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));

        _appUserDto.setMobile(txtMobile.getText().toString());
        _appUserDto.setUserId(txtEmail.getText().toString());
        _appUserDto.setPassword(appUserDto.getPassword());

        _appUserDto.setViewCrmDto(new ViewCrmNotificationDto(
                (long) PreferenceUtils.getSnNotifyCrm(this),
                PreferenceUtils.getTypeCrm(this),
                2));

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.createNewAppUser(_appUserDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            loginAfterSignup();
                        } else if (restResult.getResult() == 19) {
                            Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.verify_code_expired), Toast.LENGTH_LONG).show();
                        } else if (restResult.getResult() == 20) {
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

    private void loginAfterSignupSocail() {
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

    private void loginAfterSignup() {
        final LoginDto loginDto = new LoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setUserId(appUserDto.getEmail());
        loginDto.setPassword(appUserDto.getPassword());
        loginDto.setCache(false);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.login(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getResult() == 1) {
                        PreferenceUtils.setUserId(MemberProfileSocialActivity.this, loginDto.getUserId());
                        PreferenceUtils.setPassword(MemberProfileSocialActivity.this, loginDto.getPassword());

                        PreferenceUtils.setToken(MemberProfileSocialActivity.this, restResult.getOtherInfo());
                        PreferenceUtils.setLoginType(MemberProfileSocialActivity.this, SignupType.Manual);
                        PreferenceUtils.setAutoLogin(MemberProfileSocialActivity.this, true);
                        updateTokenToServer();
                        getAppUserForm();
                    } else {
                        DialogUtils.showMessageDialog(MemberProfileSocialActivity.this, getString(R.string.cannot_connect_to_server));
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

                        Toast.makeText(MemberProfileSocialActivity.this, getString(R.string.msg_7_2_sign_up_successful), Toast.LENGTH_SHORT).show();

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
        mobileDeviceInput.setDeviceCode(HotelApplication.ID);
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

    private int random() {
        int min = 0;
        int max = 100;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
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
