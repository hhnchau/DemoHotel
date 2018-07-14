package com.appromobile.hotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.RateReviewListAdapter;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/10/2016.
 * Kingpes
 */
public class RateReviewListActivity extends BaseActivity {
    public static Activity rateReviewListActivity;

    private static final int WRITE_REVIEW = 1001;
    private static final int LOGIN_REVIEW_LIST_REQUEST_LIKE = 1001;
    private ListView lvComment;
    private TextViewSFRegular tvHotelTitle, tvTotalReview;
    private HotelDetailForm hotelDetailForm;
    private ImageView btnWriteComment;
    private TextViewSFRegular tvMessage;
    SwipeRefreshLayout swipeContainer;
    ImageView[] stars = new ImageView[5];
    private int offset = 0;
    RateReviewListAdapter rateReviewListAdapter;
    public static final int CALL_BOOKING = 1002;
    private List<UserReviewForm> userReviewForms = new ArrayList<>();
    private int scrollStateCurrent;
    private boolean isEndList = false;
    boolean isNeedRefresh = false;
    LinearLayout btnBook;
    boolean isRequestingLogin = false;

    @Override
    public void setScreenName() {
        this.screenName = "SHotelReview";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rateReviewListActivity = this;
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.org));
        }
        setContentView(R.layout.rate_review_list_activity);

        getIntent().setExtrasClassLoader(HotelDetailForm.class.getClassLoader());
        hotelDetailForm = getIntent().getParcelableExtra("HotelDetailForm");

        swipeContainer = findViewById(R.id.swipeContainer);
        tvTotalReview = findViewById(R.id.tvTotalReview);
        lvComment = findViewById(R.id.lvComment);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        if (hotelDetailForm != null) {
            tvHotelTitle.setText(hotelDetailForm.getName());
        }
        btnWriteComment = findViewById(R.id.btnWriteComment);
        tvMessage = findViewById(R.id.tvMessage);
        btnBook = findViewById(R.id.btnBook);
        stars[0] = findViewById(R.id.btnStar1);
        stars[1] = findViewById(R.id.btnStar2);
        stars[2] = findViewById(R.id.btnStar3);
        stars[3] = findViewById(R.id.btnStar4);
        stars[4] = findViewById(R.id.btnStar5);

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNeedRefresh) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        btnWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(RateReviewListActivity.this).equals("")) {
                    Intent intent = new Intent(RateReviewListActivity.this, RateReviewActivity.class);
                    intent.putExtra("HotelDetailForm", hotelDetailForm);
                    startActivityForResult(intent, WRITE_REVIEW);
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                } else {
                    Intent intent = new Intent(RateReviewListActivity.this, LoginActivity.class);
                    startActivityForResult(intent, WRITE_REVIEW);
                }
            }
        });

        lvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserReviewForm userReviewForm = (UserReviewForm) parent.getAdapter().getItem(position);
                Intent intent = new Intent(RateReviewListActivity.this, RateReviewDetailActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("UserReviewForm", userReviewForm);
                startActivityForResult(intent, WRITE_REVIEW);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                offset = 0;
                userReviewForms.clear();
                if (rateReviewListAdapter != null) {
                    rateReviewListAdapter.notifyDataSetChanged();
                }
                isEndList = false;
                initData();
            }
        });

        lvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollStateCurrent = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
                    if (visibleItemCount != 0 && totalItemCount != 0) {
                        if ((firstVisibleItem + visibleItemCount >= totalItemCount) && !isEndList) {
                            System.out.println("Offset Loadmore: " + offset);
//                            DialogUtils.showLoadingProgress(RateReviewListActivity.this, false);
                            initData();
                        }
                    }
                }
            }
        });

        if (hotelDetailForm != null) {
            if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || (hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType())) {
                if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                    btnBook.setVisibility(View.VISIBLE);
                }
            }
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(RateReviewListActivity.this).equals("")) {
                    gotoReservation();
                } else {
                    showDialogGuestBooking();
                }
            }
        });
        initData();
        initHotelData();
    }

    private void showDialogGuestBooking() {
        Dialag.getInstance().show(this, false, true, false, null, getString(R.string.msg_3_9_book_as_guest), getString(R.string.login_button), getString(R.string.txt_3_9_book_as_guest), null, Dialag.BTN_MIDDLE, new CallbackDialag() {
            @Override
            public void button1() { //goto LogIn
                Intent intent = new Intent(RateReviewListActivity.this, LoginActivity.class);
                startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
            }

            @Override
            public void button2() { //Continues
                gotoReservation();
            }

            @Override
            public void button3(Dialog dialog) {

            }
        });
    }

    private void gotoReservation(){
        Intent intent = new Intent(RateReviewListActivity.this, ReservationActivity.class);
        intent.putExtra("HotelDetailForm", hotelDetailForm);
        startActivityForResult(intent, CALL_BOOKING);
        intent.putExtra("RoomTypeIndex", 0);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }


    private void initData() {
        if (hotelDetailForm != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("hotelSn", hotelDetailForm.getSn());
            params.put("offset", offset);
            params.put("limit", HotelApplication.LIMIT_REQUEST);

            HotelApplication.serviceApi.findUserReviewList(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserReviewForm>>() {
                @Override
                public void onResponse(Call<List<UserReviewForm>> call, Response<List<UserReviewForm>> response) {
                    swipeContainer.setRefreshing(false);
//                DialogUtils.hideLoadingProgress();
                    List<UserReviewForm> list = response.body();
                    if (response.isSuccessful()) {
                        if (list == null || list.size() == 0) {
                            isEndList = true;
                        }
                        if (list != null) {
                            userReviewForms.addAll(list);
                            if (rateReviewListAdapter == null) {
                                rateReviewListAdapter = new RateReviewListAdapter(RateReviewListActivity.this, userReviewForms, hotelDetailForm);
                            } else {
                                rateReviewListAdapter.updateData(userReviewForms);
                            }
                            offset = rateReviewListAdapter.getCount();
                            if (rateReviewListAdapter.getCount() > 0) {
                                lvComment.setAdapter(rateReviewListAdapter);
                            } else {
                                lvComment.setEmptyView(tvMessage);
                            }
                        }
                    } else if (response.code() == 401) {
                        if (!isRequestingLogin) {
                            DialogUtils.showExpiredDialog(RateReviewListActivity.this, new DialogCallback() {
                                @Override
                                public void finished() {
                                    Intent intent = new Intent(RateReviewListActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, LOGIN_REVIEW_LIST_REQUEST_LIKE);
                                }
                            });
                            isRequestingLogin = true;
                        }
                    } else {
                        lvComment.setEmptyView(tvMessage);
                    }
                }

                @Override
                public void onFailure(Call<List<UserReviewForm>> call, Throwable t) {
                    swipeContainer.setRefreshing(false);
//                DialogUtils.hideLoadingProgress();
                    Toast.makeText(RateReviewListActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        System.out.println("TimeMeasurableBeginEndView: " + Calendar.getInstance().getTimeInMillis());
    }

    private void initHotelData() {
        if (hotelDetailForm != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("hotelSn", hotelDetailForm.getSn());
            //new version 4.0 for trial hotel
            params.put("version", "4.0");
            HotelApplication.serviceApi.getHotelDetail(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<HotelDetailForm>() {
                @Override
                public void onResponse(Call<HotelDetailForm> call, Response<HotelDetailForm> response) {

                    if (response.isSuccessful()) {
                        hotelDetailForm = response.body();
                        if (hotelDetailForm != null) {
                            tvTotalReview.setText(String.valueOf(hotelDetailForm.getTotalReview()));
                            int numStar = (int) hotelDetailForm.getAverageMark();
                            if (numStar > 5) {
                                numStar = 5;
                            }
                            if (numStar < 0) {
                                numStar = 0;
                            }
                            for (ImageView star : stars) {
                                star.setImageResource(R.drawable.review_star);
                            }
                            if (0 < hotelDetailForm.getAverageMark() && hotelDetailForm.getAverageMark() <= 5) {
                                for (int i = 0; i < numStar; i++) {
                                    stars[i].setImageResource(R.drawable.review_star_fill);
                                }
                                if (numStar < stars.length) {
                                    if (numStar != hotelDetailForm.getAverageMark()) {
                                        stars[numStar].setImageResource(R.drawable.review_star_half);
                                    }
                                }
                            }
                        }
                    } else if (response.code() == 401) {
                        if (!isRequestingLogin) {
                            DialogUtils.showExpiredDialog(RateReviewListActivity.this, new DialogCallback() {
                                @Override
                                public void finished() {
                                    Intent intent = new Intent(RateReviewListActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, LOGIN_REVIEW_LIST_REQUEST_LIKE);
                                }
                            });
                            isRequestingLogin = true;
                        }
                    }
                }

                @Override
                public void onFailure(Call<HotelDetailForm> call, Throwable t) {
//                DialogUtils.hideLoadingProgress();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REVIEW) {
            if (resultCode == RESULT_OK) {
//                DialogUtils.showLoadingProgress(RateReviewListActivity.this, false);
                offset = 0;
                userReviewForms.clear();
                if (rateReviewListAdapter != null) {
                    rateReviewListAdapter.notifyDataSetChanged();
                }
                isEndList = false;
                initData();
                initHotelData();
                isNeedRefresh = true;
            } else if (requestCode == CALL_BOOKING) {
                if (resultCode == RESULT_OK) {
                    finish();
                }
            } else if (requestCode == LOGIN_REVIEW_LIST_REQUEST_LIKE) {
                offset = 0;
                userReviewForms.clear();
                rateReviewListAdapter.notifyDataSetChanged();
                isEndList = false;
                initData();
                initHotelData();
                isNeedRefresh = true;
            }
        }
        isRequestingLogin = false;
    }

    @Override
    public void onBackPressed() {
        if (isNeedRefresh) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(rateReviewListActivity != null)
//            rateReviewListActivity = null;
//    }
}
