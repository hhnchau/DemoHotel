package com.appromobile.hotel.api.controllerApi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.callback.CallbackApiFail;
import com.appromobile.hotel.model.request.BookingDto;
import com.appromobile.hotel.model.request.HomeHotelRequest;
import com.appromobile.hotel.model.request.LoginDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SearchHistoryDto;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.request.UpdatePaymentDto;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.request.UserCommonInfoDto;
import com.appromobile.hotel.model.request.UserFavoriteDto;
import com.appromobile.hotel.model.request.UserLocationForm;
import com.appromobile.hotel.model.request.UserSettingDto;
import com.appromobile.hotel.model.request.ViewCrmNotificationDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.CommonInfoForm;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.MileageHistoryForm;
import com.appromobile.hotel.model.view.MileagePointForm;
import com.appromobile.hotel.model.view.PopupApiForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RecentBookingForm;
import com.appromobile.hotel.model.view.ReservationSetting;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.SearchHistoryForm;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.model.view.UserSettingForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.model.view.ViewNotificationDto;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;

import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by appro on 07/03/2017.
 */
public class ControllerApi implements RequestApi {
    private static ControllerApi mInstance = null;

    public static ControllerApi getmInstance() {
        if (mInstance == null) {
            mInstance = new ControllerApi();
        }
        return mInstance;
    }

    @Override
    public void findUserBookingDetail(final Context context, final long sn, final boolean checkCoupon, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        Map<String, Object> params = new HashMap<>();
        params.put("sn", sn);
        params.put("checkCoupon", checkCoupon);
        HotelApplication.serviceApi.findUserBookingDetail(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<UserBookingForm>() {
            @Override
            public void onResponse(Call<UserBookingForm> call, Response<UserBookingForm> response) {
                DialogUtils.hideLoadingProgress();

                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());

                }
            }

            @Override
            public void onFailure(Call<UserBookingForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();

                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findUserBookingDetail(context, sn, checkCoupon, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void createNewReservation(final Context context, final UserBookingDto userBookingDto, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.createNewReservation(userBookingDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult result = response.body();

                if (result != null) {
                    if (result.getResult() == 1) {

                        resultApi.resultApi(result);


                        //Event Fabric
                        if (HotelApplication.isRelease) {
                            Answers.getInstance().logCustom(new CustomEvent("Make a reservation").putCustomAttribute("userBookingSn", String.valueOf(result.getSn())));
                        }

                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();

                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        createNewReservation(context, userBookingDto, resultApi);
                    }
                });

            }
        });
    }

    @Override
    public void findRecentBookingList(final Context context, final ResultApiList resultApiList) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.findRecentBookingList(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<RecentBookingForm>>() {
            @Override
            public void onResponse(Call<List<RecentBookingForm>> call, Response<List<RecentBookingForm>> response) {
                DialogUtils.hideLoadingProgress();
                List<RecentBookingForm> recentBookingFormList = response.body();
                if (response.isSuccessful() && recentBookingFormList != null) {
                    List<Object> list = new ArrayList<>();
                    list.addAll(recentBookingFormList);
                    resultApiList.resultApilist(list);
                } else {

                    retryApi(context, new CallbackRetry() {
                        @Override
                        public void retry() {
                            findRecentBookingList(context, resultApiList);
                        }
                    });

                }
            }

            @Override
            public void onFailure(final Call<List<RecentBookingForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findRecentBookingList(context, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void checkInBooking(final Context context, final BookingDto bookingDto, final ResultApi resultApi) {

        DialogUtils.showLoadingProgress(context, false);

        HotelApplication.serviceApi.checkInBooking(bookingDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                //hide dialog
                DialogUtils.hideLoadingProgress();

                RestResult result = response.body();

                if (result != null) {

                    if (result.getResult() == 1) {

                        resultApi.resultApi(response.body());
                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

                //hide dialog
                DialogUtils.hideLoadingProgress();

                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        checkInBooking(context, bookingDto, resultApi);
                    }
                });

            }
        });
    }

    @Override
    public void findReservationSetting(final Context context, final long hotelSn, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);

        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelSn);

