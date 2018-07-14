package com.appromobile.hotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.CastVideoLinkAdapter;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xuan on 11/24/2016.
 */

public class BrowserActivity extends Activity implements View.OnClickListener {
    private WebView wvContent;
    private List<String> urls = new ArrayList<>();
    private List<String> listLink = new ArrayList<>();

    private static final String VIDEO_FORMAT = "MP4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnCast).setOnClickListener(this);

//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.flash_sale_price).copy(Bitmap.Config.ARGB_8888, true);
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(50f);
//        Canvas canvas = new Canvas(bm);
//        canvas.drawText("XXX", bm.getWidth() / 2, bm.getHeight() / 2, paint);
//        i.setImageBitmap(bm);


        wvContent = (WebView) findViewById(R.id.wvContent);
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setDomStorageEnabled(true);
        wvContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvContent.getSettings().setBuiltInZoomControls(true);
        wvContent.addJavascriptInterface(new WebAppInterface(this), "CastLinkDetect");

        MyWebViewClient myWebViewClient = new MyWebViewClient();
        wvContent.setWebChromeClient(new WebChromeClient());
        wvContent.setWebViewClient(myWebViewClient);
        wvContent.loadUrl("http://hdonline.vn");

        wvContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //addJavascriptDetect();
                return false;
            }
        });
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//                    if (wvContent.canGoBack()) {
//                        wvContent.goBack();
//                    } else {
//                        finish();
//                    }
//                    return true;
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnCast:
                openCastLink();
                break;
        }

    }

    private void openCastLink() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cast_link_dialog);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final ListView lvFile = (ListView) dialog.findViewById(R.id.lvLink);

        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try{
//                    webView.stopLoading();
//                    webView.onPause();
//                }catch (Exception e){}
                String urlSelected = listLink.get(position);
                MyLog.writeLog("URL-Selected----->: " + urlSelected);
                dialog.dismiss();
                urls.clear();
            }
        });

        dialog.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //clear unknown urls
                for (int i = 0; i < urls.size(); i++) {
                    String url = urls.get(i);
                    MyLog.writeLog("Listener--->: url:-----> " + url);
                    //String fileAccept = "MP3MP4AVIGP3";
                    if (url.startsWith("http")) {

                        String fileName = Utils.getFileFromPath(url);
                        String fileExt = "";
                        try {
                            fileName = fileName.toLowerCase();
                            String[] fileCotent = fileName.split("\\.");
                            fileExt = fileCotent[fileCotent.length - 1];
                            fileExt = fileExt.toUpperCase();
                        } catch (Exception e) {
                        }
                        if (!fileExt.equals("")) {

                            if (!Utils.containsIgnoreCase(VIDEO_FORMAT, fileExt)) {
                                urls.remove(i);
                                if (i > 0) {
                                    i--;
                                }
                            }
                        } else {
                            urls.remove(i);
                            if (i > 0) {
                                i--;
                            }
                        }
                    }

                }
                Set<String> hs = new HashSet<>();
                hs.addAll(urls);
                urls.clear();
                urls.addAll(hs);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                listLink = new ArrayList<>(urls);
                CastVideoLinkAdapter adapter = new CastVideoLinkAdapter(BrowserActivity.this, listLink);
                lvFile.setAdapter(adapter);
            }
        }.execute();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            MyLog.writeLog("LINKKKKK -------><><><><------" + url);
        }
        //        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            urls.add(url);
