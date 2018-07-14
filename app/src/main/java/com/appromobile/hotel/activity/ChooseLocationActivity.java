package com.appromobile.hotel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.GeoAutoCompleteAdapter;
import com.appromobile.hotel.adapter.GeoNearbyAdapter;
import com.appromobile.hotel.gps.Constants;
import com.appromobile.hotel.gps.GeoCodeService;
import com.appromobile.hotel.gps.LocationAddress;
import com.appromobile.hotel.model.view.GoogleAddressSearchEntry;
import com.appromobile.hotel.model.view.GoogleNearbyEntry;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.MyEditText;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.igaworks.adbrix.IgawAdbrix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/8/2016.
 */
public class ChooseLocationActivity extends BaseActivity {
    private GoogleMap mMap;

    //    private ImageView btnSearch;
    private MyEditText txtSearch;
    //    private LinearLayout boxSearchResult;
    private ListView lvAddress, lvAddressPin;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GeoAutoCompleteAdapter adapter;
    private List<GoogleAddressSearchEntry> names = new ArrayList<>();
    private GoogleAddressSearchEntry selectedAddressSearchEntry;
    private double lat, lng;
    private String province, district;
    private LinearLayout btnSelectLocation;
    private RelativeLayout boxSearch;
    LinearLayout boxAutoComplete;
    View bottomSheet;
    //    ImageView btnMyLocation;
    String key = "key=AIzaSyDAXZPpfl9oNF-MpeglPBuSE1yuadS_BcA";
    CoordinatorLayout coordinatorLayout;
    private Marker selectedMarker;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String screenName = "SChooseLocation";
    private AddressResultReceiver receiver;



    protected void onStart() {
        super.onStart();

        if (HotelApplication.isRelease) {
            try {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("Fabric------------->" + e);
        }

        setContentView(R.layout.choose_location_activity);
//        btnSearch = (ImageView) findViewById(R.id.btnSearch);
        lvAddressPin =  findViewById(R.id.lvAddressPin);
        boxSearch =  findViewById(R.id.boxSearch);
//        btnBackMap = (ImageView) findViewById(R.id.btnBackMap);
        boxAutoComplete =  findViewById(R.id.boxAutoComplete);
        txtSearch = findViewById(R.id.txtSearch);
        coordinatorLayout =  findViewById(R.id.cord);
        btnSelectLocation =  findViewById(R.id.btnSelectLocation);
//        btnMyLocation = (ImageView) findViewById(R.id.btnMyLocation);
//        boxSearchResult = (LinearLayout) findViewById(R.id.boxSearchResult);
//        btnClearSearch = (ImageView) findViewById(R.id.btnClearSearch);
        lvAddress =  findViewById(R.id.lvAddress);
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(0);

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setUpMapIfNeeded();

        Utils.hideKeyboard(ChooseLocationActivity.this, txtSearch);


        txtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (boxAutoComplete.getVisibility() == View.GONE) {
                        txtSearch.selectAll();

                        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);

                        slide_down.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
//                                lvAddress.setVisibility(View.VISIBLE);
                                boxAutoComplete.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        boxAutoComplete.startAnimation(slide_down);
                        mBottomSheetBehavior.setHideable(true);
                        mBottomSheetBehavior.setPeekHeight(0);

                    }
                }
                return false;
            }
        });

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateDefaltView();
                }
            }
        });
        txtSearch.setOnKeyBoardEditListener(new MyEditText.OnKeyBoardEditListener() {
            @Override
            public void onBackPress() {
                updateDefaltView();
                Utils.hideKeyboard(ChooseLocationActivity.this, txtSearch);
            }
        });

//        btnBackMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                boxSearchResult.setVisibility(View.GONE);
//                updateDefaltView();
//                Utils.hideKeyboard(ChooseLocationActivity.this, txtSearch);
//            }
//        });

