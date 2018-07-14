package com.appromobile.hotel.gps;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.appromobile.hotel.R;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;

/**
 * Created by appro on 25/07/2017.
 */
public class CheckLocation {
    private LocationManager locationManager;
    private Context context;

    public CheckLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
    }

    public boolean getStatusLocation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            //Location Disable
            showDialogConfirm();
            return false;
        }
    }

    private void showDialogConfirm() {
        Dialag.getInstance().show(context, false, false, false,null, context.getString(R.string.app_need_to_query_location_please_turn_on_the_gps), context.getString(R.string.ok), context.getString(R.string.cancel), null,Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);

            }

            @Override
            public void button2() {

            }

            @Override
            public void button3(Dialog dialog) {

            }

        });
    }
}
