package com.appromobile.hotel.gps;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.appromobile.hotel.HotelApplication;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by xuan on 7/19/2016.
 */
public class GetAddressPositionTask extends
        AsyncTask<String, Integer, LatLng> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected LatLng doInBackground(String... plookupString) {
        String lookupString = plookupString[0];
        final String lookupStringUriencoded = Uri.encode(lookupString);
        LatLng position = null;
        Geocoder geocoder = new Geocoder(HotelApplication.getContext(), Locale.getDefault());
        // best effort zoom
        try {
            if (geocoder != null) {
                List<Address> addresses = geocoder.getFromLocationName(
                        lookupString, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address first_address = addresses.get(0);
                    position = new LatLng(first_address.getLatitude(),
                            first_address.getLongitude());
                }
            } else {
//                Log.e(TAG, "geocoder was null, is the module loaded? "
//                        + isLoaded);
            }

        } catch (IOException e) {
//            Log.e(TAG, "geocoder failed, moving on to HTTP");
        }
        if (position == null) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL("http://maps.google.com/maps/api/geocode/json?address="
                        + lookupStringUriencoded + "&sensor=true");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            JSONObject jsonObject;
            try {
                // Log.d("MAPSAPI", stringBuilder.toString());

                jsonObject = new JSONObject(stringBuilder.toString());
                if (jsonObject.getString("status").equals("OK")) {
                    jsonObject = jsonObject.getJSONArray("results")
                            .getJSONObject(0);
                    jsonObject = jsonObject.getJSONObject("geometry");
                    jsonObject = jsonObject.getJSONObject("location");
                    String lat = jsonObject.getString("lat");
                    String lng = jsonObject.getString("lng");

                    // Log.d("MAPSAPI", "latlng " + lat + ", "
                    // + lng);

                    position = new LatLng(Double.valueOf(lat),
                            Double.valueOf(lng));
                }

            } catch (JSONException e) {
//                Log.e(TAG, e.getMessage(), e);
            }
        }

        return position;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        Log.i("GEOCODE", result.toString());
        super.onPostExecute(result);
    }

};