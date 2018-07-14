package com.appromobile.hotel.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.SearchHotelAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.DialogSuspend;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.request.HomeHotelRequest;
import com.appromobile.hotel.model.request.SearchHistoryDto;
import com.appromobile.hotel.model.view.CouponConditionForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/1/2016.
 */
public class HotelSearchResultActivity extends BaseActivity {
    TextViewSFRegular tvSearchText, tvSearchContent;
    ListView lvSearchHotel;
    private HomeHotelRequest homeHotelRequest;
    String searchText;
    int offset = 0;
    boolean isEndList = false;
    private List<HotelForm> hotelForms = new ArrayList<>();
    private int scrollStateCurrent;
    SwipeRefreshLayout swipeContainer;
    private SearchHotelAdapter adapter;
    private TextViewSFRegular tvNoResult;
    LinearLayout boxNoResult;
    ImageView btnSort;

    private int searchSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.hotel_search_result_activity);
        tvNoResult = findViewById(R.id.tvNoResult);
        tvSearchText = findViewById(R.id.tvSearchText);
        tvSearchContent = findViewById(R.id.tvSearchContent);
        boxNoResult = findViewById(R.id.boxNoResult);
        btnSort = findViewById(R.id.btnSort);
        searchText = getIntent().getStringExtra("SearchText");
        lvSearchHotel = findViewById(R.id.lvSearchHotel);
        swipeContainer = findViewById(R.id.swipeContainer);
        homeHotelRequest = new HomeHotelRequest();
        homeHotelRequest.setOffset(0);
        homeHotelRequest.setLimit(HotelApplication.LIMIT_REQUEST);
        String setMobileUserId = HotelApplication.DEVICE_ID;
        homeHotelRequest.setMobileUserId(setMobileUserId);
        homeHotelRequest.setKeyword(searchText);
        homeHotelRequest.setTypeSearch(2);

        tvSearchText.setText(searchText);
        tvSearchContent.setText(searchText);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                offset = 0;
                hotelForms.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                isEndList = false;
                initData();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvSearchHotel.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (adapter != null) {
                    if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                        if (visibleItemCount != 0 && totalItemCount != 0) {
                            if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndList) {
                                DialogUtils.showLoadingProgress(HotelSearchResultActivity.this, false);
                                initData();
                            }
                        }
                    }
                }
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDistance();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        lvSearchHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (hotelForms.get(position).getHotelStatus() == ContractType.SUSPEND.getType()) {
                        //Suspended
                        showDialogSuspended();
                    } else {
                        Intent intent = new Intent(HotelSearchResultActivity.this, HotelDetailActivity.class);
                        intent.putExtra("sn", hotelForms.get(position).getSn());
                        intent.putExtra("RoomAvailable", hotelForms.get(position).getRoomAvailable());
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Update Search to Server
                ControllerApi.getmInstance().updateSearchHistory(new SearchHistoryDto(hotelForms.get(position).getSn(), searchSn));
            }
        });

        DialogUtils.showLoadingProgress(this, false);
        initData();
    }

    private void showDialogSuspended() {
        DialogSuspend.getInstance().show(this);
    }

    private void sortDistance() {
        DialogUtils.showLoadingProgress(this, false);
        if (hotelForms != null) {
            Collections.sort(hotelForms, new Comparator<HotelForm>() {
                @Override
                public int compare(HotelForm hotelForm, HotelForm t1) {
                    return Double.compare(hotelForm.getDistance(getApplicationContext()), t1.getDistance(getApplicationContext()));
                }
            });

            if (adapter != null) {
                adapter.setHotelForms(hotelForms);
                adapter.notifyDataSetChanged();
            }
        }
        DialogUtils.hideLoadingProgress();
    }

    private void initData() {
        homeHotelRequest.setOffset(offset);
//        ControllerApi.getmInstance().searchHotelList(this, homeHotelRequest, new ResultApi() {
//            @Override
//            public void resultApi(Object object) {
//                RestResult restResult = (RestResult) object;
//                searchSn = restResult.getSn();
//                String otherInfo = restResult.getOtherInfo();
//                if (otherInfo != null) {
//
//                    Type listType = new TypeToken<List<HotelForm>>() {
//                    }.getType();
//
//                    List<HotelForm> list = new Gson().fromJson(otherInfo, listType);
//
//
//                    swipeContainer.setRefreshing(false);
//                    try {
//                        if (list == null || list.size() == 0 || list.size() < HotelApplication.LIMIT_REQUEST) {
//                            isEndList = true;
//                        }
//                        hotelForms.addAll(list);
//
//                        if (adapter == null) {
//                            adapter = new SearchHotelAdapter(HotelSearchResultActivity.this, hotelForms);
//                            lvSearchHotel.setAdapter(adapter);
//                        } else {
//                            adapter.updateData(hotelForms);
//                        }
//                        offset = adapter.getCount();
//
//                        if (hotelForms == null || (hotelForms != null && hotelForms.size() == 0)) {
////                        lvSearchHotel.setEmptyView(tvNoResult);
//                            boxNoResult.setVisibility(View.VISIBLE);
//                            swipeContainer.setVisibility(View.GONE);
//                        }
//                    } catch (Exception e) {
//                        boxNoResult.setVisibility(View.VISIBLE);
//                        swipeContainer.setVisibility(View.GONE);
//                    }
//
//                }
//            }
//        });


//        HotelApplication.serviceApi.getHotelList(homeHotelRequest.getMap(), PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
//            @Override
//            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
//                swipeContainer.setRefreshing(false);
////                lvHotel.onLoadMoreComplete();
//                DialogUtils.hideLoadingProgress();
//                List<HotelForm> list = response.body();
//                try {
//                    if (list == null || list.size() == 0 || list.size() < HotelApplication.LIMIT_REQUEST) {
//                        isEndList = true;
//                    }
//                    hotelForms.addAll(list);
//
//                    if (adapter == null) {
//                        adapter = new SearchHotelAdapter(HotelSearchResultActivity.this, hotelForms);
//                        lvSearchHotel.setAdapter(adapter);
//                    } else {
//                        adapter.updateData(hotelForms);
//                    }
//                    offset = adapter.getCount();
//
//                    if (hotelForms == null || (hotelForms != null && hotelForms.size() == 0)) {
////                        lvSearchHotel.setEmptyView(tvNoResult);
//                        boxNoResult.setVisibility(View.VISIBLE);
//                        swipeContainer.setVisibility(View.GONE);
//                    }
//                } catch (Exception e) {
//                    boxNoResult.setVisibility(View.VISIBLE);
//                    swipeContainer.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
//                swipeContainer.setRefreshing(false);
////                lvHotel.onLoadMoreComplete();
//                DialogUtils.hideLoadingProgress();
////                t.printStackTrace();
////                lvSearchHotel.setEmptyView(tvNoResult);
////                boxNoResult.setVisibility(View.VISIBLE);
////                swipeContainer.setVisibility(View.GONE);
//                Toast.makeText(HotelSearchResultActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSearchResult";
    }
}
