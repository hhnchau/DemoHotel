package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.model.request.UserAreaFavoriteDto;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/12/2016.
 */
public class HotelAreaAdapter extends BaseAdapter {
    private static final int LOGIN_DETAIL_REQUEST_LIKE = 1001;
    private Context context;
    private List<District> data;
    private OnUpdateFavoriteListener onUpdateFavoriteListener;

    public HotelAreaAdapter(Context context, List<District> data, OnUpdateFavoriteListener onUpdateFavoriteListener) {
        this.data = data;
        this.context = context;
        this.onUpdateFavoriteListener = onUpdateFavoriteListener;
    }

    @Override
    public int getCount() {
        if (data != null) {
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.hotel_area_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvDistrictName =  convertView.findViewById(R.id.tvDistrictName);
            viewHolder.tvTotalHotel = convertView.findViewById(R.id.tvTotalHotel);
            viewHolder.btnLike =  convertView.findViewById(R.id.btnLike);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
            if (data.get(position).getTotalContracted() == 0) {
                viewHolder.tvDistrictName.setTextColor(context.getResources().getColor(R.color.bk));
                viewHolder.tvDistrictName.setTypeface(viewHolder.tvDistrictName.getTypeface(), Typeface.NORMAL);

            }
        }
        viewHolder.tvTotalHotel.setText("( " + String.valueOf(data.get(position).getTotalHotel()) + " )");
        viewHolder.tvDistrictName.setText(data.get(position).getName().trim());
        if (data.get(position).getTotalContracted() != 0) {
            viewHolder.tvDistrictName.setTextColor(context.getResources().getColor(R.color.org));
            viewHolder.tvDistrictName.setTypeface(viewHolder.tvDistrictName.getTypeface(), Typeface.BOLD);

        }
        final boolean isFav = isCheckLike(data.get(position).getSn(), data.get(position).getProvinceSn());
        if (isFav) {
            viewHolder.btnLike.setImageResource(R.drawable.hearth_fill);
        } else {
            viewHolder.btnLike.setImageResource(R.drawable.hearth);
        }
        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAreaFavoriteDto userAreaFavoriteDto = new UserAreaFavoriteDto();
                userAreaFavoriteDto.setFavorite(!isFav);
//                userAreaFavoriteDto.setProvinceSn(data.get(position).getProvinceSn());
                userAreaFavoriteDto.setDistrictSn(data.get(position).getSn());
                if (isFav) {
                    HotelApplication.serviceApi.updateFavoriteArea(userAreaFavoriteDto, PreferenceUtils.getToken(context), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                            RestResult restResult = response.body();
                            if (response.isSuccessful() && restResult != null) {
                                if (restResult.getResult() == 1) {
                                    onUpdateFavoriteListener.onFinished();
                                } else if (restResult.getResult() == 5) {
                                    DialogUtils.showExpiredDialog(context, new DialogCallback() {
                                        @Override
                                        public void finished() {
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            ((Activity) context).startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                        }
                                    });
                                }
                            } else if (response.code() == 401) {
                                DialogUtils.showExpiredDialog(context, new DialogCallback() {
                                    @Override
                                    public void finished() {
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        ((Activity) context).startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable t) {
                            Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }

                if (countFavorite() < HotelApplication.TOTAL_USER_FAV) {
                    HotelApplication.serviceApi.updateFavoriteArea(userAreaFavoriteDto, PreferenceUtils.getToken(context),HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                            RestResult restResult = response.body();
                            if (response.isSuccessful() && restResult != null) {
                                if (restResult.getResult() == 1) {
                                    Toast.makeText(context, context.getString(R.string.favorited) + ": " + String.valueOf(countFavorite() + 1), Toast.LENGTH_LONG).show();
                                    onUpdateFavoriteListener.onFinished();
                                } else if (restResult.getResult() == 5) {
                                    DialogUtils.showExpiredDialog(context, new DialogCallback() {
                                        @Override
                                        public void finished() {
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            ((Activity) context).startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                        }
                                    });
                                }
                            } else if (response.code() == 401) {
                                DialogUtils.showExpiredDialog(context, new DialogCallback() {
                                    @Override
                                    public void finished() {
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        ((Activity) context).startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable t) {
                            Toast.makeText(context, context.getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(context, context.getString(R.string.limit_area_user_favorite_message), Toast.LENGTH_LONG).show();
                }
            }
        });
        return convertView;
    }

    private boolean isCheckLike(int districtSn, int provinceSn) {

        if (HotelApplication.userAreaFavoriteForms != null) {
            for (int i = 0; i < HotelApplication.userAreaFavoriteForms.size(); i++) {
                if (HotelApplication.userAreaFavoriteForms.get(i).getDistrictSn() == districtSn && HotelApplication.userAreaFavoriteForms.get(i).getProvinceSn() == provinceSn) {
                    return HotelApplication.userAreaFavoriteForms.get(i).isFavorite();
                }
            }
        }
        return false;
    }

    private int countFavorite() {
        int count = 0;
        if (HotelApplication.userAreaFavoriteForms != null) {
            for (int i = 0; i < HotelApplication.userAreaFavoriteForms.size(); i++) {
                if (HotelApplication.userAreaFavoriteForms.get(i).isFavorite()) {
                    count++;
                }
            }
        }
        return count;
    }

    private class ViewHolder {
        TextView tvDistrictName;
        TextView tvTotalHotel;
        ImageView btnLike;
    }

    public interface OnUpdateFavoriteListener {
        void onFinished();
    }
}
