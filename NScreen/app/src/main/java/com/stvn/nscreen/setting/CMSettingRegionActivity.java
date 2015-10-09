package com.stvn.nscreen.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import java.util.ArrayList;

/**
 * 설정화면 > 지역설정
 * Created by kimwoodam on 2015. 9. 30..
 */
public class CMSettingRegionActivity extends CMBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView mRegionName;
    private ListView mListView;
    private CMSettingRegionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_region);
        setActionBarInfo("지역설정", CMActionBar.CMActionBarStyle.BACK);

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
        this.mRegionName = (TextView)findViewById(R.id.setting_region_name);
        ArrayList<Object> sampleList = new ArrayList<Object>();
        sampleList.add("강동구");
        sampleList.add("강서구");
        sampleList.add("송파구");
        sampleList.add("중구");
        this.mAdapter = new CMSettingRegionAdapter(this, sampleList);
        this.mListView = (ListView)findViewById(R.id.setting_region_listview);
        this.mListView.setOnItemClickListener(this);
        this.mListView.setAdapter(this.mAdapter);

        findViewById(R.id.setting_region_cancel).setOnClickListener(this);
        findViewById(R.id.setting_region_complete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_region_cancel: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.setting_region_complete: {
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object regionName = parent.getItemAtPosition(position);
        this.mRegionName.setText((String)regionName);
    }
}