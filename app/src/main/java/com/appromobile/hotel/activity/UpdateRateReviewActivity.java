package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.RoomNoSpinAdapter;
import com.appromobile.hotel.adapter.RoomTypeSpinAdapter;
import com.appromobile.hotel.model.request.UpdateUserReviewDto;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RoomForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
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
public class UpdateRateReviewActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_LOGIN_UPDATE_REVIEW = 1000;
    private EditTextSFRegular txtComment;
    private TextViewSFRegular tvHotelName;
    private TextViewSFBold btnDone;
    private Spinner spinRoomType, spinRoomNo;
    private ImageView btnBack;
    private ImageView[] btnStars = new ImageView[5];
    private RoomTypeSpinAdapter roomTypeSpinAdapter;
    private RoomNoSpinAdapter roomNoSpinAdapter;
    UserReviewForm userReviewForm;
    HotelDetailForm hotelDetailForm;
    int mark = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        setContentView(R.layout.update_rate_review_activity);
        tvHotelName = findViewById(R.id.tvHotelName);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            hotelDetailForm = bundle.getParcelable("HotelDetailForm");
            userReviewForm = bundle.getParcelable("UserReviewForm");
        }

        txtComment = findViewById(R.id.txtComment);
        btnDone = findViewById(R.id.btnDone);
        spinRoomType = findViewById(R.id.spinRoomType);
        spinRoomNo = findViewById(R.id.spinRoomNo);
        spinRoomNo.setEnabled(false);
        btnBack = findViewById(R.id.btnBack);
        btnDone.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);

        btnStars[0] = findViewById(R.id.btnStar1);
        btnStars[1] = findViewById(R.id.btnStar2);
        btnStars[2] = findViewById(R.id.btnStar3);
        btnStars[3] = findViewById(R.id.btnStar4);
        btnStars[4] = findViewById(R.id.btnStar5);

        btnStars[0].setOnClickListener(this);
        btnStars[1].setOnClickListener(this);
        btnStars[2].setOnClickListener(this);
        btnStars[3].setOnClickListener(this);
        btnStars[4].setOnClickListener(this);

        initData();
    }

    private void initData() {
        roomTypeSpinAdapter = new RoomTypeSpinAdapter(this, hotelDetailForm.getRoomTypeList());
        spinRoomType.setAdapter(roomTypeSpinAdapter);
        spinRoomType.setEnabled(false);
        tvHotelName.setText(hotelDetailForm.getName());
        txtComment.setText(userReviewForm.getComment());
        if (roomTypeSpinAdapter.getCount() > 0) {
            spinRoomType.setSelection(findRoomTypeIndex(userReviewForm.getRoomTypeSn()));
        }

        for (int i = 1; i <= userReviewForm.getMark(); i++) {
            btnStars[i - 1].setImageResource(R.drawable.review_star_fill);
        }
        mark = userReviewForm.getMark();
        spinRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initRoomNo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initRoomNo();
    }

    private void initRoomNo() {
        int roomTypeSn = (int) roomTypeSpinAdapter.getItem(spinRoomType.getSelectedItemPosition());
        Map<String, Object> params = new HashMap<>();
        params.put("roomTypeSn", roomTypeSn);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.findAllRoomNoList(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<RoomForm>>() {
            @Override
            public void onResponse(Call<List<RoomForm>> call, Response<List<RoomForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    roomNoSpinAdapter = new RoomNoSpinAdapter(UpdateRateReviewActivity.this, response.body());
                    spinRoomNo.setAdapter(roomNoSpinAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<RoomForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private int findRoomTypeIndex(int roomTypeSn) {
        for (int i = 0; i < hotelDetailForm.getRoomTypeList().size(); i++) {
            if (hotelDetailForm.getRoomTypeList().get(i).getSn() == roomTypeSn) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnStar1:
                showStar(0);
                mark = 1;
                break;
            case R.id.btnStar2:
                showStar(1);
                mark = 2;
                break;
            case R.id.btnStar3:
                showStar(2);
                mark = 3;
                break;
            case R.id.btnStar4:
                showStar(3);
                mark = 4;
                break;
            case R.id.btnStar5:
                showStar(4);
                mark = 5;
                break;
            case R.id.btnDone:
                postRateReview();
                break;
            case R.id.btnDelete:
                showPoupDelete();
                break;

        }
    }

    private void showPoupDelete() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirm_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextViewSFBold btnYes = dialog.findViewById(R.id.btnYes);
        TextViewSFBold btnNo = dialog.findViewById(R.id.btnNo);
        TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(getString(R.string.sure_delete));
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRateReview();
                dialog.dismiss();
            }
        });
    }

    private void deleteRateReview() {
        Map<String, Object> params = new HashMap<>();
        params.put("userReviewSn", userReviewForm.getSn());
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.deleteUserReview(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(UpdateRateReviewActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(UpdateRateReviewActivity.this, LoginActivity.class);
                            startActivityForResult(intent, REQUEST_LOGIN_UPDATE_REVIEW);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void postRateReview() {
        if (mark == -1) {
            return;
        }

        if (txtComment.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.enter_your_comment), Toast.LENGTH_LONG).show();
            return;
        }

        UpdateUserReviewDto userCommentDto = new UpdateUserReviewDto();
        userCommentDto.setSn(userReviewForm.getSn());
        userCommentDto.setMark(mark);
        userCommentDto.setComment(txtComment.getText().toString());
//        userCommentDto.setHotelSn(hotelDetailForm.getSn());
//        try {
//            if (roomNoSpinAdapter.getCount() > 0) {
//                int roomSn= (int) roomNoSpinAdapter.getItem(spinRoomNo.getSelectedItemPosition());
//                userCommentDto.setRoomSn((long) roomSn);
//            }
//        }catch (Exception e){}
//
//        try {
//            if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
//                int roomTypeSn= (int) roomTypeSpinAdapter.getItem(spinRoomType.getSelectedItemPosition());
//                userCommentDto.setRoomTypeSn((long) roomTypeSn);
//            }
//        }catch (Exception e){}

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.updateUserReview(userCommentDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(UpdateRateReviewActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showStar(int starIndex) {
        for (ImageView btnStar : btnStars) {
            btnStar.setImageResource(R.drawable.review_star);
        }
        for (int i = 0; i <= starIndex; i++) {
            btnStars[i].setImageResource(R.drawable.review_star_fill);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SUpdateRateReview";
    }
}