//        btnClearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txtSearch.setText("");
//            }
//        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (boxAutoComplete.getVisibility() == View.GONE) {
                    return;
                }
                if (txtSearch.getText().toString().equals("")) {
//                    btnClearSearch.setVisibility(View.GONE);
                } else {
                    names = new ArrayList<>();
//                    btnClearSearch.setVisibility(View.VISIBLE);
                    String input = "";
                    try {
//                input = "input=" + URLEncoder.encode(place[0], "utf-8");
                        input = "input=" + txtSearch.getText().toString().replace(" ", "%20");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    // place type to be searched
                    String types = "types=geocode";
                    String sensor = "sensor=false";
                    String parameters = input + "&" + types + "&" + sensor + "&" + key;
                    parameters = parameters + "&components=country:vn";
                    String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" + parameters;
                    System.out.println("URLRequest: " + url);
                    HotelApplication.serviceApi.getGoogleResult(url).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                ResponseBody res = response.body();
                                if (response.isSuccessful() && res != null) {
                                    String json = res.string();
                                    MyLog.writeLog("json: " + json);

                                    JSONArray ja = new JSONObject(json).getJSONArray("predictions");

                                    for (int i = 0; i < ja.length(); i++) {
                                        JSONObject c = ja.getJSONObject(i);
                                        String description = c.getString("description");
                                        String place_id = c.getString("place_id");
                                        String id = c.getString("id");

                                        String secondary_text = "";
                                        String main_text = "";
                                        try {
                                            JSONObject structured_formatting = c.getJSONObject("structured_formatting");
                                            main_text = structured_formatting.getString("main_text");
                                            secondary_text = structured_formatting.getString("secondary_text");
                                        } catch (Exception e) {
                                            MyLog.writeLog("JSONObject-------------------->" + e);
                                        }

                                        names.add(new GoogleAddressSearchEntry(id, place_id, description, main_text, secondary_text));
                                    }
                                    adapter = new GeoAutoCompleteAdapter(ChooseLocationActivity.this, names);
                                    // Setting the adapter
                                    lvAddress.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                MyLog.writeLog("response.isSuccessful()-------------------->" + e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }
            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_SETTLING) {
                    mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_slide_height));
                    try {
                        lvAddressPin.post(new Runnable() {
                            @Override
                            public void run() {
                                lvAddressPin.smoothScrollToPosition(0);
                            }
                        });

                    } catch (Exception e) {
                        MyLog.writeLog("lvAddressPin-------------------->" + e);
                    }
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        boxSearch.setVisibility(View.VISIBLE);
//                        btnMyLocation.setVisibility(View.VISIBLE);
                    }
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    if (fistListVisibleItem != 0) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    boxSearch.setVisibility(View.GONE);
//                    btnMyLocation.setVisibility(View.GONE);
                }

                MyLog.writeLog("newState: " + newState);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedAddressSearchEntry = (GoogleAddressSearchEntry) parent.getItemAtPosition(position);
                    txtSearch.setText(selectedAddressSearchEntry.getDescription());
//                    btnClearSearch.setVisibility(View.VISIBLE);
                    chooseDetailAddress();
                } catch (Exception e) {
                    MyLog.writeLog("lvAddress-------------------->" + e);
                }
            }
        });

        lvAddressPin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    GoogleNearbyEntry googleNearbyEntry = (GoogleNearbyEntry) parent.getItemAtPosition(position);
                    selectedAddressSearchEntry = new GoogleAddressSearchEntry();
                    selectedAddressSearchEntry.setId(googleNearbyEntry.getId());
                    selectedAddressSearchEntry.setDescription(googleNearbyEntry.getName());
                    selectedAddressSearchEntry.setPlace_id(googleNearbyEntry.getPlace_id());
                    lat = googleNearbyEntry.getLat();
                    lng = googleNearbyEntry.getLng();

//                    txtSearch.setText(selectedAddressSearchEntry.getDescription());
                    txtSearch.setText(googleNearbyEntry.getVicinity());
