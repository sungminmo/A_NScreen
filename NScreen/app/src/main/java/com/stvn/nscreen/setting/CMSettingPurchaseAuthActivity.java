package com.stvn.nscreen.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
import com.stvn.nscreen.util.CMAlertUtil;

/**
 * 설정화면 > 구매인증 비밀번호 관리
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingPurchaseAuthActivity extends CMBaseActivity implements View.OnClickListener {

    private EditText mAuthPwd, mAuthPwdRe;
    private TextView mErrorTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_purchase_auth);
        setActionBarInfo("구매인증 비밀번호 관리", CMActionBar.CMActionBarStyle.BACK);

        initializeView();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 구매인증 비밀번호 관리 화면 UI 설정
     * */
    private void initializeView() {

        mAuthPwd = (EditText)findViewById(R.id.setting_purchase_auth_password);
        mAuthPwdRe = (EditText)findViewById(R.id.setting_purchase_auth_password_re);
        mErrorTxt = (TextView)findViewById(R.id.setting_purchase_auth_error);

        findViewById(R.id.setting_purchase_auth_cancel).setOnClickListener(this);
        findViewById(R.id.setting_purchase_auth_complete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_purchase_auth_cancel: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.setting_purchase_auth_complete: {
                if (isComparePassword()) {

                    String pwd = mAuthPwd.getText().toString();
                    CMSettingData.getInstance().setPurchaseAuthPassword(CMSettingPurchaseAuthActivity.this, pwd);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
            }
        }
    }

    private boolean isComparePassword() {
        String pwd = mAuthPwd.getText().toString();
        String pwdRe = mAuthPwdRe.getText().toString();

        if (TextUtils.isEmpty(pwd)) {
            CMAlertUtil.Alert(CMSettingPurchaseAuthActivity.this, "인증번호 입력오류", "인증번호를 입력하지 않으셨습니다.");
            return false;
        } else if (TextUtils.isEmpty(pwdRe)) {
            CMAlertUtil.Alert(CMSettingPurchaseAuthActivity.this, "인증번호 입력오류", "인증번호 입력확인 입력하지 않으셨습니다.");
            return false;
        } else if (pwd.length() < 4 || pwdRe.length() < 4) {
            CMAlertUtil.Alert(CMSettingPurchaseAuthActivity.this, "인증번호 입력오류", "인증번호는 최소 4자리입니다.");
            return false;
        } else {
            if (pwd.equals(pwdRe)) {
                mAuthPwdRe.setSelected(false);
                mAuthPwdRe.setTextColor(getResources().getColor(R.color.setting_purchase_violet_color));
                mErrorTxt.setText("");
                return true;
            } else {
                mAuthPwdRe.setSelected(true);
                mAuthPwdRe.setTextColor(getResources().getColor(R.color.red));
                mErrorTxt.setText("인증번호가 일치하지 않습니다.");
                return false;
            }
        }
    }
}