package com.appromobile.hotel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appromobile.hotel.fragment.FlashSaleFragment;
import com.appromobile.hotel.model.view.HotelForm;

import java.util.List;

/**
 * Created by appro on 22/08/2017.
 */
public class AdapterFlashSale extends FragmentStatePagerAdapter {
    private List<HotelForm> listFlashSale;

    public AdapterFlashSale(List<HotelForm> listFlashSale, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.listFlashSale = listFlashSale;
    }

    @Override
    public int getCount() {
        if (listFlashSale != null) {
            return listFlashSale.size();
        } else {
            return 0;
        }
    }

    @Override
    public Fragment getItem(int position) {

        HotelForm hotelForm = listFlashSale.get(position);
        return FlashSaleFragment.newInstance(hotelForm);

    }

}
