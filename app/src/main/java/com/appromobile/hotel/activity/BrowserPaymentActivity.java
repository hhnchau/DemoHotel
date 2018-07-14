package com.appromobile.hotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.request.CreateOderRequest;
import com.appromobile.hotel.model.request.UpdatePaymentDto;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import org.json.JSONArray;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 12/13/2016.
 */

public class BrowserPaymentActivity extends BaseActivity {
    private WebView wvContent;
    private PaymentInfoForm entry;
    private long userBookingSn;
    private String methodPayment;
    private int status = RESULT_CANCELED;
    private String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";
    private boolean FLAG_REWARD;
    private boolean isPayNow;
    private boolean isFlashSale = false;
    private boolean isNewPayment = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenName();

        setContentView(R.layout.browser_payment_activity);
        wvContent = findViewById(R.id.wvContent);
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setDomStorageEnabled(true);
        wvContent.setWebViewClient(new MyWebClient());
        wvContent.setWebChromeClient(new WebChromeClient());


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isPayNow = bundle.getBoolean("IS_PAY_NOW", false);
            entry = bundle.getParcelable("PaymentInfoForm");
            userBookingSn = bundle.getLong("userBookingSn");
            methodPayment = bundle.getString("METHOD_PAYMENT");
            isFlashSale = bundle.getBoolean("FLASH_SALE", false);
            isNewPayment = bundle.getBoolean("New_Payment", false);
        }

        CreateOderRequest createOderRequest = new CreateOderRequest();
        createOderRequest.setmTransactionID(entry.getTransactionId());
        createOderRequest.setBankCode(entry.getBankCode());
        createOderRequest.setTotalAmount(entry.getTotalAmount());
        createOderRequest.setMerchantCode(entry.getMerchantCode());

        createOderRequest.setClientIP(Utils.getClientIp());
        createOderRequest.setCustName(entry.getNickName());
        createOderRequest.setCustDOB(entry.getBirthday());
        createOderRequest.setCustPhone(entry.getMobile());
        createOderRequest.setCustMail(entry.getEmail());
        createOderRequest.setPasscode(entry.getPasscode());
        createOderRequest.setChecksum(entry.getChecksum());
        createOderRequest.setCustGender(entry.getGender());
        createOderRequest.setCustAddress(entry.getAddress());
        createOderRequest.setErrorURL(entry.getErrorUrl());
        createOderRequest.setRedirectURL(entry.getRedirectUrl());
        createOderRequest.setCancelURL(entry.getCancelUrl());
        createOderRequest.setDescription(String.valueOf(userBookingSn));

        try {
            String url = entry.getCreateOrderLink();

            DialogUtils.showLoadingProgress(this, false);
            HotelApplication.serviceApi.get123PayURL(url, createOderRequest.getMapValues()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    DialogUtils.hideLoadingProgress();
                    ResponseBody res = response.body();
                    if (response.isSuccessful() && res != null) {
                        try {
                            String result = res.string();
                            MyLog.writeLog("123PayResult: " + result);
                            JSONArray jsonObject = new JSONArray(result);

                            String redirectURL = jsonObject.getString(2);

                            wvContent.loadUrl(redirectURL);
                        } catch (Exception e) {
                        }
                    } else {
                        MyLog.writeLog("123PayResult: Fail");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            MyLog.writeLog("123PayResult: Fail" + e);
        }
    }

    private void updatePaymentResult(final int index) {
        UpdatePaymentDto updatePaymentDto = new UpdatePaymentDto();

        updatePaymentDto.setClientip(Utils.getClientIp());
        //updatePaymentDto.setTransactionId(Integer.parseInt(entry.getTransactionId()));
        updatePaymentDto.setTransactionId2(entry.getTransactionId());

        HotelApplication.serviceApi.updatePaymentResult(updatePaymentDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    MyLog.writeLog("updatePaymentResult: OK");

                    /*
                    / Callback From API
                    */
                    if (index == 1) {
                        String messsage;
                        if (restResult.getOtherInfo().equals("1")) {
                            messsage = getString(R.string.msg_3_9_payment_successful);
                            status = RESULT_OK;
                            //Set Flash Sale
                            if (isFlashSale) {
                                HotelApplication.isFlashSaleChange = true;
                            }
                        } else {
                            messsage = getMessageMethodPayment();
                            status = RESULT_CANCELED;
                        }

                        final Dialog dialog = new Dialog(BrowserPaymentActivity.this, R.style.dialog_full_transparent_background);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.message_dialog);
                        Window window = dialog.getWindow();
                        if (window != null) {
                            WindowManager.LayoutParams wlp = window.getAttributes();
                            wlp.gravity = Gravity.CENTER;
                            window.setAttributes(wlp);
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            dialog.show();
                        }
                        TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
                        tvMessage.setText(messsage);
                        TextViewSFBold btnOK = dialog.findViewById(R.id.btnOK);
                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                setResult(status);
                                checkPaymentMethod(status);
                                finish();
                            }
                        });
                        /*
                        / Callback From onBackPress
                        */
                    } else if (index == -1) {
                        //call Api
                        checkPaymentMethod(status);
                    }
                } else {
                    MyLog.writeLog("updatePaymentResult: Fail");
                }

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                MyLog.writeLog("updatePaymentResult: Fail Unknown");
            }
        });
    }

    @Override
    public void setScreenName() {
        this.screenName = "SPayment";
    }

    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(entry.getRedirectUrl())) {

                updatePaymentResult(1); //Status show dialog

                return true;
            } else if (url.startsWith(entry.getCancelUrl())) {
                Toast.makeText(BrowserPaymentActivity.this, "Payment cancel", Toast.LENGTH_LONG).show();
                return true;
            } else if (url.startsWith(entry.getErrorUrl())) {
                Toast.makeText(BrowserPaymentActivity.this, "Payment error", Toast.LENGTH_LONG).show();
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        String msg = getMessageMethodPayment();
        DialogUtils.showDialogReservationSuccessful(this, msg, new DialogCallback() {
            @Override
            public void finished() {

                updatePaymentResult(-1); //Status not things

            }
        });
    }

    private void checkPaymentMethod(int status) {
        if (status == RESULT_CANCELED && isNewPayment){
            finish();
            return;
        }
        //Check Payment always Online
        if (methodPayment != null && methodPayment.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE) || methodPayment != null && methodPayment.equals(ParamConstants.METHOD_PAY_ONLINE_IN_DAY) || isFlashSale) {

            if (status == RESULT_OK) {

                //close Activity Reservation
                if (ReservationActivity.reservation != null) {
                    ReservationActivity.reservation.finish();
                }

                //Goto ReservationDetailActivity
                FLAG_REWARD = true;
                gotoReservationDetailActivity();

            } else {
                //Back Press
                if (isFlashSale) {

                    //close Activity Reservation
                    if (ReservationActivity.reservation != null) {
                        ReservationActivity.reservation.finish();
                    }
                    //Goto Home
                    finish();

                } else {

                    //Goto Reservation
                    finish();

                }

            }

            // Check Payment can Pay at Hotel
        } else {

            //close Activity Reservation
            if (ReservationActivity.reservation != null) {
                ReservationActivity.reservation.finish();
            }
            // not show popup reward again
            FLAG_REWARD = !isPayNow; //Don't show popup when callback from reservation detail and recent
            //Goto ReservationDetailActivity
            gotoReservationDetailActivity();

        }

    }

    private void gotoReservationDetailActivity() {

        DialogUtils.showLoadingProgress(BrowserPaymentActivity.this, false);
        // true = get reware coupon information
        // thanh toán xong mới gọi
        ControllerApi.getmInstance().findUserBookingDetail(BrowserPaymentActivity.this, userBookingSn, true, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                //hide loading
                DialogUtils.hideLoadingProgress();
                //result
                UserBookingForm userBookingForm = (UserBookingForm) object;

                Intent detail = new Intent(BrowserPaymentActivity.this, ReservationDetailActivity.class);
                detail.putExtra("UserBookingForm", userBookingForm);
                detail.putExtra(FLAG_SHOW_REWARD_CHECKIN, FLAG_REWARD);
                startActivity(detail);
                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });
    }

    private String getMessageMethodPayment() {
        String msg = "";
        //Check Payment always online
        if (methodPayment != null && methodPayment.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE) || methodPayment != null && methodPayment.equals(ParamConstants.METHOD_PAY_ONLINE_IN_DAY) || isFlashSale) {
            msg = getString(R.string.msg_3_9_payment_process_failed);
        } else {
            msg = getString(R.string.msg_3_9_payment_unpaid);
        }
        return msg;
    }
}
