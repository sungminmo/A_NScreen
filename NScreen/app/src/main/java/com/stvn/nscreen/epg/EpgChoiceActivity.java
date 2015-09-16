package com.stvn.nscreen.epg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 9. 14..
 */
public class EpgChoiceActivity extends AppCompatActivity {
    private static final String                 tag = EpgChoiceActivity.class.getSimpleName();
    private static       EpgChoiceActivity      mInstance;
    private              JYSharedPreferences    mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg_choice);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

    }
}
