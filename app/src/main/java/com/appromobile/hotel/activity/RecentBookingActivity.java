package com.appromobile.hotel.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.AdapterMultiRecentBooking;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.model.request.BookingDto;
import com.appromobile.hotel.model.request.CancelBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.RecentBookingForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 12/12/2016.
 */

public class RecentBookingActivity extends BaseActivity implements AdapterMultiRecentBooking.Callback_Multi_Recent_Booking, View.OnClickListener {
    private ImageView imgClose;
    private TextView cancelBooking, qrScan;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private TextViewSFRegular tvMessage;
    private LinearLayout boxData;

    private int minPrice;

    private ViewPager viewPager;

    private AdapterMultiRecentBooking adapter;

    private RelativeLayout containerIndicator;
    private View indicator;
    private int widthBar = 0;
    private int pageScrollState = 0;
    private int current = 0;

    private List<RecentBookingForm> listRecentBookingForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        setContentView(R.layout.recent_booking_activity);
        imgClose =  findViewById(R.id.btnClose);
        imgClose.setOnClickListener(this);
        cancelBooking =  findViewById(R.id.btnCancelBooking);
        cancelBooking.setOnClickListener(this);
        cancelBooking.setVisibility(View.GONE);
        qrScan =  findViewById(R.id.btnQRScan);
        qrScan.setOnClickListener(this);
        tvMessage =  findViewById(R.id.tvMessage);
        boxData =  findViewById(R.id.boxData);
        tvMessage.setVisibility(View.GONE);
        boxData.setVisibility(View.GONE);
        containerIndicator =  findViewById(R.id.container_indicator);
        indicator = findViewById(R.id.indicator);


