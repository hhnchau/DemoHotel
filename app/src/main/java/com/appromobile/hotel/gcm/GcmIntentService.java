package com.appromobile.hotel.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by xuanquach on 1/26/15.
 */
public class GcmIntentService extends IntentService{

    private final int CHAT_NOTIFICATION_ID=1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    String type= "";
    String cid="";
    String uid="";
    String message="";

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            Bundle extras = intent.getExtras();


            if (extras != null && !extras.isEmpty()) {
                type = extras.getString("type");
                cid = extras.getString("cid");
                uid = extras.getString("uid");
                message = extras.getString("msg");

            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    private void showUserProfileDetail(String uid, String message) {
//        try {
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.app_icon)
//                            .setContentTitle(message);
//
//            Intent resultIntent =new Intent(this, TodayCardViewDetailActivity.class);
//            resultIntent.putExtra("profileId", uid);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//            mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
////            Calendar calendar = Calendar.getInstance();
//            mNotificationManager.notify(Integer.parseInt(uid), mBuilder.build());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void showPopupOnly(String message) {
//        try {
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.app_icon)
//                            .setContentTitle(message);
//
//            Intent resultIntent = new Intent(this, MainActivity.class);
//            resultIntent.putExtra("InitTabNumber", 3);
//
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//            mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
////            Calendar calendar = Calendar.getInstance();
//            mNotificationManager.notify(CHAT_NOTIFICATION_ID, mBuilder.build());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
