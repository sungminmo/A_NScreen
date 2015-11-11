package com.stvn.nscreen.vod;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 11. 9..
 */

public class VodBuyDialog extends Activity {
    private static final String tag = VodBuyDialog.class.getSimpleName();
    private static VodBuyDialog mInstance;
    private JYSharedPreferences mPref;
    private Button backBtn, purchaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_vod_buy_dialog);

        mInstance = this;
        mPref = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        backBtn = (Button) findViewById(R.id.backBtn);
        purchaseBtn = (Button) findViewById(R.id.purchaseBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}