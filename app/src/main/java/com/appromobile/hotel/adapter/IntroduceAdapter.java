package com.appromobile.hotel.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.Introduce;
import com.appromobile.hotel.picture.PictureGlide;

import java.util.List;

/**
 * Created by appro on 04/05/2018.
 */

public class IntroduceAdapter extends PagerAdapter {
    private List<Introduce> mList;

    public IntroduceAdapter(List<Introduce> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_introduce, container, false);

        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        ImageView intro_top = view.findViewById(R.id.intro_top);
        ImageView intro_img = view.findViewById(R.id.intro_image);

        Introduce introduce = mList.get(position);

        if (introduce != null) {
            title.setText(introduce.getTitle());
            content.setText(introduce.getContent());
            PictureGlide.getInstance().show(introduce.getIntroTop(), intro_top);
            PictureGlide.getInstance().show(introduce.getIntroImage(), intro_img);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
