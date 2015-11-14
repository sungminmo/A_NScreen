package com.stvn.nscreen.setting.notice;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import org.json.JSONObject;

/**
 * 설정화면 > 공지사항 > 공지사항 상세
 * Created by kimwoodam on 2015. 11. 15..
 */
public class CMSettingNoticeDetailActivity extends CMBaseActivity implements View.OnClickListener {

    private TextView mContentTitle;
    private TextView mContentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notice_detail);
        setActionBarInfo("공지사항", CMActionBar.CMActionBarStyle.BACK);

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
        this.mContentTitle = (TextView)findViewById(R.id.setting_notice_detail_title);
        this.mContentText = (TextView)findViewById(R.id.setting_notice_detail_content);
        findViewById(R.id.setting_notice_detail_confirm).setOnClickListener(this);

        try {
            Intent recvIntent = getIntent();
            JSONObject noticeData = new JSONObject(recvIntent.getStringExtra("Notice_Info"));

            String title = noticeData.getString("notice_Title");
            this.mContentTitle.setText(title);

            String contents = noticeData.getString("notice_Content");
            this.mContentText.setText(Html.fromHtml(contents));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_notice_detail_confirm: {
                finish();
                break;
            }
        }
    }
}