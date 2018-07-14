package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.request.AppUserDto;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/25/2016.
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener {
    EditTextSFRegular txtEmail, txtPassword, txtConfirmPassword;
    TextViewSFBold btnCheckDuplicate;
    ImageView imgValidateConfirmPassword, imgValidatePassword;
    ImageView chkAgreement;
    ImageView btnClose;
    TextViewSFRegular tvMessage, tvDisplayMessage;

    LinearLayout btnGotoAgreement;
    TextView btnNext;
    boolean isValidateEmail = false;
    boolean isPasswordValid = false;
    boolean isConfirmPasswordValid = false;
    boolean isCheckAgreement = false;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.signup_activity);
        txtEmail =  findViewById(R.id.txtEmail);
        txtPassword =  findViewById(R.id.txtPassword);
        txtConfirmPassword =  findViewById(R.id.txtConfirmPassword);
        btnCheckDuplicate =  findViewById(R.id.btnCheckDuplicate);
        imgValidateConfirmPassword =  findViewById(R.id.imgValidateConfirmPassword);
        imgValidatePassword = findViewById(R.id.imgValidatePassword);
        chkAgreement = findViewById(R.id.chkAgreement);
        btnClose = findViewById(R.id.btnClose);
        tvMessage =  findViewById(R.id.tvMessage);
        tvDisplayMessage = findViewById(R.id.tvDisplayMessage);
        btnGotoAgreement =  findViewById(R.id.btnGotoAgreement);
        btnNext =  findViewById(R.id.btnNext);

        btnCheckDuplicate.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnGotoAgreement.setOnClickListener(this);

        Utils.showKeyboard(this);

        chkAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckAgreement = !isCheckAgreement;
                checkFormValidate(isCheckAgreement);
                Utils.hideKeyboard(SignupActivity.this, txtEmail);
                if (isCheckAgreement) {
                    chkAgreement.setImageResource(R.drawable.checkbox_selected);
                } else {
                    chkAgreement.setImageResource(R.drawable.checkbox);
                }
            }
        });


        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isPasswordValid = false;
                String password = txtPassword.getText().toString();
                if (password.length() < 6 || password.length() > 16) {
                    tvMessage.setText(getString(R.string.password_format_message));
                    tvMessage.setVisibility(View.VISIBLE);
                    imgValidatePassword.setImageResource(R.drawable.cancel);
                    tvDisplayMessage.setVisibility(View.GONE);
                    return;
                } else {
                    if (UtilityValidate.isPasswordValid(password)) {
                        tvMessage.setText("");
                        tvMessage.setVisibility(View.GONE);
                        imgValidatePassword.setImageResource(R.drawable.check);
                        isPasswordValid = true;
                        if (!txtConfirmPassword.getText().toString().equals("")) {
                            if (!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
                                tvMessage.setText(getString(R.string.password_and_confirm_not_match));
                                tvMessage.setVisibility(View.VISIBLE);
                                tvDisplayMessage.setVisibility(View.GONE);
//                                btnNext.setEnabled(false);
//                                btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
                                return;
                            }
                        }
                    } else {
                        imgValidatePassword.setImageResource(R.drawable.cancel);
                        tvMessage.setText(getString(R.string.password_format_message));
                        tvMessage.setVisibility(View.VISIBLE);
                        tvDisplayMessage.setVisibility(View.GONE);
//                        btnNext.setEnabled(false);
//                        btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
                        return;
                    }
                }
                checkFormValidate(isCheckAgreement);
            }
        });

        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password = txtPassword.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String confPassword = txtConfirmPassword.getText().toString();
                if (!confPassword.equals(password)) {
                    tvMessage.setText(getString(R.string.password_and_confirm_not_match));
                    tvMessage.setVisibility(View.VISIBLE);
                    imgValidateConfirmPassword.setImageResource(R.drawable.cancel);
                    tvDisplayMessage.setVisibility(View.GONE);
                    return;
                } else {
                    tvMessage.setText("");
                    tvMessage.setVisibility(View.GONE);
                    isConfirmPasswordValid = true;
                    imgValidateConfirmPassword.setImageResource(R.drawable.check);
                }

            }
        });
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidateEmail = false;
                tvDisplayMessage.setText("");
                tvDisplayMessage.setVisibility(View.GONE);
//                btnNext.setEnabled(false);
//                btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
//                checkFormValidate(chkAgreement.isChecked());
            }
        });
    }

    private void checkFormValidate(final boolean isChecked) {
//        btnNext.setEnabled(false);
//        btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
        if (txtEmail.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (!isValidateEmail) {
            tvMessage.setText(getString(R.string.please_duplicate_check_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (txtPassword.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_enter_your_password));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }


        if (!isConfirmPasswordValid && !isPasswordValid) {
            tvMessage.setText(getString(R.string.password_format_not_valid));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        } else if (isPasswordValid && isConfirmPasswordValid) {
            if (!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                tvMessage.setText(getString(R.string.password_and_confirm_not_match));
                tvMessage.setVisibility(View.VISIBLE);
                return;
            }
        }

//        btnNext.post(new Runnable() {
//            @Override
//            public void run() {
//                if(isChecked){
////                    btnNext.setEnabled(true);
////                    btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
//                }else{
////                    btnNext.setEnabled(false);
////                    btnNext.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
//                }
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this, txtEmail);
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnCheckDuplicate:
                checkDuplicateEmail();
                break;
            case R.id.btnNext:
                tvDisplayMessage.setVisibility(View.GONE);
                gotoNextStep();
                break;
            case R.id.btnGotoAgreement:
                gotoServiceAgreement();
                break;
        }
    }

    private void gotoServiceAgreement() {
        Intent intent = new Intent(this, TermPrivacyPolicyActivity.class);
        startActivity(intent);
    }

    private void gotoNextStep() {
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

        if (!isValidateEmail) {
            tvMessage.setText(getString(R.string.please_duplicate_check_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (txtPassword.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_enter_your_password));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (!isPasswordValid) {
            tvMessage.setText(getString(R.string.password_format_message));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        } else {
            if (!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                tvMessage.setText(getString(R.string.password_and_confirm_not_match));
                tvMessage.setVisibility(View.VISIBLE);
                return;
            }
        }

        if (!isCheckAgreement) {
            tvMessage.setText(getString(R.string.please_agree_policy));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(txtEmail.getText().toString());
        appUserDto.setMobileUserId(HotelApplication.DEVICE_ID);
        appUserDto.setPassword(Utils.md5(txtPassword.getText().toString()));
        Intent intent = new Intent(this, MemberProfileActivity.class);
        intent.putExtra("appUserDto", appUserDto);
        startActivity(intent);
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
                        isValidateEmail = true;
                        tvMessage.setText("");
                        tvMessage.setVisibility(View.GONE);
                        tvDisplayMessage.setText(getString(R.string.the_email_is_available));
                        tvDisplayMessage.setVisibility(View.VISIBLE);
                        tvMessage.setVisibility(View.GONE);
//                        Toast.makeText(SignupActivity.this, getString(R.string.the_email_is_available), Toast.LENGTH_LONG).show();
                    } else {
                        isValidateEmail = false;
                        tvMessage.setText(getString(R.string.email_already_exists));
                        tvMessage.setVisibility(View.VISIBLE);
                        tvDisplayMessage.setText("");
                        tvDisplayMessage.setVisibility(View.GONE);
                    }
                }
//                checkFormValidate(isCheckAgreement);
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                isValidateEmail = false;
//                checkFormValidate(isCheckAgreement);
                Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setScreenName() {
        this.screenName = "SLogSignUp";
    }
}
