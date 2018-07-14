package com.appromobile.hotel.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.CheckboxAdapter;
import com.appromobile.hotel.model.request.UserSettingDto;
import com.appromobile.hotel.model.view.Checkbox;
import com.appromobile.hotel.model.view.UserSettingForm;
import com.appromobile.hotel.utils.ParamConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 13/04/2018.
 */

public class NotificationSettingActivity extends BaseActivity {
    private CheckboxAdapter checkboxAdapter;
    private UserSettingForm userSettingForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_setting_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userSettingForm = bundle.getParcelable("UserSettingForm");
            initView();
        }
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView imgBack = findViewById(R.id.btnClose);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        final ListView listCheckBox = findViewById(R.id.listCheckbox);
        final List<Checkbox> checkBoxList = new ArrayList<>();
        String[] listString = getResources().getStringArray(R.array.list_notification_setting);
        if (userSettingForm != null) {
            String s = "";

            if (userSettingForm.isNotiAll())
                s = getString(R.string.txt_6_14_1_notification_on);
            else if (userSettingForm.isNotiBefore1())
                s = getString(R.string.txt_6_14_1_notification_limited_1);
            else if (userSettingForm.isNotiBefore2())
                s = getString(R.string.txt_6_14_1_notification_limited_2);
            else if (userSettingForm.isNotiOff())
                s = getString(R.string.txt_6_14_1_notification_off);

            for (String aListString : listString) {

                if (aListString.equals(s)) {
                    checkBoxList.add(new Checkbox(aListString, true));
                } else {
                    checkBoxList.add(new Checkbox(aListString, false));
                }
            }
        }
        checkboxAdapter = new CheckboxAdapter(this, checkBoxList);
        listCheckBox.setAdapter(checkboxAdapter);


        listCheckBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserSettingDto userSettingDto = new UserSettingDto();
                userSettingDto.setLanguage(userSettingForm.getLanguage());

                userSettingDto.setNotiAll(false);
                userSettingDto.setNotiBefore1(false);
                userSettingDto.setNotiBefore2(false);
                userSettingDto.setNotiOff(false);

                if (position == 0) {
                    userSettingDto.setNotiAll(true);
                } else if (position == 1) {
                    userSettingDto.setNotiBefore1(true);
                } else if (position == 2) {
                    userSettingDto.setNotiBefore2(true);
                } else if (position == 3) {
                    userSettingDto.setNotiOff(true);
                }

                checkboxAdapter.changeSelect(position, userSettingDto, -1);
            }
        });
    }

    @Override
    public void setScreenName() {

    }
}
