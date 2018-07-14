package com.appromobile.hotel.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by appro on 18/05/2018.
 */

public class Resolution {

    public void get(Context context, View v) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                Log.d("resolution", "DENSITY_LOW: " + metrics.densityDpi);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Log.d("resolution", "DENSITY_MEDIUM: " + metrics.densityDpi);
                break;
            case DisplayMetrics.DENSITY_HIGH: //Dimens-->Normal
                //resizeView(v, 13f);
                Log.d("resolution", "DENSITY_HIGH: " + metrics.densityDpi);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                Log.d("resolution", "DENSITY_XHIGH: " + metrics.densityDpi);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                Log.d("resolution", "----->DENSITY_XXHIGH: " + metrics.densityDpi);
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                Log.d("resolution", "----->DENSITY_XXXHIGH: " + metrics.densityDpi);
                break;
        }

        StringBuilder stringBuilder = new StringBuilder().append("density: ");
        stringBuilder.append(metrics.density).append("\ndensityDpi: ");
        stringBuilder.append(metrics.densityDpi).append("\nwidthPixels: ");
        stringBuilder.append(metrics.widthPixels).append("\nheightPixels: ");
        stringBuilder.append(metrics.heightPixels);

        Toast.makeText(context, stringBuilder, Toast.LENGTH_LONG).show();

    }

    private void resizeView(View v, float size) {
        //Check View
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            //-----
        } else if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setTextSize(size);
        }
    }

    //SAMSUNG -XHDPI
    /*density;// = 2.0
    densityDpi;// = 320
    heightPixels;// = 1280
    noncompatDensity;// = 2.0
    noncompatDensityDpi;// = 320
    noncompatHeightPixels;// = 1280
    noncompatScaledDensity;// = 2.0
    noncompatWidthPixels;// = 720
    noncompatXdpi;// = 265.043
    noncompatYdpi;// = 264.325
    scaledDensity;// = 2.0
    widthPixels;// = 720
    xdpi;// = 265.043
    ydpi;// = 264.325*/

    //SONY - XHDPI
    /*density = 2.0
    densityDpi = 320
    heightPixels = 1184
    noncompatDensity = 2.0
    noncompatDensityDpi = 320
    noncompatHeightPixels = 1184
    noncompatScaledDensity = 2.0
    noncompatWidthPixels = 720
    noncompatXdpi = 294.967
    noncompatYdpi = 295.563
    scaledDensity = 2.0
    widthPixels = 720
    xdpi = 294.967
    ydpi = 295.563*/

    //NEXUS -XXHDPI
    /*density = 3.0
    densityDpi = 480
    heightPixels = 1776
    noncompatDensity = 3.0
    noncompatDensityDpi = 480
    noncompatHeightPixels = 1776
    noncompatScaledDensity = 3.0
    noncompatWidthPixels = 1080
    noncompatXdpi = 442.451
    noncompatYdpi = 443.345
    scaledDensity = 3.0
    widthPixels = 1080
    xdpi = 442.451
    ydpi = 443.345*/


    //NOTE 8
    /*desity: 2.625---3.5
    densitydpi: 420---560
    width: 1080-----1440
    height: 2094-----2792*/

    //S8 Plus
    /*desity: 4.0
    densitydpi: 640
    width: 1440
    height: 2560*/
}
