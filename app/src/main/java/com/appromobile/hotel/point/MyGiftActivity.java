package com.appromobile.hotel.point;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.BaseActivity;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.model.view.MileageHistoryForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 26/03/2018.
 */

public class MyGiftActivity extends BaseActivity {
    private ImageView imgBack;
    private ListView list_gift;
    private MileagePointAdapter mileagePointAdapter;
    private List<MileageHistoryForm> listGift;
    private final static int limit = 20;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean loadNew = true;
    private TextView txtNoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_gift_activity);
        initView();
        findLimitMileageRewardForAppList();
    }

    @SuppressWarnings("unchecked")
    private void findLimitMileageRewardForAppList() {
        int type = 2;
        ControllerApi.getmInstance().findLimitMileageRewardForAppList(this, type, limit, offset, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {
                if (list != null && list.size() > 0) {

                    listGift.addAll((List<MileageHistoryForm>) (Object) list);
                    mileagePointAdapter.notifyDataSetChanged();
                    txtNoData.setVisibility(View.GONE);

                    offset = mileagePointAdapter.getCount();

                    isLoading = offset < limit;

                } else {
                    if (loadNew) {
                        //show no result
                        txtNoData.setVisibility(View.VISIBLE);
                        list_gift.setEmptyView(txtNoData);

                        loadNew = false;
                    } else {
                        //Set End List
                        isLoading = true;
                    }

                }
            }
        });
    }

    private void scrollListView() {
        list_gift.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && firstVisibleItem + visibleItemCount + 10 >= totalItemCount && totalItemCount != 0) {
                    findLimitMileageRewardForAppList();
                }
            }
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.btnClose);
        list_gift = findViewById(R.id.list_gift);
        txtNoData = findViewById(R.id.txtNoData);
        listGift = new ArrayList<>();
        mileagePointAdapter = new MileagePointAdapter(this, listGift);
        list_gift.setAdapter(mileagePointAdapter);

        scrollListView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.stable);
            }
        });
    }

    @Override
    public void setScreenName() {
    }
}
