package com.appromobile.hotel.panorama;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.appromobile.hotel.utils.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

/**
 * Created by appro on 22/09/2017.
 */
public class ImageLoaderTaskGlide extends AsyncTask<String, Void, Bitmap> {
    private Context context;
    private LoadTaskComplete loadTaskComplete;

    public ImageLoaderTaskGlide(Context context, LoadTaskComplete loadTaskComplete) {
        this.context = context;
        this.loadTaskComplete = loadTaskComplete;
    }

    protected Bitmap doInBackground(String... urls) {
        String link = urls[0];
        Bitmap bitmap = null;
        try {
            bitmap = GlideApp.with(context.getApplicationContext())
                    .asBitmap()
                    .load(link)
                    .apply(new RequestOptions().override(-1,-1))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        loadTaskComplete.onTaskComplete(result);
    }
}
