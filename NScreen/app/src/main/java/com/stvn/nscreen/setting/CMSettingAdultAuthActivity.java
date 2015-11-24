package com.stvn.nscreen.setting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jjiya.android.common.CMConstants;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정화면 > 성인인증
 * Created by kimwoodam on 2015. 11. 2..
 */

public class CMSettingAdultAuthActivity extends CMBaseActivity {

    private JYSharedPreferences  mPref;
    private WebView              mWebview;

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
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void initializeView() {
        this.mWebview = (WebView)findViewById(R.id.adult_auth_webview);
        this.mWebview.getSettings().setJavaScriptEnabled(true);
        this.mWebview.setWebViewClient(new AdultAuthWebClient());
        this.mWebview.loadUrl(CMConstants.ADULT_AUTH_URL);
    }

    /**
     * 웹뷰 페이지가 로드 되기전에 인증서 에러 페이지가 로드되면서 에러 발생하는 현상 해결을 위한 코드
     * */
    public class AdultAuthWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Uri pageURI = Uri.parse(url);

            String strScheme = pageURI.getScheme();
            if (TextUtils.isEmpty(strScheme) == false) {
                strScheme = strScheme.toLowerCase();
                if (CMConstants.CM_CUSTOM_SCHEME.equalsIgnoreCase(strScheme)) {
                    if (CMConstants.CM_ADULT_AUTH_HOST.equals(pageURI.getHost())) {
                        boolean isSucess = false;
                        String strResult = pageURI.getQueryParameter(CMConstants.CM_ADULT_AUTH_PARAM);
                        if (TextUtils.isEmpty(strResult) == false) {
                            strResult = strResult.toLowerCase();
                            if ("y".equalsIgnoreCase(strResult)) {
                                isSucess = true;
                                mPref.setIAmAdult(); // 성인인증 받았을때 호출. 성인인증 받았는지 여부는 mPref.isAdultVerification()
                                Intent intent = new Intent("I_AM_ADULT");
                                sendBroadcast(intent);
                            }
                        }

                        if (isSucess == true) {
                            setResult(Activity.RESULT_OK);
                            CMSettingData.getInstance().setAdultAuth(CMSettingAdultAuthActivity.this, true);
                        } else {
                            setResult(Activity.RESULT_OK);
                            CMSettingData.getInstance().setAdultAuth(CMSettingAdultAuthActivity.this, false);
                        }
                        finish();
                        return true;
                    }
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // ssl 인증 전 페이지가 로드되어 ssl 인증오류 발생으로 인한 앱 비정상 종료 방지를 위한 ssl 인증 오류 무시 코드
            handler.proceed();
        }
    }
}

