package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.ChooseRoomTypeAdapter;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 9/22/2016.
 */

public class ChooseRoomTypeActivity extends BaseActivity {

    private View emptyView;
    private ListView lvRoom;
    private HotelDetailForm hotelDetailForm;
    private int selectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.choose_roomtype_activity);
        emptyView = findViewById(R.id.emptyView);
        lvRoom = findViewById(R.id.lvRoom);

        hotelDetailForm = getIntent().getParcelableExtra("HotelDetailForm");
        selectedIndex = getIntent().getIntExtra("selectedIndex", 0);

        /*
        * Check HotelStatus contact or trial
        */
        if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() ||
                hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
            /*
            * Set Room type
            */
            ChooseRoomTypeAdapter roomTypeListAdapter = new ChooseRoomTypeAdapter(this, hotelDetailForm, lvRoom, selectedIndex);
            lvRoom.setAdapter(roomTypeListAdapter);
            /*
            * onClick Room type
            */
            lvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(position);
                    if (roomTypeForm.isFlashSaleRoomAvailable() && roomTypeForm.getAvailableRoom() <= 0) {
                        Toast.makeText(ChooseRoomTypeActivity.this, getString(R.string.msg_3_9_flashsale_soldout), Toast.LENGTH_LONG).show();
                        return;
                    } else if (roomTypeForm.isLocked()) {
                        Toast.makeText(ChooseRoomTypeActivity.this, getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent();
                    intent.putExtra("RoomTypeIndex", position);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);

                }
            });
        }

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                emptyView.setVisibility(View.GONE);
                overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        emptyView.setVisibility(View.GONE);
        overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                emptyView.setBackgroundColor(getResources().getColor(R.color.bk_50p));
            }
        },700);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SHotelRoomtype";
    }
}
