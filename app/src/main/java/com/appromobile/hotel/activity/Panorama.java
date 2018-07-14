package com.appromobile.hotel.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.RoomView;
import com.appromobile.hotel.panorama.ImageLoaderTaskGlide;
import com.appromobile.hotel.panorama.LoadTaskComplete;
import com.appromobile.hotel.panorama.PanoramaView;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 19/09/2017.
 */
public class Panorama extends BaseActivity implements View.OnClickListener {
    public static final int CALL_BOOKING = 1002;

    private List<HotelImageForm> listImagePanorama;
    private HotelDetailForm hotelDetailForm;
    private int roomTypeSn = -1;
    private int roomTypeIndex = -1;

    private LinearLayout panoramaCover, panoramaGuide;

    private TextView txtPos, txtTotalImage, iconBook;
    private ImageView iconBack, iconNext, iconClose;


    private LinearLayout boxDescription;

    private TextView tvRoomName, tvRoomSquare, tvRoomBed, tvRoomView, tvRoomDescription, tvRoomPromotion;
    private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount, tvPriceDailyNormal, tvPriceDailyDiscount;

    private int position = 0;
    private int totalImage = 0;

    private ImageLoaderTaskGlide imageLoaderTaskGlide;

    private VrPanoramaView vrPanoramaView;
    private ProgressBar progressBar;
    private ImageView guide;
    private TextView txtLocked;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setScreenName();
        setContentView(R.layout.activity_panorama);
        init();

