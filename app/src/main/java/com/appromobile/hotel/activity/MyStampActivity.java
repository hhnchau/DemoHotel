package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.AdapterStamp;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.model.view.UserStampForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 25/12/2017.
 */

public class MyStampActivity extends BaseActivity {
    private ImageView imgBack;
    private TextView tvTitle;
    private ListView listStamp;
    private TextView txtNoData;
    private ImageView imgNoData;
    private List<UserStampForm> listUserStamp;
    private AdapterStamp adapterStamp;
    private final static int limit = 20;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean loadNew = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_stamp_activity);
        initView();
        findLimitUserStamp();
    }

    @SuppressWarnings("unchecked")
    private void findLimitUserStamp() {
        isLoading = true;
        ControllerApi.getmInstance().findLimitUserStampFormListForMobile(this, limit, offset, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {
                if (list != null && list.size() > 0) {

                    listUserStamp.addAll((List<UserStampForm>) (Object) list);
                    adapterStamp.notifyDataSetChanged();
                    txtNoData.setVisibility(View.GONE);
                    imgNoData.setVisibility(View.GONE);

                    offset = adapterStamp.getCount();

                    isLoading = offset < limit;

                } else {
                    if (loadNew) {
                        //show no result
                        txtNoData.setVisibility(View.VISIBLE);
                        imgNoData.setVisibility(View.VISIBLE);
                        listStamp.setEmptyView(txtNoData);

                        loadNew = false;
                    } else {
                        //Set End List
                        isLoading = true;
                    }

                }

            }
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);
        txtNoData = findViewById(R.id.txtNoData);
        imgNoData = findViewById(R.id.imgNoData);
        listStamp = findViewById(R.id.lvStamp);
        listUserStamp = new ArrayList<>();
        adapterStamp = new AdapterStamp(this, listUserStamp);
        listStamp.setAdapter(adapterStamp);
        scrollListView();
    }

    //ListView Scroll
    private void scrollListView() {
        listStamp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && firstVisibleItem + visibleItemCount + 10 >= totalItemCount && totalItemCount != 0) {
                    findLimitUserStamp();
                }
            }
        });


        listStamp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoStampDetail(listUserStamp.get(position).getHotelSn());
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.stable);
            }
        });
    }

    private void gotoStampDetail(long hotelSn){
        Intent intent = new Intent(this, StampDetailActivity.class);
        intent.putExtra("hotelSn", hotelSn);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    @Override
    public void setScreenName() {

    }
}
