package com.appromobile.hotel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.IntroduceActivity;
import com.appromobile.hotel.activity.SignupActivity;

/**
 * Created by appro on 03/05/2018.
 */

public class DialogSignUp {
    private static DialogSignUp Instance = null;

    public static DialogSignUp getInstance() {
        if (Instance == null) {
            Instance = new DialogSignUp();
        }
        return Instance;
    }

    public void show(final Context context) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_signup);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                dialog.findViewById(R.id.dialog_signup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.tvSignupNow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SignupActivity.class);
                        intent.setAction("Introduce");
                        ((Activity) context).overridePendingTransition(0, 0);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(0, 0);
                        dialog.dismiss();
                    }
                });

            }

        }

    }
}