        /*
        / Get Intent
        */
        String action = getIntent().getAction();
        if (action != null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                if (action.equals("HotelDetailActivity")) {
                    hotelDetailForm = bundle.getParcelable("HotelDetailForm");
                    roomTypeSn = bundle.getInt("SelectedRoomType", -1);
                    //From Hotel Detail
                    setView();

                } else {
                    int hotelSn = bundle.getInt("hotelSn", 0);
                    getHotelDetail(hotelSn);
                }
            }
        }
    }


    private void getHotelDetail(int hotelSn) {
        ControllerApi.getmInstance().getHotelDetail(this, hotelSn, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                hotelDetailForm = (HotelDetailForm) object;

                //From Home Hotel
                setView();
            }
        });
    }

    private void setView() {
        if (hotelDetailForm != null) {

            int roomSn = -1;

            if (roomTypeSn > -1) {
                RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(roomTypeSn);
                if (roomTypeForm != null) {

                    roomSn = roomTypeForm.getSn();

                    /*
                    * Check Roomtype Locked.
                    */
                    if (roomTypeForm.isLocked()) {
                        txtLocked.setVisibility(View.VISIBLE);
                    } else {
                        txtLocked.setVisibility(View.GONE);
                    }
                }

                //Add Event
                setButtonName("B360PhotoRoomtype");
            } else {
                //Add Event
                setButtonName("B360PhotoHotel");
            }

            getUrlImage(hotelDetailForm.getSn(), roomSn);

            //tvHotelTitle.setText(hotelDetailForm.getName());


            if (hotelDetailForm.getRoomTypeList() == null || (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() == 0)) {
                findViewById(R.id.btnBook_panorama).setVisibility(View.GONE);
            } else {
                findViewById(R.id.btnBook_panorama).setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "S360Photo";
    }

    private void init() {
        vrPanoramaView = findViewById(R.id.img360);
        panoramaCover = findViewById(R.id.panorama_cover);
        panoramaGuide = findViewById(R.id.panorama_guide);
        progressBar = findViewById(R.id.progressBar);
        txtPos = findViewById(R.id.tv_back);
        txtPos.setOnClickListener(this);
        txtTotalImage = findViewById(R.id.tv_next);
        txtTotalImage.setOnClickListener(this);

        txtLocked = findViewById(R.id.roomtype_locked);


        boxDescription = findViewById(R.id.boxDescription);
        boxDescription.setVisibility(View.GONE);

        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomSquare = findViewById(R.id.tvRoomSquare);
        tvRoomBed = findViewById(R.id.tvRoomBed);
        tvRoomView = findViewById(R.id.tvRoomView);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPromotion = findViewById(R.id.tvRoomPromotion);
        tvRoomPromotion.setVisibility(View.GONE);

        tvPriceStatus = findViewById(R.id.tvPriceStatus);
        tvPriceHourlyNormal = findViewById(R.id.tvPriceHourlyNormal);
        tvPriceHourlyDiscount = findViewById(R.id.tvPriceHourlyDiscount);
        tvPriceOvernightNormal = findViewById(R.id.tvPriceOvernightNormal);
        tvPriceOvernightDiscount = findViewById(R.id.tvPriceOvernightDiscount);
        tvPriceDailyNormal = findViewById(R.id.tvPriceDailyNormal);
        tvPriceDailyDiscount = findViewById(R.id.tvPriceDailyDiscount);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvRoomName.setVisibility(View.GONE);
            tvRoomSquare.setVisibility(View.GONE);
            tvRoomBed.setVisibility(View.GONE);
            tvRoomView.setVisibility(View.GONE);
            tvRoomDescription.setVisibility(View.GONE);
            findViewById(R.id.line_des1).setVisibility(View.GONE);
            findViewById(R.id.line_des2).setVisibility(View.GONE);
        }


        guide = findViewById(R.id.guide);

        iconClose = findViewById(R.id.panorama_imageview_close);
        iconClose.setOnClickListener(this);
        iconBook = findViewById(R.id.btnBook_panorama);
        iconBook.setOnClickListener(this);
        iconBack = findViewById(R.id.panorama_icon_back);
        iconBack.setOnClickListener(this);
        iconNext = findViewById(R.id.panorama_icon_next);
        iconNext.setOnClickListener(this);

        listImagePanorama = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private void getUrlImage(long hotelSn, long roomTypeSn) {
        ControllerApi.getmInstance().findLimitHotelImageList(this, hotelSn, roomTypeSn, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {

                if (list.size() > 0) {
                    listImagePanorama = (List<HotelImageForm>) (Object) list;

                    totalImage = listImagePanorama.size();
                    txtTotalImage.setText(String.valueOf(totalImage));
                    if (totalImage > 0) {
                        changePanorama(position);
                    }

                } else {
                    //None Image
                    MyLog.writeLog("");
                }
            }
        });
    }

    private void updateRoomTypeInfo(int position) {
        boxDescription.setVisibility(View.GONE);
        if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {

            int count = 0;
            for (int i = 0; i < hotelDetailForm.getRoomTypeList().size(); i++) {
                RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(i);
                if (roomTypeForm != null) {
                    if (listImagePanorama.get(position).getRoomTypeSn() == roomTypeForm.getSn()) {


                        boxDescription.setVisibility(View.VISIBLE);

                        // Set Room Name
                        tvRoomName.setText(roomTypeForm.getName());

                        //Set Square
                        StringBuilder stringBuilder = new StringBuilder().append(getString(R.string.txt_3_2_square)).append(" ");
                        if (roomTypeForm.getSquare() <= 0) {
                            stringBuilder.append("N/A");
                        } else {
                            stringBuilder.append(String.valueOf(roomTypeForm.getSquare())).append("m2");
                        }
                        tvRoomSquare.setText(stringBuilder);

                        //Set Beb
                        tvRoomBed.setText(new StringBuilder().append(getString(R.string.txt_3_2_bed)).append(roomTypeForm.getRoomBed(roomTypeForm.getBedType())));

                        //Set View
                        List<RoomView> roomViewList = roomTypeForm.getRoomViewList();
                        stringBuilder = new StringBuilder().append(getString(R.string.txt_3_2_views)).append(" ");
                        if (roomViewList != null && roomViewList.size() > 0) {
                            for (int j = 0; j < roomViewList.size(); j++) {
                                if (HotelApplication.isEnglish) {
                                    stringBuilder.append(roomViewList.get(j).getNameEn()).append(", ");
                                } else {
                                    stringBuilder.append(roomViewList.get(j).getName()).append(", ");
                                }
                            }
                        } else {
                            stringBuilder.append("N/A, ");
                        }
                        tvRoomView.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

                        //Set Description
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            tvRoomDescription.setText(new StringBuilder().append(getString(R.string.txt_3_2_description)).append(" ").append(Html.fromHtml(roomTypeForm.getMemo(), Html.FROM_HTML_MODE_COMPACT)));
                        }else {
                            tvRoomDescription.setText(new StringBuilder().append(getString(R.string.txt_3_2_description)).append(" ").append(Html.fromHtml(roomTypeForm.getMemo())));
                        }

                        //Set Other Promotion
                        //tvRoomPromotion.setText("");

                        //--------------Set Price------------
                        int[] discount = Utils.getPromotionInfoForm(roomTypeForm.getHotelSn());

                        if (discount[0] > 0 || discount[1] > 0 || discount[2] > 0) {

                            //Set Price Status
                            tvPriceStatus.setText(getString(R.string.txt_2_coupon_applied));

                            //Hourly
                            if (discount[0] > 0) {
                                int priceHourlyDiscount = roomTypeForm.getPriceFirstHours() - discount[0];

                                if (priceHourlyDiscount < 0) {
                                    priceHourlyDiscount = 0;
                                }

                                //Set Price Hourly Normal
                                tvPriceHourlyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                                //StrikeThrough
                                tvPriceHourlyNormal.setPaintFlags(tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                //Set Price Hourly Discount
                                tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                            } else {

                                //Set Price Hourly Normal
                                tvPriceHourlyNormal.setVisibility(View.GONE);
                                //Set Price Hourly Discount
                                tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                            }

                            //Overnight
                            if (discount[1] > 0) {
                                int priceOvernightDiscount = roomTypeForm.getPriceOvernight() - discount[1];

                                if (priceOvernightDiscount < 0) {
                                    priceOvernightDiscount = 0;
                                }

                                //Set Price Overnight Normal
                                tvPriceOvernightNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                                //StrikeThrough
                                tvPriceOvernightNormal.setPaintFlags(tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                //Set Price Overnight Discount
                                tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                            } else {
                                //Set Price Overnight Normal
                                tvPriceOvernightNormal.setVisibility(View.GONE);

                                //Set Price Overnight Discount
                                tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                            }

                            //Daily
                            if (discount[2] > 0) {
                                int priceDailyDiscount = roomTypeForm.getPriceOneDay() - discount[2];

                                if (priceDailyDiscount < 0) {
                                    priceDailyDiscount = 0;
                                }

                                //Set Price Overnight Normal
                                tvPriceDailyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));
                                //StrikeThrough
                                tvPriceDailyNormal.setPaintFlags(tvPriceDailyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                //Set Price Overnight Discount
                                tvPriceDailyDiscount.setText(Utils.formatCurrency(priceDailyDiscount));
                            } else {
                                //Set Price Overnight Normal
                                tvPriceDailyNormal.setVisibility(View.GONE);

                                //Set Price Overnight Discount
                                tvPriceDailyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));
                            }

                        } else {

                            //Set Price Status
                            tvPriceStatus.setVisibility(View.GONE);

                            //Set Price Hourly Normal
                            tvPriceHourlyNormal.setVisibility(View.GONE);
                            //Set Price Hourly Discount
                            tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                            //Set Price Overnight Normal
                            tvPriceOvernightNormal.setVisibility(View.GONE);

                            //Set Price Overnight Discount
                            tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

                            //Set Price Daily Normal
                            tvPriceDailyNormal.setVisibility(View.GONE);

                            //Set Price Daily Discount
                            tvPriceDailyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));

                        }


                        roomTypeIndex = i;
                        break;

                    }

                    count++;
                }
            }

            if (count >= hotelDetailForm.getRoomTypeList().size()) {
                roomTypeIndex = 0;
            }
        }

    }


    private void gotoBooking() {
        if (!PreferenceUtils.getToken(Panorama.this).equals("")) {
            if (roomTypeIndex > -1) {
                if (hotelDetailForm.getRoomTypeList().get(roomTypeIndex).isLocked()) {
                    Toast.makeText(Panorama.this, getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Panorama.this, ReservationActivity.class);
                    intent.putExtra("HotelDetailForm", hotelDetailForm);
                    intent.putExtra("RoomTypeIndex", roomTypeIndex);
                    startActivityForResult(intent, CALL_BOOKING);
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                    finish();
                }
            } else {
                Intent intent = new Intent(Panorama.this, ReservationActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("RoomTypeIndex", 0);
                startActivityForResult(intent, CALL_BOOKING);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                finish();
            }
        } else {
            Intent intent = new Intent(Panorama.this, LoginActivity.class);
            startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_BOOKING) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panorama_imageview_close:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;

            case R.id.btnBook_panorama:
                gotoBooking();
                break;

            case R.id.panorama_icon_back:
            case R.id.tv_back:
                if (position > 0) {
                    //Stop Load Image
                    imageLoaderTaskGlide.cancel(true);
                    position--;
                    panoramaCover.setVisibility(View.VISIBLE);
                    changePanorama(position);
                }
                break;

            case R.id.panorama_icon_next:
            case R.id.tv_next:
                if (position < totalImage - 1) {
                    //Stop Load Image
                    imageLoaderTaskGlide.cancel(true);
                    position++;
                    panoramaCover.setVisibility(View.VISIBLE);
                    changePanorama(position);
                }
                break;
        }
    }

    private void changePanorama(int position) {
        //Set Position
        txtPos.setText(String.valueOf(position + 1));

        //Get Data
        HotelImageForm hotelImageForm = listImagePanorama.get(position);

        //Set Panorama
        loadPanorama(hotelImageForm.getSn(), hotelImageForm.getCustomizeName());

        //Update RoomType Info
        updateRoomTypeInfo(position);
    }

    private void loadPanorama(int sn, String customizeName) {
        final String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + sn + "&fileName=" + customizeName;
        final String higthQuality = "&highQuality=true";

        imageLoaderTaskGlide = new ImageLoaderTaskGlide(this, new LoadTaskComplete() {
            @Override
            public void onTaskComplete(Bitmap bitmap) {
                panoramaCover.setVisibility(View.GONE);
                PanoramaView.setView(vrPanoramaView, bitmap);

                progressBar.setVisibility(View.VISIBLE);

                /*
                * Load Quality Image
                */
                loadPanoramaHightQuality(url + higthQuality);


                /*
                * Show Guide
                */
                if (!checkSensor() && PreferenceUtils.getGuide(Panorama.this)) {
                    PreferenceUtils.setGuide(Panorama.this, false);
                    panoramaGuide.setVisibility(View.VISIBLE);
                    guide.setVisibility(View.VISIBLE);

                    final Animation animation = AnimationUtils.loadAnimation(Panorama.this, R.anim.guide);
                    guide.startAnimation(animation);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animation.cancel();
                            panoramaGuide.setVisibility(View.GONE);
                            guide.setVisibility(View.GONE);
                        }
                    }, 3000);

                }

            }
        });
        imageLoaderTaskGlide.execute(url);
    }

    private void loadPanoramaHightQuality(String qualityLink) {
        imageLoaderTaskGlide = new ImageLoaderTaskGlide(this, new LoadTaskComplete() {
            @Override
            public void onTaskComplete(Bitmap bitmap) {
                PanoramaView.setView(vrPanoramaView, bitmap);
                progressBar.setVisibility(View.GONE);
            }
        });
        imageLoaderTaskGlide.execute(qualityLink);
    }

    @Override
    protected void onPause() {
        PanoramaView.pausePanoramaView(vrPanoramaView);
        super.onPause();
    }

    @Override
    protected void onResume() {
        PanoramaView.resumePanoramaView(vrPanoramaView);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        PanoramaView.destroyPanoramaView(vrPanoramaView);
        super.onDestroy();
    }

    private void checkRotateScreen() {
        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            Toast.makeText(getApplicationContext(), "Rotation ON", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Rotation OFF", Toast.LENGTH_SHORT).show();
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
        }

        //<uses-permission android:name="android.permission.WRITE_SETTINGS"
    }

    private boolean checkSensor() {
        boolean result = true;
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (proximitySensor == null) {
                result = false;
            }
        }
        return result;
    }
}
