package com.appromobile.hotel.panorama;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.appromobile.hotel.utils.MyLog;

import java.io.InputStream;

/**
 * Created by appro on 29/06/2017.
 */

//  ImageLoaderTask imageLoaderTask=new ImageLoaderTask(new LoadTaskComplete(){
//  @Override
//  public void onTaskComplete(Bitmap bitmap){
//
//       }
//  });
//  imageLoaderTask.execute(MY_URL_STRING);

public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private LoadTaskComplete loadTaskComplete;

    public ImageLoaderTask(LoadTaskComplete loadTaskComplete) {
        this.loadTaskComplete = loadTaskComplete;
    }

    protected Bitmap doInBackground(String... urls) {
        String link = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(link).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            MyLog.writeLog("Load Panorama picture error:" + e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        loadTaskComplete.onTaskComplete(result);
    }

}
