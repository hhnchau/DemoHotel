package com.appromobile.hotel.gcm.action;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.utils.ParamConstants;

/**
 * Created by appro on 26/09/2017.
 */
public class PendingIntentNotify {
    private static PendingIntentNotify instance;

    public static PendingIntentNotify getInstance() {
        if (instance == null) {
            instance = new PendingIntentNotify();
        }
        return instance;
    }

    public PendingIntent getPendingIntent(Context context, NotificationData intentData, Class<?> parent, Class<?> gotoActivity) {

        Intent resultIntent = new Intent(context, gotoActivity);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        resultIntent.putExtra("NotificationData", intentData);
        resultIntent.setAction(ParamConstants.INTENT_ACTION_OPEN_NOTICE);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(parent);

        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
