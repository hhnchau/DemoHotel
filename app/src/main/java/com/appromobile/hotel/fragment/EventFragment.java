package com.appromobile.hotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.PromotionDetailActivity;
import com.appromobile.hotel.adapter.EventAdapter;
import com.appromobile.hotel.adapter.PromotionAdapter;
import com.appromobile.hotel.model.view.PromotionForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 6/24/2016.
 */
public class EventFragment extends BaseFragment implements View.OnClickListener {
    private ListView lvPromotion, lvEvent;
    private int offsetPromotion = 0, offsetEvent;
    //private SwipeRefreshLayout swipeContainerPromotion, swipeContainerEvent;
    private boolean isEndListEvent = false, isEndListPromotion = false;
    List<PromotionForm> promotionForms = new ArrayList<>();
    List<PromotionForm> promotionFormEvents = new ArrayList<>();
    private PromotionAdapter promotionAdapter;
    private EventAdapter eventAdapter;
    private TextViewSFRegular tvMessage;
    private TextViewSFRegular tvTab1, tvTab2;
    private int scrollStateCurrent;
    private int currentTab = 0;

    public EventFragment() {
        setScreenName("SPromo");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_fragment, container, false);
        //swipeContainerEvent = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainerEvent);
        //swipeContainerPromotion = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainerPromotion);
        lvEvent = rootView.findViewById(R.id.lvEvent);
        tvMessage = rootView.findViewById(R.id.tvMessage);
        tvTab1 = rootView.findViewById(R.id.tvTab1);
        tvTab2 = rootView.findViewById(R.id.tvTab2);
        tvTab1.setTextColor(getResources().getColor(R.color.org));
        lvPromotion = rootView.findViewById(R.id.lvPromotion);
        rootView.findViewById(R.id.tabPromotion).setOnClickListener(this);
        rootView.findViewById(R.id.tabEvent).setOnClickListener(this);

//        swipeContainerPromotion.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                offsetPromotion = 0;
//                promotionForms.clear();
//                promotionAdapter.notifyDataSetChanged();
//                isEndListPromotion = false;
//                initDataPromotion();
//            }
//        });

        lvPromotion.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                    if (visibleItemCount != 0 && totalItemCount != 0) {
                        if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndListPromotion) {
//                            DialogUtils.showLoadingProgress(getContext(), false);
                            initDataPromotion();
                        }
                    }
                }
            }
        });

//        swipeContainerEvent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                offsetEvent = 0;
//                promotionFormEvents.clear();
//                eventAdapter.notifyDataSetChanged();
//                isEndListEvent = false;
//                initDataEvent();
//            }
//        });

        lvEvent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                    if (visibleItemCount != 0 && totalItemCount != 0) {
                        if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndListEvent) {
//                            DialogUtils.showLoadingProgress(getContext(), false);
                            initDataEvent();
                        }
                    }
                }
            }
        });

        lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getContext(), PromotionDetailActivity.class);
                    intent.putExtra("promotionSn", promotionFormEvents.get(position).getSn());
                    //Type Promotion
                    getActivity().startActivityForResult(intent, ParamConstants.EVENT_PROMOTION_REQUEST);
                    getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                }
            }
        });

        lvPromotion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getContext(), PromotionDetailActivity.class);
                    intent.putExtra("promotionSn", promotionForms.get(position).getSn());
                    //Type Promotion
                    getActivity().startActivityForResult(intent, ParamConstants.EVENT_PROMOTION_REQUEST);
                    getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                }
            }
        });

