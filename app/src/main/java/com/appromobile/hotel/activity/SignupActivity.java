package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.FacebookInfo;
import com.appromobile.hotel.model.view.GooglePlusInfo;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.utils.Utils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/25/2016.
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private EditText txtEmail, txtPassword, txtConfirmPassword, edtfocus;
    private ImageView imgValidateMail, imgValidateConfirmPassword, imgValidatePassword;
    private ImageView chkAgreement;
    private View lineAgree;
    private TextView tvAgree;
    private ImageView btnClose;

    private LinearLayout btnGotoAgreement;
    private TextView btnNext;
    private boolean isValidateEmail = false;
    private boolean isPasswordValid = false;
    private boolean isConfirmPasswordValid = false;
    private boolean isCheckAgreement = false;

    private TextInputLayout inputLayoutMail, inputLayoutPassword, inputLayoutCofirm;

    private TextView tvSignUpFacebook, tvSignUpGooglePlus;

    private SignupType loginType;
    private AccessToken mAccessToken;
    private CallbackManager callbackManager;

    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            initGoogleApi();
            googleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        //Don't show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.signup_activity);

        callbackManager = CallbackManager.Factory.create();
        mAccessToken = AccessToken.getCurrentAccessToken();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        imgValidateMail = findViewById(R.id.imgValidateEmail);
        imgValidateMail.setOnClickListener(this);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        imgValidateConfirmPassword = findViewById(R.id.imgValidateConfirmPassword);
        imgValidateConfirmPassword.setOnClickListener(this);
        imgValidatePassword = findViewById(R.id.imgValidatePassword);
        imgValidatePassword.setOnClickListener(this);
        chkAgreement = findViewById(R.id.chkAgreement);
        chkAgreement.setOnClickListener(this);
        btnClose = findViewById(R.id.btnClose);
        btnGotoAgreement = findViewById(R.id.btnGotoAgreement);
        btnNext = findViewById(R.id.btnNext);
        btnClose.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnGotoAgreement.setOnClickListener(this);
        inputLayoutMail = findViewById(R.id.input_layout_mail);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputLayoutCofirm = findViewById(R.id.input_layout_confirm);
        lineAgree = findViewById(R.id.lineAgree);
        tvAgree = findViewById(R.id.tvAgree);
        edtfocus = findViewById(R.id.focus);
        tvSignUpFacebook = findViewById(R.id.tvSignUpFacebook);
        tvSignUpGooglePlus = findViewById(R.id.tvSignUpGooglePlus);

        String action = getIntent().getAction();
        if (action != null && action.equals("Introduce")) {
            findViewById(R.id.boxSignUpWith).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.boxSignUpWith).setVisibility(View.GONE);
        }

        Utils.hideKeyboard(this, edtfocus);

        lineAgree.setBackgroundColor(getResources().getColor(R.color.org));
        tvAgree.setVisibility(View.VISIBLE);

        //Password Check
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidatePassword.setVisibility(View.GONE);
                    checkFormatPassword();
                } else {
                    imgValidatePassword.setVisibility(View.VISIBLE);
                    inputLayoutPassword.setErrorEnabled(false);
                }
            }
        });

        //Password Check
        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidateConfirmPassword.setVisibility(View.GONE);
                    checkFormatConfirmPassword();
                } else {
                    imgValidateConfirmPassword.setVisibility(View.VISIBLE);
                    inputLayoutCofirm.setErrorEnabled(false);
                }
            }
        });


        //Check Email
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imgValidateMail.setVisibility(View.GONE);
                    checkDuplicateEmail();
                } else {
                    imgValidateMail.setVisibility(View.VISIBLE);
                    inputLayoutMail.setErrorEnabled(false);
                }
            }
        });

        //SignUp Facebook
        tvSignUpFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFacebook();
            }
        });

        //SignUp Google
        tvSignUpGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccessFromGooglePlus();
            }
        });
    }

    public void loginFacebook() {
        loginType = SignupType.Facebook;
        if (mAccessToken != null && !mAccessToken.isExpired()) {
            MyLog.writeLog("facebook.getAccessToken().getSocialToken(): " + mAccessToken.getToken());
            tryGetFacebookInfo(mAccessToken);
        } else {
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(com.facebook.login.LoginResult loginResult) {
                            MyLog.writeLog("facebook.getAccessToken().getSocialToken(): " + loginResult.getAccessToken().getToken());
                            AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                            mAccessToken = AccessToken.getCurrentAccessToken();

                            tryGetFacebookInfo(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {
                        }
                    });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));

        }
    }

    private void tryGetFacebookInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        if (object != null) {
                            Gson gson = new Gson();
                            FacebookInfo facebookInfo = gson.fromJson(object.toString(), FacebookInfo.class);

                            if (mAccessToken != null && mAccessToken.getToken() != null) {
                                tryLoginByFacebook(facebookInfo);
                            } else {
                                Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, birthday, gender, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void tryLoginByFacebook(final FacebookInfo facebookInfo) {
        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setSocialToken(mAccessToken.getToken());
        loginDto.setCache(false);
        loginDto.setViaApp(SignupType.Facebook.getType());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            PreferenceUtils.setLoginType(SignupActivity.this, loginType);
                            PreferenceUtils.setToken(SignupActivity.this, restResult.getOtherInfo());
                            PreferenceUtils.setLoginType(SignupActivity.this, loginType);
                            updateTokenToServer();
                            getAppUserForm();
                        } else if (restResult.getResult() == 11) {
                            Toast.makeText(SignupActivity.this, getString(R.string.account_cannot_used), Toast.LENGTH_LONG).show();
                        } else if (restResult.getResult() == 2) {
                            //Don't have account, signup with facebook
                            signupFacebook(facebookInfo);
                        } else {
                            Toast.makeText(SignupActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signupFacebook(final FacebookInfo facebookInfo) {
        AppUserSocialDto appUserDto = new AppUserSocialDto();
        appUserDto.setMobileUserId(HotelApplication.DEVICE_ID);
        appUserDto.setEmail(facebookInfo.getEmail());
        appUserDto.setToken(mAccessToken.getToken());
        appUserDto.setNickName(facebookInfo.getName());
        appUserDto.setViaApp(SignupType.Facebook.getType());
        Gender gender = Gender.Male;
        if (facebookInfo.getGender() != null) {
            if (!facebookInfo.getGender().equals("male")) {
                gender = Gender.Female;
            }
        }
        appUserDto.setGender(gender.getType());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.fb_date_format_view), Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(facebookInfo.getBirthday());
            calendar.setTimeInMillis(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            calendar.set(Calendar.YEAR, 1990);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));

        appUserDto.setAddress("");
        appUserDto.setMobile("");
        appUserDto.setViaApp(SignupType.Facebook.getType());

        Intent intent = new Intent(this, MemberProfileSocialActivity.class);
        intent.setAction("social");
        intent.putExtra("AppUserSocialDto", appUserDto);
        startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);

        //Event Fabric
        if (HotelApplication.isRelease) {
            Answers.getInstance().logSignUp(new SignUpEvent().putMethod("Facebook").putSuccess(true));
        }
    }

    private void revokeAccessFromGooglePlus() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        loginGooglePlus();
                    }
                });
            }
        }
    }

    private void initGoogleApi() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestIdToken("170640652110-tc01mpf2ucq65o8dejksatq699ofnfc0.apps.googleusercontent.com")
                .requestEmail().requestProfile()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        googleApiClient.registerConnectionFailedListener(this);
        googleApiClient.registerConnectionCallbacks(this);
    }

    private void loginGooglePlus() {

        //Clear Account Google +
        revokeAccessFromGooglePlus();


        loginType = SignupType.GooglePlus;
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            initGoogleApi();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

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
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
            }
        });
    }

    public void getAppUserForm() {
        DialogUtils.showLoadingProgress(this, false);
        ControllerApi.getmInstance().findApiSetting(SignupActivity.this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                if (object != null) {
                    ApiSettingForm form = (ApiSettingForm) object;
                    PreferenceUtils.setReadStatusPolicy(getApplicationContext(), form.isReadAgreementPolicy());
                }
            }
        });

        /*
        *Set Auto Login
        */

        PreferenceUtils.setAutoLogin(SignupActivity.this, true);

        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    AppUserForm appUserForm = response.body();
                    if (appUserForm != null) {

                        if (IntroduceActivity.introduceActivity != null)
                            IntroduceActivity.introduceActivity.finish();

                        //Change Language
                        String language = "en";
                        if (appUserForm.getLanguage() == 3)
                            language = "vi";
                        Locale locale = new Locale(language);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                        PreferenceUtils.setAppUser(SignupActivity.this, appUserForm);
                        setResult(Activity.RESULT_OK);
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
    public void onClick(View v) {
        Utils.hideKeyboard(this, txtEmail);
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnNext:
                requestFocus(edtfocus);
                gotoNextStep();
                break;
            case R.id.btnGotoAgreement:
                gotoServiceAgreement();
                break;
            case R.id.chkAgreement:
                requestFocus(edtfocus);
                Utils.hideKeyboard(SignupActivity.this, txtEmail);
                if (!isCheckAgreement) {
                    isCheckAgreement = true;
                    chkAgreement.setImageResource(R.drawable.checkbox_selected);
                    lineAgree.setBackgroundColor(getResources().getColor(R.color.lg));
                    tvAgree.setVisibility(View.GONE);
                } else {
                    isCheckAgreement = false;
                    chkAgreement.setImageResource(R.drawable.checkbox);
                    lineAgree.setBackgroundColor(getResources().getColor(R.color.org));
                    tvAgree.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.imgValidateEmail:
                txtEmail.setText(null);
                break;
            case R.id.imgValidatePassword:
                txtPassword.setText(null);
                break;
            case R.id.imgValidateConfirmPassword:
                txtConfirmPassword.setText(null);
                break;
        }
    }

    private void gotoServiceAgreement() {
        Intent intent = new Intent(this, TermPrivacyPolicyActivity.class);
        startActivity(intent);
    }

    private void gotoNextStep() {
        checkDuplicateEmail();
        checkFormatPassword();
        checkFormatConfirmPassword();
        checkAgreement();

        if (isValidateEmail && isConfirmPasswordValid && isPasswordValid && isCheckAgreement) {

            gotoMemberProfileActivity();

        }

    }

    private void gotoMemberProfileActivity() {
        AppUserSocialDto appUserDto = new AppUserSocialDto();
        appUserDto.setEmail(txtEmail.getText().toString());
        appUserDto.setNickName(getNickname(txtEmail.getText().toString()));
        appUserDto.setMobileUserId(HotelApplication.DEVICE_ID);
        appUserDto.setPassword(Utils.md5(txtPassword.getText().toString()));
        Intent intent = new Intent(this, MemberProfileSocialActivity.class);
        intent.setAction("manual");
        intent.putExtra("AppUserSocialDto", appUserDto);
        startActivity(intent);
    }

    private String getNickname(String email) {

        try {
            String[] dataParse = email.split("@");
            if (dataParse.length > 1) {

                return dataParse[0];

            }

        } catch (Exception e) {

        }
        return "";
    }

    private void checkAgreement() {
        if (!isCheckAgreement) {
            lineAgree.setBackgroundColor(getResources().getColor(R.color.org));
            tvAgree.setVisibility(View.VISIBLE);
        }
    }

    //Check Format Confirm Password
    private void checkFormatConfirmPassword() {
        isConfirmPasswordValid = false;
        String confPassword = txtConfirmPassword.getText().toString();
        if (!confPassword.equals(txtPassword.getText().toString().trim()) || confPassword.equals("")) {
            //Fail
            inputLayoutCofirm.setError(getString(R.string.password_and_confirm_not_match));
        } else {
            //OK
            isConfirmPasswordValid = true;
        }
    }

    //Check Format Password
    private void checkFormatPassword() {
        isPasswordValid = false;
        String password = txtPassword.getText().toString();
        //Fail
        if (password.length() < 6 || password.length() > 16) {
            inputLayoutPassword.setError(getString(R.string.password_format_message));
        } else {
            //OK
            if (UtilityValidate.isPasswordValid(password)) {
                isPasswordValid = true;
            } else {
                inputLayoutPassword.setError(getString(R.string.password_format_message));
            }
        }
    }

    //Check Duplicate Email
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
                Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SLogSignUp";
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DialogUtils.hideLoadingProgress();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginType == SignupType.Facebook) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (loginType == SignupType.GooglePlus) {
            if (requestCode == RC_SIGN_IN) {
                if (data != null) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleGoogleSignInResult(result);
                }
            }

        } else if (loginType == SignupType.Manual) {
            setResult(resultCode, data);
            finish();
        }
    }


    private void handleGoogleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                String idToken = acct.getIdToken();

                GooglePlusInfo googlePlusInfo = new GooglePlusInfo();
                googlePlusInfo.setEmail(personEmail);
                googlePlusInfo.setId(personId);
                googlePlusInfo.setName(personName);
                googlePlusInfo.setToken(idToken);
                googlePlusInfo.setGender("");
                googlePlusInfo.setBirthday("");
                tryLoginByGooglePlus(googlePlusInfo);
            }
        }
    }

    private void tryLoginByGooglePlus(final GooglePlusInfo googlePlusInfo) {
        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setSocialToken(googlePlusInfo.getToken());
        loginDto.setViaApp(SignupType.GooglePlus.getType());

        MyLog.writeLog("--> " + googlePlusInfo.getToken() + " <--");

        DialogUtils.showLoadingProgress(this, false);
        try {
            HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                @Override
                public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        RestResult restResult = response.body();
                        if (restResult != null) {
                            if (restResult.getResult() == 1) {
                                PreferenceUtils.setToken(SignupActivity.this, restResult.getOtherInfo());
                                PreferenceUtils.setLoginType(SignupActivity.this, loginType);
                                PreferenceUtils.setTokenGInfo(SignupActivity.this, googlePlusInfo.getToken());
                                updateTokenToServer();
                                getAppUserForm();
                            } else if (restResult.getResult() == 11) {
                                Toast.makeText(SignupActivity.this, getString(R.string.account_cannot_used), Toast.LENGTH_LONG).show();
                            } else if (restResult.getResult() == 2) {
                                //Don't have account, signup with facebook
                                signupGooglePlus(googlePlusInfo);
                            } else if (restResult.getResult() == 12) {
                                Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignupActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    Toast.makeText(SignupActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signupGooglePlus(final GooglePlusInfo googlePlusInfo) {
        PreferenceUtils.setTokenGInfo(this, googlePlusInfo.getToken());

        AppUserSocialDto appUserDto = new AppUserSocialDto();
        appUserDto.setMobileUserId(HotelApplication.DEVICE_ID);
        appUserDto.setToken(googlePlusInfo.getToken());
        appUserDto.setEmail(googlePlusInfo.getEmail());
        appUserDto.setNickName(googlePlusInfo.getName());
        Gender gender = Gender.Male;
        try {
            if (!googlePlusInfo.getGender().equals("male")) {
                gender = Gender.Female;
            }
        } catch (Exception e) {
            MyLog.writeLog("signupGooglePlus--------------------->" + e);
        }
        appUserDto.setGender(gender.getType());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.google_date_format_view), Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(googlePlusInfo.getBirthday());
            calendar.setTimeInMillis(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            calendar.set(Calendar.YEAR, 1990);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));
        appUserDto.setAddress("");
        appUserDto.setMobile("");
        appUserDto.setViaApp(SignupType.GooglePlus.getType());

        Intent intent = new Intent(this, MemberProfileSocialActivity.class);
        intent.setAction("social");
        intent.putExtra("AppUserSocialDto", appUserDto);
        startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);

        //Event Fabric
        if (HotelApplication.isRelease) {
            Answers.getInstance().logSignUp(new SignUpEvent().putMethod("Google").putSuccess(true));
        }
    }

}
