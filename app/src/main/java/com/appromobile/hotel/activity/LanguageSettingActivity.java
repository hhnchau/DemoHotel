package com.appromobile.hotel.activity;

import android.content.Intent;
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
import com.appromobile.hotel.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 13/04/2018.
 */

public class LanguageSettingActivity extends BaseActivity {
    private CheckboxAdapter checkboxAdapter;
    private UserSettingForm userSettingForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_setting_activity);

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

        ListView listCheckBox = findViewById(R.id.listCheckbox);
        final List<Checkbox> checkBoxList = new ArrayList<>();
        String[] listString = getResources().getStringArray(R.array.list_my_language);
        if (userSettingForm != null) {

            String s = "";
            if (userSettingForm.getLanguage() == 2)
                s = getString(R.string.txt_6_14_2_language_english);
            else if (userSettingForm.getLanguage() == 3)
                s = getString(R.string.txt_6_14_2_language_vietnamese);

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
                int lang = 3;
                if (position == 0) {
                    lang = 2;
                    userSettingDto.setLanguage(lang);
                } else if (position == 1) {
                    lang = 3;
                    userSettingDto.setLanguage(lang);
                }

                userSettingDto.setNotiAll(userSettingForm.isNotiAll());
                userSettingDto.setNotiBefore1(userSettingForm.isNotiBefore1());
                userSettingDto.setNotiBefore2(userSettingForm.isNotiBefore2());
                userSettingDto.setNotiOff(userSettingForm.isNotiOff());


                checkboxAdapter.changeSelect(position, userSettingDto, lang);

            }
        });
    }

    @Override
    public void setScreenName() {

    }
}
