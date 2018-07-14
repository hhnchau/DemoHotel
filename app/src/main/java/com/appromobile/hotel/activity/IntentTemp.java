package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.appromobile.hotel.utils.ParamConstants;


/**
 * Created by appro on 19/04/2017.
 */
public class IntentTemp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getAction();
        if (action != null) {

            switch (action) {
                case ParamConstants.ACTION_RESET_NEARBY:

                    setResult(RESULT_OK);
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);

                    break;
                case ParamConstants.ACTION_CHOOSE_AREA_FAVORITE: {

                    int position = getIntent().getIntExtra("positionFavorite", -1);

                    Intent i = new Intent();
                    i.putExtra("position", position);
                    setResult(RESULT_OK, i);
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);

                    break;
                }
                case ParamConstants.ACTION_CHANGE_AREA: {
                    String targetInfo = getIntent().getStringExtra(ParamConstants.ACTION_CHANGE_AREA);

                    Intent i = new Intent();
                    i.putExtra(ParamConstants.ACTION_CHANGE_AREA, targetInfo);
                    setResult(RESULT_OK, i);
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                    break;
                }
            }
        }
    }
}
