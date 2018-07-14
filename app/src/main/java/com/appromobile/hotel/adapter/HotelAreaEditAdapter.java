package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.model.request.UserAreaFavoriteDto;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/12/2016.
 */
public class HotelAreaEditAdapter extends BaseAdapter {
    private Context context;
    private List<UserAreaFavoriteForm> data;
    private boolean isEdit = false;
    private OnDeleteOnListener onDeleteOnListener;
    public HotelAreaEditAdapter(Context context, List<UserAreaFavoriteForm> data){
        this.data = data;
        this.context = context;
    }
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.hotel_area_edit_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvDistrictName =  convertView.findViewById(R.id.tvDistrictName);
            viewHolder.btnArrow =convertView.findViewById(R.id.btnArrow);
            viewHolder.btnRemove =  convertView.findViewById(R.id.btnRemove);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UserAreaFavoriteForm userAreaFavoriteForm = data.get(position);
        if(userAreaFavoriteForm.getSn()!=0){
            viewHolder.tvDistrictName.setText(userAreaFavoriteForm.getDistrictName()+", "+userAreaFavoriteForm.getProvinceName());
            viewHolder.btnArrow.setVisibility(View.VISIBLE);
        }else{
           // viewHolder.tvDistrictName.setText(context.getString(R.string.set_my_favorite_area));
          //  viewHolder.btnArrow.setVisibility(View.INVISIBLE);
        }
        if(isEdit){
            viewHolder.btnRemove.setVisibility(View.VISIBLE);
            viewHolder.btnRemove.setEnabled(true);
        }else{
            viewHolder.btnRemove.setVisibility(View.INVISIBLE);
            viewHolder.btnRemove.setEnabled(false);
        }

        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userAreaFavoriteForm.getSn()!=0){
                    HotelApplication.userAreaFavoriteForms.remove(position);
                    UserAreaFavoriteDto userAreaFavoriteDto = new UserAreaFavoriteDto();
                    userAreaFavoriteDto.setFavorite(false);
                    userAreaFavoriteDto.setDistrictSn(data.get(position).getDistrictSn());
                    DialogUtils.showLoadingProgress(context, false);
                    HotelApplication.serviceApi.updateFavoriteArea(userAreaFavoriteDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                            if (response.isSuccessful()) {
                                getFavorite();
                                setPosition(position);
                                isEdit = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable t) {
                            DialogUtils.hideLoadingProgress();
                            Toast.makeText(context, context.getString(R.string.update_failure), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        return convertView;
    }

    private void getFavorite() {
        HotelApplication.serviceApi.findAllFavoriteArea(PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserAreaFavoriteForm>>() {
            @Override
            public void onResponse(Call<List<UserAreaFavoriteForm >> call, Response<List<UserAreaFavoriteForm>> response) {
                DialogUtils.hideLoadingProgress();
                if(response.isSuccessful()){
                    HotelApplication.userAreaFavoriteForms = response.body();
                    if(onDeleteOnListener!=null){
                        onDeleteOnListener.onFinished();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserAreaFavoriteForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                t.printStackTrace();
            }
        });
    }

    public void setEdit() {
        isEdit = !isEdit;
    }
    public boolean isEdit(){
        return isEdit;
    }

    public void setOnDeleteOnListener(OnDeleteOnListener onDeleteOnListener) {
        this.onDeleteOnListener = onDeleteOnListener;
    }

    public void updateData(List<UserAreaFavoriteForm> userAreaFavoriteForms) {
        this.data = userAreaFavoriteForms;
    }

    private class ViewHolder{
        TextViewSFRegular tvDistrictName;
        ImageView btnArrow;
        ImageView btnRemove;
    }

    public interface OnDeleteOnListener{
        void onFinished();
    }
}
