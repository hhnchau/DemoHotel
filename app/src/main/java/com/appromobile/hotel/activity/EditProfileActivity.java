package com.appromobile.hotel.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
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
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFMedium;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class EditProfileActivity extends BaseActivity implements View.OnClickListener{
    private static final int REQUEST_LOGIN_UPDATE_PROFILE = 1000;
    private AppUserForm appUserForm;
    private TextViewSFRegular tvFemale, tvMale, tvBirthday, btnOK, tvMessage;
    private ImageView imgValidateEmail,btnCheckDuplicateNickName;
    private EditTextSFRegular txtNickname, txtAddress, txtMobile, txtEmail, txtVerifyCode;
    private ImageView btnClose;
    boolean isNicknameValid = false;
//    boolean isEmailValid =false;
    private Gender gender;
    ImageView chkFemale, chkMale;
    TextViewSFMedium btnGetCode;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenName();

        try {
            Fabric.with(this, new Crashlytics());
        }catch (Exception e){}

        setContentView(R.layout.edit_profile_activity);
        appUserForm = getIntent().getParcelableExtra("AppUserForm");

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                System.out.println("KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {

        }
        btnGetCode =  findViewById(R.id.btnGetCode);
        txtVerifyCode = findViewById(R.id.txtVerifyCode);
        chkFemale =  findViewById(R.id.chkFemale);
        chkMale =  findViewById(R.id.chkMale);
        btnOK = findViewById(R.id.btnOK);
        imgValidateEmail =  findViewById(R.id.imgValidateEmail);
        btnCheckDuplicateNickName =  findViewById(R.id.btnCheckDuplicateNickName);
        tvFemale =  findViewById(R.id.tvFemale);
        tvMale =  findViewById(R.id.tvMale);
        tvBirthday =  findViewById(R.id.tvBirthday);
        tvBirthday.setText(getString(R.string.default_birthday));
        btnClose =  findViewById(R.id.btnClose);
        tvMessage =  findViewById(R.id.tvMessage);
        txtNickname =  findViewById(R.id.txtNickname);
        txtAddress =  findViewById(R.id.txtAddress);
        txtMobile =  findViewById(R.id.txtMobile);
        txtEmail =  findViewById(R.id.txtEmail);

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
                if(!txtNickname.getText().toString().equals(appUserForm.getNickName()))
                {
                    btnCheckDuplicateNickName.setVisibility(View.VISIBLE);
                    isNicknameValid = false;
                }else{
                    btnCheckDuplicateNickName.setVisibility(View.GONE);
                    isNicknameValid = true;
                }
//                btnOK.setEnabled(false);
//                btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
            }
        });

        txtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtMobile.getText().toString().equals(appUserForm.getMobile())){
                    btnGetCode.setVisibility(View.GONE);
                    findViewById(R.id.boxVerifyCode).setVisibility(View.GONE);
                }else{
                    btnGetCode.setVisibility(View.VISIBLE);
                    findViewById(R.id.boxVerifyCode).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                if(!txtEmail.getText().toString().equals("")) {
                    if (!UtilityValidate.isEmailValid(txtEmail.getText().toString())) {
                        tvMessage.setText(getString(R.string.email_format_not_valid));
                        tvMessage.setVisibility(View.VISIBLE);
                        imgValidateEmail.setImageResource(R.drawable.cancel);
                    } else {
                        tvMessage.setVisibility(View.GONE);
                        imgValidateEmail.setImageResource(R.drawable.check);
                    }
                }else{
                    imgValidateEmail.setImageResource(R.drawable.cancel);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initData();
    }

    private void initData() {
        txtNickname.setText(appUserForm.getNickName());
        txtEmail.setText(appUserForm.getEmail());
        if(!txtEmail.getText().toString().equals("")){
            txtEmail.setEnabled(false);
//            isEmailValid = true;
            txtNickname.requestFocus();
        }else{
            txtEmail.requestFocus();
        }
        Calendar calendar =Calendar.getInstance();
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request));
            calendar.setTimeInMillis(simpleDateFormat.parse(appUserForm.getBirthday()).getTime());
        }catch (Exception e){}
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_view));
        tvBirthday.setText(simpleDateFormat.format(calendar.getTime()));
        gender = Gender.toType(appUserForm.getGender());
        txtAddress.setText(appUserForm.getAddress());
        txtMobile.setText(appUserForm.getMobile());

        setupGenderView();
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
        switch (v.getId()){
            case R.id.btnClose:
                finish();
                break;
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
        if(txtMobile.getText().toString().equals("")){
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
                RestResult restResult = response.body();

                if(response.isSuccessful() && restResult != null){

                    if(restResult.getResult()==1){
                        btnGetCode.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this, getString(R.string.verify_code_button, HotelApplication.apiSettingForm.getTimeIntervalGetCode()), Toast.LENGTH_LONG).show();
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
                        },1000*HotelApplication.apiSettingForm.getTimeIntervalGetCode());

                    } else if(restResult.getResult()==12){
                        Toast.makeText(EditProfileActivity.this, getString(R.string.number_is_wrong_format), Toast.LENGTH_LONG).show();
                    }else if(restResult.getResult()==13){
                        Toast.makeText(EditProfileActivity.this, getString(R.string.server_cannot_send_sms), Toast.LENGTH_LONG).show();
                    }
                }else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(EditProfileActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                            startActivityForResult(intent, REQUEST_LOGIN_UPDATE_PROFILE);
                        }
                    });
                }else{
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

//    private void checkDuplicateEmail() {
//        if(txtEmail.getText().toString().equals("")){
//            tvMessage.setText(getString(R.string.please_input_email));
//            tvMessage.setVisibility(View.VISIBLE);
//            return;
//        }
//        if(!UtilityValidate.isEmailValid(txtEmail.getText().toString()))
//        {
//            tvMessage.setText(getString(R.string.email_format_not_valid));
//            tvMessage.setVisibility(View.VISIBLE);
//            return;
//        }
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("userId", txtEmail.getText().toString());
//
//        DialogUtils.showLoadingProgress(this, false);
//        HotelApplication.serviceApi.checkUserIdInSytem(params).enqueue(new Callback<RestResult>() {
//            @Override
//            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
//                DialogUtils.hideLoadingProgress();
//                if(response.isSuccessful()){
//                    RestResult restResult = response.body();
//                    if (restResult.getOtherInfo().trim().equals("0")) {
////                        isEmailValid = true;
//                        tvMessage.setText("");
//                        tvMessage.setVisibility(View.GONE);
//                        Toast.makeText(EditProfileActivity.this, getString(R.string.the_email_is_available), Toast.LENGTH_LONG).show();
//                    }else{
////                        isEmailValid = false;
//                        tvMessage.setVisibility(View.VISIBLE);
//                        tvMessage.setText(getString(R.string.email_already_exists));
//                    }
//                }
//                checkFormValidate();
//            }
//
//
//
//            @Override
//            public void onFailure(Call<RestResult> call, Throwable t) {
//                DialogUtils.hideLoadingProgress();
////                isEmailValid = false;
//                checkFormValidate();
//                Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void checkFormValidate() {
        if(isNicknameValid) {
//            btnOK.setEnabled(true);
//            btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
        }else {
//            btnOK.setEnabled(false);
//            btnOK.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
        }
    }

    private void showDateTimeChoose() {

        Calendar minYear = Calendar.getInstance();
        minYear.set(Calendar.YEAR, 1950);

        Calendar maxYear = Calendar.getInstance();
        maxYear.set(Calendar.YEAR, maxYear.get(Calendar.YEAR)-18);
        DateTimeDialogUtils.showDatePickerDialog(this, tvBirthday, minYear, maxYear);
    }

    private void checkDuplicateNickname() {
        if(txtNickname.getText().toString().equals("")){
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
                if(response.isSuccessful()){
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getOtherInfo().trim().equals("0")) {
                        isNicknameValid = true;
                        tvMessage.setText("");
                        tvMessage.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this, getString(R.string.the_nickname_is_available), Toast.LENGTH_LONG).show();
                        checkFormValidate();
                    }else{
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
                Toast.makeText(EditProfileActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendSignup() {
        if(txtEmail.getText().toString().equals("")){
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

        if(txtNickname.getText().toString().equals("")){
            tvMessage.setText(getString(R.string.please_input_nickname));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if(!isNicknameValid){
            tvMessage.setText(getString(R.string.please_check_nickname_first));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if(tvBirthday.getText().toString().equals("")){
            tvMessage.setText(getString(R.string.please_choose_birthday));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if(txtMobile.getText().toString().equals("")) {
            tvMessage.setText(getString(R.string.please_input_phone_number));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        if(txtVerifyCode.getText().toString().equals("")){
            if(!txtMobile.getText().toString().equals(appUserForm.getMobile())) {
                tvMessage.setText(getString(R.string.please_input_verify_code));
                tvMessage.setVisibility(View.VISIBLE);
                return;
            }
        }
        UpdateAppUserDto appUserDto = new UpdateAppUserDto();

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

        DialogUtils.showLoadingProgress(this, false);

        HotelApplication.serviceApi.updateAppUser(appUserDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if(response.isSuccessful() && restResult != null){

                    if(restResult.getResult()==1){
                        getAppUserForm();
                    }else if(restResult.getResult()==12){
                        Toast.makeText(EditProfileActivity.this, getString(R.string.nickname_already_exists), Toast.LENGTH_LONG).show();
                    } else if(restResult.getResult()==19){
                        Toast.makeText(EditProfileActivity.this, getString(R.string.verify_code_expired), Toast.LENGTH_LONG).show();
                    }else if(restResult.getResult()==20){
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
//                        Intent intentResult = new Intent();
//                        setResult(Activity.RESULT_OK, intentResult);
//                        finish();

                        /*Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
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
    protected void onDestroy() {
        super.onDestroy();
        try{
            timer.cancel();
        }catch (Exception e){}
    }

    @Override
    public void setScreenName() {
        this.screenName ="SSetProfile";
    }
}
