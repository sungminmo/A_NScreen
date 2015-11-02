package com.stvn.nscreen.setting;

import android.app.Activity;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
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

            if (CMConstants.CM_CUSTOM_SCHEME.equals(pageURI.getScheme())) {
                if (CMConstants.CM_ADULT_AUTH_HOST.equals(pageURI.getHost())) {
                    String result = pageURI.getQueryParameter(CMConstants.CM_ADULT_AUTH_PARAM);
                    if ("Y".equals(result)) {
                        setResult(Activity.RESULT_OK);
                        CMSettingData.getInstance().setAdultAuth(CMSettingAdultAuthActivity.this, true);
                    } else {
                        setResult(Activity.RESULT_OK);
                        CMSettingData.getInstance().setAdultAuth(CMSettingAdultAuthActivity.this, false);
                    }
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
}

