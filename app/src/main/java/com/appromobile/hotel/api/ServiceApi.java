package com.appromobile.hotel.api;

import com.appromobile.hotel.model.request.AppUserDto;
import com.appromobile.hotel.model.request.AppUserSocialDto;
import com.appromobile.hotel.model.request.BookingDto;
import com.appromobile.hotel.model.request.CancelBookingDto;
import com.appromobile.hotel.model.request.CheckInRoomDto;
import com.appromobile.hotel.model.request.CounselingDetailDto;
import com.appromobile.hotel.model.request.CounselingDto;
import com.appromobile.hotel.model.request.LoginDto;
import com.appromobile.hotel.model.request.LogoutDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.PushNotificationDto;
import com.appromobile.hotel.model.request.ReportHotelDto;
import com.appromobile.hotel.model.request.ResetPasswordDto;
import com.appromobile.hotel.model.request.SendSmsDto;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.request.UpdateAppUserDto;
import com.appromobile.hotel.model.request.UpdatePasswordDto;
import com.appromobile.hotel.model.request.UpdatePaymentDto;
import com.appromobile.hotel.model.request.UpdateReasonDto;
import com.appromobile.hotel.model.request.UpdateUserReviewDto;
import com.appromobile.hotel.model.request.UserAreaFavoriteDto;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.request.UserCommonInfoDto;
import com.appromobile.hotel.model.request.UserFavoriteDto;
import com.appromobile.hotel.model.request.UserLocationForm;
import com.appromobile.hotel.model.request.UserReviewDto;
import com.appromobile.hotel.model.request.WriteLogDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppNoticeForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.CommonInfoForm;
import com.appromobile.hotel.model.view.CounselingDetailForm;
import com.appromobile.hotel.model.view.CounselingForm;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.InviteFriendForm;
import com.appromobile.hotel.model.view.BannerForm;
import com.appromobile.hotel.model.view.NoticeForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.PopupApiForm;
import com.appromobile.hotel.model.view.PromotionForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.Province;
import com.appromobile.hotel.model.view.RecentBookingForm;
import com.appromobile.hotel.model.view.ReservationSetting;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RewardForm;
import com.appromobile.hotel.model.view.RoomForm;
import com.appromobile.hotel.model.view.RoomTypeDetailForm;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.model.view.ViewNotificationDto;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by xuan on 6/24/2016.
 */
public interface ServiceApi {

