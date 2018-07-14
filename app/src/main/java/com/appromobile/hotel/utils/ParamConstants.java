package com.appromobile.hotel.utils;

/**
 * Created by xuan on 9/21/2016.
 */

public class ParamConstants {
    public static final int REQUEST_CHANGE_AREA = 1108;
    public static final int REQUEST_CHOOSE_AREA_FAVORITE = 1109;
    public static final int REQUEST_RESET_NEARBY = 1110;
    public static final int REQUEST_CHOOSE_AREA_HOME = 1111;
    public static final int REQUEST_LOGIN_MY_PAGE = 1112;
    public static final int REQUEST_LOGIN_AREA_SETTING_HOME = 1113;
    public static final int REQUEST_LOGIN_HOME = 1114;
    public static final int REQUEST_MY_FAVORITE = 1115;
    public static final int REQUEST_CHOOSE_AREA_MAP = 1116;
    public static final int REQUEST_SEARCH_TEXT = 1117;
    public static final int REQUEST_REPORT_NEW_HOTEL = 1118;
    public static final int QUICK_REVIEW = 1119;
    public static final int EVENT_PROMOTION_REQUEST = 1120;
    public static final int REQUEST_LOGIN_HOTEL_DETAIL = 1121;
    public static final int LOGIN_USER_INFO_REQUEST = 1122;
    public static final int ZBAR_SCANNER_REQUEST = 1123;
    public static final int QR_LOGIN_REQUEST = 1124;
    public static final int RECENT_BOOKING_REQUEST = 1125;
    public static final int INVITE_FRIEND_BANNER_REQUEST = 1126;
    public static final int REQUEST_CHANGE_PROFILE = 1127;
    public static final int REQUEST_CHOOSE_FAVORITE_AREA_HOME = 1128;
    public static final int REQUEST_CHOOSE_FAVORITE_AREA_MAP = 1129;
    public static final int REQUEST_LOGIN_AREA_SETTING_MAP = 1130;
    public static final int LOGIN_DETAIL_REQUEST_LIKE = 1131;
    public static final int REQUEST_LOGIN_TO_SEE_POLICY = 1132;
    public static final int REQUEST_LOGIN_TO_APPLY_PROMOTION = 1133;

    public static final int PAYMENT_METHOD_PAID_AT_HOTEL = 0;
    public static final int PAYMENT_METHOD_PAY123 = 1;
    public static final int PAYMENT_METHOD_PAYOO = 2;
    public static final int PAYMENT_METHOD_MOMO = 3;

    public final static int NOTHING = -1;

    public final static int DISCOUNT_MONEY = 1;
    public final static int DISCOUNT_PERCENT = 2;

    public final static int PAYMENT_BOTH = 1;
    public final static int PAYMENT_PAY_AT_HOTEL = 2;
    public final static int PAYMENT_ONLINE = 3;

    public final static String METHOD_BOTH = "1";
    public final static String METHOD_PAY_AT_HOTEL = "2";
    public final static String METHOD_ALWAYS_PAY_ONLINE = "3";
    public final static String METHOD_PAY_ONLINE_IN_DAY = "4";
    public final static String METHOD_FLASH_SALE = "5";

    // Use for send broadcast to display popup notification
    public final static String BROADCAST_POPUP_ACTION_COUPON = "BROADCAST_POPUP_ACTION_COUPON";
    public final static String BROADCAST_POPUP_ACTION_POLICY = "BROADCAST_POPUP_ACTION_POLICY";

    // INTENT ITEM'S KEY
    public final static String INTENT_KEY_BUNDLE_NOTI = "INTENT_KEY_BUNDLE_NOTI";
    public final static String INTENT_KEY_PROMOTION_POPUP = "INTENT_KEY_PROMOTION_POPUP";

    // INTENT ACTION
    public final static String INTENT_ACTION_CLOSE_APP = "INTENT_ACTION_CLOSE_APP";
    public final static String INTENT_ACTION_OPEN_MY_COUPON = "INTENT_ACTION_OPEN_MY_COUPON";
    public final static String INTENT_ACTION_OPEN_PROMOTION_DETAIL = "INTENT_ACTION_OPEN_PROMOTION_DETAIL";
    public final static String INTENT_ACTION_OPEN_NOTICE = "INTENT_ACTION_OPEN_NOTICE";

    //ACTION DEEPLINK
    public final static String INTENT_ACTION_PROMOTION = "INTENT_ACTION_PROMOTION";
    public final static String INTENT_ACTION_DISTRICT = "INTENT_ACTION_DISTRICT";
    public final static String INTENT_ACTION_HOTEL = "INTENT_ACTION_HOTEL";

    //ACTION CHANGE AREA
    public final static String ACTION_RESET_NEARBY = "ACTION_RESET_NEARBY";
    public final static String ACTION_CHANGE_AREA = "ACTION_CHANGE_AREA";
    public final static String ACTION_CHOOSE_AREA_FAVORITE = "ACTION_CHOOSE_AREA_FAVORITE";

    //Coupon
    public final static int CAN_USE = 1;
    public final static int HOTEL_NOT_ACCEPT = 2;
    public final static int NOT_ENOUGH_CONDITION = 3;

    // Language
    public final static String VIETNAM = "vi";
    public final static String ENGLISH = "en";
    public final static String KOREA = "ko";

    // NOTIFICATION TARGET TYPE
    public final static int TARGET_TYPE_PROMOTION = 7;

    // POPUP_ACTION_BUTTON
    public final static int BUTTON_SHOW = 1;
    public final static int BUTTON_INVISIBLE = 2;
    public final static int BUTTON_HIDE = 3;

    //ROOM TYPE
    public final static int ROOM_TYPE_FLASH_SALE = 1;
    public final static int ROOM_TYPE_CINEJOY = 2;
    public final static int ROOM_TYPE_NORMAL = 3;

    public final static int LOCK_TODAY = 2;

}
