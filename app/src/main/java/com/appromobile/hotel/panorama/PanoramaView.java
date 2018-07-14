package com.appromobile.hotel.panorama;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.appromobile.hotel.utils.MyLog;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by appro on 30/06/2017.
 */
public class PanoramaView {
    public static void setView(VrPanoramaView view, Bitmap bitmap) {

        VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO;

        view.setTouchTrackingEnabled(true);
        view.setFullscreenButtonEnabled(false);
        view.setStereoModeButtonEnabled(false);
        view.setInfoButtonEnabled(false);
        view.loadImageFromBitmap(bitmap, options);
    }

    public static void pausePanoramaView(VrPanoramaView view) {
        view.pauseRendering();
    }

    public static void resumePanoramaView(VrPanoramaView view) {
        view.resumeRendering();
    }

    public static void destroyPanoramaView(VrPanoramaView view) {
        view.shutdown();
    }
}
