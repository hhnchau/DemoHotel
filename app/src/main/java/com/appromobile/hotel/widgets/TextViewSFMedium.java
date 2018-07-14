package com.appromobile.hotel.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.appromobile.hotel.utils.UtilityFont;

/**
 * Created by xuanquach on 12/14/15.
 */
public class TextViewSFMedium extends android.support.v7.widget.AppCompatTextView{

    public TextViewSFMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TextViewSFMedium(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewSFMedium(Context context) {
        super(context);
    }


    @Override
    public void setTypeface(Typeface tf) {
        String fontName = "SF-UI-Display-Medium.ttf";
        super.setTypeface(UtilityFont.getFont(getContext(), UtilityFont.SanFrancisco_Medium, fontName));
    }

}
