package com.appromobile.hotel.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.AreaSettingActivity;
import com.appromobile.hotel.activity.ChooseAreaActivity;
import com.appromobile.hotel.activity.IntentTemp;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.adapter.HomeFavoriteAdapter;
import com.appromobile.hotel.api.controllerApi.CallbackPromotionInfoForm;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.callback.ResultCallback;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.MapFilterType;
import com.appromobile.hotel.gps.Constants;
import com.appromobile.hotel.gps.FetchUrl;
import com.appromobile.hotel.gps.GeoCodeService;
import com.appromobile.hotel.gps.ParseCallBack;
import com.appromobile.hotel.model.request.HomeHotelRequest;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.MarkerWrapper;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.panorama.LoadTaskComplete;
import com.appromobile.hotel.utils.AddressResultReceiver;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.TextOnDrawable;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 6/24/2016.
 */
public class MapTabFragment extends BaseFragment implements ParseCallBack, View.OnClickListener {
    private GoogleMap mMap;
    private double latitude, longitude;
    private List<MarkerWrapper> markers = new ArrayList<>();
    List<HotelForm> hotelForms = new ArrayList<>();
    private Polyline line;
    private AddressResultReceiver mResultReceiver;
    private TextView tvLocation;
    private TextView tvMyFavorite;
    private LinearLayout dropdownAreaSetting;
    private TextView tvChooseArea;
    private TextView tvAreaSelected;
    private ListView lvFavorite;
    private ImageView imgDropDownArea;
    private TextViewSFRegular tvNearby;
    private double mapLat, mapLng;
    private boolean
            isFilterAvailable = false,
            isFilterPromotion = false,
            isFilterNew = false,
            isFilterHot = false,
            isFilterFlashSale = false;
    private boolean isFilterDistrict = false;
    private ImageView btnMyLocation;
    String province = "";
    private int districtSn = -1;
    private String districName;
    boolean isMoved = true;

    private Marker previousMarkerClick = null;

