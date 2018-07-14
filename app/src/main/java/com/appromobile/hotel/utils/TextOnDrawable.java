package com.appromobile.hotel.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.appromobile.hotel.R;
import com.appromobile.hotel.panorama.LoadTaskComplete;

/**
 * Created by appro on 10/08/2017.
 */
public class TextOnDrawable extends AsyncTask<Void, Void, Bitmap> {
    private Context context;
    private int resource;
    private String price;
    private LoadTaskComplete loadTaskComplete;

    public TextOnDrawable(Context context, int resource, String price, LoadTaskComplete loadTaskComplete) {
        this.context = context.getApplicationContext();
        this.resource = resource;
        this.price = price;
        this.loadTaskComplete = loadTaskComplete;
    }


    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource).copy(Bitmap.Config.ARGB_8888, true);
        if (price != null && !price.equals("-1")) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Medium.ttf");
            paint.setTypeface(font);
            paint.setTextSize(context.getResources().getDimension(R.dimen.flash_sale_text_maker));
            Canvas canvas = new Canvas(bitmap);

            canvas.drawText(price, bitmap.getWidth() / 2, bitmap.getHeight() / 1.6f, paint);
        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        loadTaskComplete.onTaskComplete(bitmap);
    }
}
