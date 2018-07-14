package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.model.request.LogoutDto;
import com.appromobile.hotel.model.request.UpdatePasswordDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/1/2016.
 */
public class AccountSettingActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOGIN_UPDATE_PASSWORD_REQUEST = 1000;
    private static final int LOGIN_UNREGISTER_REQUEST = 1001;
    EditTextSFRegular txtCurrentPassword, txtPassword, txtConfirmPassword;

    private ToggleButton btnPasscodeLock;
    private LinearLayout btnPasscodeSetting;
    private AppUserForm appUserForm;
    private ToggleButton btnAutoLogin;
    private LinearLayout changePasswordBox, autoLoginBox;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void setScreenName() {
        this.screenName = "SSetAccount";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.account_setting_activity);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestIdToken("170640652110-tc01mpf2ucq65o8dejksatq699ofnfc0.apps.googleusercontent.com")
                .requestEmail().requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        changePasswordBox =  findViewById(R.id.changePasswordBox);
        autoLoginBox =  findViewById(R.id.autoLoginBox);
        appUserForm = getIntent().getParcelableExtra("AppUserForm");
//        imgValidateConfirmPassword = (ImageView) findViewById(R.id.imgValidateConfirmPassword);
//        imgValidatePassword = (ImageView) findViewById(R.id.imgValidatePassword);
        txtCurrentPassword =  findViewById(R.id.txtCurrentPassword);
        btnPasscodeSetting =  findViewById(R.id.btnPasscodeSetting);
        txtPassword =  findViewById(R.id.txtPassword);
        txtConfirmPassword =  findViewById(R.id.txtConfirmPassword);
        btnPasscodeLock =  findViewById(R.id.btnPasscodeLock);
