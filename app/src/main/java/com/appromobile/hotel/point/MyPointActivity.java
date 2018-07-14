package com.appromobile.hotel.point;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.BaseActivity;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.calendar.CallbackCalendar;
import com.appromobile.hotel.calendar.MyCalendar;
import com.appromobile.hotel.clock.HelperClock;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.MileageHistoryForm;
import com.appromobile.hotel.model.view.MileagePointForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by appro on 20/03/2018.
 */

public class MyPointActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView tvFrom, tvTo, tvNumEffective, tvNumEarned, tvNumSpent, tvNumExpired, tvCondition, btnOK;
    private ListView list_point;

    private MileagePointAdapter mileagePointAdapter;
    private List<MileageHistoryForm> listMileage;
    private final static int limit = 20;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean loadNew = true;
    private TextView txtNoData;

    private static final int TYPE_FROM = 1;
    private static final int TYPE_TO = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_point_activity);

        initView();

        getMyPoint();

    }

    private void findGeneralMileagePointInfo() {
        String startDate = Utils.formatDate(this, tvFrom.getText().toString());
        String endDate = Utils.formatDate(this, tvTo.getText().toString());
        ControllerApi.getmInstance().findGeneralMileagePointInfo(this, startDate, endDate, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                MileagePointForm mileagePointForm = (MileagePointForm) object;
                AppUserForm appUserForm = new AppUserForm();
                appUserForm.setMileageAmount(mileagePointForm.getMileageAmount());
                appUserForm.setMileageEarned(mileagePointForm.getMileageEarned());
                appUserForm.setMileageUsed(mileagePointForm.getMileageUsed());
                appUserForm.setMileageExpired(mileagePointForm.getMileageExpired());

                updateViewMyPoint(appUserForm);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void findLimitMileageHistoryList() {
        String startDate = Utils.formatDate(this, tvFrom.getText().toString());
        String endDate = Utils.formatDate(this, tvTo.getText().toString());
        ControllerApi.getmInstance().findLimitMileageHistoryList(this, startDate, endDate, limit, offset, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {
                if (list != null && list.size() > 0) {

                    listMileage.addAll((List<MileageHistoryForm>) (Object) list);
                    mileagePointAdapter.notifyDataSetChanged();
                    txtNoData.setVisibility(View.GONE);

                    offset = mileagePointAdapter.getCount();

                    isLoading = offset < limit;

                } else {
                    if (loadNew) {
                        //show no result
                        txtNoData.setVisibility(View.VISIBLE);
                        list_point.setEmptyView(txtNoData);

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
        list_point.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && firstVisibleItem + visibleItemCount + 10 >= totalItemCount && totalItemCount != 0) {
                    findLimitMileageHistoryList();
                }
            }
        });
    }

    private void getMyPoint() {
        ControllerApi.getmInstance().findAppUser(this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                AppUserForm appUserForm = (AppUserForm) object;
                if (appUserForm != null) {

                    String day = appUserForm.getMileageFirstTime();
                    if (day == null || day.equals("")) {
                        day = Utils.getSystemDay(MyPointActivity.this);
                    }else {
                        day = Utils.formatDateddmmyyyy(MyPointActivity.this, day);
                    }
                    tvFrom.setText(day);
                    tvTo.setText(Utils.getSystemDay(MyPointActivity.this));

                    updateViewMyPoint(appUserForm);

                }
            }
        });
    }

    private void updateViewMyPoint(AppUserForm appUserForm) {
        tvNumEffective.setText(String.valueOf(appUserForm.getMileageAmount()));
        tvNumEarned.setText(String.valueOf(appUserForm.getMileageEarned()));
        tvNumSpent.setText(String.valueOf(appUserForm.getMileageUsed()));
        tvNumExpired.setText(String.valueOf(appUserForm.getMileageExpired()));

        findLimitMileageHistoryList();
    }

    private void showCalendar(final int type) {
        String dateFrom = "01/01/2016";
        String dateTo = "01/01/2020";
        String select;
        if (type == TYPE_FROM) {
            select = tvFrom.getText().toString();
        } else {
            select = tvTo.getText().toString();
        }

        MyCalendar.getInstance().show(this, dateFrom, select, dateTo, new CallbackCalendar() {
            @Override
            public void onDate(String date) {
                if (type == TYPE_FROM) {
                    tvFrom.setText(date);
                    int i = Utils.compareDate(MyPointActivity.this, tvFrom.getText().toString(), tvTo.getText().toString());
                    if (i == 3) {
                        tvTo.setText(Utils.getAfterDate(MyPointActivity.this, date));
                    }
                } else {
                    tvTo.setText(date);
                    int i = Utils.compareDate(MyPointActivity.this, tvTo.getText().toString(), tvFrom.getText().toString());
                    if (i == 1) {
                        tvFrom.setText(Utils.getBeforeDate(MyPointActivity.this, date));
                    }
                }
                //Update Data
                listMileage.clear();
                findGeneralMileagePointInfo();
            }
        });
    }


    private void initView() {
        imgBack = findViewById(R.id.btnClose);
        imgBack.setOnClickListener(this);
        tvFrom = findViewById(R.id.tvFrom);
        tvFrom.setOnClickListener(this);
        tvTo = findViewById(R.id.tvTo);
        tvTo.setOnClickListener(this);
        tvNumEffective = findViewById(R.id.tvNumEffective);
        tvNumEarned = findViewById(R.id.tvNumEarned);
        tvNumSpent = findViewById(R.id.tvNumSpent);
        tvNumExpired = findViewById(R.id.tvNumExpired);

        tvCondition = findViewById(R.id.tvCondition);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvCondition.setText(Html.fromHtml(getString(R.string.msg_6_13_terms_and_condition),Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvCondition.setText(Html.fromHtml(getString(R.string.msg_6_13_terms_and_condition)));
        }
        tvCondition.setMovementMethod(new ScrollingMovementMethod());

        btnOK = findViewById(R.id.btnOK);
        btnOK.setOnClickListener(this);
        list_point = findViewById(R.id.list_point);
        txtNoData = findViewById(R.id.txtNoData);
        listMileage = new ArrayList<>();
        mileagePointAdapter = new MileagePointAdapter(this, listMileage);
        list_point.setAdapter(mileagePointAdapter);

        scrollListView();
    }

    @Override
    public void setScreenName() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.stable);
                break;
            case R.id.btnOK:
                startActivity(new Intent(MyPointActivity.this, MyGiftActivity.class));
                break;
            case R.id.tvFrom:
                showCalendar(TYPE_FROM);
                break;
            case R.id.tvTo:
                showCalendar(TYPE_TO);
                break;
        }
    }
}
