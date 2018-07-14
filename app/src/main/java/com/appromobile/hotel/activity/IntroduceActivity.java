package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.IntroduceAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.model.view.Introduce;
import com.appromobile.hotel.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by appro on 03/05/2018.
 */

public class IntroduceActivity extends BaseActivity {
    public static Activity introduceActivity;


    private TextView btnStartNow;

    private int dots_count;
    private ImageView[] dots;
    private int currentItem = 0;

    private List<Introduce> list;

    @Override
    public void setScreenName() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_activity);
        introduceActivity = this;

        initView();

    }


    private void initView() {
        btnStartNow = findViewById(R.id.tvStart);

        final ViewPager viewPager = findViewById(R.id.item_viewPager);
        LinearLayout indicator = findViewById(R.id.indicator_item_viewpager);

        list = new ArrayList<>();
        list.add(new Introduce(getString(R.string.txt_6_15_flash_sale_title), getString(R.string.txt_6_15_flash_sale_content), R.drawable.intro_top1, R.drawable.intro_flashsale));
        list.add(new Introduce(getString(R.string.txt_6_15_stamp_title), getString(R.string.txt_6_15_stamp_content), R.drawable.intro_top2, R.drawable.intro_stamp));
        list.add(new Introduce(getString(R.string.txt_6_15_promotion_title), getString(R.string.txt_6_15_promotion_content), R.drawable.intro_top3,R.drawable.intro_promotion));
        list.add(new Introduce(getString(R.string.txt_6_15_map_title), getString(R.string.txt_6_15_map_content), R.drawable.intro_top4,R.drawable.intro_map));
        list.add(new Introduce(getString(R.string.txt_6_15_booking_title), getString(R.string.txt_6_15_booking_content), R.drawable.intro_top5,R.drawable.intro_booking));

        IntroduceAdapter popupCenterAdapter = new IntroduceAdapter(list);
        viewPager.setAdapter(popupCenterAdapter);

        currentItem = 0;

        initDot(this, indicator, popupCenterAdapter.getCount());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentItem = position;

                updateDot(IntroduceActivity.this, currentItem);

                updateView(currentItem);


            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnStartNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem == list.size() - 1) {
                    //Stop Introduce
                    PreferenceUtils.setIntroduce(IntroduceActivity.this, false);

                    ControllerApi.getmInstance().sendCrmNotification();

                    finish();
                } else {
                    autoScrollItem(IntroduceActivity.this, viewPager, dots_count, currentItem);
                }
            }
        });
        findViewById(R.id.tvSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroduceActivity.this, SignupActivity.class);
                intent.setAction("Introduce");
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);

                //Stop Introduce
                PreferenceUtils.setIntroduce(IntroduceActivity.this, false);

                //ControllerApi.getmInstance().sendCrmNotification();

                //finish();
            }
        });

    }

    private void updateView(int position) {
        if (position == list.size() - 1)
            btnStartNow.setText(getString(R.string.txt_6_15_start));
        else
            btnStartNow.setText(getString(R.string.txt_6_15_skip));
    }

    private void initDot(Context context, LinearLayout indicator, int totalDot) {
        dots_count = totalDot;
        dots = new ImageView[dots_count];

        if (dots_count > 1) {
            for (int i = 0; i < dots_count; i++) {
                dots[i] = new ImageView(context);
                dots[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_non_active_org));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                indicator.addView(dots[i], params);
            }

            //Set Default
            dots[0].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_active_org));
        }
    }

    private void updateDot(Context context, int currentItem) {
        if (dots_count > 1) {
            for (int i = 0; i < dots_count; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_non_active_org));
            }

            dots[currentItem].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dot_active_org));

        }
    }

    private void autoScrollItem(Context context, final ViewPager viewPager, final int totalItem, final int position) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (viewPager != null && viewPager.getAdapter() != null) {
                    if (totalItem > 1) {
                        if (position < totalItem - 1) {
                            viewPager.setCurrentItem(position + 1, true);
                        } else {
                            viewPager.setCurrentItem(0, true);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
