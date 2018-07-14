package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.HotelAreaEditAdapter;
import com.appromobile.hotel.adapter.HotelFavoriteListAdapter;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/1/2016.
 */
public class MyFavoriteActivity extends BaseActivity implements View.OnClickListener, HotelAreaEditAdapter.OnDeleteOnListener, AdapterView.OnItemClickListener {
    private static final int LOGIN_FAV_REQUEST_LIKE = 1000;
    ListView lvFavoriteArea, lvFavoriteHotel;
    HotelAreaEditAdapter hotelAreaEditAdapter;
    TextView tvMessage;
    List<HotelForm> hotelForms;
    TextViewSFBold btnEdit;
    TextViewSFRegular txtSetFavArea;
    private int position;
    private List<UserAreaFavoriteForm> userAreaFavoriteForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.my_favorite_activity);
        txtSetFavArea = findViewById(R.id.txtSetFavArea);
        tvMessage =  findViewById(R.id.tvMessage);
        findViewById(R.id.btnClose).setOnClickListener(this);
        btnEdit =  findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        lvFavoriteArea =  findViewById(R.id.lvFavoriteArea);
        lvFavoriteHotel =  findViewById(R.id.lvFavoriteHotel);
        txtSetFavArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyFavoriteActivity.this, AreaSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });
        lvFavoriteHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(MyFavoriteActivity.this, HotelDetailActivity.class);
                    intent.putExtra("sn", hotelForms.get(position).getSn());
                    intent.putExtra("RoomAvailable", hotelForms.get(position).getRoomAvailable());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userAreaFavoriteForms = new ArrayList<>();
        initData();
        initFavoriteData();
    }

    private void initFavoriteData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("limit", 100);
        params.put("version", "4.0");
        MyLog.writeLog("MyFavoriteActivity---->findLimitFavoriteHotelList");
        HotelApplication.serviceApi.findLimitFavoriteHotelList(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    hotelForms = response.body();
                    HotelFavoriteListAdapter adapter = new HotelFavoriteListAdapter(MyFavoriteActivity.this, hotelForms);
                    lvFavoriteHotel.setAdapter(adapter);
                    if (hotelForms == null || hotelForms.size() == 0) {
                        lvFavoriteHotel.setEmptyView(tvMessage);
                    }
                } else {
                    lvFavoriteHotel.setEmptyView(tvMessage);
                    DialogUtils.showExpiredDialog(MyFavoriteActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(MyFavoriteActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_FAV_REQUEST_LIKE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
//                lvFavoriteHotel.setEmptyView(tvMessage);
                DialogUtils.hideLoadingProgress();
                Toast.makeText(MyFavoriteActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateFavoriteAreaAfterDelete() {
        if (hotelAreaEditAdapter != null && hotelAreaEditAdapter.isEdit() && userAreaFavoriteForms.size() != 0) {
            userAreaFavoriteForms.remove(position);
            hotelAreaEditAdapter.updateData(userAreaFavoriteForms);
            hotelAreaEditAdapter.notifyDataSetChanged();
        }
        if(userAreaFavoriteForms.size()==0){
            displaySetFavArea(true);
        }
    }

    private void displaySetFavArea(boolean b) {
        if (b)
            txtSetFavArea.setVisibility(View.VISIBLE);
        else
            txtSetFavArea.setVisibility(View.GONE);
    }

    private void initData() {
        if (HotelApplication.userAreaFavoriteForms == null || (HotelApplication.userAreaFavoriteForms != null && HotelApplication.userAreaFavoriteForms.size() == 0)) {
            lvFavoriteArea.setVisibility(View.GONE);
            displaySetFavArea(true);
        } else {
            for (int i = 0; i < HotelApplication.userAreaFavoriteForms.size(); i++) {
                lvFavoriteArea.setVisibility(View.VISIBLE);
                userAreaFavoriteForms.add(HotelApplication.userAreaFavoriteForms.get(i).clone());
            }
        }
        if (hotelAreaEditAdapter == null) {
            hotelAreaEditAdapter = new HotelAreaEditAdapter(this, userAreaFavoriteForms);
            hotelAreaEditAdapter.setOnDeleteOnListener(this);
            lvFavoriteArea.setAdapter(hotelAreaEditAdapter);
            lvFavoriteArea.setOnItemClickListener(this);
        } else {
            hotelAreaEditAdapter.updateData(userAreaFavoriteForms);
            hotelAreaEditAdapter.notifyDataSetChanged();
        }
        if(userAreaFavoriteForms.size()!=0){
            displaySetFavArea(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnEdit:
                editArea();
                break;
        }
    }

    private void editArea() {
        if (!hotelAreaEditAdapter.isEdit()) {
            btnEdit.setText(getString(R.string.done));
        } else {
            btnEdit.setText(getString(R.string.txt_3_5_edit));
        }
        hotelAreaEditAdapter.setEdit();
        hotelAreaEditAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinished() {

        position = hotelAreaEditAdapter.getPosition();
        updateFavoriteAreaAfterDelete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (hotelAreaEditAdapter.isEdit()) {
            Intent intent = new Intent(this, AreaSettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        } else {
            UserAreaFavoriteForm userAreaFavoriteForm = (UserAreaFavoriteForm) hotelAreaEditAdapter.getItem(position);
            if (userAreaFavoriteForm.getSn() != 0) {
                Intent intent = new Intent();
                intent.putExtra("districtSn", userAreaFavoriteForm.getDistrictSn());
                intent.putExtra("districtName", userAreaFavoriteForm.getDistrictName());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            } else {
                Intent intent = new Intent(this, AreaSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_FAV_REQUEST_LIKE) {
            if (resultCode == RESULT_OK) {
                initFavoriteData();
            } else {
                finish();
            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetFavorite";
    }
}
