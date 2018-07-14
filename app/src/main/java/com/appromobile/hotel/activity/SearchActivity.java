package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.AdapterFlashSale;
import com.appromobile.hotel.adapter.SearchAdapter;
import com.appromobile.hotel.adapter.SearchHotelAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.dialog.DialogSuspend;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.RecyclerViewType;
import com.appromobile.hotel.model.request.SearchHistoryDto;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.SearchHistoryForm;
import com.appromobile.hotel.model.view.SectionSearchForm;
import com.appromobile.hotel.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xuan on 7/6/2016.
 */
public class SearchActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerViewType recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;
    private List<SectionSearchForm> sectionSearchFormList;
    private SearchAdapter adapterHistory;

    private ProgressBar progressBar;
    private ImageView icSearch, icClear;
    private EditText edtSearch;

    private int searchSn;
    private ListView lvSearchHotel;

    private SearchHotelAdapter adapterSearch;
    private List<HotelForm> hotelFormsList = new ArrayList<>();

    private String keyword = "";
    private int offset = 0;
    private int limit = 10;
    private boolean isLoading = false;
    private TextView txtNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        setContentView(R.layout.search_activity);

        //Don't show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        lvSearchHotel = findViewById(R.id.lvSearchHotel);
        txtNoData = findViewById(R.id.view_no_result);
        txtNoData.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerViewSearch);
        edtSearch = findViewById(R.id.txtSearch);
        progressBar = findViewById(R.id.progressBar);
        icSearch = findViewById(R.id.btnSearch);
        icClear = findViewById(R.id.btnClear);

        scrollListView();


        lvSearchHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (hotelFormsList.get(position).getHotelStatus() == ContractType.SUSPEND.getType()) {
                        //Suspended
                        showDialogSuspended();
                    } else {
                        Intent intent = new Intent(SearchActivity.this, HotelDetailActivity.class);
                        intent.putExtra("sn", hotelFormsList.get(position).getSn());
                        intent.putExtra("RoomAvailable", hotelFormsList.get(position).getRoomAvailable());
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Update Search to Server
                ControllerApi.getmInstance().updateSearchHistory(new SearchHistoryDto(hotelFormsList.get(position).getSn(), searchSn));
            }
        });


        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Utils.hideKeyboard(SearchActivity.this);
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        icClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            private Timer timer = new Timer();
            private final long DELAY = 700;

            @Override
            public void afterTextChanged(final Editable s) {
                if (!s.toString().equals("")) {

                    icClear.setVisibility(View.GONE);
                    icSearch.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    offset = 0;

                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    hotelFormsList.clear();
                                    keyword = s.toString();
                                    suggestSearch(keyword, offset, limit);
                                }
                            },
                            DELAY
                    );
                } else {

                    if (timer != null)
                        timer.cancel();

                    /*Show Icon*/
                    icClear.setVisibility(View.GONE);
                    icSearch.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    /*Show RecyclerView*/
                    recyclerView.setVisibility(View.VISIBLE);
                    lvSearchHotel.setVisibility(View.GONE);

                    findRecommend();
                }
            }
        });

        findRecommend();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    private void showDialogSuspended() {
        DialogSuspend.getInstance().show(this);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSearch";
    }

    private void setUpRecyclerView() {
        sectionSearchFormList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterHistory = new SearchAdapter(this, recyclerViewType, sectionSearchFormList);
        recyclerView.setAdapter(adapterHistory);
    }

    @SuppressWarnings("unchecked")
    private void findHistorySearch() {
        ControllerApi.getmInstance().findLimitSearchHistoryList(this, 0, 10, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {
                if (list != null && list.size() > 0) {

                    List<SearchHistoryForm> searchHistoryFormList = new ArrayList<>();
                    searchHistoryFormList.addAll((List<SearchHistoryForm>) (Object) list);

                    sectionSearchFormList.add(new SectionSearchForm(getString(R.string.search_history_search), searchHistoryFormList));

                    adapterHistory.notifyDataSetChanged();

                }
            }
        });
    }


    /*
    / Get Flash Sale Hotel
    */
    @SuppressWarnings("unchecked")
    private void findFlashSaleHotelList() {
        ControllerApi.getmInstance().findFlashSaleHotelList(this, 10, 0, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {

                if (list != null && list.size() > 0) {

                    List<HotelForm> listFlashSale = new ArrayList<>();
                    listFlashSale.addAll((List<HotelForm>) (Object) list);

                    List<SearchHistoryForm> searchHistoryFormList = new ArrayList<>();
                    for (int i = 0; i < listFlashSale.size(); i++) {
                        searchHistoryFormList.add(new SearchHistoryForm(
                                true,
                                listFlashSale.get(i).getName(),
                                listFlashSale.get(i).getAddress(),
                                false,
                                listFlashSale.get(i).getSn(),
                                listFlashSale.get(i).getHotelStatus()
                        ));
                    }

                    sectionSearchFormList.add(new SectionSearchForm(getString(R.string.search_flashsale_hotels), searchHistoryFormList));

                    adapterHistory.notifyDataSetChanged();
                }

            }
        });
    }


    private void suggestSearch(String _keyword, int _offset, int _limit) {

        Map<String, Object> params = new HashMap<>();
        params.put("offset", _offset);
        params.put("limit", _limit);
        params.put("keyword", _keyword);

        ControllerApi.getmInstance().searchHotelList(this, params, new ResultApi() {
            @Override
            public void resultApi(Object object) {

                /*Show Icon*/
                progressBar.setVisibility(View.GONE);
                if (edtSearch.getText() != null && !edtSearch.getText().toString().equals("")){
                    icClear.setVisibility(View.VISIBLE);
                    icSearch.setVisibility(View.GONE);
                }else {
                    icClear.setVisibility(View.GONE);
                    icSearch.setVisibility(View.VISIBLE);
                }

                /*Hide RecyclerView*/
                    recyclerView.setVisibility(View.GONE);
                lvSearchHotel.setVisibility(View.VISIBLE);

                RestResult restResult = (RestResult) object;
                searchSn = restResult.getSn();
                String otherInfo = restResult.getOtherInfo();
                if (otherInfo != null) {

                    Type listType = new TypeToken<List<HotelForm>>() {
                    }.getType();

                    List<HotelForm> list = new Gson().fromJson(otherInfo, listType);

                    if (list == null || list.size() == 0) {

                        //show no result
                        txtNoData.setVisibility(View.VISIBLE);
                        lvSearchHotel.setEmptyView(txtNoData);

                        isLoading = true;


                    } else {
                        hotelFormsList.addAll(list);

                        if (adapterSearch == null) {
                            adapterSearch = new SearchHotelAdapter(SearchActivity.this, hotelFormsList);
                            lvSearchHotel.setAdapter(adapterSearch);
                        } else {
                            adapterSearch.setHotelForms(hotelFormsList);
                            adapterSearch.notifyDataSetChanged();
                        }

                        offset = adapterSearch.getCount();

                        isLoading = offset < limit;

                    }

                }
            }
        });
    }

    //ListView Scroll
    private void scrollListView() {
        lvSearchHotel.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && firstVisibleItem + visibleItemCount + 10 >= totalItemCount && totalItemCount != 0) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        suggestSearch(keyword, offset, limit);
    }

    private void findRecommend() {
        setUpRecyclerView();

        findFlashSaleHotelList();

        findHistorySearch();
    }
}
