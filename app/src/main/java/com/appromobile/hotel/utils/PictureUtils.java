package com.appromobile.hotel.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thanh on 3/8/2017.
 */

public class PictureUtils {
    private static PictureUtils instance = null;

    private PictureUtils() {
    }

    public static PictureUtils getInstance() {
        if (instance == null) {
            instance = new PictureUtils();
        }
        return instance;
    }

    public void load(String url, int width, int height, int placeholder, ImageView view) {
        if (view != null && view.getContext() != null) {
            GlideApp
                    .with(view.getContext())
                    .load(url)
                    .override(width, height)
                    .placeholder(placeholder)
                    .transition(withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
        }
    }

    public void clearCache(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
        thread.start();
    }
}

