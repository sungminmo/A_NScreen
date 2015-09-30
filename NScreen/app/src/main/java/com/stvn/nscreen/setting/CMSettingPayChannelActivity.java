package com.stvn.nscreen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import java.util.ArrayList;

/**
 * 설정화면 > 유료채널 안내 관리
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingPayChannelActivity extends CMBaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private CMSettingPayChannelAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pay_channel);
        setActionBarInfo("유료채널 안내", CMActionBar.CMActionBarStyle.BACK);

        initializeView();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 지역설정 화면 UI 설정
     * */
    private void initializeView() {
        ArrayList<Object> sampleList = new ArrayList<Object>();
        sampleList.add("채널 1");
        sampleList.add("채널 2");
        sampleList.add("채널 3");
        sampleList.add("채널 4");
        this.mAdapter = new CMSettingPayChannelAdapter(this, sampleList);
        this.mListView = (ListView)findViewById(R.id.setting_pay_channel_listview);
        this.mListView.setOnItemClickListener(this);
        this.mListView.setAdapter(this.mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent nextIntent = new Intent(CMSettingPayChannelActivity.this, CMSettingPayChannelDetailActivity.class);
        startActivity(nextIntent);
    }
}
