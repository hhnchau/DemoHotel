package com.appromobile.hotel.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by APPRO on 15-12-15.
 *
 * Cache custom font avoid to reading from asset folder a lot
 *
 * Read more: https://nayaneshguptetechstuff.wordpress.com/2014/06/20/slow-activity-transition-using-custom-typeface-font/
 */
public class UtilityFont {

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    /**
     * ASSET_PATH is hard coded for example. It can be passed from different views in case different
     * fonts are expected.
     */

    public static final String SanFrancisco_Regular = "SanFrancisco_Regular";
    public static final String SanFrancisco_Heavy = "SanFrancisco_Heavy";
    public static final String SanFrancisco_Medium = "SanFrancisco_Medium";
    public static final String SanFrancisco_Bold = "SanFrancisco_Bold";

    public static Typeface getFont(Context c, String assetPath, String fontName) {
        synchronized (cache) {

            if (!cache.containsKey(assetPath)) {

                try {
                    Typeface t =(Typeface.createFromAsset(c.getAssets(), "fonts/" + fontName));
                    cache.put(assetPath, t);

                } catch (Exception e) {
                    MyLog.writeLog("UtilityFont----------------------->"+e);
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
