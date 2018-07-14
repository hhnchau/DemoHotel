package com.appromobile.hotel.payoo;

import android.app.Activity;
import android.content.Context;


import com.appromobile.hotel.model.view.PayooInfoForm;

import vn.payoo.paymentsdk.PayooMerchant;
import vn.payoo.paymentsdk.PayooPaymentSDK;
import vn.payoo.paymentsdk.data.converter.OrderConverter;


/**
 * Created by appro on 04/10/2017.
 */
public class PayooHandle {
    private static PayooHandle instance;
    static OrderConverter converter;

    public static PayooHandle getInstance() {
        if (instance == null) {
            instance = new PayooHandle();
        }
        if (converter == null) {
            converter = PayooConverter.create();
        }
        return instance;
    }

    public void init(Context context, PayooInfoForm payooInfoForm) {
        /*
        / Convert
        */

        /*
        / Payoo
        */
        PayooMerchant payooMerchant = PayooMerchant.newBuilder()
                .merchantId(payooInfoForm.getMerchantId())
                .secretKey(payooInfoForm.getMerchantSecrectKey())
                .isDevMode(payooInfoForm.isDevelopmentMode())
                .converter(converter)
                .build();

        PayooPaymentSDK.initialize(context, payooMerchant);
    }

    public void request(Activity activity, CreateOrderResponse createOrderResponse) {
        PayooPaymentSDK.pay(activity, createOrderResponse);
    }
}
