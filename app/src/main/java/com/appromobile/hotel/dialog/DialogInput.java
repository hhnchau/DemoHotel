package com.appromobile.hotel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.ReservationActivity;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.Utils;

/**
 * Created by appro on 22/01/2018.
 */

public class DialogInput {
    private static DialogInput Instance = null;

    public static DialogInput getInstance() {
        if (Instance == null) {
            Instance = new DialogInput();
        }
        return Instance;
    }

    public void show(final Context context, final CallbackInput callbackInput) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context, R.style.dialog_full_transparent_background);
            dialog.setOwnerActivity((Activity) context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_input);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();

                // FIND VIEW BY ID
                dialog.findViewById(R.id.dialog_input).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                final EditText edt = dialog.findViewById(R.id.edtPhone);
                TextView tvCance = dialog.findViewById(R.id.tvCance);
                TextView tvNext = dialog.findViewById(R.id.tvNext);

                tvCance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                tvNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String p = edt.getText().toString().trim();
                        if (p.length() > 9 && p.length() < 12) {
                            callbackInput.onInput(p);
                            Utils.hideKeyboard((Activity) context, edt);
                            dialog.dismiss();
                        }else {
                            Toast.makeText(context, context.getString(R.string.number_is_wrong_format), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
