package com.stvn.nscreen.rmt;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;
import com.stvn.nscreen.epg.EpgSubListViewAdapter;

/**
 * Created by limdavid on 15. 10. 26..
 */
public class RemoteControllerActivity extends AppCompatActivity{

    private static final String                 tag = RemoteControllerActivity.class.getSimpleName();
    private static       RemoteControllerActivity         mInstance;
    private JYSharedPreferences mPref;

    // network
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    // gui
    private RemoteControllerListViewAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_controller);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter = new RemoteControllerListViewAdapter(this, null);

        // for test
        for (int i = 0; i < 1000; i++) {
            String sChannel        = String.format("%02d", i);
            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\"" + sChannel + "\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
            mAdapter.addItem(obj);
        }

        mListView = (ListView)findViewById(R.id.remote_controller_listview);
        mListView.setAdapter(mAdapter);
    }
}