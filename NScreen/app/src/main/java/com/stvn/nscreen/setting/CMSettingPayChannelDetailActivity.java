package com.stvn.nscreen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

/**
 * 설정화면 > 유료채널 안내 > 채널 상세 화면
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingPayChannelDetailActivity extends CMBaseActivity implements View.OnClickListener {

    private ImageView mChannelImage;
    private TextView mContentText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pay_channel_detail);

        Intent recvIntent = getIntent();
        String title = recvIntent.getStringExtra("Channel_Title");

        setActionBarInfo(title, CMActionBar.CMActionBarStyle.BACK);

        initializeView();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 설정 메인 화면 UI 설정
     * */
    private void initializeView() {
        this.mChannelImage = (ImageView)findViewById(R.id.setting_pay_channel_detail_image);
        this.mContentText = (TextView)findViewById(R.id.setting_pay_channel_detail_content);
        findViewById(R.id.setting_pay_channel_detail_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_pay_channel_detail_confirm: {
                finish();
                break;
            }
        }
    }
}
