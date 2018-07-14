package com.appromobile.hotel.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.AccountSettingActivity;
import com.appromobile.hotel.activity.EditProfileActivity;
import com.appromobile.hotel.activity.HistoryActivity;
import com.appromobile.hotel.activity.InviteFriendActivity;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.activity.MyCouponActivity;
import com.appromobile.hotel.activity.MyFavoriteActivity;
import com.appromobile.hotel.activity.MyStampActivity;
import com.appromobile.hotel.activity.NoticeQAActivity;
import com.appromobile.hotel.activity.TermPrivacyPolicyActivity;
import com.appromobile.hotel.activity.VersionActivity;
import com.appromobile.hotel.activity.WebviewDetail;
import com.appromobile.hotel.enums.InviteFriendType;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.model.request.PushNotificationDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 6/24/2016.
 */
public class MyPageFragment extends BaseFragment implements View.OnClickListener {
    private TextView tvNickname, tvUserId;
    private ScrollView mainScroll;
    private AppUserForm appUserForm;
    ToggleButton chkNotification;
    private boolean isTrustPassCode = false;
    private ImageButton btnMyFavoriteArrow, btnHistoryArrow, btnCouponArrow, btnNoticeQAArrow, btnTermPolicyArrow, btnInviteFriendArrow;
    private TextView txtBadgeNotice, txtBadgeBooking;

    public MyPageFragment() {
        setScreenName("SSetPage");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_page_fragment, container, false);
        tvNickname = rootView.findViewById(R.id.tvNickname);
        tvUserId = rootView.findViewById(R.id.tvUserId);
        mainScroll = rootView.findViewById(R.id.mainScroll);
        chkNotification = rootView.findViewById(R.id.chkNotification);
        rootView.findViewById(R.id.btnAccountSetting).setOnClickListener(this);
        rootView.findViewById(R.id.btnMyFavorite).setOnClickListener(this);
        rootView.findViewById(R.id.btnHistory).setOnClickListener(this);
        rootView.findViewById(R.id.btnCoupon).setOnClickListener(this);
        rootView.findViewById(R.id.btnNoticeQA).setOnClickListener(this);
        rootView.findViewById(R.id.btnTermPolicy).setOnClickListener(this);
        rootView.findViewById(R.id.btnVersion).setOnClickListener(this);
        rootView.findViewById(R.id.btnVersionArrow).setOnClickListener(this);
        rootView.findViewById(R.id.btnInviteFriend).setOnClickListener(this);
        rootView.findViewById(R.id.btnMyProfile).setOnClickListener(this);
        rootView.findViewById(R.id.btnMyProfileArrow).setOnClickListener(this);
        rootView.findViewById(R.id.btnAbout).setOnClickListener(this);
        rootView.findViewById(R.id.btnAboutArrow).setOnClickListener(this);
        rootView.findViewById(R.id.btnStamp).setOnClickListener(this);
        rootView.findViewById(R.id.btnStampArrow).setOnClickListener(this);
        btnInviteFriendArrow = rootView.findViewById(R.id.btnInviteFriendArrow);
        btnMyFavoriteArrow = rootView.findViewById(R.id.btnMyFavoriteArrow);
        btnHistoryArrow = rootView.findViewById(R.id.btnHistoryArrow);
        btnCouponArrow = rootView.findViewById(R.id.btnCouponArrow);
        btnNoticeQAArrow = rootView.findViewById(R.id.btnNoticeQAArrow);
        btnTermPolicyArrow = rootView.findViewById(R.id.btnTermPolicyArrow);
        btnMyFavoriteArrow.setOnClickListener(this);
        btnHistoryArrow.setOnClickListener(this);
        btnCouponArrow.setOnClickListener(this);
        btnNoticeQAArrow.setOnClickListener(this);
        btnTermPolicyArrow.setOnClickListener(this);
        btnInviteFriendArrow.setOnClickListener(this);

