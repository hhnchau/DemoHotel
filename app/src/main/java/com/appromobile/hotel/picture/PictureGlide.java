package com.appromobile.hotel.picture;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.utils.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by thanh on 3/8/2017.
 */

public class PictureGlide {
    private static PictureGlide instance = null;

    private PictureGlide() {
    }

    public static PictureGlide getInstance() {
        if (instance == null) {
            instance = new PictureGlide();
        }
        return instance;
    }

    public void show(String url, int width, int height, int placeholder, ImageView view) {
        if (view != null && view.getContext() != null) {
            try {
                GlideApp
                        .with(view.getContext())
                        .load(url)
                        .override(width, height)
                        .placeholder(placeholder)
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show(String url, ImageView view) {
        if (view != null && view.getContext() != null) {
            try {
                GlideApp
                        .with(view.getContext())
                        .load(url)
                        .placeholder(R.drawable.loading_big)
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show(int drawable, ImageView view) {
        if (view != null && view.getContext() != null) {
            try {
                GlideApp
                        .with(view.getContext())
                        .load(drawable)
                        .placeholder(drawable)
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show(Uri uri, int width, int height, int placeholder, ImageView view) {
        if (view != null && view.getContext() != null) {
            try {
                GlideApp
                        .with(view.getContext())
                        .load(uri)
                        .override(width, height)
                        .placeholder(placeholder)
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show(Uri uri, ImageView view) {
        if (view != null && view.getContext() != null) {
            try {
                GlideApp
                        .with(view.getContext())
                        .load(uri)
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCache(final Context context) {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(context).clearDiskCache();
//            }
//        });
//        thread.start();
    }
}

