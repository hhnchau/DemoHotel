package com.appromobile.hotel.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.appromobile.hotel.utils.UtilityFont;


/**
 * Created by xuanquach on 12/14/15.
 */
public class TextViewSFRegular extends android.support.v7.widget.AppCompatTextView{

    public TextViewSFRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TextViewSFRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewSFRegular(Context context) {
        super(context);
    }


    @Override
    public void setTypeface(Typeface tf) {
        String fontName = "SF-UI-Display-Regular.ttf";
        super.setTypeface(UtilityFont.getFont(getContext(), UtilityFont.SanFrancisco_Regular, fontName));
    }

}
