package com.appromobile.hotel.activity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.ChooseHotelAreaAdapter;
import com.appromobile.hotel.adapter.ProvinceAdapter;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.Province;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/12/2016.
 */
public class ChooseAreaActivity extends BaseActivity implements ChooseHotelAreaAdapter.OnUpdateFavoriteListener {
    private ListView lvProvinces;
    private ListView lvHotelArea;
    private ProvinceAdapter provinceAdapter;
    private ChooseHotelAreaAdapter hotelAreaAdapter;
    private List<Province> provinces;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParamConstants.LOGIN_DETAIL_REQUEST_LIKE) {
            if (resultCode == RESULT_OK) {
                onFinished();
                if(hotelAreaAdapter!=null){
                    hotelAreaAdapter.clearData();
                    setupDefaultDistrict();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.area_setting_activity);

        lvHotelArea =  findViewById(R.id.lvHotelArea);
        lvProvinces =  findViewById(R.id.lvProvinces);


        lvProvinces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> params = new HashMap<>();
                params.put("provinceSn", provinceAdapter.getItem(position));
                DialogUtils.showLoadingProgress(ChooseAreaActivity.this, false);
                provinceAdapter.updateSelected(position);
                HotelApplication.serviceApi.findAllDistrictInProvince(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<District>>() {
                    @Override
                    public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                        DialogUtils.hideLoadingProgress();
                        if (response.isSuccessful()) {
                            hotelAreaAdapter = new ChooseHotelAreaAdapter(ChooseAreaActivity.this, response.body(), ChooseAreaActivity.this);
                            lvHotelArea.setAdapter(hotelAreaAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<District>> call, Throwable t) {
                        DialogUtils.hideLoadingProgress();
                        Toast.makeText(ChooseAreaActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        lvHotelArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                District district = (District) hotelAreaAdapter.getItem(position);
                intent.putExtra("districtSn", district.getSn());
                intent.putExtra("districtName", district.getName());
                intent.putExtra("provinceCode", findProviceCode(district.getProvinceSn()));
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobileUserId", HotelApplication.DEVICE_ID);
        HotelApplication.serviceApi.findAllProvinceCity(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    provinces = response.body();
                    provinceAdapter = new ProvinceAdapter(ChooseAreaActivity.this, provinces);
                    lvProvinces.setAdapter(provinceAdapter);

                    setupDefaultDistrict();
                }
            }


            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(ChooseAreaActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });


    }

    private void setupDefaultDistrict() {
        Address address = PreferenceUtils.getLastAddress(this);
        if (address != null) {
            String province = address.getAdminArea();
            int provinceSn = findProvinceSn(province);

            Map<String, Object> params = new HashMap<>();
            if (provinceSn != 0) {
                params.put("provinceSn", provinceSn);
            } else {
                if (provinces != null && provinces.size() > 0) {
                    provinceSn = provinces.get(0).getSn();
                }
                params.put("provinceSn", provinceSn);
            }
            DialogUtils.showLoadingProgress(ChooseAreaActivity.this, false);
            HotelApplication.serviceApi.findAllDistrictInProvince(params,HotelApplication.DEVICE_ID).enqueue(new Callback<List<District>>() {
                @Override
                public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        hotelAreaAdapter = new ChooseHotelAreaAdapter(ChooseAreaActivity.this, response.body(), ChooseAreaActivity.this);
                        lvHotelArea.setAdapter(hotelAreaAdapter);

                    }
                }

                @Override
                public void onFailure(Call<List<District>> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    Toast.makeText(ChooseAreaActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private int findProvinceSn(String province) {
        province = Utils.removeAccent(province).toLowerCase();
        if (provinces != null) {
            for (int i = 0; i < provinces.size(); i++) {
                String temp = Utils.removeAccent(provinces.get(i).getName()).toLowerCase();
                if (temp.equals(province)) {
                    return provinces.get(i).getSn();
                }
            }
        }
        return 0;
    }

    private String findProviceCode(int proviceSn) {
        for (int i = 0; i < provinces.size(); i++) {
            if (provinces.get(i).getSn() == proviceSn) {
                return Utils.removeAccent(provinces.get(i).getNameCode());
            }
        }
        return "Ho Chi Minh";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void onFinished() {
        try {
            DialogUtils.showLoadingProgress(this, false);
            HotelApplication.serviceApi.findAllFavoriteArea(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserAreaFavoriteForm>>() {
                @Override
                public void onResponse(Call<List<UserAreaFavoriteForm>> call, Response<List<UserAreaFavoriteForm>> response) {
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        HotelApplication.userAreaFavoriteForms = response.body();
                        hotelAreaAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<UserAreaFavoriteForm>> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SMapArea";
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
