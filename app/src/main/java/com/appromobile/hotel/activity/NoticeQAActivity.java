package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.AppNoticeAdapter;
import com.appromobile.hotel.adapter.AppQAAdapter;
import com.appromobile.hotel.model.view.AppNoticeForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.CounselingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/1/2016.
 */
public class NoticeQAActivity extends BaseActivity implements View.OnClickListener {
    ListView lvNotice, lvQA;
    int offsetNotice = 0, offsetQA;
    private List<AppNoticeForm> appNoticeForms = new ArrayList<>();
    private List<CounselingForm> counselingForms = new ArrayList<>();
    private AppNoticeAdapter appNoticeAdapter;
    SwipeRefreshLayout swipeContainerNotice, swipeContainerQA;
    private int scrollStateCurrent;
    private boolean isEndListNotice = false, isEndListQA;
    private TextView tvMessage;
    ImageView btnAddQA;
    private AppQAAdapter appQAAdapter;
    int currentTab = 0;
    private AppUserForm appUserForm;
    TextViewSFBold tvTab1, tvTab2;
    LinearLayout tabNotice, tabQA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("NoticeQAActivity" + e);
        }

        setContentView(R.layout.notice_qa_activity);
        appUserForm = PreferenceUtils.getAppUser(this);
        btnAddQA =  findViewById(R.id.btnAddQA);
        tvMessage = findViewById(R.id.tvMessage);
        swipeContainerNotice =  findViewById(R.id.swipeContainerNotice);
        swipeContainerQA =  findViewById(R.id.swipeContainerQA);
        lvNotice =  findViewById(R.id.lvNotice);
        lvNotice.setOnItemClickListener(noticeItemClick);
        lvQA =  findViewById(R.id.lvQA);
        tvTab1 =  findViewById(R.id.tvTab1);
        tvTab2 =  findViewById(R.id.tvTab2);
        tabNotice =  findViewById(R.id.tabNotice);
        tabNotice.setOnClickListener(this);
        tabQA =  findViewById(R.id.tabQA);
        tabQA.setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });


        swipeContainerNotice.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (appNoticeAdapter != null) {
                    offsetNotice = 0;
                    appNoticeForms.clear();
                    appNoticeAdapter.notifyDataSetChanged();
                    isEndListNotice = false;
                }
                initNoticeData();
            }
        });

        swipeContainerQA.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if(appQAAdapter != null){
                    offsetQA = 0;
                    counselingForms.clear();
                    appQAAdapter.notifyDataSetChanged();
                    isEndListQA = false;
                }
                initQAData();
            }
        });

        lvNotice.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                    if (visibleItemCount != 0 && totalItemCount != 0) {
                        if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndListNotice) {
                            MyLog.writeLog("Offset Loadmore: " + offsetNotice);
                            DialogUtils.showLoadingProgress(NoticeQAActivity.this, false);
                            initNoticeData();
                        }
                    }
                }
            }
        });

        lvQA.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                    if (visibleItemCount != 0 && totalItemCount != 0) {
                        if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndListQA) {
                            DialogUtils.showLoadingProgress(NoticeQAActivity.this, false);
                            initQAData();
                        }
                    }
                }
            }
        });


        lvQA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoticeQAActivity.this, QADetailActivity.class);
                intent.putExtra("Title", counselingForms.get(position).getTitle());
                intent.putExtra("counselingSn", counselingForms.get(position).getSn());
                intent.putExtra("Date", counselingForms.get(position).getLastUpdate());
                intent.putExtra("Nickname", counselingForms.get(position).getAppUserNickName());
                intent.putExtra("AppUserForm", appUserForm);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        btnAddQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddQA();
            }
        });
        DialogUtils.showLoadingProgress(this, false);
        initNoticeData();
    }

    private void showAddQA() {
        Intent intent = new Intent(this, WriteAQuestionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    AdapterView.OnItemClickListener noticeItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(NoticeQAActivity.this, NoticeAppDetailActivity.class);
            intent.putExtra("AppNoticeForm", appNoticeForms.get(position));
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    };

    private void initNoticeData() {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offsetNotice);
        params.put("limit", HotelApplication.LIMIT_REQUEST);
        //
        HotelApplication.serviceApi.findLimitAppNoticeList(params, PreferenceUtils.getToken(NoticeQAActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<AppNoticeForm>>() {
            @Override
            public void onResponse(Call<List<AppNoticeForm>> call, Response<List<AppNoticeForm>> response) {
                DialogUtils.hideLoadingProgress();

                List<AppNoticeForm> list = response.body();
                if (response.isSuccessful()) {
                    if (list == null || list.size() == 0) {
                        isEndListNotice = true;
                    }

                    if (list != null) {
                        appNoticeForms.addAll(list);
                    }
                    if (appNoticeAdapter == null) {
                        appNoticeAdapter = new AppNoticeAdapter(NoticeQAActivity.this, appNoticeForms);
                        lvNotice.setAdapter(appNoticeAdapter);
                    } else {
                        appNoticeAdapter.updateData(appNoticeForms);
                    }
                    offsetNotice = appNoticeAdapter.getCount();

                    if (appNoticeAdapter.getCount() > 0) {
                        lvNotice.setAdapter(appNoticeAdapter);
                    } else {
                        lvNotice.setEmptyView(tvMessage);
                    }
                } else {
                    lvNotice.setEmptyView(tvMessage);
                }
                swipeContainerNotice.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<AppNoticeForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                swipeContainerNotice.setRefreshing(false);
                Toast.makeText(NoticeQAActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabNotice:
                setButtonName("BSetNotice");
                currentTab = 0;
                setupTab(currentTab);
                break;
            case R.id.tabQA:
                setButtonName("BSetQNA");
                currentTab = 1;
                setupTab(currentTab);
                break;
        }
    }

    public void setupTab(int tab) {
        lvNotice.setVisibility(View.GONE);
        lvQA.setVisibility(View.GONE);
        swipeContainerNotice.setVisibility(View.GONE);
        swipeContainerQA.setVisibility(View.GONE);
        btnAddQA.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        switch (tab) {
            case 0:
                tabQA.setBackgroundColor(getResources().getColor(R.color.transparent));
                tabNotice.setBackgroundResource(R.drawable.box_tab0_white_bg);
                tvTab1.setTextColor(getResources().getColor(R.color.org));
                tvTab2.setTextColor(getResources().getColor(R.color.bk));
                lvNotice.setVisibility(View.VISIBLE);
                swipeContainerNotice.setVisibility(View.VISIBLE);
                if (appNoticeForms.size() == 0) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(getString(R.string.done_have_notice));
                } else if (appNoticeForms.size() > 0) {
                    tvMessage.setVisibility(View.GONE);
                }
                break;
            case 1:
                tabNotice.setBackgroundColor(getResources().getColor(R.color.transparent));
                tabQA.setBackgroundResource(R.drawable.box_tab1_white_bg);
                tvTab2.setTextColor(getResources().getColor(R.color.org));
                tvTab1.setTextColor(getResources().getColor(R.color.bk));

                lvQA.setVisibility(View.VISIBLE);
                swipeContainerQA.setVisibility(View.VISIBLE);
                btnAddQA.setVisibility(View.VISIBLE);
                if (counselingForms.size() == 0) {
                    initQAData();
                } else {
                    tvMessage.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initQAData() {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offsetQA);
        params.put("limit", HotelApplication.LIMIT_REQUEST);
        HotelApplication.serviceApi.findLimitCounselingList(params, PreferenceUtils.getToken(NoticeQAActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<CounselingForm>>() {
            @Override
            public void onResponse(Call<List<CounselingForm>> call, Response<List<CounselingForm>> response) {
                DialogUtils.hideLoadingProgress();
                List<CounselingForm> list = response.body();
                if (response.isSuccessful()) {
                    if (list == null || list.size() == 0) {
                        isEndListQA = true;
                    }

                    if (list != null) {
                        counselingForms.addAll(list);
                    }
                    if (appQAAdapter == null) {
                        appQAAdapter = new AppQAAdapter(NoticeQAActivity.this, counselingForms);
                        lvQA.setAdapter(appQAAdapter);
                    } else {
                        appQAAdapter.updateData(counselingForms);
                    }
                    offsetQA = appQAAdapter.getCount();

                    if (appQAAdapter.getCount() > 0) {
                        lvQA.setAdapter(appQAAdapter);
                    } else {
                        tvMessage.setText(getString(R.string.done_have_question));
                        lvQA.setEmptyView(tvMessage);
                    }
                } else {
                    tvMessage.setText(getString(R.string.done_have_question));
                    lvQA.setEmptyView(tvMessage);
                }
                swipeContainerQA.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<CounselingForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                swipeContainerQA.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentTab == 1) {
            offsetQA = 0;
            counselingForms.clear();
            initQAData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetNotice";
    }
}
