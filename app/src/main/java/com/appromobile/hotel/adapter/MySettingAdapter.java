package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.UserSettingForm;

/**
 * Created by appro on 16/04/2018.
 */

public class MySettingAdapter extends BaseAdapter {
    private String[] list;
    private UserSettingForm userSettingForm;

    public MySettingAdapter(String[] list, UserSettingForm userSettingForm) {
        this.list = list;
        this.userSettingForm = userSettingForm;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();


            convertView = inflater.inflate(R.layout.item_my_setting_activity, parent, false);


            viewHolder = new ViewHolder();

            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.imgStatus = convertView.findViewById(R.id.imgStatus);
            viewHolder.textStatus = convertView.findViewById(R.id.textStatus);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Update View
        String title = list[position];
        viewHolder.title.setText(title);

        if (title.equals(parent.getContext().getString(R.string.account_setting))) {
            viewHolder.imgStatus.setVisibility(View.VISIBLE);
            viewHolder.imgStatus.setImageResource(R.drawable.next_org);
            viewHolder.textStatus.setVisibility(View.GONE);
        } else if (title.equals(parent.getContext().getString(R.string.txt_6_14_notification_setting))) {
            viewHolder.imgStatus.setVisibility(View.GONE);
            viewHolder.textStatus.setVisibility(View.VISIBLE);
            if (userSettingForm != null){
                //viewHolder.textStatus.setText(userSettingForm.isNotiAll()? "":"ON");
                if (userSettingForm.isNotiAll())
                    viewHolder.textStatus.setText(parent.getContext().getString(R.string.txt_6_14_setting_notification_on));
                else if (userSettingForm.isNotiBefore1())
                    viewHolder.textStatus.setText(parent.getContext().getString(R.string.txt_6_14_setting_notification_limited));
                else if (userSettingForm.isNotiBefore2())
                    viewHolder.textStatus.setText(parent.getContext().getString(R.string.txt_6_14_setting_notification_limited));
                else if (userSettingForm.isNotiOff())
                    viewHolder.textStatus.setText(parent.getContext().getString(R.string.txt_6_14_setting_notification_off));
            }
        } else if (title.equals(parent.getContext().getString(R.string.txt_6_14_language_setting))) {
            if (userSettingForm != null) {
                int lang = userSettingForm.getLanguage();
                if (lang == 1) {

                } else if (lang == 2) {
                    viewHolder.textStatus.setText("ENG");
                } else if (lang == 3) {
                    viewHolder.textStatus.setText("VIE");
                }
                viewHolder.textStatus.setVisibility(View.VISIBLE);
                viewHolder.imgStatus.setVisibility(View.GONE);

            }
        } else if (title.equals(parent.getContext().getString(R.string.txt_6_1_logout))) {
            viewHolder.imgStatus.setVisibility(View.VISIBLE);
            viewHolder.imgStatus.setImageResource(R.drawable.logout_org);
            viewHolder.textStatus.setVisibility(View.GONE);
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView title;
        private ImageView imgStatus;
        private TextView textStatus;
    }
}

