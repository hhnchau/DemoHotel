package com.appromobile.hotel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.enums.FragmentType;
import com.appromobile.hotel.enums.MapFilterType;
import com.appromobile.hotel.utils.MyLog;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.igaworks.adbrix.IgawAdbrix;

/**
 * Created by xuan on 6/24/2016.
 */
public class BaseFragment extends Fragment {

    private String screenName = "";

    @Override
    public void onStart() {
        super.onStart();
        MyLog.writeLog("BaseFragment--->onStart");

        if (HotelApplication.isRelease) {

            //setEventAnalytic(this.getScreenName());


            setEventIgawAdbrix(getScreenName());

            /*
            / Facebook
            */
            AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());
            logger.logEvent(getScreenName());

            /*
            * AppFlyer
            */
            //AppsFlyerLib.getInstance().trackEvent(getActivity(), getScreenName(), null);
        }
    }


    /*
* IgawAdbrix
*/
    private void setEventIgawAdbrix(String event) {
        if (event != null) {
            IgawAdbrix.retention(event);
            IgawAdbrix.firstTimeExperience(event);
            MyLog.writeLog("BaseActivity IgawAdbrix---->onStart");
        }
    }


    private void setEventAnalytic(String event) {
        try {
            if (getContext() != null) {
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
                if (mFirebaseAnalytics != null && event != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
                    mFirebaseAnalytics.logEvent(event, bundle);
                    MyLog.writeLog("BaseFragment Event: " + event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("BaseFragment Event: " + e);
        }
    }

    public void onUpdateLocation() {

    }

    public void onRefreshFavorite(int position) {
    }

    public void onRefreshNearby() {

    }

    public void onRefreshData() {
    }

    public void setDistrictName(String districtName) {

    }

    public void chooseArea(int districtSn, String districtName) {

    }

    public void closeClickEvent() {
    }

    public void onFilter(MapFilterType mapFilterType, boolean isChecked) {

    }

    public void requestAreaSetting(boolean isShowDropdown) {
    }

    public void initAdvertising() {
    }

    public void initDataMyPageFragment() {
    }

    public static BaseFragment newInstance(FragmentType fragmentType) {
        BaseFragment baseFragment = null;
        switch (fragmentType) {
            case HOME:
                baseFragment = new HomeFragment();
                break;
            case MAP:
                baseFragment = new MapTabFragment();
                break;
            case EVENT:
                baseFragment = new EventFragment();
                break;
            case MY_PAGE:
                baseFragment = new MyPageFragment();
                break;
        }
        return baseFragment;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setButtonName(String buttonName) {
        if (HotelApplication.isRelease && buttonName != null) {
            setEventAnalytic(buttonName);
            setEventIgawAdbrix(buttonName);
        }
    }

  
}
