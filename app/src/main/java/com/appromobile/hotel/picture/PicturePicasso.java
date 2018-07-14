package com.appromobile.hotel.picture;

import android.widget.ImageView;

import com.appromobile.hotel.R;
import com.squareup.picasso.Picasso;

/**
 * Created by appro on 07/03/2018.
 */

public class PicturePicasso {
    private static PicturePicasso instance = null;

    public static PicturePicasso getInstance() {
        if (instance == null) {
            instance = new PicturePicasso();
        }
        return instance;
    }

    public void show(ImageView imageView, String url) {
        if (imageView.getContext() != null) {
            Picasso.with(imageView.getContext())
                    .load(url)
                    .placeholder(R.drawable.loading_small)
                    .fit()
                    .centerInside()
                    .into(imageView);
        }
    }
}
