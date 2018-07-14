package com.appromobile.hotel.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import com.appromobile.hotel.R;

import java.util.List;

/**
 * Created by xuan on 7/13/2016.
 */
public class ShareUtils {
    public static void shareFacebook(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.facebook.katana");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }

    public static void shareViber(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.viber.voip")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.viber.voip");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }


    public static void shareSkype(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.skype.raider")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.skype.raider");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }


    public static void shareZalo(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.zing.zalo")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.zing.zalo");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }

    public void sendMail(String path) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"receiver@website.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Truiton Test Mail");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "This is an autogenerated mail from Truiton's InAppMail app");
        emailIntent.setType("image/png");
        Uri myUri = Uri.parse("file://" + path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public static void shareSMS(Context context, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, url);

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            context.startActivity(sendIntent);

        }
        else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address","phoneNumber");
            smsIntent.putExtra("sms_body",url);
            context.startActivity(smsIntent);
        }
    }

    public static void shareFacebookMessenger(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.facebook.orca")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.facebook.orca");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }

    public static void shareWhatsApp(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.whatsapp")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
           MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.whatsapp");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }

    public static void shareLine(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("jp.naver.line.android")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "jp.naver.line.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }

    public static void shareWeChat(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, url);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
        boolean isHaveApp = false;
        for (final ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains("com.tencent.mm")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                share.setComponent(name);
                context.startActivity(share);
                isHaveApp = true;
                break;
            }
        }
        if (!isHaveApp) {
            MyLog.writeLog("Do not Have Intent");
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.tencent.mm");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            context.startActivity(marketIntent);
        }
    }
}
