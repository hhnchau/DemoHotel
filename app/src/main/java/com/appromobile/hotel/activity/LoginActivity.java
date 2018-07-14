package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.appromobile.hotel.model.request.LoginDto;
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
import com.appromobile.hotel.utils.Utils;
import com.crashlytics.android.Crashlytics;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.igaworks.adbrix.IgawAdbrix;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/25/2016.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final int RC_SIGN_IN = 1001;
    private AccessToken mAccessToken;
    private CallbackManager callbackManager;
    private SignupType loginType;
    private GooglePlusInfo googlePlusInfo;
    private EditText txtEmail;
    private EditText txtPassword;
    private TextView tvMessage;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String screenName = "SLogIn";
    private GoogleSignInAccount acct;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

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

    @Override
    protected void onStart() {
        super.onStart();
        deleteLoginDataOnPref();

        if (googleApiClient != null) {
            googleApiClient.connect();
        } else {
            initGoogleApi();
            googleApiClient.connect();
        }


        if (HotelApplication.isRelease) {
            try {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screenName);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                mFirebaseAnalytics.logEvent(screenName, bundle);
                MyLog.writeLog("BaseActivity: " + screenName);
            } catch (Exception e) {
                MyLog.writeLog("mFirebaseAnalytics------------->" + e);
            }

            try {
                IgawAdbrix.retention(screenName);
                IgawAdbrix.firstTimeExperience(screenName);
            } catch (Exception e) {
                MyLog.writeLog("IgawAdbrix------------->" + e);
            }
        }

    }

    @Override
    public void setScreenName() {

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
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("Fabric------------->" + e);
        }

        setContentView(R.layout.login_activity);
        initGoogleApi();
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
//        txtPassword.setText("hZAXi60Ie6?0");
        tvMessage = findViewById(R.id.tvMessage);
        findViewById(R.id.tvFacebook).setOnClickListener(this);
        findViewById(R.id.tvGooglePlus).setOnClickListener(this);
        findViewById(R.id.tvSignup).setOnClickListener(this);
        findViewById(R.id.tvForgotPW).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);


        callbackManager = CallbackManager.Factory.create();
        mAccessToken = AccessToken.getCurrentAccessToken();

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginEmail();
                    handled = true;
                }
                return handled;
            }
        });
    }


    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this, txtEmail);
        switch (v.getId()) {
            case R.id.tvFacebook:
                loginFacebook();
                break;
            case R.id.tvGooglePlus:
                revokeAccessFromGooglePlus();
                break;
            case R.id.btnLogin:
                loginEmail();
                break;
            case R.id.tvSignup:
                signupAccount();
                break;
            case R.id.tvForgotPW:
                forgotPassword();
                break;
            case R.id.btnClose: {
                closeLoginPage();
                break;
            }
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

    private void closeLoginPage() {

        onBackPressed();
        finish();
    }

    private void deleteLoginDataOnPref() {
        PreferenceUtils.setToken(LoginActivity.this, "");
        PreferenceUtils.setPassword(LoginActivity.this, "");
        PreferenceUtils.setUserId(LoginActivity.this, "");
        PreferenceUtils.setPasscode(LoginActivity.this, "");
        PreferenceUtils.setIsPasscode(LoginActivity.this, false);
        PreferenceUtils.setAutoLogin(LoginActivity.this, false);
    }

    private void forgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void signupAccount() {

        //Event Fabric
        if (HotelApplication.isRelease) {
            Answers.getInstance().logSignUp(new SignUpEvent().putMethod("Manual").putSuccess(true));
        }

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
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
//                            Toast.makeText(LoginActivity.this, "Login FB OK", Toast.LENGTH_LONG).show();
                            //goToTodayCard();

                            tryGetFacebookInfo(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
//                            Toast.makeText(LoginActivity.this, "Login FB Cancel", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
//                            Toast.makeText(LoginActivity.this, "Login FB Fail", Toast.LENGTH_LONG).show();
                        }
                    });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

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
                            FacebookInfo facebookInfo = new FacebookInfo(object);
                            if (mAccessToken != null && mAccessToken.getToken() != null) {
                                tryLoginByFacebook(facebookInfo);
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,birthday,gender,email");
        request.setParameters(parameters);
        request.executeAsync();
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
            acct = result.getSignInAccount();
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                String idToken = acct.getIdToken();
                googlePlusInfo = new GooglePlusInfo();
                googlePlusInfo.setEmail(personEmail);
                googlePlusInfo.setId(personId);
                googlePlusInfo.setName(personName);
                googlePlusInfo.setToken(idToken);
                googlePlusInfo.setGender("male");
                googlePlusInfo.setBirthday("1990");
                MyLog.writeLog("idToken: " + idToken);
                tryLoginByGooglePlus(googlePlusInfo);
            }
        }
    }

    private void tryLoginByFacebook(final FacebookInfo facebookInfo) {
        MyLog.writeLog("TimeMeasurableBeginApiLoginFb: " + Calendar.getInstance().getTimeInMillis());
        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setSocialToken(mAccessToken.getToken());
        loginDto.setCache(false);
        loginDto.setViaApp(SignupType.Facebook.getType());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                MyLog.writeLog("TimeMeasurableFinishedApiLoginFb: " + Calendar.getInstance().getTimeInMillis());
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            PreferenceUtils.setLoginType(LoginActivity.this, loginType);
                            MyLog.writeLog("restResult.message: " + restResult.getMessage());
                            MyLog.writeLog("restResult.getOtherInfo: " + restResult.getOtherInfo());
                            PreferenceUtils.setToken(LoginActivity.this, restResult.getOtherInfo());
                            PreferenceUtils.setLoginType(LoginActivity.this, loginType);
                            updateTokenToServer();
                            getAppUserForm();
                        } else if (restResult.getResult() == 11) {
                            Toast.makeText(LoginActivity.this, getString(R.string.account_cannot_used), Toast.LENGTH_LONG).show();
                        } else if (restResult.getResult() == 2) {
                            //Don't have account, signup with facebook
                            signupFacebook(facebookInfo);
//                    } else {
//                        Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                //DialogUtils.showMessageDialog(LoginActivity.this, getString(R.string.incorrect_email_or_password_please_try_again));
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.fb_date_format_view));
        try {
            Date date = simpleDateFormat.parse(facebookInfo.getBirthday());
            calendar.setTimeInMillis(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            calendar.set(Calendar.YEAR, 1990);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request));
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));

        appUserDto.setAddress("");
        appUserDto.setMobile("");
        appUserDto.setViaApp(SignupType.Facebook.getType());

        Intent intent = new Intent(this, MemberProfileSocialActivity.class);
        intent.putExtra("AppUserSocialDto", appUserDto);
        startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);

        //Event Fabric
        if (HotelApplication.isRelease) {
            Answers.getInstance().logSignUp(new SignUpEvent().putMethod("Facebook").putSuccess(true));
        }
    }


    private void signupGooglePlus(final GooglePlusInfo googlePlusInfo) {
        PreferenceUtils.setTokenGInfo(this, googlePlusInfo.getToken());
        Log.d("TOKEN", "--> " + googlePlusInfo.getToken() + " <--");

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.google_date_format_view));
        try {
            Date date = simpleDateFormat.parse(googlePlusInfo.getBirthday());
            calendar.setTimeInMillis(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            calendar.set(Calendar.YEAR, 1990);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_request));
        appUserDto.setBirthday(simpleDateFormat.format(calendar.getTime()));
        appUserDto.setAddress("");
        appUserDto.setMobile("");
        appUserDto.setViaApp(SignupType.GooglePlus.getType());

        Intent intent = new Intent(this, MemberProfileSocialActivity.class);
        intent.putExtra("AppUserSocialDto", appUserDto);
        startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);

        //Event Fabric
        if (HotelApplication.isRelease) {
            Answers.getInstance().logSignUp(new SignUpEvent().putMethod("Google").putSuccess(true));
        }
    }

    private void tryLoginByGooglePlus(final GooglePlusInfo googlePlusInfo) {
        MyLog.writeLog("TimeMeasurableBeginApiLoginFb: " + Calendar.getInstance().getTimeInMillis());
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
                    MyLog.writeLog("TimeMeasurableFinishedApiLoginFb: " + Calendar.getInstance().getTimeInMillis());
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        RestResult restResult = response.body();
                        if (restResult != null) {
                            if (restResult.getResult() == 1) {
                                MyLog.writeLog("restResult.message: " + restResult.getMessage());
                                MyLog.writeLog("restResult.getOtherInfo: " + restResult.getOtherInfo());
                                PreferenceUtils.setToken(LoginActivity.this, restResult.getOtherInfo());
                                MyLog.writeLog("SESSION:------>" + restResult.getOtherInfo());
                                PreferenceUtils.setLoginType(LoginActivity.this, loginType);
                                PreferenceUtils.setTokenGInfo(LoginActivity.this, googlePlusInfo.getToken());
                                updateTokenToServer();
                                getAppUserForm();
                            } else if (restResult.getResult() == 11) {
                                Toast.makeText(LoginActivity.this, getString(R.string.account_cannot_used), Toast.LENGTH_LONG).show();
                            } else if (restResult.getResult() == 2) {
                                //Don't have account, signup with facebook
                                signupGooglePlus(googlePlusInfo);
                            } else if (restResult.getResult() == 12) {
                                MyLog.writeLog("result code = 12");
                                Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    MyLog.writeLog("result code = failure");
                    Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("LoginViaSocial------------->" + e);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        MyLog.writeLog("--> onConnected <--");
        DialogUtils.hideLoadingProgress();
        //revokeAccessFromGooglePlus();


    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    private void loginEmail() {
        loginType = SignupType.Manual;

        if (txtEmail.getText().toString().equals("")) {
//            DialogUtils.showMessageDialog(this, getString(R.string.please_enter_your_email));
            tvMessage.setText(getString(R.string.please_enter_your_email));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }
        if (txtPassword.getText().toString().equals("")) {
//            DialogUtils.showMessageDialog(this, getString(R.string.please_enter_your_password));
            tvMessage.setText(getString(R.string.please_enter_your_password));
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        LoginDto loginDto = new LoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setUserId(txtEmail.getText().toString());
        loginDto.setCache(true);
        loginDto.setPassword(Utils.md5(txtPassword.getText().toString()));

        DialogUtils.showLoadingProgress(this, false);

        HotelApplication.serviceApi.login(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            PreferenceUtils.setLoginType(LoginActivity.this, loginType);
                            PreferenceUtils.setLoginType(LoginActivity.this, loginType);
                            PreferenceUtils.setUserId(LoginActivity.this, txtEmail.getText().toString());
                            PreferenceUtils.setPassword(LoginActivity.this, Utils.md5(txtPassword.getText().toString()));
                            PreferenceUtils.setToken(LoginActivity.this, restResult.getOtherInfo());
                            MyLog.writeLog("SESSION:------>" + restResult.getOtherInfo());
                            updateTokenToServer();
                            getAppUserForm();
                        } else if (restResult.getResult() == 11) {//11
                            Toast.makeText(LoginActivity.this, getString(R.string.account_cannot_used), Toast.LENGTH_LONG).show();
                        } else {
                            tvMessage.setText(getString(R.string.incorrect_email_or_password_please_try_again));
                            tvMessage.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(LoginActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
//                DialogUtils.showMessageDialog(LoginActivity.this, getString(R.string.incorrect_email_or_password_please_try_again));
            }
        });
    }

    public void getAppUserForm() {
        DialogUtils.showLoadingProgress(this, false);
        ControllerApi.getmInstance().findApiSetting(LoginActivity.this, new ResultApi() {
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

        PreferenceUtils.setAutoLogin(LoginActivity.this, true);


        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    AppUserForm appUserForm = response.body();
                    if (appUserForm != null) {
                        PreferenceUtils.setAppUser(LoginActivity.this, appUserForm);
//                        Intent intentResult = new Intent();
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
                    MyLog.writeLog("updateAppUserToken: Fail: " + response.message());
//                    checkAutoLogin();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                MyLog.writeLog("updateAppUserToken: Fail:");
//                checkAutoLogin();
            }
        });
    }
}
