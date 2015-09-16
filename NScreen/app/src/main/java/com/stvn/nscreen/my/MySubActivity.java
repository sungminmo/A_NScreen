package com.stvn.nscreen.my;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 9. 16..
 */
public class MySubActivity extends AppCompatActivity {
    private static final String                 tag = MySubActivity.class.getSimpleName();
    private static       MySubActivity         mInstance;
    private              JYSharedPreferences   mPref;

    private              MySubListViewAdapter  mAdapter;
    private              ListView              mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sub);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter  = new MySubListViewAdapter(this, null);
        // for test
        for ( int i = 0; i < 1000; i++ ) {
            String sChannel        = String.format("%02d", i);
            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\""+sChannel+"\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
            mAdapter.addItem(obj);
        }

        mListView = (ListView)findViewById(R.id.my_sub_listview);
        mListView.setAdapter(mAdapter);
    }
}