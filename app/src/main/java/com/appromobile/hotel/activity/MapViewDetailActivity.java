package com.appromobile.hotel.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.gps.FetchUrl;
import com.appromobile.hotel.gps.ParseCallBack;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.igaworks.adbrix.IgawAdbrix;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 8/2/2016.
 */
public class MapViewDetailActivity extends BaseActivity implements ParseCallBack {
    private GoogleMap mMap;
    private List<Marker> markers = new ArrayList<>();
    private HotelDetailForm hotelDetailForm;
    private int roomAvailable = 0;
    private Polyline line = null;

    private LinearLayout boxHotelPopup;
    private ImageView imgHotel, img360, imgIconPromotion1; //imgIconPromotion2, imgIconPromotion3, imgIconPromotion4;
    private TextView tvHotelNamePopup, tvAddressPopup, tvReview;
    private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvenightNormal, tvPriceOvernightDiscount;
    private LinearLayout boxHourly;

    private ImageView btnMyLocation;
    private Marker clickedMarker = null;

    @Override
    protected void onStart() {
        super.onStart();
        PictureGlide.getInstance().clearCache(this);
        if (HotelApplication.isRelease) {
            String screenName = "SHotelMap";
            try {
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screenName);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                mFirebaseAnalytics.logEvent(screenName, bundle);
                MyLog.writeLog("BaseActivity: " + screenName);
            } catch (Exception e) {
                MyLog.writeLog("mFirebaseAnalytics------------->" + e);
            }

            try {
                IgawAdbrix.retention(screenName);
                IgawAdbrix.firstTimeExperience(screenName);
            } catch (Exception e) {
                MyLog.writeLog("IgawAdbrix------------->" + e);
            }
        }
    }

    @Override
    public void setScreenName() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("Fabric------------->" + e);
        }

        setContentView(R.layout.map_view_detail_activity);

        hotelDetailForm = getIntent().getParcelableExtra("HotelDetailForm");

        roomAvailable = getIntent().getIntExtra("RoomAvailable", 1);

        btnMyLocation = findViewById(R.id.btnMyLocation);

        imgHotel = findViewById(R.id.imgHotel);
        boxHotelPopup = findViewById(R.id.boxHotelPopup);
        tvHotelNamePopup = findViewById(R.id.tvNameVip);
        tvAddressPopup = findViewById(R.id.tvAddressPopup);
        tvReview = findViewById(R.id.txtReview);
        tvPriceStatus = findViewById(R.id.tvPriceStatus);
        tvPriceHourlyNormal = findViewById(R.id.tvPriceHourlyNormal);
        tvPriceHourlyDiscount = findViewById(R.id.tvPriceHourlyDiscount);
        tvPriceOvenightNormal = findViewById(R.id.tvPriceOvernightNormal);
        tvPriceOvernightDiscount = findViewById(R.id.tvPriceOvernightDiscount);

        boxHourly = findViewById(R.id.boxHourly);

        img360 = findViewById(R.id.item_hotel_icon_360);

        imgIconPromotion1 = findViewById(R.id.imgIconPromotion1);


        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        boxHotelPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        setUpMapIfNeeded();

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int i = 0; i < markers.size(); i++) {
                        builder.include(markers.get(i).getPosition());
                    }
                    Location location = mMap.getMyLocation();
                    if (location != null) {
                        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                    if (markers.size() > 0) {
                        int width = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                        int height = getResources().getDisplayMetrics().heightPixels - 3 * getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                        int padding = (int) (width * 0.10);
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cu);

                    }
                } catch (Exception e) {
                    MyLog.writeLog("btnMyLocation------------->" + e);
                }
            }
        });
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        if (ActivityCompat.checkSelfPermission(MapViewDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapViewDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            return;
                        }
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.setMyLocationEnabled(true);
                        // Check if we were successful in obtaining the map.
                        if (mMap != null) {
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
//                                    String fileName = "on_clicked";
//
//                                    if (roomAvailable == 0) {
//                                        fileName = "off_clicked";
//                                    }
//                                    if (hotelDetailForm != null && hotelDetailForm.getHasPromotion() == 1) {
//                                        fileName += "_p";
//
//                                    }
//                                    if (hotelDetailForm != null && hotelDetailForm.getNewHotel() == 1) {
//                                        fileName += "_n";
//
//                                    }
//                                    if (hotelDetailForm != null && hotelDetailForm.getHotHotel() == 1) {
//                                        fileName += "_h";
//
//                                    }
//
//                                    if (hotelDetailForm != null) {
//                                        if (hotelDetailForm.getHotelStatus() == ContractType.GENERAL.getType() || hotelDetailForm.getHotelStatus() == ContractType.TERMINAL.getType()) {
//                                            fileName = "uncontract_on";
//                                        }
//                                    }
//                                    int resID = getResources().getIdentifier(fileName, "drawable", getPackageName());
//
//                                    try {
//                                        if (clickedMarker != null) {
//                                            clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(resID));
//                                        }
//                                    } catch (Exception e) {
//                                        MyLog.writeLog("hotelDetailForm------------->" + e);
//                                        e.printStackTrace();
//                                    }

                                    Location location = mMap.getMyLocation();
                                    if (line == null && location != null) {
                                        DialogUtils.showLoadingProgress(MapViewDetailActivity.this, false);
                                        FetchUrl fetchUrl = new FetchUrl(MapViewDetailActivity.this);
                                        LatLng dest = marker.getPosition();
                                        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                                        String url = Utils.getMapRootUrl(origin, dest);
                                        fetchUrl.execute(url);
                                    }
                                    showHotelBox();
                                    return false;
                                }
                            });

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
//                                    if (clickedMarker != null) {
//                                        String fileName = "on";
//
//                                        if (roomAvailable == 0) {
//                                            fileName = "off";
//                                        }
//                                        if (hotelDetailForm != null && hotelDetailForm.getHasPromotion() == 1) {
//                                            fileName += "_p";
//
//                                        }
//                                        if (hotelDetailForm != null && hotelDetailForm.getNewHotel() == 1) {
//                                            fileName += "_n";
//
//                                        }
//                                        if (hotelDetailForm != null && hotelDetailForm.getHotHotel() == 1) {
//                                            fileName += "_h";
//
//                                        }
//
//                                        MyLog.writeLog("MarkerfileName: " + fileName);
//
//                                        if (hotelDetailForm != null) {
//                                            if (hotelDetailForm.getHotelStatus() == ContractType.GENERAL.getType() || hotelDetailForm.getHotelStatus() == ContractType.TERMINAL.getType()) {
//                                                fileName = "uncontract";
//                                            }
//                                        }
//                                        int resID = getResources().getIdentifier(fileName, "drawable", getPackageName());
//
//                                        clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(resID));
//                                    }
                                    hideHotelBox();
                                }
                            });
                            setUpMap();
                            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {
                                    if (markers.size() > 0 && line == null) {
                                        DialogUtils.showLoadingProgress(MapViewDetailActivity.this, false);
                                        FetchUrl fetchUrl = new FetchUrl(MapViewDetailActivity.this);
                                        LatLng dest = markers.get(0).getPosition();
                                        LatLng origin = null;
                                        try {
                                            origin = new LatLng(Double.parseDouble(PreferenceUtils.getLatLocation(MapViewDetailActivity.this)),
                                                    Double.parseDouble(PreferenceUtils.getLongLocation(MapViewDetailActivity.this)));
                                        } catch (Exception e) {
                                            MyLog.writeLog("showLoadingProgress------------->" + e);
                                        }
                                        if (origin != null) {
                                            String url = Utils.getMapRootUrl(origin, dest);
                                            fetchUrl.execute(url);
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
            }

        }
    }

    public void setUpMap() {
        markers.clear();
        mMap.clear();

        new AddTextToMaker().execute();


    }

    @Override
    protected void onDestroy() {
        if (mMap != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();//.commit();
            mMap = null;
        }
        super.onDestroy();
    }

    private Marker firstRoutMarker = null, endRoutMarker = null;

    @Override
    public void onFinishedParser(PolylineOptions polylineOptions, LatLng firstPoint, LatLng
            endPoint) {
        DialogUtils.hideLoadingProgress();
        try {
            if (polylineOptions != null) {
                if (line != null) {
                    line.remove();
                }
                line = mMap.addPolyline(polylineOptions);
                if (firstRoutMarker != null) {
                    firstRoutMarker.remove();
                }

                if (endRoutMarker != null) {
                    endRoutMarker.remove();
                }
                int width = getResources().getDimensionPixelSize(R.dimen.rout_marker_size);

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.myspot);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, width, false);
                MarkerOptions markerOptionsFirst = new MarkerOptions();
                markerOptionsFirst.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                markerOptionsFirst.position(endPoint);
                markerOptionsFirst.anchor(0.5f, 0.5f);
                firstRoutMarker = mMap.addMarker(markerOptionsFirst);

                MarkerOptions markerOptionEnd = new MarkerOptions();
                markerOptionEnd.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                markerOptionEnd.position(firstPoint).anchor(0.5f, 0.5f);
                endRoutMarker = mMap.addMarker(markerOptionEnd);
            }
        } catch (Exception e) {
            MyLog.writeLog("onFinishedParser------------->" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    public void showHotelBox() {
        if (boxHotelPopup.getVisibility() == View.GONE) {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.hotel_slide_up);
            slide_up.setFillBefore(true);
            slide_up.setFillAfter(true);
            boxHotelPopup.setVisibility(View.INVISIBLE);
            slide_up.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    boxHotelPopup.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            boxHotelPopup.startAnimation(slide_up);
        }
        if (hotelDetailForm != null) {

            //Set Hotel Name
            tvHotelNamePopup.setText(hotelDetailForm.getName());
            //Set Address
            tvAddressPopup.setText(hotelDetailForm.getAddress());

            //Set Rating
            double rate = hotelDetailForm.getAverageMark() * 2;
            if (rate <= 0) {
                tvReview.setVisibility(View.GONE);
            } else {
                tvReview.setText(String.valueOf(rate));
            }


            //Get FlashSale
            int p = hotelDetailForm.checkFlashSale();
            //Check Flash Sale
            if (p > -1) {
                RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(p);
                if (roomTypeForm != null) {
                    int rooms = roomTypeForm.getAvailableRoom();
                    String s = "";

                    int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
                    int priceOvernightDiscount = roomTypeForm.getPriceOvernight();
                    if (superSale > 0) {
                        priceOvernightDiscount = priceOvernightDiscount - superSale;
                        if (rooms > 0) {
                            //if (rooms <= 5)
                                s = getString(R.string.txt_2_super_flashsale_room_left, String.valueOf(rooms));
                        } else {
                            s = getString(R.string.txt_2_super_flashsale_sold_out);
                        }
                    } else { // normal
                        if (rooms > 0) {
                            if (rooms <= 5) {
                                s = String.format(getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                            }
                        } else {
                            s = getString(R.string.txt_2_flashsale_sold_out);
                        }
                    }
                    //Set Price Status
                    tvPriceStatus.setText(s);

                    //Set Label Flash Sale
                    imgIconPromotion1.setVisibility(View.VISIBLE);
                    imgIconPromotion1.setImageResource(R.drawable.icon_sale);

                    boxHourly.setVisibility(View.GONE);

                    //Set Price Overnight Normal
                    tvPriceOvenightNormal.setText(Utils.formatCurrency(hotelDetailForm.getLowestPrice()));
                    //StrikeThrough
                    tvPriceOvenightNormal.setPaintFlags(tvPriceOvenightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    //Set Price Overnight Discount
                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

                    //Feature92
                    if (superSale > 0){
                        TextView tvSupperSaleNormal = findViewById(R.id.tvSupperSaleNormal);
                        tvSupperSaleNormal.setVisibility(View.VISIBLE);
                        tvSupperSaleNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                        tvSupperSaleNormal.setPaintFlags(tvSupperSaleNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        TextView tvSupperSaleDiscount = findViewById(R.id.tvSupperSaleDiscount);
                        tvSupperSaleDiscount.setVisibility(View.VISIBLE);
                        tvSupperSaleDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

                        tvPriceOvernightDiscount.setVisibility(View.GONE);

                    }

                }
            } else {

                imgIconPromotion1.setVisibility(View.GONE);
                boxHourly.setVisibility(View.VISIBLE);

                //Set Price Status
                tvPriceStatus.setText("");

                //--------------Set Price------------
                int[] discount = Utils.getPromotionInfoForm(
                        hotelDetailForm.getSn(),
                        hotelDetailForm.getLowestPrice(),
                        hotelDetailForm.getLowestPriceOvernight(),
                        0,
                        0);

                //-------------Set Label Hourly---------------
                TextView txtLabelPriceHourly = findViewById(R.id.label_price_hourly);
                String s = getString(R.string.txt_2_flashsale_hourly_price, String.valueOf(hotelDetailForm.getFirstHours()));
                txtLabelPriceHourly.setText(s);

                if (discount[0] > 0 || discount[1] > 0) {

                    //Set Price Status
                    tvPriceStatus.setVisibility(View.VISIBLE);
                    tvPriceStatus.setText(getString(R.string.txt_2_coupon_applied));

                    //Hourly
                    if (discount[0] > 0) {
                        int priceHourlyDiscount = hotelDetailForm.getLowestPrice() - discount[0];

                        if (priceHourlyDiscount < 0) {
                            priceHourlyDiscount = 0;
                        }

                        //Set Price Hourly Normal
                        tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                        tvPriceHourlyNormal.setText(Utils.formatCurrency(hotelDetailForm.getLowestPrice()));
                        //StrikeThrough
                        tvPriceHourlyNormal.setPaintFlags(tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        //Set Price Hourly Discount
                        tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                    } else {

                        //Set Price Hourly Normal
                        tvPriceHourlyNormal.setVisibility(View.GONE);
                        //Set Price Hourly Discount
                        tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelDetailForm.getLowestPrice()));

                    }

                    //Overnight
                    if (discount[1] > 0) {
                        int priceOvernightDiscount = hotelDetailForm.getLowestPriceOvernight() - discount[1];

                        if (priceOvernightDiscount < 0) {
                            priceOvernightDiscount = 0;
                        }

                        //Set Price Overnight Normal
                        tvPriceOvenightNormal.setVisibility(View.VISIBLE);
                        tvPriceOvenightNormal.setText(Utils.formatCurrency(hotelDetailForm.getLowestPriceOvernight()));
                        //StrikeThrough
                        tvPriceOvenightNormal.setPaintFlags(tvPriceOvenightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        //Set Price Overnight Discount
                        tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                    } else {
                        //Set Price Overnight Normal
                        tvPriceOvenightNormal.setVisibility(View.GONE);

                        //Set Price Overnight Discount
                        tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelDetailForm.getLowestPriceOvernight()));
                    }

                } else {

                    //Set Price Status
                    tvPriceStatus.setVisibility(View.GONE);

                    //Set Price Hourly Normal
                    tvPriceHourlyNormal.setVisibility(View.GONE);
                    //Set Price Hourly Discount
                    tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelDetailForm.getLowestPrice()));

                    //Set Price Overnight Normal
                    tvPriceOvenightNormal.setVisibility(View.GONE);

                    //Set Price Overnight Discount
                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelDetailForm.getLowestPriceOvernight()));

                }

            }

            /*
            /Set Icon 360
            */
            if (hotelDetailForm.getCountExifImage() > 0) {
                img360.setVisibility(View.VISIBLE);
            } else {
                img360.setVisibility(View.GONE);
            }

            img360.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            String url = "";
            if (hotelDetailForm != null && hotelDetailForm.getHotelImageList().size() > 0) {
                //url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelHomeImage?hotelSn=" + hotelDetailForm.getSn() + "&fileName=" + hotelDetailForm.getHotelImageList().get(0).getCustomizeName();
                url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + hotelDetailForm.getImageKey();
            }
            PictureGlide.getInstance().show(
                    url,
                    getResources().getDimensionPixelSize(R.dimen.hotel_popup_width),
                    getResources().getDimensionPixelSize(R.dimen.hotel_popup_height),
                    R.drawable.loading_big,
                    imgHotel
            );
        }
    }

    public void hideHotelBox() {
        if (boxHotelPopup.getVisibility() == View.VISIBLE) {
            Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.hotel_slide_down);
            slide_down.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    boxHotelPopup.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            boxHotelPopup.startAnimation(slide_down);
        }
    }

    private String getPricePromotion(HotelDetailForm hotelDetailForm) {
        String price;
        //Get Price
        int[] discount = Utils.getPromotionInfoForm(
                hotelDetailForm.getSn(),
                hotelDetailForm.getLowestPrice(),
                hotelDetailForm.getLowestPriceOvernight(),
                0,
                0);

            if (discount[0] > 0) {
                int p = hotelDetailForm.getLowestPrice() - discount[0];
                if (p < 0) {
                    p = 0;
                }
                price = String.valueOf(Utils.formatCurrencyK(p));
            } else {
                price = String.valueOf(Utils.formatCurrencyK(hotelDetailForm.getLowestPrice()));
            }

        return price;
    }

    private class AddTextToMaker extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {

            String fileName = "marker";

            String price = "-1";

            if (hotelDetailForm != null) {
                price = getPricePromotion(hotelDetailForm);

                int f = hotelDetailForm.checkFlashSale();


                //Check Flash Sale
                if (f == -1) {
                    //Check not Available
                    if (roomAvailable == 0) {
                        fileName += "_gray";
                    } else {

                        //Check normal
                        int[] p = Utils.getPromotionInfoForm(hotelDetailForm.getSn(), 0, 0, 0,0);
                        if (p[0] > 0 || p[1] > 0 || p[2] > 0) {
                            fileName += "_green";
                        } else {
                            //Check Promotion
                            fileName += "_org";
                        }
                    }

                    //Check New
                    if (hotelDetailForm.getNewHotel() == 1) {
                        fileName += "_new";
                    }
                    //Check unContract
                    if (hotelDetailForm.getHotelStatus() == ContractType.GENERAL.getType() || hotelDetailForm.getHotelStatus() == ContractType.TERMINAL.getType()) {
                        fileName = "uncontract";
                        price = "-1";
                    }

                } else {
                    //IsFlashSale
                    fileName += "_red";

                    RoomTypeForm roomTypeForm = hotelDetailForm.getFlashSaleRoomTypeForm();
                    int priceDiscount = 0;
                    if (roomTypeForm != null) {
                        priceDiscount = roomTypeForm.getPriceOvernight();
                        int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
                        if (superSale > 0) {
                            priceDiscount = priceDiscount - superSale;
                        }
                    }

                    price = String.valueOf(Utils.formatCurrencyK(priceDiscount));
                }
            }

            int resID = getResources().getIdentifier(fileName, "drawable", getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resID).copy(Bitmap.Config.ARGB_8888, true);
            if (!price.equals("-1")) {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                paint.setTextAlign(Paint.Align.CENTER);
                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Display-Medium.ttf");
                paint.setTypeface(font);
                paint.setTextSize(getResources().getDimension(R.dimen.flash_sale_text_maker));
                Canvas canvas = new Canvas(bitmap);

                canvas.drawText(price, bitmap.getWidth() / 2, bitmap.getHeight() / 1.6f, paint);
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(hotelDetailForm.getLatitude(), hotelDetailForm.getLongitude()));

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                clickedMarker = mMap.addMarker(markerOptions);
                markers.add(clickedMarker);


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < markers.size(); i++) {
                    builder.include(markers.get(i).getPosition());
                }
                try {
                    if (mMap.getMyLocation() != null) {
                        builder.include(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()));
                    } else {
                        try {
                            builder.include(new LatLng(Double.parseDouble(PreferenceUtils.getLatLocation(MapViewDetailActivity.this)), Double.parseDouble(PreferenceUtils.getLongLocation(MapViewDetailActivity.this))));
                        } catch (Exception e) {
                            MyLog.writeLog("builder------------->" + e);
                        }
                    }
                    if (markers.size() > 0) {
                        int width = getResources().getDimensionPixelSize(R.dimen.marker_width1);
                        int widthScreen = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                        int heightScreen = getResources().getDisplayMetrics().heightPixels - 3 * getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                        int padding = (int) (width * 0.10);
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, widthScreen, heightScreen, padding);
                        mMap.animateCamera(cu);

                    }
                } catch (Exception e) {
                    MyLog.writeLog("LatLngBounds------------->" + e);
                }
            }

        }
    }
}

