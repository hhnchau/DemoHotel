package com.appromobile.hotel.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;

/**
 * Created by appro on 11/09/2017.
 */
public class WebviewDetail extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private TextView title;
    private ImageView img;
    private String html =
            "<p style=\"text-align:center\"><strong>TH&Ocirc;NG TIN LI&Ecirc;N HỆ</strong></p>\n" +
            "\n" +
            "<p><br />\n" +
            "C&Ocirc;NG TY CỔ PHẦN APPRO MOBILE</p>\n" +
            "\n" +
            "<p>Số 41 L&ecirc; Trung Nghĩa, Phường 12, Quận T&acirc;n B&igrave;nh, TP.HCM</p>\n" +
            "\n" +
            "<p>Email: support@go2joy.vn</p>\n" +
            "\n" +
            "<p>Điện thoại : &lrm;028 3811 3782</p>";

    @Override
    public void setScreenName() {
        this.screenName = "SAboutUs";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_detail);
        setScreenName();
        init();
        createView(html);
    }

    private void createView(String html) {
        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    private void init() {
        webView =  findViewById(R.id.webview_detail);
        title =  findViewById(R.id.tvDetailTitle);
        title.setText(getString(R.string.txt_6_about_us));
        img =  findViewById(R.id.btnCloseDetail);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCloseDetail) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }
}