//            //addJavascriptDetect();
////            analyzeURL();
//
//
////            try{
////                final Runtime info = Runtime.getRuntime();
////                long  freeSize = info.freeMemory();
////                if(freeSize<=MEMSIZE) {
////                    new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////                            info.gc();
////                        }
////                    }).start();
////                }
////                webView.clearHistory();
////                webView.freeMemory();
////                webView.clearCache(true);
////
////            }catch (Exception e){
////
////            }
//        }
//
//        @Override
//        public void onLoadResource(WebView view, String url) {
//            try {
//                super.onLoadResource(view, url);
//                //addJavascriptDetect();
//                if (url.startsWith("http")) {
//                    MyLog.writeLog("httpLink:----> " + url);
////                    LazyLoaderService.download(url, urls);
//                    LazyDetectLinkService.download(url, urls);
//                    urls.add(url);
//                }
////                try {
////                    final Runtime info = Runtime.getRuntime();
////                    long  freeSize = info.freeMemory();
////                    if(freeSize<=MEMSIZE) {
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                info.gc();
////                            }
////                        }).start();
////                    }
////                    webView.clearHistory();
////                    webView.freeMemory();
////                    webView.clearCache(true);
////                } catch (Exception e) {
////                }
//            } catch (Exception e) {
//            }
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            boolean isResource = false;
//            try {
//                if (url.startsWith("http")) {
//                    urls.add(url);
//                }
//                //addJavascriptDetect();
//
////                try {
////                    final Runtime info = Runtime.getRuntime();
////                    long  freeSize = info.freeMemory();
////                    if(freeSize<=MEMSIZE) {
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                info.gc();
////                            }
////                        }).start();
////                    }
////                    webView.clearHistory();
////                    webView.freeMemory();
////                    webView.clearCache(true);
////                } catch (Exception e) {
////                }
//                return super.shouldOverrideUrlLoading(view, url);
//            } catch (Exception e) {
//
//            }
//            return isResource;
//        }
    }

    private void addJavascriptDetect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MyLog.writeLog("---->DetectUrl<----");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("javascript:try{");
            stringBuilder.append("var vid  = document.getElementsByTagName('video'); ");
            stringBuilder.append("var currentSrc=''; ");

            stringBuilder.append("try{ ");
            stringBuilder.append("var sources  = vid[0].getElementsByTagName('source');");
            stringBuilder.append("currentSrc = sources[0].src ;");
            stringBuilder.append("}catch(error1){} ");

            stringBuilder.append("if(currentSrc!=null){");
            stringBuilder.append("currentSrc = vid[0].src ;");
            stringBuilder.append("}");

            stringBuilder.append(" return currentSrc;");
            stringBuilder.append("}catch(error){}");

            wvContent.evaluateJavascript(
                    "(function() { " + stringBuilder.toString() + " })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(final String url) {
                            addLinkDetected(url);
                        }
                    });
        } else {
            MyLog.writeLog("---->DetectUrl<----");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("javascript:try{");
            stringBuilder.append("var vid  = document.getElementsByTagName('video'); ");
            stringBuilder.append("var currentSrc=''; ");

            stringBuilder.append("try{ ");
            stringBuilder.append("var sources  = vid[0].getElementsByTagName('source');");
            stringBuilder.append("currentSrc = sources[0].src ;");
            stringBuilder.append("}catch(error1){} ");

            stringBuilder.append("if(currentSrc!=null){");
            stringBuilder.append("currentSrc = vid[0].src ;");
            stringBuilder.append("}");

            stringBuilder.append("CastLinkDetect.addURL");
            stringBuilder.append("(");

            Object param = " currentSrc ";
            if (param instanceof String) {
                stringBuilder.append(param);

            }
            stringBuilder.append(")}catch(error){CastLinkDetect.onError(error.message);}");
            wvContent.loadUrl(stringBuilder.toString());
        }
    }

    private void stopLoading(boolean pause) {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod(pause
                            ? "onPause"
                            : "onResume", (Class[]) null)
                    .invoke(wvContent, (Object[]) null);
        } catch (Exception e) {
        }
    }

    private void addLinkDetected(String url) {
        if (url != null && !url.equals("null")) {
            url = url.replace("\"", "");
            MyLog.writeLog("URLVIDEO:------> " + url);
            urls.add(url);
        }
    }

    public void analyzeURL() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    for (int i = 0; i < urls.size(); i++) {
                        String url = urls.get(i);
                        MyLog.writeLog("fileExt: url:----> " + url);
                        //String fileAccept = "MP3MP4AVIGP3";
                        if (url.startsWith("http")) {

                            String fileName = Utils.getFileFromPath(url);
                            String fileExt = "";
                            try {
                                fileName = fileName.toLowerCase();
                                String[] fileCotent = fileName.split("\\.");
                                fileExt = fileCotent[fileCotent.length - 1];
                                fileExt = fileExt.toUpperCase();
                            } catch (Exception e) {
                            }
                            if (!fileExt.equals("")) {
                                if (!Utils.containsIgnoreCase(VIDEO_FORMAT, fileExt)) {
                                    urls.remove(i);
                                    if (i > 0) {
                                        i--;
                                    }
                                }
                            } else {
                                urls.remove(i);
                                if (i > 0) {
                                    i--;
                                }
                            }
                        }

                    }
                    Set<String> hs = new HashSet<>();
                    hs.addAll(urls);
                    urls.clear();
                    urls.addAll(hs);
                } catch (Exception e) {
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the callback and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void addURL(final String url) {
            addURL(url);
        }

        @JavascriptInterface
        public void onError(String error) {
            MyLog.writeLog("Detect Link:----> " + error);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wvContent.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wvContent.onResume();
    }

    private class ListVideo {
        private static final String PHIMMOI_NET = "https://redirector.googlevideo.com/videoplayback?id=";
    }
}