        HotelApplication.serviceApi.findReservationSetting(params, HotelApplication.DEVICE_ID).enqueue(new Callback<ReservationSetting>() {
            @Override
            public void onResponse(Call<ReservationSetting> call, Response<ReservationSetting> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                }
            }

            @Override
            public void onFailure(Call<ReservationSetting> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findReservationSetting(context, hotelSn, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findPopupInfo(final Context context, final String token, final ResultApi resultApi) {
        HotelApplication.serviceApi.findPopupInfo(token, HotelApplication.DEVICE_ID).enqueue(new Callback<PopupApiForm>() {
            @Override
            public void onResponse(Call<PopupApiForm> call, Response<PopupApiForm> response) {
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                }
            }

            @Override
            public void onFailure(Call<PopupApiForm> call, Throwable t) {
//                retryApi(context, new CallbackRetry() {
//                    @Override
//                    public void retry() {
//                        findPopupInfo(context, token, resultApi);
//                    }
//                });
            }
        });
    }

    @Override
    public void findPopupInfoList(Context context, String token, final ResultApi resultApi) {
        HotelApplication.serviceApi.findPopupInfoList(token, HotelApplication.DEVICE_ID).enqueue(new Callback<PopupApiForm>() {
            @Override
            public void onResponse(Call<PopupApiForm> call, Response<PopupApiForm> response) {
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                }
            }

            @Override
            public void onFailure(Call<PopupApiForm> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateAppUserToken(final Context context, final MobileDeviceInput mobileDeviceInput, final ResultApi resultApi) {
        HotelApplication.serviceApi.updateAppUserToken(mobileDeviceInput, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                RestResult result = response.body();
                if (result != null) {
                    if (result.getResult() == 1) {
                        resultApi.resultApi(response.body());
                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateAppUserToken(context, mobileDeviceInput, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void updateUserLocation(final Context context, final UserLocationForm userLocationForm, final ResultApiList resultApiList) {
        HotelApplication.serviceApi.updateUserLocation(userLocationForm, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new retrofit2.Callback<List<HotelForm>>() {

            @Override
            public void onResponse(Call<List<HotelForm>> call, final Response<List<HotelForm>> response) {

                List<HotelForm> l = response.body();
                if (response.isSuccessful() && l != null) {
                    List<Object> list = new ArrayList<>();
                    list.addAll(l);
                    resultApiList.resultApilist(list);
                } else {
                    retryApi(context, new CallbackRetry() {
                        @Override
                        public void retry() {
                            updateUserLocation(context, userLocationForm, resultApiList);
                        }
                    });
                }

            }


            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateUserLocation(context, userLocationForm, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void findAppUser(final Context context, final ResultApi resultApi) {
        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                }
            }

            @Override
            public void onFailure(Call<AppUserForm> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findAppUser(context, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void login(final Context context, final LoginDto loginDto, final ResultApi resultApi) {
        HotelApplication.serviceApi.login(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                RestResult result = response.body();
                if (result != null) {
                    if (result.getResult() == 1) {
                        resultApi.resultApi(response.body());
                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        login(context, loginDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void loginViaSocialApp(final Context context, final SocialLoginDto socialLoginDto, final ResultApi resultApi) {
        HotelApplication.serviceApi.loginViaSocialApp(socialLoginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                RestResult result = response.body();
                if (result != null) {
                    if (result.getResult() == 1) {
                        resultApi.resultApi(response.body());
                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        loginViaSocialApp(context, socialLoginDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void checkPayInAdvance(final Context context, final UserBookingDto userBookingDto, final ResultApi resultApi) {
        HotelApplication.serviceApi.checkPayInAdvance(userBookingDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                RestResult result = response.body();
                if (result != null) {
                    if (result.getResult() == 1) {
                        resultApi.resultApi(response.body());
                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        checkPayInAdvance(context, userBookingDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void updateReadStatusCommonInfo(final Context context, final UserCommonInfoDto userCommonInfoDto, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.updateReadStatusCommonInfo(userCommonInfoDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                // không cần xử lý
                DialogUtils.hideLoadingProgress();
                RestResult result = response.body();
                if (result != null && result.getResult() == 1) {
                    resultApi.resultApi(response.body());
                } else {
                    resultApi.resultApi(null);
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                // không cần xử lý
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateReadStatusCommonInfo(context, userCommonInfoDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findCommonInfo(final Context context, final ResultApi resultApi) {
        HotelApplication.serviceApi.findCommonInfo().enqueue(new Callback<CommonInfoForm>() {
            @Override
            public void onResponse(Call<CommonInfoForm> call, Response<CommonInfoForm> response) {

                if (response.isSuccessful() && response.body() != null) {
                    resultApi.resultApi(response.body());
                } else {
                    Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CommonInfoForm> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findCommonInfo(context, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void applyPromotionEvent(final Context context, final int sn, final ResultApi resultApi) {
        Map<String, Object> map = new HashMap<>();
        map.put("promotionSn", sn);
        if (context != null) DialogUtils.showLoadingProgress(context, false);

        HotelApplication.serviceApi.applyPromotionEvent(map, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult result = response.body();
                if (result != null) {
                    if (response.isSuccessful()) {
                        if (result.getResult() == 1)
                            resultApi.resultApi(result);
                        else {
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                            resultApi.resultApi(null);
                        }
                    } else {
                        resultApi.resultApi(null);
                        Toast.makeText(context, R.string.cannot_connect_to_server, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        applyPromotionEvent(context, sn, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findFlashSaleHotelList(Context context, int limit, int offset, final ResultApiList resultApiList) {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("offset", offset);

        HotelApplication.serviceApi.findFlashSaleHotelList(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {

                List<HotelForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateFavoriteHotelForUser(final Context context, final UserFavoriteDto userFavoriteDto, final ResultApi resultApi) {
        HotelApplication.serviceApi.updateFavoriteHotelForUser(userFavoriteDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

                RestResult restResult = response.body();
                if (restResult != null) {
                    if (response.isSuccessful()) {
                        if (restResult.getResult() == 1) {

                            resultApi.resultApi(response.body());

                        } else if (restResult.getResult() == 5) {
                            DialogUtils.showExpiredDialog(context, new DialogCallback() {
                                @Override
                                public void finished() {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    ((Activity) context).startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                                }
                            });
                        }
                    } else if (response.code() == 401) {
                        DialogUtils.showExpiredDialog(context, new DialogCallback() {
                            @Override
                            public void finished() {
                                Intent intent = new Intent(context, LoginActivity.class);
                                ((Activity) context).startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateFavoriteHotelForUser(context, userFavoriteDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findDistrictInformation(final Context context, final int districtSn, final ResultApi resultApi) {
        Map<String, Object> params = new HashMap<>();
        params.put("districtSn", districtSn);

        HotelApplication.serviceApi.findDistrictInformation(params, HotelApplication.DEVICE_ID).enqueue(new Callback<District>() {
            @Override
            public void onResponse(Call<District> call, Response<District> response) {
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                } else {
                    Toast.makeText(context, R.string.cannot_connect_to_server, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<District> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findDistrictInformation(context, districtSn, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findLimitHotelImageList(final Context context, final long hotelSn, final long roomTypeSn, final ResultApiList resultApiList) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelSn);
        params.put("type", 2); //360 = 2
        params.put("limit", 30);
        params.put("offset", 0);
        if (roomTypeSn > -1) {
            params.put("roomTypeSn", roomTypeSn);
        }

        HotelApplication.serviceApi.findLimitHotelImageList(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelImageForm>>() {
            @Override
            public void onResponse(Call<List<HotelImageForm>> call, Response<List<HotelImageForm>> response) {

                List<HotelImageForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    } else {

                        retryApi(context, new CallbackRetry() {
                            @Override
                            public void retry() {
                                findRecentBookingList(context, resultApiList);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HotelImageForm>> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findLimitHotelImageList(context, hotelSn, roomTypeSn, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void getHotelDetail(final Context context, long hotelSn, final ResultApi resultApi) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelSn);
        params.put("version", "4.0");

        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.getHotelDetail(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<HotelDetailForm>() {
            @Override
            public void onResponse(Call<HotelDetailForm> call, Response<HotelDetailForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful() && response.body() != null) {
                    resultApi.resultApi(response.body());
                } else {
                    Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HotelDetailForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void findApiSetting(final Context context, final ResultApi resultApi) {
        HotelApplication.serviceApi.findApiSetting(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<ApiSettingForm>() {
            @Override
            public void onResponse(Call<ApiSettingForm> call, Response<ApiSettingForm> response) {
                if (response.isSuccessful()) {
                    resultApi.resultApi(response.body());
                } else {
                    Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiSettingForm> call, Throwable t) {
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findApiSetting(context, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findAllHotelContractTrialList(final Context context, final ResultApiList resultApiList) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.findAllHotelContractTrialList(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                DialogUtils.hideLoadingProgress();

                List<HotelForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    } else {

                        retryApi(context, new CallbackRetry() {
                            @Override
                            public void retry() {
                                findAllHotelContractTrialList(context, resultApiList);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findAllHotelContractTrialList(context, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void updateViewNotification(int sn) {
        ViewNotificationDto viewNotificationDto = new ViewNotificationDto();
        viewNotificationDto.setSn(sn);
        HotelApplication.serviceApi.updateViewNotification(viewNotificationDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }

    @Override
    public void findPromotionInformation(Context context, final CallbackPromotionInfoForm callbackPromotionInfoForm) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.findPromotionInformation(HotelApplication.DEVICE_ID).enqueue(new Callback<Map<String, PromotionInfoForm>>() {
            @Override
            public void onResponse(Call<Map<String, PromotionInfoForm>> call, Response<Map<String, PromotionInfoForm>> response) {
                DialogUtils.hideLoadingProgress();
                callbackPromotionInfoForm.map(response.body());
            }

            @Override
            public void onFailure(Call<Map<String, PromotionInfoForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    @Override
    public void findLimitUserStampFormListForMobile(Context context, int limit, int offset, final ResultApiList resultApiList) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);

        HotelApplication.serviceApi.findLimitUserStampFormListForMobile(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserStampForm>>() {
            @Override
            public void onResponse(Call<List<UserStampForm>> call, Response<List<UserStampForm>> response) {
                List<UserStampForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserStampForm>> call, Throwable t) {

            }
        });
    }

    @Override
    public void findUserStampFormDetail(Context context, long hotelSn, boolean withStampList, final ResultApi resultApi) {
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelSn);
        params.put("withStampList", withStampList);

        HotelApplication.serviceApi.findUserStampFormDetail(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<UserStampForm>() {
            @Override
            public void onResponse(Call<UserStampForm> call, Response<UserStampForm> response) {
                resultApi.resultApi(response.body());
            }

            @Override
            public void onFailure(Call<UserStampForm> call, Throwable t) {
            }
        });
    }

    @Override
    public void updatePayooPaymentResult(final Context context, final String clientip, final String transactionId2, final String paymentCode, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        UpdatePaymentDto updatePaymentDto = new UpdatePaymentDto();
        updatePaymentDto.setTransactionId2(transactionId2);
        updatePaymentDto.setClientip(clientip);
        if (paymentCode != null) {
            updatePaymentDto.setPaymentCode(paymentCode);
        }
        HotelApplication.serviceApi.updatePayooPaymentResult(updatePaymentDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult result = response.body();
                if (result != null) {
                    if (response.isSuccessful()) {
                        if (result.getResult() == 1)
                            resultApi.resultApi(result);
                        else {
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                            //resultApi.resultApi(null);
                        }
                    } else {
                        //resultApi.resultApi(null);
                        Toast.makeText(context, R.string.cannot_connect_to_server, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updatePayooPaymentResult(context, clientip, transactionId2, paymentCode, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findPaymentInfoFormMap(Context context, long userBookingSn, String clientIp, final ResultMapApi resultMapApi) {
        Map<String, Object> params = new HashMap<>();
        params.put("userBookingSn", userBookingSn);
        params.put("clientip", clientIp);

        HotelApplication.serviceApi.findPaymentInfoFormMap(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    resultMapApi.map(response.body());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });
    }

    @Override
    public void checkMobileInSystem(final Context context, final String phone, final ResultApi resultApi) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobile", phone);
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.checkMobileInSystem(param, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult result = response.body();
                if (result != null) {
                    if (response.isSuccessful()) {
                        if (result.getResult() == 1)
                            resultApi.resultApi(result);
                        else {
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                            resultApi.resultApi(null);
                        }
                    } else {
                        resultApi.resultApi(null);
                        Toast.makeText(context, R.string.cannot_connect_to_server, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        checkMobileInSystem(context, phone, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void checkVerifyCode(final Context context, final String phone, final String verify, final ResultApi resultApi) {
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("verifyCode", verify);
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.checkVerifyCode(params, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                if (response.isSuccessful()) {
                    RestResult result = response.body();
                    if (result != null) {

                        resultApi.resultApi(result);

                    } else {
                        resultApi.resultApi(null);
                        Toast.makeText(context, R.string.cannot_connect_to_server, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        checkVerifyCode(context, phone, verify, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void createNewUserBooking(final Context context, final UserBookingDto userBookingDto, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.createNewUserBooking(userBookingDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult result = response.body();

                if (result != null) {
                    if (result.getResult() == 1) {

                        resultApi.resultApi(result);


                        //Event Fabric
                        if (HotelApplication.isRelease) {
                            Answers.getInstance().logCustom(new CustomEvent("Make a reservation").putCustomAttribute("userBookingSn", String.valueOf(result.getSn())));
                        }

                    } else {
                        Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();

                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        createNewUserBooking(context, userBookingDto, resultApi);
                    }
                });

            }
        });
    }

    @Override
    public void updateViewNotificationCrm(long sn, int typeCrm) {
        ViewCrmNotificationDto viewCrmNotificationDto = new ViewCrmNotificationDto(sn, typeCrm, 2);
        HotelApplication.serviceApi.updateViewNotificationCrm(viewCrmNotificationDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }

    @Override
    public void findLimitMileageHistoryList(final Context context, final String startDate, final String endDate, final int limit, final int offset, final ResultApiList resultApiList) {
        DialogUtils.showLoadingProgress(context, false);
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("limit", limit);
        params.put("offset", offset);
        HotelApplication.serviceApi.findLimitMileageHistoryList(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<MileageHistoryForm>>() {
            @Override
            public void onResponse(Call<List<MileageHistoryForm>> call, Response<List<MileageHistoryForm>> response) {
                DialogUtils.hideLoadingProgress();
                List<MileageHistoryForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<MileageHistoryForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findLimitMileageHistoryList(context, startDate, endDate, limit, offset, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void findLimitMileageRewardForAppList(final Context context, final int type, final int limit, final int offset, final ResultApiList resultApiList) {
        DialogUtils.showLoadingProgress(context, false);
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("limit", limit);
        params.put("offset", offset);
        HotelApplication.serviceApi.findLimitMileageRewardForAppList(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<MileageHistoryForm>>() {
            @Override
            public void onResponse(Call<List<MileageHistoryForm>> call, Response<List<MileageHistoryForm>> response) {
                DialogUtils.hideLoadingProgress();
                List<MileageHistoryForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MileageHistoryForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findLimitMileageRewardForAppList(context, type, limit, offset, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void findGeneralMileagePointInfo(final Context context, final String startDate, final String endDate, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        HotelApplication.serviceApi.findGeneralMileagePointInfo(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<MileagePointForm>() {
            @Override
            public void onResponse(Call<MileagePointForm> call, Response<MileagePointForm> response) {
                DialogUtils.hideLoadingProgress();
                MileagePointForm mileagePointForm = response.body();
                if (mileagePointForm != null) {
                    if (response.isSuccessful()) {
                        resultApi.resultApi(mileagePointForm);
                    }
                }
            }

            @Override
            public void onFailure(Call<MileagePointForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findGeneralMileagePointInfo(context, startDate, endDate, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void findUserSettingViaAppUserSn(final Context context, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.findUserSettingViaAppUserSn(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<UserSettingForm>() {
            @Override
            public void onResponse(Call<UserSettingForm> call, Response<UserSettingForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        resultApi.resultApi(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserSettingForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findUserSettingViaAppUserSn(context, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void updateUserSetting(final Context context, final UserSettingDto userSettingDto, final ResultApi resultApi) {
        DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.updateUserSetting(userSettingDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        resultApi.resultApi(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateUserSetting(context, userSettingDto, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void searchHotelList(final Context context, final Map<String, Object> params, final ResultApi resultApi) {
        //DialogUtils.showLoadingProgress(context, false);
        HotelApplication.serviceApi.searchHotelList(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                //DialogUtils.hideLoadingProgress();
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        resultApi.resultApi(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                //DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        searchHotelList(context, params, resultApi);
                    }
                });
            }
        });
    }

    @Override
    public void updateSearchHistory(SearchHistoryDto searchHistoryDto) {
        HotelApplication.serviceApi.updateSearchHotel(searchHistoryDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }

    @Override
    public void findLimitSearchHistoryList(final Context context, final int offset, final int limit, final ResultApiList resultApiList) {
        DialogUtils.showLoadingProgress(context, false);
        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("offset", offset);
        HotelApplication.serviceApi.findLimitSearchHistoryList(params, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<SearchHistoryForm>>() {
            @Override
            public void onResponse(Call<List<SearchHistoryForm>> call, Response<List<SearchHistoryForm>> response) {
                DialogUtils.hideLoadingProgress();
                List<SearchHistoryForm> l = response.body();
                if (l != null) {
                    if (response.isSuccessful()) {
                        List<Object> list = new ArrayList<>();
                        list.addAll(l);
                        resultApiList.resultApilist(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SearchHistoryForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                retryApi(context, new CallbackRetry() {
                    @Override
                    public void retry() {
                        findLimitSearchHistoryList(context, limit, offset, resultApiList);
                    }
                });
            }
        });
    }

    @Override
    public void sendCrmNotification() {
        Map<String, Object> params = new HashMap<>();
        params.put("targetType", 1);

        HotelApplication.serviceApi.sendCrmNotification(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<RestResult>>() {
            @Override
            public void onResponse(Call<List<RestResult>> call, Response<List<RestResult>> response) {

            }

            @Override
            public void onFailure(Call<List<RestResult>> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateUninstallAndroid() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", HotelApplication.DEVICE_ID);

        HotelApplication.serviceApi.updateUninstallAndroid(params, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }

    //Dialog
    private void retryApi(final Context context, final CallbackRetry callbackRetry) {
        DialogUtils.apiFail(context, context.getString(R.string.cannot_connect_to_server), context.getString(R.string.close), context.getString(R.string.retry), new CallbackApiFail() {
            @Override
            public void onPress(boolean retry) {
                if (retry) {
                    callbackRetry.retry();
                } else {
                    //Intent intent = new Intent(context, ExitActivity.class);
                    //context.startActivity(intent);
                }
            }
        });
    }
}