//        tvMessage = (TextView) findViewById(R.id.tvMessage);
        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnUnregister).setOnClickListener(this);
        btnPasscodeSetting.setOnClickListener(this);
        findViewById(R.id.btnChangePassword).setOnClickListener(this);
        btnPasscodeLock.setChecked(PreferenceUtils.getIsPasscode(this));
        btnAutoLogin =  findViewById(R.id.btnAutoLogin);

        btnAutoLogin.setChecked(PreferenceUtils.isAutoLogin(this));

        btnPasscodeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setIsPasscode(AccountSettingActivity.this, isChecked);
                if (isChecked) {
                    if (PreferenceUtils.getPasscode(AccountSettingActivity.this).equals("")) {
                        showPasscodeSetup();
                    }
                }
            }
        });

        btnAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setAutoLogin(AccountSettingActivity.this, isChecked);
            }
        });

        try {
            if (appUserForm.getViaApp() != SignupType.Manual.getType()) {
//            autoLoginBox.setVisibility(View.GONE);
                changePasswordBox.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }

    private String passcode;
    private String rePasscode;

    private void showPasscodeSetup() {
        setButtonName("SSetPass");
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.passcode_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        Utils.showKeyboard(this);
        final ImageView[] imgCode = new ImageView[6];

        final EditText txtPasscode = (EditText) dialog.findViewById(R.id.txtPasscode);
        TextView btnOK =  dialog.findViewById(R.id.btnOK);
        imgCode[0] =  dialog.findViewById(R.id.code1);
        imgCode[1] =  dialog.findViewById(R.id.code2);
        imgCode[2] =  dialog.findViewById(R.id.code3);
        imgCode[3] =  dialog.findViewById(R.id.code4);
        imgCode[4] =  dialog.findViewById(R.id.code5);
        imgCode[5] =  dialog.findViewById(R.id.code6);

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(AccountSettingActivity.this, txtPasscode);
                dialog.dismiss();
                if (PreferenceUtils.getPasscode(AccountSettingActivity.this).equals("")) {
                    btnPasscodeLock.setChecked(false);
                }
            }
        });


        txtPasscode.setTextIsSelectable(false);
        txtPasscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int lengthCode = txtPasscode.getText().toString().length();
                for (int i = 0; i < 6; i++) {
                    if (i < lengthCode) {
                        imgCode[i].setVisibility(View.VISIBLE);
                    } else {
                        imgCode[i].setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtPasscode.getText().toString().equals("")) {
                    if (txtPasscode.getText().toString().length() == 6) {
                        passcode = txtPasscode.getText().toString();
                        dialog.dismiss();
                        showRePasscodeSetup();
                    }
                }
            }
        });
    }

    private void showRePasscodeSetup() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.passcode_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        TextView btnOK =  dialog.findViewById(R.id.btnOK);
        final ImageView[] imgCode = new ImageView[6];
        imgCode[0] =  dialog.findViewById(R.id.code1);
        imgCode[1] =  dialog.findViewById(R.id.code2);
        imgCode[2] =  dialog.findViewById(R.id.code3);
        imgCode[3] =  dialog.findViewById(R.id.code4);
        imgCode[4] =  dialog.findViewById(R.id.code5);
        imgCode[5] =  dialog.findViewById(R.id.code6);
        final EditText txtPasscode =  dialog.findViewById(R.id.txtPasscode);

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(AccountSettingActivity.this, txtPasscode);
                dialog.dismiss();
                if (PreferenceUtils.getPasscode(AccountSettingActivity.this).equals("")) {
                    btnPasscodeLock.setChecked(false);
                }
                passcode = "";

            }
        });

        TextView tvMessagePasscode =  dialog.findViewById(R.id.tvMessage);
        tvMessagePasscode.setText(getString(R.string.enter_your_passcode_one_more_time));

        txtPasscode.setTextIsSelectable(false);
        txtPasscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int lengthCode = txtPasscode.getText().toString().length();
                for (int i = 0; i < 6; i++) {
                    if (i < lengthCode) {
                        imgCode[i].setVisibility(View.VISIBLE);
                    } else {
                        imgCode[i].setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtPasscode.getText().toString().equals("")) {
                    if (txtPasscode.getText().toString().length() == 6) {
                        rePasscode = txtPasscode.getText().toString();
                        if (passcode.equals(rePasscode)) {
                            PreferenceUtils.setPasscode(AccountSettingActivity.this, Utils.md5(passcode + passcode));
                            Utils.hideKeyboard(AccountSettingActivity.this, txtPasscode);
                            dialog.dismiss();
                            btnPasscodeLock.setChecked(true);
                        } else {
                            txtPasscode.setText("");
                            rePasscode = "";
                        }

                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnChangePassword:
                changePassword();
                break;
            case R.id.btnUnregister:
                unregister();
                break;
            case R.id.btnPasscodeSetting:
                showPasscodeSetup();
                break;
        }
    }

    private void unregister() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirm_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextViewSFBold btnYes =  dialog.findViewById(R.id.btnYes);
        TextViewSFBold btnNo =  dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUnregisterAccount();
                dialog.dismiss();
            }
        });
    }

    private void postUnregisterAccount() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobileUserId", HotelApplication.DEVICE_ID);
        HotelApplication.serviceApi.unregisterUser(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    PreferenceUtils.setToken(AccountSettingActivity.this, "");
                    PreferenceUtils.setUserId(AccountSettingActivity.this, "");
                    PreferenceUtils.setPassword(AccountSettingActivity.this, "");
                    PreferenceUtils.setPasscode(AccountSettingActivity.this, "");
                    PreferenceUtils.setAutoLogin(AccountSettingActivity.this, false);
                    Intent intent = new Intent(AccountSettingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                } else {
                    DialogUtils.showExpiredDialog(AccountSettingActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(AccountSettingActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_UNREGISTER_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(AccountSettingActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changePassword() {
        if (txtCurrentPassword.getText().toString().equals("")) {
            Toast.makeText(AccountSettingActivity.this, getString(R.string.please_input_current_password), Toast.LENGTH_LONG).show();
            return;
        }

        if (txtPassword.getText().toString().equals("")) {
            Toast.makeText(AccountSettingActivity.this, getString(R.string.please_enter_new_password), Toast.LENGTH_LONG).show();
            return;
        } else {
            if (txtPassword.getText().toString().length() < 6) {
                Toast.makeText(AccountSettingActivity.this, getString(R.string.password_format_message), Toast.LENGTH_LONG).show();
                return;
            } else {
                if (UtilityValidate.isPasswordValid(txtPassword.getText().toString())) {

                } else {
                    Toast.makeText(AccountSettingActivity.this, getString(R.string.password_format_message), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (txtConfirmPassword.getText().toString().equals("")) {
            Toast.makeText(AccountSettingActivity.this, getString(R.string.please_enter_confirm_password), Toast.LENGTH_LONG).show();
            return;
        } else {
            if (txtConfirmPassword.getText().toString().length() < 6) {
                Toast.makeText(AccountSettingActivity.this, getString(R.string.password_format_message), Toast.LENGTH_LONG).show();
                return;
            } else {
                if (UtilityValidate.isPasswordValid(txtConfirmPassword.getText().toString())) {

                } else {
                    Toast.makeText(AccountSettingActivity.this, getString(R.string.password_format_message), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
            Toast.makeText(AccountSettingActivity.this, getString(R.string.password_and_confirm_not_match), Toast.LENGTH_LONG).show();
            return;
        }

        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setCurrentPassword(Utils.md5(txtCurrentPassword.getText().toString()));
        updatePasswordDto.setNewPassword(Utils.md5(txtPassword.getText().toString()));
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.updatePassword(updatePasswordDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();

                    if (restResult != null && restResult.getResult() == 1) {
                        DialogUtils.showMessageDialog(AccountSettingActivity.this, getString(R.string.your_password_has_changed));
                    } else {
                        DialogUtils.showMessageDialog(AccountSettingActivity.this, getString(R.string.current_password_wrong));
                    }

                } else {
                    DialogUtils.showExpiredDialog(AccountSettingActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(AccountSettingActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_UPDATE_PASSWORD_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void logout() {
        LogoutDto logoutDto = new LogoutDto(HotelApplication.DEVICE_ID);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.logout(logoutDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                SignupType type = PreferenceUtils.getLoginType(AccountSettingActivity.this);
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
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
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

    public void deleteDataOnPref() {
        PreferenceUtils.setToken(AccountSettingActivity.this, "");
        PreferenceUtils.setPassword(AccountSettingActivity.this, "");
        PreferenceUtils.setUserId(AccountSettingActivity.this, "");
        PreferenceUtils.setPasscode(AccountSettingActivity.this, "");
        PreferenceUtils.setIsPasscode(AccountSettingActivity.this, false);
        PreferenceUtils.setAutoLogin(AccountSettingActivity.this, false);
        Intent intent = new Intent(AccountSettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_UPDATE_PASSWORD_REQUEST) {
            if (resultCode == RESULT_OK) {
                changePassword();
            }
        } else if (requestCode == LOGIN_UNREGISTER_REQUEST) {
            unregister();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
