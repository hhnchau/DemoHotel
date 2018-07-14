package com.appromobile.hotel.gps;

import android.os.AsyncTask;

import com.appromobile.hotel.utils.MyLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xuan on 8/3/2016.
 */
public class FetchUrl extends AsyncTask<String, Void, String> {
    private ParseCallBack parseCallBack;
    public FetchUrl(ParseCallBack parseCallBack){
        this.parseCallBack = parseCallBack;
    }

    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
//            Log.d("Background Task data", data.toString());
        } catch (Exception e) {
//            Log.d("Background Task", e.toString());
            MyLog.writeLog("doInBackground----------------------->"+e);
        }
        return data;
    }
    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
//            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            MyLog.writeLog("downloadUrl----------------------->" + e);
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask(parseCallBack);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }
}