    @GET("/hotelapi/hotel/view/findAllHotelListInDistrict")
    Call<List<HotelForm>> getAllHotelListInDistrict(@Query("districtSn") String districtSn, @Header("authorization") String authorization, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findHotelList")
    Call<List<HotelForm>> getHotelList(@QueryMap Map<String, Object> params, @Header("authorization") String authorization, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findLimitHotelListInDistrict/")
    Call<List<HotelForm>> getLimitHotelListInDistrict(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/viewHotelDetail")
    Call<HotelDetailForm> getHotelDetail(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/notice/view/findLimitNoticeList")
    Call<List<NoticeForm>> getNoticeList(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotel/view/viewRoomTypeDetail ")
    Call<RoomTypeDetailForm> getRoomTypeDetail(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateUserLocation")
    Call<List<HotelForm>> updateUserLocation(@Body UserLocationForm params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/download/downloadHotelImage")
    Call<ResponseBody> getImageView(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/login")
    Call<RestResult> login(@Body LoginDto params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/mobile/update/updateAppUserToken")
    Call<RestResult> updateAppUserToken(@Body MobileDeviceInput params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/area/view/findAllProvinceCity")
    Call<List<Province>> findAllProvinceCity(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/area/view/findAllDistrictInProvince")
    Call<List<District>> findAllDistrictInProvince(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/area/view/findDistrictInformation")
    Call<District> findDistrictInformation(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findAllFavoriteArea")
    Call<List<UserAreaFavoriteForm>> findAllFavoriteArea(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateFavoriteHotelForUser")
    Call<RestResult> updateFavoriteHotelForUser(@Body UserFavoriteDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateFavoriteArea")
    Call<RestResult> updateFavoriteArea(@Body UserAreaFavoriteDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/user/create/createNewAppUser")
    Call<RestResult> createNewAppUser(@Body AppUserDto params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/checkUserIdInSytem")
    Call<RestResult> checkUserIdInSytem(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/checkNickNameInSytem")
    Call<RestResult> checkNickNameInSytem(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateUserReview")
    Call<RestResult> updateUserReview(@Body UpdateUserReviewDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/user/create/createUserReview")
    Call<RestResult> createUserReview(@Body UserReviewDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findUserReviewList")
    Call<List<UserReviewForm>> findUserReviewList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findAllRoomList")
    Call<List<RoomForm>> findAllRoomNoList(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @DELETE("/hotelapi/user/delete/deleteUserReview")
    Call<RestResult> deleteUserReview(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/loginViaSocialApp")
    Call<RestResult> loginViaSocialApp(@Body SocialLoginDto params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/user/create/createNewAppUserViaSocialApp")
    Call<RestResult> createNewAppUserViaSocialApp(@Body AppUserSocialDto params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateAppUser")
    Call<RestResult> updateAppUser(@Body UpdateAppUserDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/forgetAppUserPassword")
    Call<RestResult> forgetAppUserPassword(@Body ResetPasswordDto params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findAppUser")
    Call<AppUserForm> findAppUser(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updatePassword")
    Call<RestResult> updatePassword(@Body UpdatePasswordDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/logout")
    Call<RestResult> logout(@Body LogoutDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findLimitFavoriteHotelList")
    Call<List<HotelForm>> findLimitFavoriteHotelList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findLimitHistoryReservationList")
    Call<List<UserBookingForm>> findLimitHistoryReservationList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findUserBookingDetail")
    Call<UserBookingForm> findUserBookingDetail(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findLimitCouponList")
    Call<List<CouponIssuedForm>> findLimitCouponList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findHotelListForMap")
    Call<List<HotelForm>> findHotelListForMap(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findBasicHotelInformation")
    Call<HotelDetailForm> findBasicHotelInformation(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/notice/view/findLimitAppNoticeList")
    Call<List<AppNoticeForm>> findLimitAppNoticeList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/conversation/view/findLimitCounselingList")
    Call<List<CounselingForm>> findLimitCounselingList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/conversation/create/createNewCounseling")
    Call<RestResult> createNewCounseling(@Body CounselingDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/conversation/view/findCounselingDetail")
    Call<List<CounselingDetailForm>> findCounselingDetail(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/conversation/update/replyCounselingDetail")
    Call<RestResult> replyCounselingDetail(@Body CounselingDetailDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @DELETE("/hotelapi/user/delete/unregisterUser")
    Call<RestResult> unregisterUser(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @GET
    Call<ResponseBody> getGoogleResult(@Url String url);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/hotel/create/reportNewHotel")
    Call<RestResult> reportNewHotel(@Body ReportHotelDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Multipart
    @POST("/hotelapi/hotel/create/createUpdateHotelHomeImage")
    Call<RestResult> createUpdateHotelHomeImage(@QueryMap Map<String, Object> params, @Part MultipartBody.Part filePart, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/mobile/update/updatePushNotification")
    Call<RestResult> updatePushNotification(@Body PushNotificationDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/mobile/view/findPushNotificationStatus")
    Call<RestResult> findPushNotificationStatus(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findLimitPromotionList")
    Call<List<PromotionForm>> findLimitPromotionList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findLimitEventList")
    Call<List<PromotionForm>> findLimitEventList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/promotion/insert/applyPromotionEvent")
    Call<RestResult> applyPromotionEvent(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/reservation/view/createNewReservation")
    Call<RestResult> createNewReservation(@Body UserBookingDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/reservation/view/checkBeforeCreateReservation")
    Call<RestResult> checkBeforeCreateReservation(@Body UserBookingDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/setting/view/findApiSetting ")
    Call<ApiSettingForm> findApiSetting(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findLimitRecentHotelList")
    Call<List<HotelForm>> findLimitRecentHotelList(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findAllBannerList")
    Call<List<BannerForm>> findAllBannerList(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findLimitHotelListInDistrict")
    Call<List<HotelForm>> findLimitHotelListInDistrict(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findLimitCouponListCanUsed")
    Call<List<CouponIssuedForm>> findLimitCouponListCanUsed(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/notice/view/findAppNotice")
    Call<AppNoticeForm> findAppNotice(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/notice/view/findNotice")
    Call<NoticeForm> findNotice(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/setting/view/findReservationSetting")
    Call<ReservationSetting> findReservationSetting(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/conversation/view/findCounselingViaUser")
    Call<CounselingForm> findCounselingViaUser(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findPromotionForApp")
    Call<PromotionForm> findPromotionForApp(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    //Reward
    @GET("/hotelapi/promotion/view/findRewardForApp")
    Call<RewardForm> findRewardForApp(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findUserReviewViaUser")
    Call<List<UserReviewForm>> findUserReviewViaUser(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findInviteFriendLink")
    Call<RestResult> findInviteFriendLink(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findShareHotelLink")
    Call<RestResult> findShareHotelLink(@Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/mobile/create/sendVerifyCode")
    Call<RestResult> sendVerifyCode(@Body SendSmsDto params, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/reservation/update/checkInHotelRoom")
    Call<RestResult> checkInHotelRoom(@Body CheckInRoomDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/reservation/update/checkInBooking")
    Call<RestResult> checkInBooking(@Body BookingDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findRecentBooking")
    Call<RecentBookingForm> findRecentBooking(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findRecentBookingList")
    Call<List<RecentBookingForm>> findRecentBookingList(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/reservation/update/cancelReservation")
    Call<RestResult> cancelReservation(@Body CancelBookingDto cancelBookingDto, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST()
    Call<ResponseBody> get123PayURL(@Url String url, @QueryMap Map<String, String> params);

    //PUT /reservation/update/updatePaymentResult
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/reservation/update/updatePaymentResult")
    Call<RestResult> updatePaymentResult(@Body UpdatePaymentDto params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/reservation/view/findPaymentInfoForm")
    Call<PaymentInfoForm> findPaymentInfoForm(@QueryMap Map<String, Object> params, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/user/view/findPopupInfo")
    Call<PopupApiForm> findPopupInfo(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/reservation/update/updateReasonNotCheckin")
    Call<RestResult> updateReasonNotCheckin(@Body UpdateReasonDto updateReasonDto, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findInviteFriendInfo")
    Call<InviteFriendForm> findInviteFriendInfo(@Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/reservation/view/checkPayInAdvance")
    Call<RestResult> checkPayInAdvance(@Body UserBookingDto userBookingDto, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @POST("/hotelapi/mobile/create/writeLogFile")
    Call<RestResult> writeLogFile(@Body WriteLogDto writeLogDto, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/user/update/updateReadStatusCommonInfoUser")
    Call<RestResult> updateReadStatusCommonInfo(@Body UserCommonInfoDto userCommonInfoDto, @Header("authorization") String token, @Header("deviceid") String deviceid);

    @GET("/hotelapi/commonInfo/view/findCommonInfo")
    Call<CommonInfoForm> findCommonInfo();

    @GET("/hotelapi/hotel/view/findFlashSaleHotelList")
    Call<List<HotelForm>> findFlashSaleHotelList(@QueryMap Map<String, Object> params, @Header("authorization") String authorization, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findLimitHotelImageList")
    Call<List<HotelImageForm>> findLimitHotelImageList(@QueryMap Map<String, Object> params, @Header("deviceid") String deviceid);

    @GET("/hotelapi/hotel/view/findAllHotelContractTrialList")
    Call<List<HotelForm>>  findAllHotelContractTrialList(@Header("authorization") String authorization, @Header("deviceid") String deviceid);

    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    @PUT("/hotelapi/notice/update/updateViewNotification")
    Call<RestResult> updateViewNotification(@Body ViewNotificationDto viewNotificationDto, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findPromotionInformation")
    Call<Map<String, PromotionInfoForm>>  findPromotionInformation(@Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findLimitUserStampFormListForMobile")
    Call<List<UserStampForm>>  findLimitUserStampFormListForMobile(@QueryMap Map<String, Object> params, @Header("authorization") String authorization, @Header("deviceid") String deviceid);

    @GET("/hotelapi/promotion/view/findUserStampFormDetail")
    Call<UserStampForm>  findUserStampFormDetail(@QueryMap Map<String, Object> params, @Header("authorization") String authorization,@Header("deviceid") String deviceid);


}
