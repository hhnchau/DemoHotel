package com.appromobile.hotel.gps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.appromobile.hotel.R;
import com.appromobile.hotel.utils.MyLog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCodeService extends IntentService {
    protected ResultReceiver mReceiver;

    public GeoCodeService() {
        super("GeoCodeService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras() != null) {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";
            double latitude = intent.getDoubleExtra(Constants.LATITUDE_DATA_EXTRA, 0);
            double longitude = intent.getDoubleExtra(Constants.LONGITUDE_DATA_EXTRA, 0);
            mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            List<Address> addresses = null;
            MyLog.writeLog("GeoCodeService --->start Intent Service");
            try {
                MyLog.writeLog("GeoCodeService --->1");
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                // Handle case where no address was found.
                MyLog.writeLog("GeoCodeService --->2");
                if (addresses == null || addresses.size() == 0) {
                    MyLog.writeLog("GeoCodeService --->3");
                    if (errorMessage.isEmpty()) {
                        errorMessage = getString(R.string.no_address_found);
                        MyLog.writeLog("GeoCodeService---------->" + errorMessage);
                    }
                    deliverFailureResultToReceiver(Constants.FAILURE_RESULT, errorMessage, latitude, longitude);
                } else {
                    MyLog.writeLog("GeoCodeService --->4");
                    Address address = addresses.get(0);
                    String addressString = "";
                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressString += address.getAddressLine(i) + " ";
                    }
                    MyLog.writeLog(getString(R.string.address_found) + " " + address.toString());
                    deliverSuccessResultToReceiver(Constants.SUCCESS_RESULT, addressString, address);
                }
            } catch (IOException ioException) {
                errorMessage = getString(R.string.service_not_available);
                MyLog.writeLog(errorMessage + ioException);
                deliverFailureResultToReceiver(Constants.FAILURE_RESULT, errorMessage, latitude, longitude);
                MyLog.writeLog("GeoCodeService IOException------------------------>" + ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
                MyLog.writeLog("GeoCodeService IOException---------------------->" + errorMessage + ". " + "Latitude = " + latitude + ", Longitude = " + longitude + illegalArgumentException);
                deliverFailureResultToReceiver(Constants.FAILURE_RESULT, errorMessage, latitude, longitude);
            }
        }
    }

    private void deliverFailureResultToReceiver(int resultCode, String message, double latitude, double longitude) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putDouble(Constants.LATITUDE_DATA_EXTRA, latitude);
        bundle.putDouble(Constants.LONGITUDE_DATA_EXTRA, longitude);
        mReceiver.send(resultCode, bundle);
        MyLog.writeLog("Deliver fail GeoCodeService");
    }

    private void deliverSuccessResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        mReceiver.send(resultCode, bundle);
        MyLog.writeLog("Deliver success GeoCodeService");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.writeLog("Intent Service onDestroy GeoCodeService");
    }

}
