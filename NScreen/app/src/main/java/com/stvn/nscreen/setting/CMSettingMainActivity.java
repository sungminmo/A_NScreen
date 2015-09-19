package com.stvn.nscreen.setting;

import android.os.Bundle;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정 화면
 * Created by kimwoodam on 2015. 9. 19..
 */
public class CMSettingMainActivity extends CMBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        setActionBarInfo("설정", CMActionBar.CMActionBarStyle.BACK);
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

}
