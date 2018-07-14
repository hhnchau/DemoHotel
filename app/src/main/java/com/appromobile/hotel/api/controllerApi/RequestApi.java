package com.appromobile.hotel.api.controllerApi;

import android.content.Context;

import com.appromobile.hotel.model.request.BookingDto;
import com.appromobile.hotel.model.request.LoginDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.request.UserCommonInfoDto;
import com.appromobile.hotel.model.request.UserFavoriteDto;
import com.appromobile.hotel.model.request.UserLocationForm;


/**
 * Created by appro on 08/03/2017.
 */
public interface RequestApi {
    void findUserBookingDetail(Context context, long sn, boolean checkCoupon, ResultApi resultApi);

    void createNewReservation(Context context, UserBookingDto userBookingDto, ResultApi resultApi);

    void findRecentBookingList(Context context, ResultApiList resultApiList);

    void checkInBooking(Context context, BookingDto bookingDto, ResultApi resultApi);

    void findReservationSetting(Context context, long hotelSn, ResultApi resultApi);

    void findApiSetting(Context context, ResultApi resultApi);

    void findPopupInfo(Context context, String token, ResultApi resultApi);

    void updateAppUserToken(Context context, MobileDeviceInput mobileDeviceInput, ResultApi resultApi);

    void updateUserLocation(Context context, UserLocationForm userLocationForm, ResultApiList resultApiList);

    void findAppUser(Context context, ResultApi resultApi);

    void login(Context context, LoginDto loginDto, ResultApi resultApi);

    void loginViaSocialApp(Context context, SocialLoginDto socialLoginDto, ResultApi resultApi);

    void checkPayInAdvance(Context context, UserBookingDto userBookingDto, ResultApi resultApi);

    void updateReadStatusCommonInfo(Context context, UserCommonInfoDto userCommonInfoDto, ResultApi resultApi);

    void findCommonInfo(Context context, ResultApi resultApi);

    void applyPromotionEvent(Context context, int sn, ResultApi resultApi);

    void findFlashSaleHotelList(Context context, int limit, int offset, ResultApiList resultApiList);

    void updateFavoriteHotelForUser(Context context, UserFavoriteDto userFavoriteDto, ResultApi resultApi);

    void findDistrictInformation(Context context, int districtSn, ResultApi resultApi);

    void findLimitHotelImageList(Context context, long hotelSn, long roomTypeSn, ResultApiList resultApiList);

    void getHotelDetail(Context context, long hotelSn, ResultApi resultApi);

    void findAllHotelContractTrialList(Context context, ResultApiList resultApiList);

    void updateViewNotification(int sn);

    void findPromotionInformation(Context context, CallbackPromotionInfoForm callbackPromotionInfoForm);

    void findLimitUserStampFormListForMobile(Context context, int limit, int offset, ResultApiList resultApiList);

    void findUserStampFormDetail(Context context, long hotelSn, boolean withStampList, ResultApi resultApi);
}
