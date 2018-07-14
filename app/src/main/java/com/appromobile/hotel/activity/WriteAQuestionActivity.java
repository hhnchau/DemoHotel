package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.QAScope;
import com.appromobile.hotel.model.request.CounselingDto;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/5/2016.
 */
public class WriteAQuestionActivity extends BaseActivity {
    private static final int REQUEST_LOGIN_WRITE_QUESTION = 1000;
    private TextViewSFRegular btnSend;
    EditTextSFRegular txtTitle, txtContent;
    ImageView chkPrivate;
    boolean isCheckPrivate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.write_a_question_activity);
        chkPrivate =  findViewById(R.id.chkPrivate);
        btnSend =  findViewById(R.id.btnSend);
        txtTitle =  findViewById(R.id.txtTitle);
        txtContent =  findViewById(R.id.txtContent);
        Utils.showKeyboard(this);

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(WriteAQuestionActivity.this);
                finish();
            }
        });
        chkPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckPrivate = !isCheckPrivate;
                if (isCheckPrivate) {
                    chkPrivate.setImageResource(R.drawable.checkbox_selected);
                } else {
                    chkPrivate.setImageResource(R.drawable.checkbox);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtTitle.getText().toString().trim().equals("") || txtTitle.getText().length() == 0) {
                    Toast.makeText(WriteAQuestionActivity.this, getString(R.string.please_input_title), Toast.LENGTH_LONG).show();
                } else {
                    if (txtContent.getText().toString().trim().equals("") || txtContent.getText().length() == 0) {
                        Toast.makeText(WriteAQuestionActivity.this, getString(R.string.please_input_content), Toast.LENGTH_LONG).show();
                    } else {
                        sendQuestion();
                    }
                }

            }
        });

    }

    private void sendQuestion() {
        CounselingDto counselingDto = new CounselingDto();
        counselingDto.setContent(txtContent.getText().toString());
        counselingDto.setTitle(txtTitle.getText().toString());
        counselingDto.setScope(isCheckPrivate ? QAScope.Private.getType() : QAScope.Public.getType());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.createNewCounseling(counselingDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null && restResult.getResult() == 1) {
                        Toast.makeText(WriteAQuestionActivity.this, getString(R.string.create_question_successful), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(WriteAQuestionActivity.this, getString(R.string.create_question_fail), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(WriteAQuestionActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(WriteAQuestionActivity.this, LoginActivity.class);
                            startActivityForResult(intent, REQUEST_LOGIN_WRITE_QUESTION);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(WriteAQuestionActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN_WRITE_QUESTION) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SWriteAQuestion";
    }
}
