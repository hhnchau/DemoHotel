package com.appromobile.hotel.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.appromobile.hotel.utils.UtilityFont;

/**
 * Created by xuanquach on 12/14/15.
 */
public class TextViewSFHeavy extends android.support.v7.widget.AppCompatTextView{

    public TextViewSFHeavy(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TextViewSFHeavy(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewSFHeavy(Context context) {
        super(context);
    }


    @Override
    public void setTypeface(Typeface tf) {
        String fontName = "SF-UI-Display-Heavy.ttf";
        super.setTypeface(UtilityFont.getFont(getContext(), UtilityFont.SanFrancisco_Heavy, fontName));
    }

}