    public MapTabFragment() {
        setScreenName("SMap");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        province = getString(R.string.default_province);
        tvLocation = rootView.findViewById(R.id.tvLocation);
        btnMyLocation = rootView.findViewById(R.id.btnMyLocation);
        tvAreaSelected = rootView.findViewById(R.id.tvAreaSelected);
        dropdownAreaSetting = rootView.findViewById(R.id.dropdownAreaSetting);
        imgDropDownArea = rootView.findViewById(R.id.imgDropDownArea);
        tvMyFavorite = rootView.findViewById(R.id.tvMyFavorite);
        tvChooseArea = rootView.findViewById(R.id.tvChooseArea);
        lvFavorite = rootView.findViewById(R.id.lvFavorite);
        tvNearby = rootView.findViewById(R.id.tvNearby);
        tvAreaSelected.setOnClickListener(this);
        tvMyFavorite.setOnClickListener(this);
        tvChooseArea.setOnClickListener(this);
        lvFavorite.setOnItemClickListener(favoriteAreaItemClick);
        tvNearby.setOnClickListener(this);

        mResultReceiver = new AddressResultReceiver(new Handler(), new ResultCallback() {
            @Override
            public void onFinishedResult(String province, String messageResult) {
                try {
                    if (province.equals("") && messageResult.equals(getString(R.string.service_not_available))) {
                        startGeoCodeIntentService(mapLat, mapLng);
                    } else if (!province.equals("")) {
                        initData(province);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("mResultReceiver---------------->" + e);
                }
            }
        });

        setUpMapIfNeeded();

        //Onclick Dropdown
        dropdownAreaSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownAreaSetting.setVisibility(View.GONE);
                imgDropDownArea.setImageResource(R.drawable.combobox_down);
            }
        });

        //
        imgDropDownArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDropdownVisible();
            }
        });

        String lastLocation = PreferenceUtils.getLatLocation(getContext());
        if (!lastLocation.equals("")) {
            latitude = Double.parseDouble(PreferenceUtils.getLatLocation(getContext()));
            longitude = Double.parseDouble(PreferenceUtils.getLongLocation(getContext()));
        }

        Location location = Utils.getLocationFromPref(getContext());
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        currentPosition = new LatLng(latitude, longitude);
        Address address = PreferenceUtils.getLastAddress(getActivity());
        if (address != null) {
            province = address.getAdminArea();
            if ((province != null && province.equals("")) || (province == null)) {
                province = getString(R.string.default_province);
            }
        }

        //Set toolbar Address
        setAddressLocation();

        //Map Icon Click --> move to My Address
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentZoom = 14.0f;
                    String lastLocation = PreferenceUtils.getLatLocation(getContext());
                    if (!lastLocation.equals("")) {
                        latitude = Double.parseDouble(PreferenceUtils.getLatLocation(getContext()));
                        longitude = Double.parseDouble(PreferenceUtils.getLongLocation(getContext()));
                    }
                    Location location = Utils.getLocationFromPref(getContext());
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                            longitude), currentZoom));
                } catch (Exception e) {
                    MyLog.writeLog("btnMyLocation---------------->" + e);
                }
            }
        });
        return rootView;
    }

    private void setAddressLocation() {
        String address = PreferenceUtils.getCurrentAddress(getActivity());
        tvLocation.setText(address);
    }

    @Override
    public void onUpdateLocation() {
        super.onUpdateLocation();
        refreshNeaby();
        setAddressLocation();
    }

    private void initData(String provinceCode) {
        MyLog.writeLog("Start init data for map");
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put("provinceCode", provinceCode);
            params.put("typeSearch", 1);
            try {
                LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                Location loc1 = new Location("gps");
                loc1.setLatitude(mMap.getCameraPosition().target.latitude);
                loc1.setLongitude(mMap.getCameraPosition().target.longitude);

                Location loc2 = new Location("gps");
                loc2.setLatitude(latLngBounds.northeast.latitude);
                loc2.setLongitude(latLngBounds.northeast.longitude);

                float distance = loc1.distanceTo(loc2) / 1000;
                params.put("radius", Math.abs(distance));
                params.put("longitude", mMap.getCameraPosition().target.longitude);
                params.put("latitude", mMap.getCameraPosition().target.latitude);

                boolean isGetall = false;

                //Filter
                if (!isFilterHot && !isFilterNew && !isFilterPromotion && !isFilterAvailable && !isFilterFlashSale) {
                    isGetall = true;
                }

                //Filter
                if (isFilterHot && isFilterNew && isFilterPromotion && isFilterAvailable && isFilterFlashSale) {
                    isGetall = true;
                }

                if (!isGetall) {
                    params.put("hotHotel", isFilterHot);
                    params.put("newHotel", isFilterNew);
                    params.put("hasPromotion", isFilterPromotion);
                    params.put("roomAvailable", isFilterAvailable);
                    params.put("hasFlashsale", isFilterFlashSale);
                }
            } catch (Exception e) {
                MyLog.writeLog("Error findHotelListForMap:---------------------------> " + e);
            }

            //Get Promotion Info Form
            if (HotelApplication.mapPromotionInfoForm == null || HotelApplication.mapPromotionInfoForm.size() == 0) {
                ControllerApi.getmInstance().findPromotionInformation(getActivity(), new CallbackPromotionInfoForm() {
                    @Override
                    public void map(Map<String, PromotionInfoForm> map) {
                        HotelApplication.mapPromotionInfoForm = map;

                        findHotelListForMap(params);

                    }
                });
            } else {
                findHotelListForMap(params);

            }

        } catch (Exception e) {
            MyLog.writeLog("Error initDataMyPageFragment:---------------------------> " + e);
        }
    }

    private void findHotelListForMap(Map<String, Object> params) {
        HotelApplication.serviceApi.findHotelListForMap(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                if (response.isSuccessful()) {
                    hotelForms = response.body();
                    if (hotelForms != null && hotelForms.size() > 0) {
                        setUpMap(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
            }
        });
    }

    //Load default for Map
    public void setUpMap(boolean isFilterDistrict) {
        if (getContext() != null) {
        /*
        / For showing a move to my location button
        */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
            }

            boolean isGetall = false;
            if (!isFilterHot && !isFilterNew && !isFilterPromotion && !isFilterAvailable && !isFilterFlashSale) {
                isGetall = true;
            }
            if (isFilterHot || isFilterNew || isFilterPromotion || isFilterAvailable || isFilterFlashSale) {
                isGetall = true;
            }

            if (markers != null) {
                for (MarkerWrapper markerWrapper : markers) {
                    markerWrapper.getMarker().setVisible(false);
                }
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < hotelForms.size(); i++) {

                //Get Hotel Form
                HotelForm hotelForm = hotelForms.get(i);
                //Get Location
                LatLng latLng = new LatLng(hotelForm.getLatitude(), hotelForm.getLongitude());

                if (isFilterDistrict) {
                    builder.include(latLng);
                }

                setupMarkerForMap(hotelForm, latLng, isGetall);

            }
            if (isFilterDistrict) {
                int width = getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                int height = getResources().getDisplayMetrics().heightPixels - 3 * getResources().getDimensionPixelSize(R.dimen.map_icon_size);
                int padding = (int) (width * 0.10);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mMap.animateCamera(cu);
            }
            MyLog.writeLog("markersSize: " + markers.size());
        }
    }

    private AdapterView.OnItemClickListener favoriteAreaItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (getActivity() != null) {
                Intent intent = new Intent(getContext(), IntentTemp.class);
                intent.setAction(ParamConstants.ACTION_CHOOSE_AREA_FAVORITE);
                intent.putExtra("positionFavorite", position);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_AREA_FAVORITE);

                //setFavorite(position);
            }
        }
    };

    private void setFavorite(int position) {
        UserAreaFavoriteForm userAreaFavoriteForm = HotelApplication.userAreaFavoriteForms.get(position);
        districtSn = userAreaFavoriteForm.getDistrictSn();
        districName = userAreaFavoriteForm.getDistrictName();
        chooseArea(userAreaFavoriteForm.getDistrictSn(), userAreaFavoriteForm.getDistrictName());
    }

    private void setDropdownVisible() {
        if (dropdownAreaSetting.getVisibility() == View.VISIBLE) {
            dropdownAreaSetting.setVisibility(View.GONE);
            imgDropDownArea.setImageResource(R.drawable.combobox_down);
        } else {
            dropdownAreaSetting.setVisibility(View.VISIBLE);
            imgDropDownArea.setImageResource(R.drawable.combobox_up);
            if (PreferenceUtils.getToken(getContext()).equals("")) {
                lvFavorite.setVisibility(View.GONE);
                tvMyFavorite.setVisibility(View.VISIBLE);
                tvMyFavorite.setText(getString(R.string.set_my_favorite_area));
            } else {
                // call api to create new data
                HotelApplication.serviceApi.findAllFavoriteArea(PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserAreaFavoriteForm>>() {
                    @Override
                    public void onResponse(Call<List<UserAreaFavoriteForm>> call, Response<List<UserAreaFavoriteForm>> response) {

                        List<UserAreaFavoriteForm> list = response.body();
                        if (response.isSuccessful() && list != null) {
                            // if size = 0, hide list view and distplay set my favorite String
                            if (list.size() == 0) {
                                tvMyFavorite.setText(getString(R.string.set_my_favorite_area));
                                tvMyFavorite.setVisibility(View.VISIBLE);
                                lvFavorite.setVisibility(View.GONE);
                            } else {
                                // create apdater and set data
                                HotelApplication.userAreaFavoriteForms = list;
                                tvMyFavorite.setVisibility(View.GONE);
                                HomeFavoriteAdapter homeFavoriteAdaper = new HomeFavoriteAdapter(getContext(), HotelApplication.userAreaFavoriteForms);
                                lvFavorite.setAdapter(homeFavoriteAdaper);
                                lvFavorite.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserAreaFavoriteForm>> call, Throwable t) {

                    }
                });

            }
        }
    }

    @Override
    public void chooseArea(int districtSn, String districtName) {
        super.chooseArea(districtSn, districtName);
        setDropdownVisible();
        this.districtSn = districtSn;
        this.districName = districtName;
        filterDistrict(districtSn, districtName);
        tvAreaSelected.setText(districtName);
        imgDropDownArea.setImageResource(R.drawable.combobox_down);
    }

    private void filterDistrict(int districtSn, final String districtName) {
        isMoved = false;
        isFilterDistrict = true;
        dropdownAreaSetting.setVisibility(View.GONE);
        MyLog.writeLog("isMoved: " + isMoved);
        HomeHotelRequest homeHotelRequest = new HomeHotelRequest();
        homeHotelRequest.setOffset(0);
        homeHotelRequest.setLimit(HotelApplication.LIMIT_REQUEST);
        homeHotelRequest.setMobileUserId(HotelApplication.DEVICE_ID);
        homeHotelRequest.setDistrictSn(String.valueOf(districtSn));
        Map<String, Object> params = new HashMap<>();
        params.put("hotHotel", isFilterHot);
        params.put("newHotel", isFilterNew);
        params.put("hasPromotion", isFilterPromotion);
        params.put("roomAvailable", isFilterAvailable);
        params.put("hasFlashsale", isFilterFlashSale);
        params.put("offset", 0);
        params.put("limit", HotelApplication.LIMIT_REQUEST);
        if (districtSn > 0) {
            params.put("districtSn", districtSn);
        }

        //Filter in District
        HotelApplication.serviceApi.findLimitHotelListInDistrict(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                try {
                    if (response.isSuccessful()) {
                        hotelForms = response.body();
                        if (hotelForms == null || hotelForms.size() == 0) {
                            Toast.makeText(getContext(), getString(R.string.dont_have_hotel_in_district) + " " + districtName, Toast.LENGTH_LONG).show();
                        } else {
                            setUpMap(true);
                        }
                    }
                } catch (Exception e) {
                    MyLog.writeLog("findLimitHotelListInDistrict------------------------>" + e);
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (getActivity() != null && mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = null;

            try {
                mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        // Check if we were successful in obtaining the map.
                        if (mMap != null) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            mMap.getUiSettings().setCompassEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    try {

                                        //Check Map Click
                                        if (previousMarkerClick != null) {
                                            final MarkerWrapper markerWrapper = findHotelSn(previousMarkerClick);
                                            if (getContext() != null && markerWrapper != null) {
                                                HotelForm hotelForm = markerWrapper.getHotelForm();
                                                if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType()) {

                                                    //Check Marker
                                                    String fileName = "on";

                                                    //Check Flash Sale
                                                    if (!hotelForm.isFlashSale()) {

                                                        if (hotelForm.getRoomAvailable() == 0) {
                                                            fileName = "off";
                                                        }
                                                        if (hotelForm.getHasPromotion() == 1) {
                                                            fileName += "_p";

                                                        }
                                                        if (hotelForm.getNewHotel() == 1) {
                                                            fileName += "_n";

                                                        }
                                                        if (hotelForm.getHotHotel() == 1) {
                                                            fileName += "_h";
                                                        }
                                                    } else {
                                                        fileName = "star";
                                                    }

                                                    int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());


                                                    TextOnDrawable textOnDrawable = new TextOnDrawable(getActivity(), resID, "0", new LoadTaskComplete() {
                                                        @Override
                                                        public void onTaskComplete(Bitmap bitmap) {
                                                            if (bitmap != null) {
                                                                previousMarkerClick.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                            }
                                                        }
                                                    });
                                                    //textOnDrawable.execute("mapclick");


                                                } else {
                                                    String fileName = "uncontract";
                                                    int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
                                                    previousMarkerClick.setIcon(BitmapDescriptorFactory.fromResource(resID));
                                                }
                                                previousMarkerClick = null;
                                                line.remove();
                                            }
                                        }

                                        //Clear Start
                                        if (firstRoutMarker != null) {
                                            firstRoutMarker.remove();
                                            firstRoutMarker = null;
                                        }

                                        //Clear End
                                        if (endRoutMarker != null) {
                                            endRoutMarker.remove();
                                            endRoutMarker = null;
                                        }

                                        //Remove line
                                        if (line != null) {
                                            line.remove();
                                            line = null;
                                        }

                                        //Hide Box Hotel
                                        ((MainActivity) getActivity()).hideHotelBox();

                                    } catch (Exception e) {
                                        MyLog.writeLog("Error previousMarkerClick:---------------------------> " + e);
                                    }
                                }
                            });

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(final Marker marker) {
                                    try {

                                        //Marker Click
                                        if (previousMarkerClick != null) {
                                            final MarkerWrapper markerWrapper = findHotelSn(previousMarkerClick);
                                            if (getContext() != null && markerWrapper != null) {
                                                HotelForm hotelForm = markerWrapper.getHotelForm();
                                                if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType()) {

                                                    String fileName = "on";
                                                    //Check Flash Sale
                                                    if (!hotelForm.isFlashSale()) {

                                                        if (hotelForm.getRoomAvailable() == 0) {
                                                            fileName = "off";
                                                        }
                                                        if (hotelForm.getHasPromotion() == 1) {
                                                            fileName += "_p";
                                                        }
                                                        if (hotelForm.getNewHotel() == 1) {
                                                            fileName += "_n";
                                                        }
                                                        if (hotelForm.getHotHotel() == 1) {
                                                            fileName += "_h";
                                                        }
                                                    } else {
                                                        fileName = "star";
                                                    }

                                                    int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());


                                                    TextOnDrawable textOnDrawable = new TextOnDrawable(getActivity(), resID, "0", new LoadTaskComplete() {
                                                        @Override
                                                        public void onTaskComplete(Bitmap bitmap) {
                                                            if (bitmap != null) {
                                                                previousMarkerClick.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                            }
                                                        }
                                                    });
                                                    //textOnDrawable.execute("lisner");

                                                } else {
                                                    String fileName = "uncontract";
                                                    int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
                                                    previousMarkerClick.setIcon(BitmapDescriptorFactory.fromResource(resID));
                                                }
                                            }
                                        }
                                        not_first_time_showing_info_window = false;
                                        if (line != null) {
                                            line.remove();
                                        }

                                        //OnClick Marker
                                        final MarkerWrapper markerWrapper = findHotelSn(marker);
                                        if (markerWrapper != null) {
                                            HotelForm hotelForm = markerWrapper.getHotelForm();

                                            setButtonName("SMapHotel");
                                            ((MainActivity) getActivity()).showHotelBox(markerWrapper.getHotelForm());

                                            if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType()) {

                                                String fileName = "on_clicked";
                                                //Check Flash Sale
                                                if (!hotelForm.isFlashSale()) {
                                                    if (hotelForm.getRoomAvailable() == 0) {
                                                        fileName = "off_clicked";
                                                    }
                                                    if (hotelForm.getHasPromotion() == 1) {
                                                        fileName += "_p";
                                                    }
                                                    if (hotelForm.getNewHotel() == 1) {
                                                        fileName += "_n";
                                                    }
                                                    if (hotelForm.getHotHotel() == 1) {
                                                        fileName += "_h";
                                                    }
                                                } else {
                                                    fileName = "star";
                                                }

                                                int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());

                                                TextOnDrawable textOnDrawable = new TextOnDrawable(getActivity(), resID, "0", new LoadTaskComplete() {
                                                    @Override
                                                    public void onTaskComplete(Bitmap bitmap) {
                                                        if (bitmap != null) {
                                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                            previousMarkerClick = marker;
                                                        }
                                                    }
                                                });
                                                //textOnDrawable.execute("marnul");

                                            } else {
                                                int size = getResources().getDimensionPixelSize(R.dimen.general_hotel_map_size);
                                                String fileName = "uncontract_on";
                                                int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
                                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(resID);
                                                Bitmap b = bitmapdraw.getBitmap();
                                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, size, size, false);
                                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                                previousMarkerClick = marker;
                                            }
                                        }
                                        FetchUrl fetchUrl = new FetchUrl(MapTabFragment.this);
                                        LatLng dest = marker.getPosition();
                                        LatLng origin = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                                        String url = Utils.getMapRootUrl(origin, dest);
                                        fetchUrl.execute(url);
                                    } catch (Exception e) {
                                        MyLog.writeLog("Error MarkerWrapper:---------------------------> " + e);
                                    }
                                    return false;
                                }
                            });

                            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {
                                    try {
                                        if (isFilterDistrict) {
                                            isFilterDistrict = false;
                                            return;
                                        }
                                        isMoved = true;
                                        if (currentZoom != mMap.getCameraPosition().zoom || currentPosition != mMap.getCameraPosition().target) {
                                            currentZoom = mMap.getCameraPosition().zoom;
                                            currentPosition = mMap.getCameraPosition().target;
                                            mapLat = currentPosition.latitude;
                                            mapLng = currentPosition.longitude;
                                            MyLog.writeLog("Start GeoCodeService MAP");
                                            startGeoCodeIntentService(mapLat, mapLng);
                                        }
                                    } catch (Exception e) {
                                        MyLog.writeLog("Error setOnCameraIdleListener:---------------------------> " + e);
                                    }
                                }
                            });
                            // move camera to current position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                                    longitude), currentZoom));

                        }
                    }
                });
            }

        }
    }

    protected void startGeoCodeIntentService(double lat, double lng) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), GeoCodeService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.LATITUDE_DATA_EXTRA, lat);
            intent.putExtra(Constants.LONGITUDE_DATA_EXTRA, lng);
            getActivity().startService(intent);
        }
    }

    boolean not_first_time_showing_info_window;

    private MarkerWrapper findHotelSn(Marker marker) {
        for (int i = 0; i < markers.size(); i++) {
            if (marker.getId().equals(markers.get(i).getMarker().getId())) {
                MyLog.writeLog("MarkerID: " + marker.getId());
                return markers.get(i);
            }
        }
        return null;
    }

    private float currentZoom = 14.0f;
    private LatLng currentPosition;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAreaSelected:
                setDropdownVisible();
                break;
            case R.id.tvMyFavorite:
                myFavoriteClick();
                break;
            case R.id.tvChooseArea:
                dropdownAreaSetting.setVisibility(View.GONE);
                imgDropDownArea.setImageResource(R.drawable.combobox_down);
                chooseArea();
                break;
            case R.id.tvNearby:
                if (getActivity() != null) {
                    //((MainActivity) getActivity()).getHomeHotelRequest().setDistrictSn(null);
                    //refreshNeaby();
                    setDropdownVisible(); //Show list nearby

                    Intent intent = new Intent(getContext(), IntentTemp.class);
                    intent.setAction(ParamConstants.ACTION_RESET_NEARBY);
                    getActivity().startActivityForResult(intent, ParamConstants.REQUEST_RESET_NEARBY);
                }
                break;
        }
    }

    private void refreshNeaby() {
        currentZoom = 14.0f;
        isMoved = true;

        tvAreaSelected.setText(getString(R.string.near_by));
        String lastLocation = PreferenceUtils.getLatLocation(getContext());
        if (!lastLocation.equals("")) {
            latitude = Double.parseDouble(PreferenceUtils.getLatLocation(getContext()));
            longitude = Double.parseDouble(PreferenceUtils.getLongLocation(getContext()));
        }
        Location location = Utils.getLocationFromPref(getContext());
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                    longitude), currentZoom));
        } catch (Exception e) {
            MyLog.writeLog("Error setOnCameraIdleListener:---------------------------> " + e);
        }
        //setDropdownVisible(); //Show list nearby
    }

    private void chooseArea() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), ChooseAreaActivity.class);
            getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_AREA_MAP);
        }
    }

    private void myFavoriteClick() {
        if (getActivity() != null) {
            if (!PreferenceUtils.getToken(getContext()).equals("")) {
                Intent intent = new Intent(getActivity(), AreaSettingActivity.class);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_FAVORITE_AREA_MAP);
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_AREA_SETTING_MAP);
            }
            setDropdownVisible();
        }
    }

    @Override
    public void closeClickEvent() {
        super.closeClickEvent();

        if (line != null) {
            line.remove();
        }
        if (firstRoutMarker != null) {
            firstRoutMarker.remove();
        }
        if (endRoutMarker != null) {
            endRoutMarker.remove();
        }

        if (previousMarkerClick != null) {
            try {
                final MarkerWrapper markerWrapper = findHotelSn(previousMarkerClick);
                if (getContext() != null && markerWrapper != null) {
                    HotelForm hotelForm = markerWrapper.getHotelForm();
                    if (hotelForm.getHotelStatus() == ContractType.CONTRACT.getType()) {

                        String fileName = "on";
                        //Check Flash Sale
                        if (!hotelForm.isFlashSale()) {
                            if (hotelForm.getRoomAvailable() == 0) {
                                fileName = "off";
                            }
                            if (hotelForm.getHasPromotion() == 1) {
                                fileName += "_p";
                            }
                            if (hotelForm.getNewHotel() == 1) {
                                fileName += "_n";
                            }
                            if (hotelForm.getHotHotel() == 1) {
                                fileName += "_h";
                            }
                        } else {
                            fileName = "star";
                        }
                        int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());

                        previousMarkerClick.setIcon(BitmapDescriptorFactory.fromResource(resID));
                        previousMarkerClick = null;

                    } else {
                        String fileName = "uncontract";
                        int resID = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
                        previousMarkerClick.setIcon(BitmapDescriptorFactory.fromResource(resID));
                        previousMarkerClick = null;
                    }
                }
            } catch (Exception e) {
                MyLog.writeLog("Error setOnCameraIdleListener:---------------------------> " + e);
            }
        }

    }

    private MarkerWrapper isExitMarker(int hotelSn) {
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).getHotelForm().getSn() == hotelSn) {
                return markers.get(i);
            }
        }
        return null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            mMap = null;
        }
    }

    @Override
    public void onFilter(MapFilterType mapFilterType, boolean isChecked) {
        super.onFilter(mapFilterType, isChecked);

        switch (mapFilterType) {
            case Available:
                isFilterAvailable = isChecked;
                break;
            case Promotion:
                isFilterPromotion = isChecked;
                break;
            case New:
                isFilterNew = isChecked;
                break;
            case Hot:
                isFilterHot = isChecked;
            case FlashSale:
                isFilterFlashSale = isChecked;
                break;
        }
        if (isMoved) {
            initData(province);
        } else {
            filterDistrict(districtSn, districName);
        }

        setButtonName("SMapFilter");

    }

    @Override
    public void onRefreshFavorite(int position) {
        super.onRefreshFavorite(position);

        if (position != -1) {
            setFavorite(position);
        }

    }

    @Override
    public void onRefreshNearby() {
        super.onRefreshNearby();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).getHomeHotelRequest().setDistrictSn(null);
            refreshNeaby();
        }
    }

    @Override
    public void onRefreshData() {
        super.onRefreshData();
        if (getActivity() != null) {
            tvAreaSelected.setText(getString(R.string.near_by));
            HotelApplication.serviceApi.getHotelList(((MainActivity) getActivity()).getHomeHotelRequest().getMap(), PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
                @Override
                public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {

                    hotelForms = response.body();
                    if (hotelForms != null && hotelForms.size() > 0) {
                        setUpMap(false);
                    }
                }

                @Override
                public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onFinishedParser(PolylineOptions polylineOptions, LatLng firstPoint, LatLng endPoint) {
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
            MyLog.writeLog("Error onFinishedParser:---------------------------> " + e);
        }
    }

    private Marker firstRoutMarker = null, endRoutMarker = null;

    private String getPricePromotion(HotelForm hotelForm) {
        String price;
        //Get Price
        int[] discount = Utils.getPromotionInfoForm(
                hotelForm.getSn(),
                hotelForm.getLowestPrice(),
                hotelForm.getLowestPriceOvernight(),
                0,
                0);

        if (discount[0] > 0) {
            int p = hotelForm.getLowestPrice() - discount[0];
            if (p < 0) {
                p = 0;
            }
            price = String.valueOf(Utils.formatCurrencyK(p));
        } else {
            price = String.valueOf(Utils.formatCurrencyK(hotelForm.getLowestPrice()));
        }

        return price;
    }

    private void setupMarkerForMap(final HotelForm hotelForm, final LatLng latLng, final boolean isGetall) {

        String fileName = "marker";
        String price = getPricePromotion(hotelForm);

        //Check Flash Sale
        if (!hotelForm.isFlashSale()) {
            //Check not Available
            if (hotelForm.getRoomAvailable() != 1) {
                fileName += "_gray";
            } else {

                //Check normal
                int[] p = Utils.getPromotionInfoForm(hotelForm.getSn(), 0, 0, 0,0);
                if (p[0] > 0 || p[1] > 0 || p[2] > 0) {
                    fileName += "_green";
                } else {
                    //Check Promotion
                    fileName += "_org";
                }
            }

            //Check New
            if (hotelForm.getNewHotel() == 1) {
                fileName += "_new";
            }
            //Check unContract
            if (hotelForm.getHotelStatus() == ContractType.GENERAL.getType() || hotelForm.getHotelStatus() == ContractType.TERMINAL.getType() || hotelForm.getHotelStatus() == ContractType.SUSPEND.getType()) {
                fileName = "uncontract";
                price = "-1";
            }

        } else {
            //IsFlashSale
            fileName += "_red";

            RoomTypeForm roomTypeForm = hotelForm.getFlashSaleRoomTypeForm();
            int priceDiscount = 0;
            if (roomTypeForm != null) {
                priceDiscount = roomTypeForm.getPriceOvernight();
                int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
                if (superSale > 0) {
                    priceDiscount = priceDiscount - superSale;
                }
            }

            price = Utils.formatCurrencyK(priceDiscount);
        }

        if (getContext() != null) {
            final int idResource = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());


            final MarkerWrapper currentMarker = isExitMarker(hotelForm.getSn());

            //Create new Maker
            if (currentMarker == null) {
                TextOnDrawable textOnDrawable = new TextOnDrawable(getActivity(), idResource, price, new LoadTaskComplete() {
                    @Override
                    public void onTaskComplete(Bitmap bitmap) {

                        try {
                            if (bitmap != null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                MarkerWrapper markerWrapper = new MarkerWrapper(hotelForm, mMap.addMarker(markerOptions));
                                markers.add(markerWrapper);

                                if (!isGetall) {
                                    markerWrapper.getMarker().setVisible(false);
                                    if (isFilterAvailable && markerWrapper.getHotelForm().getRoomAvailable() == 1) {
                                        markerWrapper.getMarker().setVisible(true);
                                    }
                                    if (isFilterPromotion && markerWrapper.getHotelForm().getHasPromotion() == 1) {
                                        markerWrapper.getMarker().setVisible(true);
                                    }
                                    if (isFilterNew && markerWrapper.getHotelForm().getNewHotel() == 1) {
                                        markerWrapper.getMarker().setVisible(true);
                                    }
                                    if (isFilterHot && markerWrapper.getHotelForm().getHotHotel() == 1) {
                                        markerWrapper.getMarker().setVisible(true);
                                    }
                                    if (isFilterFlashSale && markerWrapper.getHotelForm().getHotHotel() == 1) {
                                        //
                                        //currentMarker.getMarker().setVisible(true);
                                        //
                                    }
                                } else {
                                    markerWrapper.getMarker().setVisible(true);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                textOnDrawable.execute();


            } else {
                currentMarker.setHotelForm(hotelForm);

                if (!fileName.equals("uncontract")) {
                    TextOnDrawable textOnDrawable = new TextOnDrawable(getActivity(), idResource, price, new LoadTaskComplete() {
                        @Override
                        public void onTaskComplete(Bitmap bitmap) {
                            if (bitmap != null) {
                                currentMarker.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            }

                        }
                    });
                    textOnDrawable.execute();
                } else {
                    currentMarker.getMarker().setIcon(BitmapDescriptorFactory.fromResource(idResource));
                }

                if (!isGetall) {
                    currentMarker.getMarker().setVisible(false);
                    if (isFilterAvailable && currentMarker.getHotelForm().getRoomAvailable() == 1) {
                        currentMarker.getMarker().setVisible(true);
                    }
                    if (isFilterPromotion && currentMarker.getHotelForm().getHasPromotion() == 1) {
                        currentMarker.getMarker().setVisible(true);
                    }
                    if (isFilterNew && currentMarker.getHotelForm().getNewHotel() == 1) {
                        currentMarker.getMarker().setVisible(true);
                    }
                    if (isFilterHot && currentMarker.getHotelForm().getHotHotel() == 1) {
                        currentMarker.getMarker().setVisible(true);
                    }
                    if (isFilterFlashSale && currentMarker.getHotelForm().getHotHotel() == 1) {
                        //
                        //currentMarker.getMarker().setVisible(true);
                        //
                    }
                } else {
                    currentMarker.getMarker().setVisible(true);
                }

            }

        }

    }
}