        /*
        / Set Badge
        */
        txtBadgeNotice = rootView.findViewById(R.id.badge_notice);
        //txtBadgeBooking = (TextView) rootView.findViewById(R.id.badge_booking);


        Map<String, Object> map = new HashMap<>();
        map.put("mobileUserId", HotelApplication.DEVICE_ID);
        //Check Notify
        HotelApplication.serviceApi.findPushNotificationStatus(map, PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    try {
                        if (Integer.parseInt(restResult.getOtherInfo()) == 1) {
                            chkNotification.setChecked(true);
                        } else {
                            chkNotification.setChecked(false);
                        }
                    } catch (Exception e) {
                        MyLog.writeLog("findPushNotificationStatus" + e);
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });


        chkNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PushNotificationDto pushNotificationDto = new PushNotificationDto();
                pushNotificationDto.setMobileUserId(HotelApplication.DEVICE_ID);
                pushNotificationDto.setPush(isChecked);
                HotelApplication.serviceApi.updatePushNotification(pushNotificationDto, PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                    @Override
                    public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                        MyLog.writeLog("UpdateNotificationResponse");
                    }

                    @Override
                    public void onFailure(Call<RestResult> call, Throwable t) {
                        MyLog.writeLog("UpdateNotificationResponse");
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (PreferenceUtils.getIsPasscode(getContext()) && !PreferenceUtils.getPasscode(getContext()).equals("")) {
            mainScroll.setVisibility(View.GONE);
            MyLog.writeLog("Mypage----> MainScroll Gone");
            showPasscodeVerifyPopup();
        } else {
            mainScroll.setVisibility(View.VISIBLE);
            MyLog.writeLog("Mypage----> MainScroll Visible");
            initDataMyPageFragment();
        }
        /*
        / Show Badge If exist
        */
        int countNotice = PreferenceUtils.getCounterNotifi(getActivity());
        if (countNotice > 0) {
            txtBadgeNotice.setText(" (" + countNotice + ") ");
        } else {
            txtBadgeNotice.setText(null);
        }

        //Show Badge If exist (Not Use)
        //int countBooking = PreferenceUtils.getCounterBooking(getActivity());
        //if (countBooking > 0) {
        //    txtBadgeBooking.setText(" (" + countBooking + ") ");
        //} else {
        //txtBadgeBooking.setText(null);
        //}
    }

