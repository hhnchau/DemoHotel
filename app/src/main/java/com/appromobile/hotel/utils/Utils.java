package com.appromobile.hotel.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuan on 7/12/2016.
 */
public class Utils {
    public static boolean isOpenWifi(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } else {
            return false;
        }
    }

    //ping Google
    static boolean isInternetWork() {
        try {
            int timeoutMs = 2000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            MyLog.writeLog("isInternetWork----------------->" + e);
            return false;
        }
    }

    public static Location getLocationFromPref(Context context) {
        Location prefLocation = new Location("gps");
        try {
            prefLocation.setLatitude(Double.parseDouble(PreferenceUtils.getLatLocation(context)));
            prefLocation.setLongitude(Double.parseDouble(PreferenceUtils.getLongLocation(context)));
            return prefLocation;
        } catch (Exception e) {
            MyLog.writeLog("getLocationFromPref----------------->" + e);
            return null;

        }
    }

    public static String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            MyLog.writeLog("md5----------------->" + e);
        }
        return null;
    }

    public static String meterToKm(float distance) {
        String distanceStr;
        if (distance > 1000) {
            distanceStr = String.format(Locale.getDefault(), "%.1f", (distance / 1000)) + " km";
        } else {
            distanceStr = Integer.toString((int) distance) + "m";
        }
        return distanceStr;
    }

    public static String formatCurrency(int price) {
        String currency;
        DecimalFormat formatter = new DecimalFormat("#,###");
        currency = formatter.format(price);
        return currency;
    }

    public static String formatCurrencyK(int price) {
        price = price / 1000;
        return String.valueOf(price) + "K";
    }

    public static String formatDate(Context context, String date) {
        final SimpleDateFormat displayFormat = new SimpleDateFormat(context.getString(R.string.date_format_view), Locale.ENGLISH);
        final SimpleDateFormat requestFormat = new SimpleDateFormat(context.getString(R.string.date_format_request), Locale.ENGLISH);

        try {
            Date d = displayFormat.parse(date);
            return requestFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDisplayLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static float getScreenWidthPixel() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float getScreenHeightPixel() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private static float convertPixelToDP(float pixel) {
        float dpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return pixel * 160 / dpi;
    }

    public static String handlePictureRatio(String myData, float padding) {
        if (myData != null) {
            List<String> widthArray = new ArrayList<>();
            List<String> heightArray = new ArrayList<>();
            Matcher widthMatcher = Pattern.compile("width:(.*?)px").matcher(myData);
            Matcher heightMatcher = Pattern.compile("height:(.*?)px").matcher(myData);
            while (widthMatcher.find()) {
                widthArray.add(widthMatcher.group(1));
            }
            while (heightMatcher.find()) {
                heightArray.add(heightMatcher.group(1));
            }
            int size = widthArray.size();
            for (int i = 0; i < size; i++) {
                if (Float.parseFloat(widthArray.get(i)) < Utils.convertPixelToDP(Utils.getScreenWidthPixel() - padding)) {
                    continue;
                }
                myData = replacePictureHeight(widthArray.get(i), heightArray.get(i), myData, padding);
                myData = replacePictureWidth(widthArray.get(i), myData, padding);
            }
            return myData;
        }
        return "";
    }

    private static String replacePictureWidth(String oldWidth, String content, float padding) {

        String newWidth = String.valueOf(Utils.convertPixelToDP(Utils.getScreenWidthPixel()) - padding);
        content = content.replace("width:" + oldWidth + "px", "width:" + newWidth + "px");
        return content;
    }

    private static String replacePictureHeight(String oldWidth, String oldHeight, String content, float padding) {
        float screenWidth = Utils.getScreenWidthPixel();
        float newHeight = screenWidth * Float.parseFloat(oldHeight) / Float.parseFloat(oldWidth);
        newHeight = Utils.convertPixelToDP(newHeight) - padding;
        content = content.replace("height:" + oldHeight + "px", "height:" + newHeight + "px");
        return content;
    }

    public static String getMapRootUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    public static String removeAccent(String s) {
        if (s != null) {
            String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
        }
        return "";
    }

    public static void hideKeyboard(Context context, View txtEdit) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(txtEdit.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void showKeyboard(Context context, EditText txtEdit) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(txtEdit, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static int roundUp(int n, int interval) {
        return (n + interval - 1) / interval * interval;
    }

    public static String getFileFromPath(String urlPath) {
        try {
            if (!urlPath.equals("")) {
                URL url = new URL(urlPath);
                String fileURL = url.getFile();
                return fileURL.substring(fileURL.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0].split("\\&")[0];
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            MyLog.writeLog("getFileFromPath----------------->" + e);
        }
        return "file_" + System.currentTimeMillis();
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle != null && needle.equals(""))
            return true;
        if (haystack == null || needle == null || haystack.equals(""))
            return false;

        Pattern p = Pattern.compile(needle, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
        Matcher m = p.matcher(haystack);
        return m.find();
    }

    public static boolean searchInString(String content, String value) {
        if (content != null && value != null) {
            Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
            Matcher matcher = pattern.matcher(content);
            return matcher.find();
        }
        return false;
    }

    public static class checkInternet extends AsyncTask<Context, Void, Integer> {
        @Override
        protected Integer doInBackground(Context... contexts) {
            if (isOpenWifi(contexts[0])) {
                if (isInternetWork()) {
                    // Internet work successfully
                    return 1;
                } else {
                    // Connected to wifi but internet not working
                    return 2;
                }
            } else {
                // Not open wifi
                return 3;
            }
        }
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static float getDistanceBetween2Location(Location oldLocation, Location newLocation) {
        return newLocation.distanceTo(oldLocation);
    }

    public static Bitmap drawableToBitmap(Context context, int res) {
        return BitmapFactory.decodeResource(context.getResources(), res);
    }

    private void getSHA(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                MyLog.writeLog("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            MyLog.writeLog("MainActivity PackageInfo---------------------------------->" + e);
            // to display Service AgreementPopup

        }
    }

    public static Bitmap addTextToDrawable(Activity context, int resource, String txt) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource).copy(Bitmap.Config.ARGB_8888, true);
        if (txt != null) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Medium.ttf");
            paint.setTypeface(font);
            paint.setTextSize(context.getResources().getDimension(R.dimen.flash_sale_text_maker));
            Canvas canvas = new Canvas(bitmap);

            canvas.drawText(txt, bitmap.getWidth() / 2, bitmap.getHeight() / 1.6f, paint);
        }

        return bitmap;
    }

    public static int[] getPromotionInfoForm(int sn) {
        int[] discount = new int[3];
        if (HotelApplication.mapPromotionInfoForm != null && HotelApplication.mapPromotionInfoForm.size() > 0) {
            PromotionInfoForm promotionInfoForm = HotelApplication.mapPromotionInfoForm.get(String.valueOf(sn));
            if (promotionInfoForm != null) {
                discount[0] = promotionInfoForm.getMaxHourlyDiscountMoney();
                discount[1] = promotionInfoForm.getMaxOvernightDiscountMoney();
                discount[2] = promotionInfoForm.getMaxDailyDiscountMoney();
            }
        }
        return discount;
    }
}
