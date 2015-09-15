package com.stvn.nscreen.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 9. 15..
 */
public class SearchMainActivity extends AppCompatActivity {
    private static final String                     tag = SearchMainActivity.class.getSimpleName();
    private static       SearchMainActivity         mInstance;
    private              JYSharedPreferences        mPref;

    private              SearchMainListViewAdapter  mAdapter;
    private              ListView                   mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter  = new SearchMainListViewAdapter(this, null);
        // for test
        for ( int i = 0; i < 1000; i++ ) {
            String sChannel        = String.format("%02d", i);
            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\""+sChannel+"\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
            mAdapter.addItem(obj);
        }

        mListView = (ListView)findViewById(R.id.search_main_listview);
        mListView.setAdapter(mAdapter);
    }
}
