package com.appromobile.hotel.gcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;


import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.activity.MyCouponActivity;
import com.appromobile.hotel.activity.SplashActivity;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.enums.NotificationAction;
import com.appromobile.hotel.enums.NotificationType;
import com.appromobile.hotel.gcm.action.ActionNotify;
import com.appromobile.hotel.gcm.action.ControllerNotify;
import com.appromobile.hotel.gcm.action.PendingIntentNotify;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by xuan on 11/7/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            MyLog.writeLog("MessagingService: receive");
            MyLog.writeLog("MessagingService: " + remoteMessage.getData().toString());
            Gson gson = new Gson();
            NotificationData notificationData = gson.fromJson(remoteMessage.getData().get("message"), NotificationData.class);

            if (notificationData != null) {

                if (notificationData.getType() == NotificationType.UNINSTALL) {

                    ControllerApi.getmInstance().updateUninstallAndroid();

                }else {

                    notificationAppNotice(notificationData);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("MessagingService: receive" + e);
        }
    }

    private void sendBroadcastToDisplayPopupNotification(String msg, String action) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        broadcastIntent.putExtra(ParamConstants.INTENT_KEY_BUNDLE_NOTI, msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    /*
    / Process Notification
    */
    private void notificationAppNotice(NotificationData notificationData) {
        String notificatonTitle = "";
        int notiId = getRandomNumber();

        if (notificationData.getType() == NotificationType.APP_NOTICE) {
            //Storage
            PreferenceUtils.setCounterNotifi(getApplication(), PreferenceUtils.getCounterNotifi(getApplication()) + 1);
            /*
            / Set Shortcut Badger
            */
            ShortcutBadger.applyCount(this, PreferenceUtils.getCounterNotifi(this)); //Delete = 0

            //Set Notification
            notificatonTitle = notificationData.getTitle();

        } else if (notificationData.getType() == NotificationType.NOTICE) {

            if (notificationData.getActionType() == NotificationAction.ACTION_NEW) {
                notificatonTitle = getString(R.string.you_have_new_notice);
            } else if (notificationData.getActionType() == NotificationAction.ACTION_UPDATE) {
                notificatonTitle = getString(R.string.you_have_update_notice);
            }

        } else if (notificationData.getType() == NotificationType.COUNSELING) {

            if (notificationData.getActionType() == NotificationAction.ACTION_NEW) {
                notificatonTitle = notificationData.getTitle();
            } else if (notificationData.getActionType() == NotificationAction.ACTION_UPDATE) {
                notificatonTitle = getString(R.string.you_have_update_answer);
            }

        } else if (notificationData.getType() == NotificationType.UPDATE_POLICY) {
            PreferenceUtils.setReadStatusPolicy(this, false);
            if (HotelApplication.isActivityVisible()) {
                //Force_ground
                // Display popup when notification coming
                playSoundForPopup();
                sendBroadcastToDisplayPopupNotification("", ParamConstants.BROADCAST_POPUP_ACTION_POLICY);
                return;
            } else {
                notificatonTitle = getString(R.string.msg_6_9_3_notification_message);
            }

        } else if (notificationData.getType() == NotificationType.COUPON_DISCOUNT) {

            String discount = notificationData.getOtherInfoList()[0];

            if (notificationData.getOtherInfoList()[1].equals("1")) {
                discount = Utils.formatCurrency(Integer.parseInt(discount)) + " VNĐ";
            } else if (notificationData.getOtherInfoList()[1].equals("2")) {
                discount = discount + "%";
            }

            String message = getResources().getString(R.string.msg_3_9_congrat_receive_coupon_successful);
            message = message.replace("couponNumber", notificationData.getOtherInfoList()[3]);
            message = message.replace("couponMoney", discount);

            //Check background _ forceground
            if (HotelApplication.isActivityVisible()) {
                //Force_ground
                // Display popup when notification coming
                playSoundForPopup();
                sendBroadcastToDisplayPopupNotification(message, ParamConstants.BROADCAST_POPUP_ACTION_COUPON);
                return;
            } else {
                notificatonTitle = message;
            }

        } else if (notificationData.getType() == NotificationType.FLASH_SALE) { //Flash Sale

            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    SplashActivity.class
            );

            AppUserForm appUserForm = PreferenceUtils.getAppUser(this);
            String name = "Bạn";
            if (appUserForm != null && !PreferenceUtils.getToken(this).equals("")) {
                name = appUserForm.getNickName();
            }

            String title = notificationData.getTitle();
            if (title != null && !title.equals("")){
                title = title.replace("#userName", name);
            }
            String subTitle = notificationData.getSubTitle();
            if (subTitle != null && !subTitle.equals("")){
                subTitle = subTitle.replace("#userName", name);
            }else {
                subTitle = title;
                title = getString(R.string.app_name);
            }

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    title,
                    subTitle,
                    null
            );

            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();

        } else if (notificationData.getType() == NotificationType.PROMOTION) {


            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    SplashActivity.class
            );

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    notificationData.getTitle(),
                    notificationData.getSubTitle(),
                    notificationData.getIconUrl()
            );

            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();


        } else if (notificationData.getType() == NotificationType.HOTEL_DETAIL) {

            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    SplashActivity.class
            );

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    notificationData.getTitle(),
                    notificationData.getSubTitle(),
                    notificationData.getIconUrl()
            );
            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();


        } else if (notificationData.getType() == NotificationType.COUPON_ISSUED) {
            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    MyCouponActivity.class
            );

            String msg = "";
            if (notificationData.getActionType() == 1) {
                msg = getString(R.string.notification_birthday_coupon_issued);
                msg = msg.replace("coupon_money", notificationData.getOtherInfoList()[0]);
            } else if (notificationData.getActionType() == 2) {
                msg = getString(R.string.notification_birthday_7_days);
                msg = msg.replace("coupon_money", notificationData.getOtherInfoList()[0]);
                msg = msg.replace("coupon_date", notificationData.getOtherInfoList()[1]);
            }

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    getString(R.string.app_name),
                    msg,
                    notificationData.getIconUrl()
            );
            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();
        } else if (notificationData.getType() == NotificationType.STAMP) {

            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    MainActivity.class
            );

            String msg;
            int resID;
            if (notificationData.isKey()) {
                resID = getResources().getIdentifier(notificationData.getTitle(), "string", getApplicationContext().getPackageName());
                msg = getString(resID);
                if (notificationData.getOtherInfoList().length > 1) {
                    msg = getString(resID, notificationData.getOtherInfoList()[0], notificationData.getOtherInfoList()[1]);
                }else if(notificationData.getOtherInfoList().length > 0){
                    msg = getString(resID, notificationData.getOtherInfoList()[0]);
                }
            } else {
                msg = notificationData.getTitle();
            }

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    getString(R.string.app_name),
                    msg,
                    notificationData.getIconUrl()
            );
            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();

        } else if (notificationData.getType() == NotificationType.CRM) {
            //Store Type Crm
            PreferenceUtils.setTypeCrm(getApplication(), Integer.parseInt(notificationData.getOtherInfoList()[1]));
            PreferenceUtils.setSnNotifyCrm(getApplication(), notificationData.getSn());

            PendingIntent pendingIntent = PendingIntentNotify.getInstance().getPendingIntent(
                    this,
                    notificationData,
                    MainActivity.class,
                    MainActivity.class
            );

            ActionNotify actionNotify = new ActionNotify(
                    getString(R.string.app_name),
                    notificationData.getTitle(),
                    notificationData.getSubTitle(),
                    notificationData.getIconUrl()
            );
            ControllerNotify notifyAction = new ControllerNotify(this, actionNotify, pendingIntent, null);
            notifyAction.show();

        } else if (notificationData.getOtherInfoList()[0].equals("1")) {
            notificatonTitle = getString(R.string.you_have_a_reservation_confirm);
        } else if (notificationData.getOtherInfoList()[0].equals("2")) {
            notificatonTitle = getString(R.string.msg_6_3_1_check_in_successful);
        } else if (notificationData.getOtherInfoList()[0].equals("3")) {
            notificatonTitle = getString(R.string.you_have_a_reservation_reject);
        }

        //Create Chanel for android 8
        final String PRIMARY_CHANNEL = "default";

        //Set Notification
        if (!notificatonTitle.equals("")) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL);

            //Check
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.go2joy_notification_19);
            } else {
                mBuilder.setSmallIcon(R.drawable.go2joy_notification);

                //Set auto slide
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            }

            //Get Bitmap Image
            String url = notificationData.getIconUrl();
            Bitmap remote_picture = null;
            if (url != null) {
                try {
                    remote_picture = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Expand Notification
            if (remote_picture != null) {
                NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
                notiStyle.bigPicture(remote_picture);
                notiStyle.setBigContentTitle(notificatonTitle);

                if (notificationData.getSubTitle() != null) {
                    String subTitle = notificationData.getSubTitle();
                    notiStyle.setSummaryText(subTitle);
                }

                mBuilder.setLargeIcon(remote_picture);
                mBuilder.setStyle(notiStyle);
            } else if (notificationData.getSubTitle() != null && !notificationData.getSubTitle().equals("")) {
                NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
                notiStyle.bigText(notificationData.getSubTitle());
                mBuilder.setStyle(notiStyle);
            }

//            }else{
            if (notificationData.getSubTitle() == null || notificationData.getSubTitle().equals("")) {
                mBuilder.setContentTitle(getString(R.string.app_name));
                mBuilder.setContentText(notificatonTitle);
            } else {
                mBuilder.setContentTitle(notificatonTitle);
                mBuilder.setContentText(notificationData.getSubTitle());
            }
            //
            // check to display GET COUPON button
            if (notificationData.getTargetType() == ParamConstants.TARGET_TYPE_PROMOTION) {
                PendingIntent pendingIntent;
                if (HotelApplication.isActivityVisible()) {
                    pendingIntent = createIntentForButton(notificationData, MainActivity.class, notiId);
                    mBuilder.setContentIntent(createIntentForNotice(notificationData, MainActivity.class));
                } else {
                    pendingIntent = createIntentForButton(notificationData, SplashActivity.class, notiId);
                    mBuilder.setContentIntent(createIntentForNotice(notificationData, SplashActivity.class));
                }
                if (pendingIntent != null) {
                    mBuilder.addAction(new NotificationCompat.Action.Builder(0, "", null).build());
                    mBuilder.addAction(new NotificationCompat.Action.Builder(0, getString(R.string.btn_12_2_get_coupon), pendingIntent).build());// 0 = No Icon

                }
            } else {
                //Check App Open
                if (HotelApplication.isActivityVisible()) {

                    mBuilder.setContentIntent(intentFromForeground(notificationData));
                } else {
                    mBuilder.setContentIntent(intentFromBackground(notificationData));
                }
            }
            //
//            }

            //Set Sound
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(soundUri);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //For android 8
            if (mNotificationManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant") NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                            "OKOKO", NotificationManager.IMPORTANCE_DEFAULT);
                    chan1.setLightColor(Color.GREEN);
                    chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                    mNotificationManager.createNotificationChannel(chan1);
                }


                Notification notification = mBuilder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                //Set background for Android 5
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notification.color = ContextCompat.getColor(this, R.color.org);
                }

                mNotificationManager.notify(notiId, notification);

            }
        }

    }

    private int getRandomNumber() {
        return (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    }

    private PendingIntent createIntentForNotice(NotificationData data, Class<?> to) {

        Intent resultIntent = new Intent(HotelApplication.getContext(), to);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        resultIntent.putExtra("NotificationData", data);
        resultIntent.setAction(ParamConstants.INTENT_ACTION_OPEN_NOTICE);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(SplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createIntentForButton(NotificationData data, Class<?> to, int id) {
        Intent resultIntent = new Intent(HotelApplication.getContext(), to);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        resultIntent.putExtra("NotificationData", data);
        resultIntent.putExtra("NOTI_ID", id);
        resultIntent.setAction(ParamConstants.INTENT_ACTION_OPEN_MY_COUPON);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(SplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent intentFromBackground(NotificationData notificationData) {
        //Intent
        Intent resultIntent = new Intent(HotelApplication.getContext(), SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        resultIntent.putExtra("NotificationData", notificationData);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(SplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent intentFromForeground(NotificationData notificationData) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        resultIntent.putExtra("NotificationData", notificationData);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void playSoundForPopup() {
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this);
        noBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, noBuilder.build());
        }
    }
}
