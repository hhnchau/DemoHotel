package com.appromobile.hotel.gps;

import android.os.AsyncTask;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.utils.MyLog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xuan on 8/3/2016.
 */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private ParseCallBack parseCallBack;
    public ParserTask(ParseCallBack parseCallBack){
        this.parseCallBack = parseCallBack;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
//            Log.d("ParserTask",jsonData[0].toString());
            DataParser parser = new DataParser();
//            Log.d("ParserTask", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
//            Log.d("ParserTask","Executing routes");
//            Log.d("ParserTask",routes.toString());

        } catch (Exception e) {
//            Log.d("ParserTask",e.toString());
            MyLog.writeLog("ParserTask------------------------>"+e);
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        LatLng fristPoint = null;
        LatLng endPoint = null;
        // Traversing through all the routes
        if(result!=null) {
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    if (i == 0 && j == 0) {
                        fristPoint = new LatLng(lat, lng);
                    } else if (i == result.size() - 1 && j == path.size() - 1) {
                        endPoint = new LatLng(lat, lng);
                    }
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(HotelApplication.getContext().getResources().getDimensionPixelSize(R.dimen.dp_6));
                lineOptions.color(HotelApplication.getContext().getResources().getColor(R.color.org));
//            Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }
        }

        // Drawing polyline in the Google Map for the i-th route
//        if(lineOptions != null) {
//            if(parseCallBack!=null){
//                parseCallBack.onFinishedParser(lineOptions, fristPoint, endPoint);
//            }
//        }else{
//
//        }
        if(parseCallBack!=null){
            parseCallBack.onFinishedParser(lineOptions, fristPoint, endPoint);
        }
        else {
//            Log.d("onPostExecute","without Polylines drawn");
        }
    }

}