//                    btnClearSearch.setVisibility(View.VISIBLE);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    chooseDetailAddress();

                } catch (Exception e) {
                    MyLog.writeLog("lvAddressPin-------------------->" + e);
                }
            }
        });

        lvAddressPin.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                fistListVisibleItem = firstVisibleItem;
            }
        });

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocationToUpload();
            }
        });
    }

    int fistListVisibleItem = 0;

    private void selectLocationToUpload() {
        Intent intent = new Intent();
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("province", province);
        intent.putExtra("district", district);
        intent.putExtra("address", txtSearch.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void chooseDetailAddress() {

        String input = "";
        try {
//                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            input = "placeid=" + selectedAddressSearchEntry.getPlace_id();
        } catch (Exception e1) {
            MyLog.writeLog("chooseDetailAddress-------------------->" + e1);
            e1.printStackTrace();
        }

        // place type to be searched
        String types = "types=geocode";
        String sensor = "sensor=false";
        String language = "language=vi";
        String parameters = input + "&" + types + "&" + sensor + "&" + language + "&" + key;
        parameters = parameters + "&components=country:vn";
        String url = "https://maps.googleapis.com/maps/api/place/details/json?" + parameters;
//        System.out.println("URLRequest: " + url);
        HotelApplication.serviceApi.getGoogleResult(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody res = response.body();
                    if (response.isSuccessful() && res != null) {
                        String json = res.string();
                        MyLog.writeLog("json: " + json);

                        JSONObject data = new JSONObject(json);
                        JSONArray address_components = data.getJSONObject("result").getJSONArray("address_components");
                        for (int i = 0; i < address_components.length(); i++) {
                            JSONObject jsonObject = address_components.getJSONObject(i);
                            if (jsonObject.toString().contains("administrative_area_level_1")) {
                                province = jsonObject.getString("short_name");
                            } else if (jsonObject.toString().contains("administrative_area_level_2")) {
                                district = jsonObject.getString("short_name");
                            }
                        }

                        JSONObject location = data.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                        lat = location.getDouble("lat");
                        lng = location.getDouble("lng");

//                        mBottomSheetBehavior.setHideable(false);
//                        mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_slide_height));
                        searchNearby(lat, lng);

                        updateDefaltView();

                        updateMapLocationChoose();
                        Utils.hideKeyboard(ChooseLocationActivity.this);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("getGoogleResult-------------------->" + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updateMapLocationChoose() {
//        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));

        int markerSize = getResources().getDimensionPixelSize(R.dimen.choose_location_map_width);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mapicon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        markerOptions.title(selectedAddressSearchEntry.getDescription());
        if (selectedMarker != null) {
            selectedMarker.remove();
        }

        selectedMarker = mMap.addMarker(markerOptions);

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//        builder.include(marker.getPosition());


//        if (marker != null) {
//            int width = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.map_icon_size);
//            int height = getResources().getDisplayMetrics().heightPixels - 3 * getResources().getDimensionPixelSize(R.dimen.map_icon_size);
//            int padding = (int) (width * 0.10);
//            LatLngBounds bounds = builder.build();
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//            mMap.animateCamera(cu);
//        } else {
        // For zooming automatically to the Dropped PIN Location
        if (mMap != null) {
            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                        lng), 14.0f));
            } catch (Exception e) {
                MyLog.writeLog("updateMapLocationChoose-------------------->" + e);
            }
        }
//        }
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
                        // Check if we were successful in obtaining the map.
                        if (mMap != null) {
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);

                                    int markerSize = getResources().getDimensionPixelSize(R.dimen.choose_location_map_width);
                                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mapicon);
                                    Bitmap b = bitmapdraw.getBitmap();
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                                    markerOptions.title(selectedAddressSearchEntry.getDescription());
                                    if (selectedMarker != null) {
                                        selectedMarker.remove();
                                    }

                                    selectedMarker = mMap.addMarker(markerOptions);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

                                    DialogUtils.showLoadingProgress(ChooseLocationActivity.this, false);
                                    queryAddress(latLng.latitude, latLng.longitude);
                                    lat = latLng.latitude;
                                    lng = latLng.longitude;
                                    try {
                                        searchNearby(lat, lng);
                                    } catch (Exception e) {
                                        MyLog.writeLog("searchNearby-------------------->" + e);
                                    }
                                    mBottomSheetBehavior.setHideable(false);
                                    mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_slide_height));
                                    MyLog.writeLog("lat: " + lat + " :lng: " + lng);
                                }
                            });

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(Double.parseDouble(PreferenceUtils.getLatLocation(ChooseLocationActivity.this)),
                                    Double.parseDouble(PreferenceUtils.getLongLocation(ChooseLocationActivity.this))));

                            int markerSize = getResources().getDimensionPixelSize(R.dimen.choose_location_map_width);
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mapicon);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, markerSize, markerSize, false);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                                    markerOptions.title(selectedAddressSearchEntry.getDescription());
                            if (selectedMarker != null) {
                                selectedMarker.remove();
                            }

                            selectedMarker = mMap.addMarker(markerOptions);


                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(PreferenceUtils.getLatLocation(ChooseLocationActivity.this)),
                                    Double.parseDouble(PreferenceUtils.getLongLocation(ChooseLocationActivity.this))), 14.0f));
                        }
                    }
                });
            }

        }
    }

    private void searchNearby(double lat, double lng) {
        try {
            String input = "";
            try {
//                input = "input=" + URLEncoder.encode(place[0], "utf-8");
                input = "location=" + Double.toString(lat) + "," + Double.toString(lng) + "&radius=500";
            } catch (Exception e1) {
                MyLog.writeLog("searchNearby-------------------->" + e1);
                e1.printStackTrace();
            }

            String parameters = input + "&" + key;

            StringBuilder strBuild = new StringBuilder();
            strBuild.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters + "&type=hotel");

            MyLog.writeLog("strBuildURL: " + strBuild.toString());
            HotelApplication.serviceApi.getGoogleResult(strBuild.toString()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ResponseBody res = response.body();
                        if (response.isSuccessful() && res != null) {
                            String json = res.string();
                            MyLog.writeLog("json111: " + json);

                            JSONObject data = new JSONObject(json);
                            List<GoogleNearbyEntry> googleNearbyEntries = new ArrayList<>();
                            JSONArray results = data.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                GoogleNearbyEntry entry = new GoogleNearbyEntry();
                                JSONObject jsonObject = results.getJSONObject(i);
                                entry.setPlace_id(jsonObject.getString("place_id"));
                                entry.setId(jsonObject.getString("id"));
                                entry.setName(jsonObject.getString("name"));
                                entry.setIcon(jsonObject.getString("icon"));

                                JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                                entry.setLat(location.getDouble("lat"));
                                entry.setLng(location.getDouble("lng"));
                                try {
                                    entry.setVicinity(jsonObject.getString("vicinity"));
                                } catch (Exception e) {
                                    MyLog.writeLog("searchNearby-------------------->" + e);
                                }
                                googleNearbyEntries.add(entry);
                            }

                            MyLog.writeLog("REsultSzie: " + googleNearbyEntries.size());

                            final GeoNearbyAdapter geoNearbyAdapter = new GeoNearbyAdapter(ChooseLocationActivity.this, googleNearbyEntries);
                            lvAddressPin.setAdapter(geoNearbyAdapter);
                            lvAddressPin.requestLayout();

                            coordinatorLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    coordinatorLayout.requestLayout();
                                    coordinatorLayout.invalidate();
                                    mBottomSheetBehavior.setHideable(false);
                                    mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_slide_height));

                                }
                            });

                        }
                    } catch (Exception e) {
                        MyLog.writeLog("searchNearby-------------------->" + e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            MyLog.writeLog("searchNearby-------------------->" + e);
            e.printStackTrace();
        }
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

    protected void startGeoCodeIntentService(double latitude, double longitude) {
        Intent intent = new Intent(this, GeoCodeService.class);
        intent.putExtra(Constants.RECEIVER, receiver);
        intent.putExtra(Constants.LATITUDE_DATA_EXTRA, latitude);
        intent.putExtra(Constants.LONGITUDE_DATA_EXTRA, longitude);
        startService(intent);
    }

    public void queryAddress(double latitude, double longitude) {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Address address = bundle.getParcelable("Address");

                    if (address != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(", ");
                        }
                        String locationAddress = sb.toString();
                        try {
                            locationAddress = locationAddress.trim();
                            locationAddress = locationAddress.substring(0, locationAddress.length() - 1);
                        } catch (Exception e) {
                            MyLog.writeLog("GeocoderHandler-------------------->" + e);
                        }
                        if (selectedMarker != null) {
                            selectedMarker.setTitle(locationAddress);
                        }
                        txtSearch.setText(locationAddress);

                        province = address.getAdminArea();
                        district = address.getSubAdminArea();
                    }

                    DialogUtils.hideLoadingProgress();

                    break;
            }
        }
    }

    private void updateDefaltView() {
        boxAutoComplete.setVisibility(View.GONE);
//        btnBackMap.setVisibility(View.GONE);
//        btnSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {

        if (boxAutoComplete.getVisibility() == View.VISIBLE) {
            updateDefaltView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("ParcelCreator")
    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {

            } else if (resultCode == Constants.FAILURE_RESULT) {
                MyLog.writeLog("Receive result fail");
                String failure = resultData.getString(Constants.RESULT_DATA_KEY);
                // invalid lat-lng params (very rarely happen)
                if (failure.equals(getString(R.string.invalid_lat_long_used))) {

                } else if (failure.equals(getString(R.string.service_not_available))) {
                    // can not connect Internet

                } else {
                    // can not find address with lat-lng

                }
            }
        }
    }
}
