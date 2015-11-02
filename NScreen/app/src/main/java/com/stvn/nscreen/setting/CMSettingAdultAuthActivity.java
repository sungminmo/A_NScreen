package com.stvn.nscreen.setting;

import android.os.Bundle;
import android.webkit.WebView;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정화면 > 성인인증
 * Created by kimwoodam on 2015. 11. 2..
 */
public class CMSettingAdultAuthActivity extends CMBaseActivity {

    private JYSharedPreferences mPref;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_adult_auth);
        setActionBarInfo("성인인증", CMActionBar.CMActionBarStyle.BACK);

        mPref = new JYSharedPreferences(this);
        initializeView();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    private void initializeView() {
        this.mWebview = (WebView)findViewById(R.id.adult_auth_webview);
        this.mWebview.loadUrl("http://58.141.255.80/CheckPlusSafe_ASP/checkplus_main.asp");
    }

}