//        initDataPromotion();
//        initDataEvent();
        if (eventAdapter != null) {
            offsetEvent = 0;
            promotionFormEvents.clear();
            eventAdapter.notifyDataSetChanged();
            isEndListEvent = false;
        }
        initDataEvent();
        if (promotionAdapter != null) {
            offsetPromotion = 0;
            promotionForms.clear();
            promotionAdapter.notifyDataSetChanged();
            isEndListPromotion = false;
        }
        initDataPromotion();
        return rootView;
    }

    /*
    / Get Promotion
    */
    private void initDataPromotion() {
//        DialogUtils.showLoadingProgress(getContext(), false);
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offsetPromotion);
        params.put("limit", HotelApplication.LIMIT_REQUEST);

        MyLog.writeLog("TimeMeasurableBeginApi: " + Calendar.getInstance().getTimeInMillis());
        HotelApplication.serviceApi.findLimitPromotionList(params, PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<PromotionForm>>() {
            @Override
            public void onResponse(Call<List<PromotionForm>> call, Response<List<PromotionForm>> response) {
                MyLog.writeLog("TimeMeasurableFinishedApi: " + Calendar.getInstance().getTimeInMillis());
//                swipeContainerPromotion.setRefreshing(false);
//                DialogUtils.hideLoadingProgress();

                List<PromotionForm> list = response.body();

                if (response.isSuccessful()) {
                    if (list == null || list.size() == 0) {
                        MyLog.writeLog("Offset Loadmore: No");
                        isEndListPromotion = true;
                    }
                    if (list != null) {
                        promotionForms.addAll(list);
                        if (promotionAdapter == null) {
                            promotionAdapter = new PromotionAdapter(getContext(), promotionForms);
                        } else {
                            promotionAdapter.updateData(promotionForms);
                        }
                        offsetPromotion = promotionAdapter.getCount();
                        if (promotionAdapter.getCount() > 0) {
                            lvPromotion.setAdapter(promotionAdapter);
                        } else {
                            tvMessage.setText(getString(R.string.no_result_found));
                            lvPromotion.setEmptyView(tvMessage);
                        }
                    }
                } else {
                    tvMessage.setText(getString(R.string.no_result_found));
                    lvPromotion.setEmptyView(tvMessage);
                }
            }

            @Override
            public void onFailure(Call<List<PromotionForm>> call, Throwable t) {
//                swipeContainerPromotion.setRefreshing(false);
//                DialogUtils.hideLoadingProgress();
            }
        });
    }

    @Override
    public void onRefreshData() {
        super.onRefreshData();
        if (currentTab == 0) {
            offsetPromotion = 0;
            promotionForms.clear();
            promotionAdapter.notifyDataSetChanged();
            isEndListPromotion = false;
            initDataPromotion();
        } else {
            offsetEvent = 0;
            promotionFormEvents.clear();
            eventAdapter.notifyDataSetChanged();
            isEndListEvent = false;
            initDataEvent();
        }
    }

    private void initDataEvent() {
//        DialogUtils.showLoadingProgress(getContext(), false);
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offsetEvent);
        params.put("limit", HotelApplication.LIMIT_REQUEST);
        System.out.println("TimeMeasurableBeginApi: " + Calendar.getInstance().getTimeInMillis());
        HotelApplication.serviceApi.findLimitEventList(params, PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<PromotionForm>>() {
            @Override
            public void onResponse(Call<List<PromotionForm>> call, Response<List<PromotionForm>> response) {
                System.out.println("TimeMeasurableFinishedApi: " + Calendar.getInstance().getTimeInMillis());
//                swipeContainerEvent.setRefreshing(false);
//                DialogUtils.hideLoadingProgress();
                List<PromotionForm> list = response.body();
                if (response.isSuccessful()) {
                    if (list == null || list.size() == 0) {
                        System.out.println("Offset Loadmore: No");
                        isEndListEvent = true;
                    }

                    if (list != null) {
                        promotionFormEvents.addAll(list);
                        if (eventAdapter == null) {
                            eventAdapter = new EventAdapter(getContext(), promotionFormEvents);
                        } else {
                            eventAdapter.updateData(promotionFormEvents);
                        }
                        offsetEvent = eventAdapter.getCount();
                        if (eventAdapter.getCount() > 0) {
                            lvEvent.setAdapter(eventAdapter);
                        } else {
                            tvMessage.setText(getString(R.string.no_result_found));
                            lvEvent.setEmptyView(tvMessage);
                        }
                    }
                } else {
                    tvMessage.setText(getString(R.string.no_result_found));
                    lvEvent.setEmptyView(tvMessage);
                }
            }

            @Override
            public void onFailure(Call<List<PromotionForm>> call, Throwable t) {
                //Toast.makeText(getActivity(), getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabEvent:
                setButtonName("BPromoTab2");
                setTab(1);
                tvTab2.setTextColor(getResources().getColor(R.color.org));
                tvTab1.setTextColor(getResources().getColor(R.color.bk));
                currentTab = 1;
                break;
            case R.id.tabPromotion:
                setButtonName("BPromoTab1");
                setTab(0);
                tvTab1.setTextColor(getResources().getColor(R.color.org));
                tvTab2.setTextColor(getResources().getColor(R.color.bk));
                currentTab = 0;
                break;
        }
    }

    private void setTab(final int tab) {
        switch (tab) {
            case 0:
                lvPromotion.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.GONE);
//                swipeContainerEvent.setVisibility(View.GONE);
//                swipeContainerPromotion.setVisibility(View.VISIBLE);
                break;
            case 1:
                lvPromotion.setVisibility(View.GONE);
                lvEvent.setVisibility(View.VISIBLE);
//                swipeContainerEvent.setVisibility(View.VISIBLE);
//                swipeContainerPromotion.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(eventAdapter!=null) {
//            offsetEvent = 0;
//            promotionFormEvents.clear();
//            eventAdapter.notifyDataSetChanged();
//            isEndListEvent = false;
//        }
//        initDataEvent();
//        if(promotionAdapter!=null){
//            offsetPromotion = 0;
//            promotionForms.clear();
//            promotionAdapter.notifyDataSetChanged();
//            isEndListPromotion = false;
//        }
//        initDataPromotion();
    }
}
