package com.appromobile.hotel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.MemberProfileSocialActivity;
import com.appromobile.hotel.utils.Utils;

/**
 * Created by appro on 23/01/2018.
 */

public class DialogVerify {
    private static DialogVerify Instance = null;

    public static DialogVerify getInstance() {
        if (Instance == null) {
            Instance = new DialogVerify();
        }
        return Instance;
    }

    private Dialog dialog;

    public void create(final Context context, String error, final CallbackVerify callbackVerify) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setCancelable(false);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_verify);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                dialog.findViewById(R.id.dialog_verify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //dialog.dismiss();
                    }
                });

                final EditText edt = dialog.findViewById(R.id.edtVerify);
                TextView tvNext = dialog.findViewById(R.id.tvNext);
                final TextView tvCance = dialog.findViewById(R.id.tvCance);
                tvCance.setTextColor(context.getResources().getColor(R.color.bk_15p));
                final TextInputLayout inputLayoutError = dialog.findViewById(R.id.input_layout_verify);

                final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                final ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 6000.0f, 0.0f);
                anim.setDuration(60000);
                progressBar.startAnimation(anim);

                final Animation animation = progressBar.getAnimation();
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvCance.setTextColor(context.getResources().getColor(R.color.bk_85p));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                if (error != null) {
                    inputLayoutError.setErrorEnabled(true);
                    inputLayoutError.setError(error);
                }

                tvNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String p = edt.getText().toString().trim();
                        if (!p.equals("")) {
                            inputLayoutError.setErrorEnabled(false);
                            callbackVerify.onVerify(p);
                            Utils.hideKeyboard(context, edt);
                            dialog.dismiss();
                            dialog= null;
                            progressBar.clearAnimation();
                        } else {
                            inputLayoutError.setErrorEnabled(true);
                            inputLayoutError.setError(context.getString(R.string.msg_7_2_verify_phone));
                        }
                    }
                });


                tvCance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (animation != null && animation.hasStarted() && !animation.hasEnded()) {
                            Toast.makeText(context, context.getString(R.string.verify_code_button, 60), Toast.LENGTH_LONG).show();
                        } else {
                            callbackVerify.onVerify(null);
                            progressBar.startAnimation(anim);
                            //dialog.dismiss();
                        }
                    }
                });

                dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hide();
                    }
                });

            }
        }
    }

    public void show(){
        if (dialog != null) {
            dialog.show();
        }
    }

    public void hide(){
        if (dialog != null) {
            dialog.hide();
        }
    }

}
