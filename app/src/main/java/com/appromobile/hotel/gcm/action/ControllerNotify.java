package com.appromobile.hotel.gcm.action;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.appromobile.hotel.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by appro on 26/09/2017.
 */
public class ControllerNotify {
    //Create Chanel for android 8
    final String PRIMARY_CHANNEL = "default";

    private Context context;
    private ActionNotify actionNotify;
    private PendingIntent pendingIntent;
    private List<ButtonNotify> buttonNotifyList;

    public ControllerNotify(Context context, ActionNotify actionNotify, PendingIntent pendingIntent, List<ButtonNotify> buttonNotifyList) {
        this.context = context;
        this.actionNotify = actionNotify;
        this.pendingIntent = pendingIntent;
        this.buttonNotifyList = buttonNotifyList;
    }

    public void show() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.go2joy_notification_19);
        } else {
            mBuilder.setSmallIcon(R.drawable.go2joy_notification);
        }

        String linkImage = actionNotify.getUrlIcon();

        if (linkImage != null) {

            Bitmap img = getImage(linkImage);

            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            notiStyle.bigPicture(img);

            if (actionNotify.getSubTitle().equals("")) {
                notiStyle.setBigContentTitle(actionNotify.getAppName());
                notiStyle.setSummaryText(actionNotify.getMainTitle());
            }else{
                notiStyle.setBigContentTitle(actionNotify.getMainTitle());
                notiStyle.setSummaryText(actionNotify.getSubTitle());
            }

            mBuilder.setLargeIcon(img);
            mBuilder.setStyle(notiStyle);

        } else if (!actionNotify.getSubTitle().equals("")) {
            NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
            notiStyle.bigText(actionNotify.getSubTitle());
            mBuilder.setStyle(notiStyle);
        }

        if (actionNotify.getSubTitle().equals("")) {
            mBuilder.setContentTitle(actionNotify.getAppName());
            mBuilder.setContentText(actionNotify.getMainTitle());
        } else {
            mBuilder.setContentTitle(actionNotify.getMainTitle());
            mBuilder.setContentText(actionNotify.getSubTitle());
        }

        //Add Intent
        mBuilder.setContentIntent(pendingIntent);

        //Add icon Button
        if (buttonNotifyList != null) {
            for (ButtonNotify item : buttonNotifyList) {
                mBuilder.addAction(item.getDrawableIcon(), item.getTitle(), item.getPendingIntent());
            }
        }

        //Sound
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(soundUri);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification.color = ContextCompat.getColor(context, R.color.org);
            }

            mNotificationManager.notify(getRandomId(), notification);
        }

    }

    private Bitmap getImage(String url) {
        Bitmap bitmap = null;
        if (url != null) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private int getRandomId() {
        return (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    }

}
