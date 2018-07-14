package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.LanguageSettingActivity;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.request.UserSettingDto;
import com.appromobile.hotel.model.view.Checkbox;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;

import java.util.List;

/**
 * Created by appro on 16/04/2018.
 */

public class CheckboxAdapter extends BaseAdapter {
    private Context context;
    private List<Checkbox> checkboxList;
    private int selectPosition = -1;

    public CheckboxAdapter(Context context, List<Checkbox> checkboxList) {
        this.context = context;
        this.checkboxList = checkboxList;
    }

    @Override
    public int getCount() {
        return checkboxList.size();
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


            convertView = inflater.inflate(R.layout.item_list_check_box, parent, false);


            viewHolder = new ViewHolder();

            viewHolder.title = convertView.findViewById(R.id.checkboxTitle);
            viewHolder.img = convertView.findViewById(R.id.checkboxImg);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Update View
        Checkbox checkbox = checkboxList.get(position);
        if (checkbox != null) {
            viewHolder.title.setText(checkbox.getTitle());

            if (checkbox.isSelect()) {
                viewHolder.img.setImageResource(R.drawable.check_org);
                selectPosition = position;
            } else
                viewHolder.img.setImageResource(0);
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView title;
        private ImageView img;
    }

    public void changeSelect(final int p, UserSettingDto userSettingDto, final int lang) {
        if (selectPosition != p) {
            //Update to Server

            ControllerApi.getmInstance().updateUserSetting(context, userSettingDto, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    //UnCheck
                    checkboxList.set(selectPosition, checkboxList.get(selectPosition).setSelect(false));

                    //Select
                    checkboxList.set(p, checkboxList.get(p).setSelect(true));

                    notifyDataSetChanged();

                    if (lang > -1) {
                        if (lang == 2)
                            PreferenceUtils.setLanguage(context, "en");
                        else if (lang == 3) {
                            PreferenceUtils.setLanguage(context, "vi");
                        }

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.stable, R.anim.left_to_right);

                    }
                }
            });

        }
    }
}
