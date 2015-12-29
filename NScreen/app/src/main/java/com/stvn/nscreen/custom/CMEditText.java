package com.stvn.nscreen.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by gim-udam on 15. 12. 29..
 */
public class CMEditText extends EditText {

    public interface CMEditTextImeBackListener {
        public abstract void onImeBack(CMEditText ctrl, String text);
    }

    private CMEditTextImeBackListener mOnImeBack;

    public CMEditText(Context context) {
        super(context);
    }

    public CMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CMEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(CMEditTextImeBackListener listener) {
        mOnImeBack = listener;
    }

}