    private void showPasscodeVerifyPopup() {
        if (getContext() != null) {
            MyLog.writeLog("Mypage----> Show PasscodeVerify");
            final Dialog dialog = new Dialog(getContext(), R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.passcode_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }

            TextView btnOK = dialog.findViewById(R.id.btnOK);
            final EditText txtPasscode = dialog.findViewById(R.id.txtPasscode);
            dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(getActivity(), txtPasscode);
                    dialog.dismiss();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                    return false;
                }
            });

            final ImageView[] imgCode = new ImageView[6];
            imgCode[0] = dialog.findViewById(R.id.code1);
            imgCode[1] = dialog.findViewById(R.id.code2);
            imgCode[2] = dialog.findViewById(R.id.code3);
            imgCode[3] = dialog.findViewById(R.id.code4);
            imgCode[4] = dialog.findViewById(R.id.code5);
            imgCode[5] = dialog.findViewById(R.id.code6);

            Utils.showKeyboard(getActivity());
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String passcode = txtPasscode.getText().toString();
                    if (!passcode.equals("") && passcode.length() == 6) {
                        if (PreferenceUtils.getPasscode(getContext()).equals(Utils.md5(passcode + passcode))) {
                            Utils.hideKeyboard(getActivity(), txtPasscode);
                            mainScroll.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                            isTrustPassCode = true;
                            initDataMyPageFragment();
                        }
                    }
                }
            });

            txtPasscode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int lengthCode = txtPasscode.getText().toString().length();
                    for (int i = 0; i < 6; i++) {
                        if (i < lengthCode) {
                            imgCode[i].setVisibility(View.VISIBLE);
                        } else {
                            imgCode[i].setVisibility(View.INVISIBLE);
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    @Override
    public void initDataMyPageFragment() {
        super.initDataMyPageFragment();

        MyLog.writeLog("Mypage----> Init Data");
        appUserForm = PreferenceUtils.getAppUser(getContext());
        if (appUserForm != null) {
            if (appUserForm.getViaApp() != SignupType.Manual.getType()) {
                if (appUserForm.getEmail() != null && !appUserForm.getEmail().equals("")) {
                    tvUserId.setText(appUserForm.getEmail());
                } else {
                    tvUserId.setText(appUserForm.getUserId());
                }
            } else {
                tvUserId.setText(appUserForm.getUserId());
            }
            tvNickname.setText(appUserForm.getNickName());
        } else {
            DialogUtils.showExpiredDialog(getActivity(), new DialogCallback() {
                @Override
                public void finished() {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivityForResult(intent, ParamConstants.LOGIN_USER_INFO_REQUEST);
                    }
                }
            });
        }

    }


    @Override
    public void onRefreshData() {
        super.onRefreshData();
        if (PreferenceUtils.getIsPasscode(getContext()) && !PreferenceUtils.getPasscode(getContext()).equals("") && !isTrustPassCode) {
            mainScroll.setVisibility(View.GONE);
            showPasscodeVerifyPopup();
            MyLog.writeLog("MyPage---->Refresh");
        }
    }

    //btnMyFavoriteArrow, btnHistoryArrow, btnCouponArrow, btnNoticeQAArrow, btnTermPolicyArrow
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccountSetting:
                gotoAccountSetting();
                break;
            case R.id.btnMyFavoriteArrow:
            case R.id.btnMyFavorite:
                gotoMyFavorite();
                break;
            case R.id.btnHistoryArrow:
            case R.id.btnHistory:
                //Clear Badge (Not Use)
                //PreferenceUtils.setCounterBooking(getActivity(), 0);
                //txtBadgeBooking.setText(null);
                gotoHistory();
                break;
            case R.id.btnCouponArrow:
            case R.id.btnCoupon:
                gotoMyCoupon();
                break;
            case R.id.btnNoticeQAArrow:
            case R.id.btnNoticeQA:
                //Clear Badge
                PreferenceUtils.setCounterNotifi(getActivity(), 0);
                txtBadgeNotice.setText(null);
                gotoNoticeQA();
                break;
            case R.id.btnTermPolicy:
            case R.id.btnTermPolicyArrow:
                gotoTermPolicy();
                break;
            case R.id.btnVersion:
            case R.id.btnVersionArrow:
                gotoVersion();
                break;
            case R.id.btnInviteFriend:
            case R.id.btnInviteFriendArrow:
                gotoInviteFriend();
                break;
            case R.id.btnMyProfile:
            case R.id.btnMyProfileArrow:
                openMyProfile();
                break;
            case R.id.btnAbout:
            case R.id.btnAboutArrow:
                gotoAboutUs();
                break;
            case R.id.btnStamp:
            case R.id.btnStampArrow:
                gotoMyStamp();
                break;
        }
    }

    private void gotoMyStamp(){
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), MyStampActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoAboutUs() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), WebviewDetail.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void openMyProfile() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHANGE_PROFILE);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoInviteFriend() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), InviteFriendActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            intent.putExtra("InviteFriendType", InviteFriendType.MY_PAGE.ordinal());
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoVersion() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), VersionActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoTermPolicy() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), TermPrivacyPolicyActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoNoticeQA() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), NoticeQAActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoMyCoupon() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), MyCouponActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoHistory() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), HistoryActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoMyFavorite() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), MyFavoriteActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivityForResult(intent, ParamConstants.REQUEST_MY_FAVORITE);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void gotoAccountSetting() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), AccountSettingActivity.class);
            intent.putExtra("AppUserForm", appUserForm);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
