package com.appromobile.hotel.api;

import com.appromobile.hotel.HotelApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xuan on 7/5/2016.
 */
public class HotelRequestInterceptor implements Interceptor{
    private final HotelApplication app;

    public HotelRequestInterceptor(HotelApplication app) {
        this.app = app;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();


//        builder.addHeader("Accept", "application/json").build();
//        builder.header("Content-Type", "application/json");

//        if (app.getUser() != null && app.getUser().getAccess_token() != null) {
//            builder.addHeader("Authorization",  app.getUser().getAccess_token());
//        }else{
//            if(app.getAccessToken() != null && !app.getAccessToken().isEmpty()){
//                builder.addHeader("Authorization", "Bearer " + app.getAccessToken());
//            }
//        }
        return chain.proceed(builder.build());


    }
}
