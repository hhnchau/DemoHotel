package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackVerify;
import com.appromobile.hotel.dialog.DialogVerify;
import com.appromobile.hotel.enums.Gender;
import com.appromobile.hotel.model.request.SendSmsDto;
import com.appromobile.hotel.model.request.UpdateAppUserDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DateTimeDialogUtils;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;

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
public class EditProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_LOGIN_UPDATE_PROFILE = 1000;
    private AppUserForm appUserForm;

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
    private String  nickname;
    private String mobile;

    private boolean isSend = false;
    private boolean isOk;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenName();

        setContentView(R.layout.edit_profile_activity);

        appUserForm = getIntent().getParcelableExtra("AppUserForm");


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
                        if (txtEmail.getText().toString().trim().equals("")) {
                            checkDuplicateEmail();
                        }else {
                            isValidateEmail = true;
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
                    isOk = false;
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
                    isOk = false;
                    checkDuplicateMobile();
                } else {
                    imgValidateMobile.setVisibility(View.VISIBLE);
                    inputLayoutMobile.setErrorEnabled(false);
                }
            }
        });

        //Check Birthday

        //Check Invite

        initData(appUserForm);
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
                Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
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
                        if (isOk){
                            updateUser("");
                        }
                    } else {

                        isNicknameValid = true;
                        txtNickname.setText(nickName + random());
                        if (isOk){
                            inputLayoutNickname.setError(getString(R.string.nickname_already_exists));
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                isNicknameValid = false;
                Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
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

    private int random() {
        int min = 0;
        int max = 100;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    private void sendSms(final boolean isNew) {
        if (isMobileValid) {
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
                                    Toast.makeText(EditProfileActivity.this, getString(R.string.txt_7_2_resend_sucessful), Toast.LENGTH_SHORT).show();

                            } else if (restResult.getResult() == 12) {
                                isMobileValid = false;
                                inputLayoutMobile.setError(getString(R.string.number_is_wrong_format));
                                //Toast.makeText(EditProfileActivity.this, getString(R.string.number_is_wrong_format), Toast.LENGTH_LONG).show();
                            } else if (restResult.getResult() == 13) {
                                Toast.makeText(EditProfileActivity.this, getString(R.string.server_cannot_send_sms), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showDialogVerify(String error) {
        DialogVerify.getInstance().create(this, error, new CallbackVerify() {
            @Override
            public void onVerify(String s) {
                if (s != null) {
                    checkVerifyCode(txtMobile.getText().toString(), s);
                }
            }
        });
    }

    private void checkVerifyCode(String _phone, final String _verify){
        ControllerApi.getmInstance().checkVerifyCode(this, _phone, _verify, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                RestResult restResult = (RestResult) object;
                if (restResult != null){
                    if (restResult.getResult() == 1){
                        updateUser(_verify);
                    }else {
                        //Not match
                        showDialogVerify(getString(R.string.msg_7_2_verify_code_not_match));
                    }
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

    private void initData(AppUserForm _appUserForm) {
        if (_appUserForm != null) {
            String email = _appUserForm.getEmail();
            if (email != null && !email.equals("")) {
                txtEmail.setText(email);
                txtEmail.setEnabled(false);
            } else {
                txtEmail.setEnabled(true);
            }

            nickname = _appUserForm.getNickName();
            if (nickname != null && !nickname.equals("")) {
                txtNickname.setText(nickname);
            }

            mobile = _appUserForm.getMobile();
            if (mobile != null && !mobile.equals("")) {
                txtMobile.setText(mobile);
            }


            String birthday = _appUserForm.getBirthday();
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

            gender = Gender.toType(_appUserForm.getGender());
            setupGenderView();
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
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnOK:
                if (!isSend) {
                    isSend = true;
                    handleBtnOk();
                }else {
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

    private void showDateTimeChoose() {

        Calendar minYear = Calendar.getInstance();
        minYear.set(Calendar.YEAR, 1950);

        Calendar maxYear = Calendar.getInstance();
        maxYear.set(Calendar.YEAR, maxYear.get(Calendar.YEAR) - 18);
        DateTimeDialogUtils.showDatePickerDialog(this, tvBirthday, minYear, maxYear);
    }

    private void handleBtnOk(){
        isOk = true;
        if (nickname != null && !nickname.equals("") && !nickname.equals(txtNickname.getText().toString().trim())){
            checkDuplicateNickname();
        }else if(mobile != null && !mobile.equals("") && !mobile.equals(txtMobile.getText().toString().trim())){
            checkDuplicateMobile();
        }else {
            updateUser("");
        }
    }

    private void updateUser(String verify) {

        UpdateAppUserDto appUserDto = new UpdateAppUserDto();

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
        appUserDto.setNickName(txtNickname.getText().toString());
        appUserDto.setMobile(txtMobile.getText().toString());
        appUserDto.setVerifyCode(verify);

        DialogUtils.showLoadingProgress(this, false);

        HotelApplication.serviceApi.updateAppUser(appUserDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {

                    if (restResult.getResult() == 1) {
                        getAppUserForm();
                    } else if (restResult.getResult() == 12) {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.nickname_already_exists), Toast.LENGTH_LONG).show();
                    } else if (restResult.getResult() == 19) {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.verify_code_expired), Toast.LENGTH_LONG).show();
                    } else if (restResult.getResult() == 20) {
                        Toast.makeText(EditProfileActivity.this, getString(R.string.verify_code_not_match), Toast.LENGTH_LONG).show();
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

    public void getAppUserForm() {
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    AppUserForm appUserForm = response.body();
                    if (appUserForm != null) {
                        PreferenceUtils.setAppUser(EditProfileActivity.this, appUserForm);
                        setResult(ParamConstants.REQUEST_CHANGE_PROFILE);
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

    @Override
    public void setScreenName() {
        this.screenName = "SSetProfile";
    }
}
