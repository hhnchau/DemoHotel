package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.request.ResetPasswordDto;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.UtilityValidate;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/25/2016.
 */
public class ForgotPasswordActivity extends BaseActivity {
    TextViewSFRegular btnSend, tvMessage;
    ImageView imgValidateEmail;
    EditTextSFRegular txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        }catch (Exception e){}

        setContentView(R.layout.forgot_password_activity);

        btnSend = findViewById(R.id.btnSend);
        tvMessage =  findViewById(R.id.tvMessage);
        txtEmail =  findViewById(R.id.txtEmail);
        imgValidateEmail =  findViewById(R.id.imgValidateEmail);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                if(!UtilityValidate.isEmailValid(txtEmail.getText().toString()))
                {
                    tvMessage.setText(getString(R.string.email_format_not_valid));
                    btnSend.setEnabled(false);
                    imgValidateEmail.setImageResource(R.drawable.cancel);
//                    btnSend.setBackgroundColor(getResources().getColor(R.color.button_color_disable));
                }else{
                    btnSend.setEnabled(true);
//                    btnSend.setBackgroundColor(getResources().getColor(R.color.button_color_enable));
                    tvMessage.setText("");
                    tvMessage.setVisibility(View.GONE);
                    imgValidateEmail.setImageResource(R.drawable.check);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().equals("")) {
                    tvMessage.setText(getString(R.string.please_input_email));
                    return;
                }
                System.out.println("TimeMeasurableBeginApi: " +Calendar.getInstance().getTimeInMillis());

                DialogUtils.showLoadingProgress(ForgotPasswordActivity.this, false);
                HotelApplication.serviceApi.forgetAppUserPassword(new ResetPasswordDto(txtEmail.getText().toString()), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                    @Override
                    public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                        System.out.println("TimeMeasurableFinishedApi: " + Calendar.getInstance().getTimeInMillis());
                        DialogUtils.hideLoadingProgress();
                        RestResult restResult = response.body();
                        if(response.isSuccessful() && restResult != null){

                            if (restResult.getResult()==1){
                                String message=getString(R.string.temporagy_sent);
                                showMessageForgotPasswordDialog(ForgotPasswordActivity.this, message, txtEmail.getText().toString());
                            }else if (restResult.getResult()==11){
//                                System.out.println("Message: "+restResult.getMessage() +" Info: "+restResult.getOtherInfo());
//                                DialogUtils.showMessageDialog(ForgotPasswordActivity.this, restResult.getMessage());
                                tvMessage.setText(getString(R.string.email_not_exist_in_system));
                                tvMessage.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.msg_7_3_can_not_send_email), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResult> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
//                        tvMessage.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void showMessageForgotPasswordDialog(Context context, String message, String email){
        final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_forgot_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
        TextViewSFRegular tvEmail =  dialog.findViewById(R.id.tvEmail);
        tvEmail.setText("Email: "+email);

        tvMessage.setText(message);
        TextViewSFBold btnOK =  dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void setScreenName() {
        this.screenName = "SForgotPassword";
    }
}
