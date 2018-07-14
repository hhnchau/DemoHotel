package com.appromobile.hotel.services;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LazyDetectLinkService {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static Runnable runnable;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            NUMBER_OF_CORES * 1,
            NUMBER_OF_CORES * 1,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );
    static boolean isStop= false;
    public static void download(final String url, final List<String> urls) {
        try {
            if(runnable!=null){
                isStop = true;
                executor.remove(runnable);
                executor.shutdownNow();
                executor = new ThreadPoolExecutor(
                        NUMBER_OF_CORES * 1,
                        NUMBER_OF_CORES * 1,
                        30L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>()
                );
            }
            runnable =new Runnable() {
                @Override
                public void run() {
                    try {
                        isStop =false;
                        String content = "";
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url)
                                .addHeader("Accept-Encoding", "identity")
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept-Encoding", "UTF-8")
                                .build();

                        Response response = client.newCall(request).execute();
                        String contentType = response.header("Content-Type");

                        if(contentType!=null && contentType.toLowerCase().contains("application/json")) {
//                            int bufferSize =1024;
                            BufferedReader in = new BufferedReader(new InputStreamReader(response.body().byteStream()));

                            StringBuilder stringBuilder = new StringBuilder();
                            String inputLine;

//                            String newLine = System.getProperty("line.separator");
                            while ((inputLine = in.readLine()) != null && !isStop)
                            {
                                stringBuilder.append(inputLine);
                            }

                            in.close();

                            content = stringBuilder.toString();

                            if (!content.equals("")) {
                                // find deeply file url
                                JSONObject jsonObject = new JSONObject(content);
                                parse(jsonObject, urls);
                            }
                        }else{
                            response.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            executor.execute(runnable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parse(JSONObject json, List<String> urls) throws JSONException {
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = null;
            try {
                JSONArray jsonArray = json.getJSONArray(key);
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if(isStop){
                            return;
                        }
                        parse(jsonArray.getJSONObject(i), urls);
                    }
                }
            } catch (Exception e) {
                try {
                    JSONObject value = json.getJSONObject(key);
                    parse(value, urls);
                } catch (Exception e1) {
                    val = json.getString(key);
                }
            }
            if (val != null && (val.startsWith("http"))) {
//                out.put(key, val);
                urls.add(val);
            }
        }
        return;
    }

    private static boolean isExist(String url, List<String> urls) {

        for (String item : urls) {
            if (url.hashCode() == item.hashCode())
                return true;
        }
        return false;
    }

}
