package com.appromobile.hotel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;


/**
 * Created by Chau Huynh on 17/03/02017.
 */

public class Dialag {
    private static Dialag Instance = null;
    public final static int BTN_LEFT = 1;
    public final static int BTN_MIDDLE = 2;
    public final static int BTN_RIGHT = 3;

    public static Dialag getInstance() {
        if (Instance == null) {
            Instance = new Dialag();
        }
        return Instance;
    }

    public void show(Context context, boolean logo, final boolean outside, boolean cancel, String title, CharSequence message, String btn1, String btn2, String btn3, int btn_color, final CallbackDialag callbackDialag) {
        try {
            if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
                final Dialog dialog = new Dialog(context, R.style.myDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dialog_popup);
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    window.setAttributes(wlp);
                    dialog.setCancelable(cancel);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.show();
                }
                TextView tvTitle =  dialog.findViewById(R.id.popup_title);
                TextView tvmessage =  dialog.findViewById(R.id.popup_text);
                TextView button1 =  dialog.findViewById(R.id.popup_btn1);
                TextView button2 =  dialog.findViewById(R.id.popup_btn2);
                TextView button3 = dialog.findViewById(R.id.popup_btn3);
                ImageView imgLogo =  dialog.findViewById(R.id.imageView_logo);
                RelativeLayout Outside =  dialog.findViewById(R.id.dialog_popup);
                View vBtn2 = dialog.findViewById(R.id.view_space_btn2);
                View vBtn3 = dialog.findViewById(R.id.view_space_btn3);

                Outside.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (outside) {
                            dialog.dismiss();
                        }
                    }
                });

                if (logo) {
                    imgLogo.setVisibility(View.VISIBLE);
                }

                if (title == null) {
                    tvTitle.setVisibility(View.GONE);
                } else {
                    tvTitle.setText(title);
                }

                if (message != null) {
                    tvmessage.setText(message);
                    tvmessage.setHighlightColor(Color.TRANSPARENT);
                    tvmessage.setMovementMethod(LinkMovementMethod.getInstance());

                    if (tvmessage.getLinksClickable()) {
                        tvmessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                }

                if (btn1 == null) {
                    button1.setVisibility(View.GONE);
                } else {
                    button1.setText(btn1);
                }

                if (btn2 == null) {
                    vBtn2.setVisibility(View.GONE);
                    button2.setVisibility(View.GONE);
                } else {
                    button2.setText(btn2);
                }

                if (btn3 == null) {
                    vBtn3.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                } else {
                    button3.setText(btn3);
                }

                switch (btn_color) {
                    case BTN_LEFT:
                        button1.setBackgroundColor(ContextCompat.getColor(context, R.color.org));
                        break;
                    case BTN_MIDDLE:
                        button2.setBackgroundColor(ContextCompat.getColor(context, R.color.org));
                        break;
                    case BTN_RIGHT:
                        button3.setBackgroundColor(ContextCompat.getColor(context, R.color.org));
                        break;
                }

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbackDialag.button1();
                        dialog.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbackDialag.button2();
                        dialog.dismiss();
                    }
                });

                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callbackDialag.button3(dialog);

                    }

                });
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
