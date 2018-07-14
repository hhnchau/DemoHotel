package com.appromobile.hotel.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.FacilityForm;

/**
 * Created by xuan on 9/27/2016.
 */

public class FacilityView extends LinearLayout {
    public FacilityView(Context context, FacilityForm facilityForm) {
        super(context);
//        View root = inflate(context, R.layout.facility_item, this);
        View root = LayoutInflater.from(context).inflate(R.layout.facility_item, this);
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View root = inflater.inflate(R.layout.facility_item, null);
        TextViewSFRegular tvName = (TextViewSFRegular) root.findViewById(R.id.tvName);
        tvName.setText(facilityForm.getName());
    }
}
