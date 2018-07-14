package com.appromobile.hotel.api.controllerApi;

import android.content.Context;

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
import com.appromobile.hotel.model.view.PopupForm;
import com.appromobile.hotel.payoo.CreateOrderRequest;

import java.util.Map;


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

    void findPopupInfoList(Context context, String token, ResultApi resultApi);

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

    void updatePayooPaymentResult(Context context, String clientIp, String transactionId2, String paymentCode, ResultApi resultApi);

    void findPaymentInfoFormMap(Context context, long userBookingSn, String clientIp, ResultMapApi resultMapApi);

    void checkMobileInSystem(Context context, String phone, ResultApi resultApi);

    void checkVerifyCode(Context context, String phone, String verify, ResultApi resultApi);

    void createNewUserBooking(Context context, UserBookingDto userBookingDto, ResultApi resultApi);

    void updateViewNotificationCrm(long sn, int typeCrm);

    void findLimitMileageHistoryList(Context context, String startDate, String endDate, int limit, int offset, ResultApiList resultApiList);

    void findLimitMileageRewardForAppList(Context context, int type, int limit, int offset, ResultApiList resultApiList);

    void findGeneralMileagePointInfo(Context context, String startDate, String endDate, ResultApi resultApi);

    void findUserSettingViaAppUserSn(Context context, ResultApi resultApi);

    void updateUserSetting(Context context, UserSettingDto userSettingDto, ResultApi resultApi);

    void searchHotelList(Context context, Map<String, Object> params, ResultApi resultApi);

    void updateSearchHistory(SearchHistoryDto searchHistoryDto);

    void findLimitSearchHistoryList(Context context, int offset, int limit, ResultApiList resultApiList);

    void sendCrmNotification();

    void updateUninstallAndroid();
}
