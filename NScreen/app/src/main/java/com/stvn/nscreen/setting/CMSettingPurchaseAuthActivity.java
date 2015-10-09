package com.stvn.nscreen.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정화면 > 구매인증 비밀번호 관리
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingPurchaseAuthActivity extends CMBaseActivity implements View.OnClickListener {

    private TextView mRegionName;
    private ListView mListView;
    private CMSettingRegionAdapter mAdapter;

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
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
        }
    }
}