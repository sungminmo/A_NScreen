package com.stvn.nscreen.epg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;
import com.stvn.nscreen.my.MyMainActivity;

public class EpgMainActivity extends AppCompatActivity {

    private static final String                 tag = EpgMainActivity.class.getSimpleName();
    private static       EpgMainActivity        mInstance;
    private              JYSharedPreferences    mPref;

    private              EpgMainListViewAdapter mAdapter;
    private              ListView               mListView;

    private              ImageButton            imageButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg_main);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if ( mPref.isLogging() ) { Log.d(tag, "onCreate()"); }

        mAdapter  = new EpgMainListViewAdapter(this, null);
        // for test
        for ( int i = 0; i < 1000; i++ ) {
            String sChannel        = String.format("%02d", i);
            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\""+sChannel+"\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
            mAdapter.addItem(obj);
        }

        mListView = (ListView)findViewById(R.id.epg_main_listview);
        mListView.setAdapter(mAdapter);

        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EpgMainActivity.this, EpgChoiceActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_epg_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
