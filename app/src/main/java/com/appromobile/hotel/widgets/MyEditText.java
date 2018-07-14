package com.appromobile.hotel.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.appromobile.hotel.utils.UtilityFont;

/**
 * Created by xuan on 9/12/2016.
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {
    private OnKeyBoardEditListener onKeyBoardEditListener;
    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//            if(onKeyBoardEditListener!=null){
//                onKeyBoardEditListener.onBackPress();
//            }
//
//            return true;
//        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public void setTypeface(Typeface tf) {
        String fontName = "SF-UI-Display-Regular.otf";
        super.setTypeface(UtilityFont.getFont(getContext(), UtilityFont.SanFrancisco_Regular, fontName));
    }

    public void setOnKeyBoardEditListener(OnKeyBoardEditListener onKeyBoardEditListener) {
        this.onKeyBoardEditListener = onKeyBoardEditListener;
    }

    public interface OnKeyBoardEditListener{
        void onBackPress();
    }
}
