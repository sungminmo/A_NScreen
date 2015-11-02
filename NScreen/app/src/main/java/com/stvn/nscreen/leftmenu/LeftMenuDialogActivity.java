package com.stvn.nscreen.leftmenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 11. 2..
 */

public class LeftMenuDialogActivity extends Activity {
    private static final String                 tag = LeftMenuDialogActivity.class.getSimpleName();
    private static       LeftMenuDialogActivity mInstance;
    private              JYSharedPreferences    mPref;

    private              ImageButton            leftMenu_dialog_closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_leftmenu_dialog);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        leftMenu_dialog_closeBtn = (ImageButton) findViewById(R.id.leftMenu_dialog_closeBtn);

        leftMenu_dialog_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