        loadListBooking();


    }

    @SuppressWarnings("unchecked")
    private void loadListBooking() {
        DialogUtils.showLoadingProgress(this, false);

        if (HotelApplication.apiSettingForm == null) {
            ControllerApi.getmInstance().findApiSetting(this, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    ApiSettingForm apiSettingForm = (ApiSettingForm) object;
                    minPrice = apiSettingForm.getMinMoney();
                }
            });
        } else {
            minPrice = HotelApplication.apiSettingForm.getMinMoney();
        }

        ControllerApi.getmInstance().findRecentBookingList(this, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {

                DialogUtils.hideLoadingProgress();

                if (list.size() != 0) {

                    boxData.setVisibility(View.VISIBLE);

                    viewPager =  findViewById(R.id.my_viewPager);

                    listRecentBookingForm = (List<RecentBookingForm>) (Object) list;

                    adapter = new AdapterMultiRecentBooking(RecentBookingActivity.this, listRecentBookingForm, minPrice, RecentBookingActivity.this);

                    viewPager.setAdapter(adapter);

                    createStatusbar();


                } else {

                    boxData.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.VISIBLE);

                }

            }
        });

    }

    //    Stautus bar
    private void createStatusbar() {

        if (viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 1) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.dp_32);
            widthBar = screenWidth / viewPager.getAdapter().getCount();
            indicator.getLayoutParams().width = widthBar + 10;
            indicator.requestLayout();

            //listener Scroll
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(final int position) {


                    if (viewPager.getAdapter().getCount() > 1) {
                        if (pageScrollState == ViewPager.SCROLL_STATE_SETTLING) {
                            TranslateAnimation animation = null;
                            if (current < position) {
                                animation = new TranslateAnimation(0, 1, 0, 0);
                            } else if (current > position) {
                                animation = new TranslateAnimation(0, -1, 0, 0);
                            }
                            if (animation != null) {
                                animation.setDuration(200);
                                animation.setFillAfter(false);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        RelativeLayout.LayoutParams layout_description = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
                                        layout_description.leftMargin = widthBar * position;
                                        indicator.setLayoutParams(layout_description);
                                        current = position;
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                indicator.startAnimation(animation);

                            }
                        }

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    pageScrollState = state;
                }
            });
        } else {
            //list only 1
            containerIndicator.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParamConstants.ZBAR_SCANNER_REQUEST) {
            if (resultCode == RESULT_OK) {

                String dataResult = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                updateCheckin(dataResult);

            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Camera unavailable", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(this, ZBarScannerActivity.class);
                    startActivityForResult(intent, ParamConstants.ZBAR_SCANNER_REQUEST);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Cemara Denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateCheckin(String dataResult) {
        System.out.println("dataResult: " + dataResult);
        int hotelSn = 0;
        try {
            hotelSn = Integer.parseInt(dataResult);
        } catch (Exception e) {
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setHotelSn(hotelSn);
        bookingDto.setUserBookingSn(listRecentBookingForm.get(viewPager.getCurrentItem()).getSn());


        ControllerApi.getmInstance().checkInBooking(this, bookingDto, new ResultApi() {
            @Override
            public void resultApi(Object object) {


                RestResult restResult = (RestResult) object;

                if (restResult.getResult() == 1) {
                    Toast.makeText(RecentBookingActivity.this, getString(R.string.checkin_successfully), Toast.LENGTH_LONG).show();

//                    Refresh List

                    loadListBooking();


                } else if (restResult.getResult() == 11 || restResult.getResult() == 12) {
                    Toast.makeText(RecentBookingActivity.this, getString(R.string.msg_1_5_no_reservation_to_checkin), Toast.LENGTH_LONG).show();
                } else if (restResult.getResult() == 13) {
                    Toast.makeText(RecentBookingActivity.this, getString(R.string.msg_1_4_not_yet_confirmed), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RecentBookingActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void setScreenName() {
        this.screenName = "SFloatRecent";
    }


    @Override
    public void paynow() {
        paynowClick(listRecentBookingForm.get(viewPager.getCurrentItem()).getSn());
    }

    @Override
    public void statusBooking(int staus) {
        if (staus == AdapterMultiRecentBooking.SHOW) {
            cancelBooking.setVisibility(View.VISIBLE);
        } else {
            cancelBooking.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnClose) {
            finish();
            return;
        }
        if (v.getId() == R.id.btnCancelBooking) {
            cancelBooking(listRecentBookingForm.get(viewPager.getCurrentItem()).getSn());
        }
        if (v.getId() == R.id.btnQRScan) {
            qrScan();
        }
    }

    private void cancelBooking(int snBooking) {
        DialogUtils.showLoadingProgress(RecentBookingActivity.this, false);
        CancelBookingDto cancelBookingDto = new CancelBookingDto();
        cancelBookingDto.setUserBookingSn(snBooking);
        HotelApplication.serviceApi.cancelReservation(cancelBookingDto, PreferenceUtils.getToken(RecentBookingActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    if (restResult.getResult() == 1) {

                        Toast.makeText(RecentBookingActivity.this, getString(R.string.cancel_booking_successfully), Toast.LENGTH_LONG).show();

                        //Refesh List
                        loadListBooking();


                    } else {
                        //Toast.makeText(RecentBookingActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        //Change Display popup
                        Dialag.getInstance().show(RecentBookingActivity.this, false, false,true, null, restResult.getMessage(), getString(R.string.ok), null, null,Dialag.BTN_LEFT, new CallbackDialag() {
                            @Override
                            public void button1() {

                            }

                            @Override
                            public void button2() {

                            }

                            @Override
                            public void button3(Dialog dialog) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void qrScan() {
        int hasCameraPermission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        Intent intent = new Intent(RecentBookingActivity.this, ZBarScannerActivity.class);
        startActivityForResult(intent, ParamConstants.ZBAR_SCANNER_REQUEST);
    }

    private void paynowClick(final int snBooking) {
        DialogUtils.showLoadingProgress(RecentBookingActivity.this, false);
        /*
        / Check Booking Status
        */
        ControllerApi.getmInstance().findUserBookingDetail(this, snBooking, false, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                //result
                UserBookingForm userBookingForm = (UserBookingForm) object;
                if (userBookingForm.getBookingStatus() == 1) { //Booked
                    //Goto Pay123
                    gotoPay123(snBooking);
                } else {
                    //hide loading
                    DialogUtils.hideLoadingProgress();
                    //Cancel
                    Toast.makeText(RecentBookingActivity.this, getString(R.string.msg_6_3_1_booking_was_canceled), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void gotoPay123(final int snBooking) {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userBookingSn", snBooking);
            params.put("clientip", ip);

            HotelApplication.serviceApi.findPaymentInfoForm(params, PreferenceUtils.getToken(RecentBookingActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<PaymentInfoForm>() {
                @Override
                public void onResponse(Call<PaymentInfoForm> call, Response<PaymentInfoForm> response) {

                    //hide loading
                    DialogUtils.hideLoadingProgress();
                    PaymentInfoForm paymentInfoForm = response.body();
                    if (response.isSuccessful() && paymentInfoForm != null) {

                        if (Integer.parseInt(paymentInfoForm.getTotalAmount()) < minPrice) {
                            String s = " " + Utils.formatCurrency(minPrice) + " VNĐ";
                            Toast.makeText(RecentBookingActivity.this, getString(R.string.msg_3_1_min_price) + s, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(RecentBookingActivity.this, BrowserPaymentActivity.class);
                            intent.putExtra("IS_PAY_NOW", true);
                            intent.putExtra("PaymentInfoForm", response.body());
                            intent.putExtra("userBookingSn", snBooking);

                            if (listRecentBookingForm.get(viewPager.getCurrentItem()).getPaymentOption() == ParamConstants.PAYMENT_ONLINE) {
                                intent.putExtra("METHOD_PAYMENT", ParamConstants.METHOD_ALWAYS_PAY_ONLINE);
                            } else {
                                intent.putExtra("METHOD_PAYMENT", ParamConstants.METHOD_PAY_AT_HOTEL);
                            }

                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaymentInfoForm> call, Throwable t) {

                }
            });
        }
    }

}
