package com.appromobile.hotel.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.appromobile.hotel.utils.UtilityFont;


/**
 * Created by xuanquach on 12/14/15.
 */
public class EditTextSFRegular extends android.support.v7.widget.AppCompatEditText{

    public EditTextSFRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextSFRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextSFRegular(Context context) {
        super(context);
    }


    @Override
    public void setTypeface(Typeface tf) {
        String fontName = "SF-UI-Display-Regular.otf";
        super.setTypeface(UtilityFont.getFont(getContext(), UtilityFont.SanFrancisco_Regular, fontName));
    }
}
