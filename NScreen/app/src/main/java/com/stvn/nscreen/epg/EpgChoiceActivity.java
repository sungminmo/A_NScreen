package com.stvn.nscreen.epg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

/**
 * Created by limdavid on 15. 9. 14..
 */

public class EpgChoiceActivity extends Activity {
    private static final String                 tag = EpgChoiceActivity.class.getSimpleName();
    private static       EpgChoiceActivity      mInstance;
    private              JYSharedPreferences    mPref;

    private              ImageButton            epg_choice_close_imageButton;

    private Button epg_choice_genre_all_button, epg_choice_genre_like_button, epg_choice_genre_one_button, epg_choice_genre_two_button, epg_choice_genre_three_button, epg_choice_genre_four_button, epg_choice_genre_five_button, epg_choice_genre_six_button, epg_choice_genre_seven_button, epg_choice_genre_eight_button, epg_choice_genre_nine_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_epg_choice);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        epg_choice_close_imageButton = (ImageButton) findViewById(R.id.epg_choice_close_imageButton);

        epg_choice_genre_all_button = (Button) findViewById(R.id.epg_choice_genre_all_button);
        epg_choice_genre_like_button = (Button) findViewById(R.id.epg_choice_genre_like_button);
        epg_choice_genre_one_button = (Button) findViewById(R.id.epg_choice_genre_one_button);
        epg_choice_genre_two_button = (Button) findViewById(R.id.epg_choice_genre_two_button);
        epg_choice_genre_three_button = (Button) findViewById(R.id.epg_choice_genre_three_button);
        epg_choice_genre_four_button = (Button) findViewById(R.id.epg_choice_genre_four_button);
        epg_choice_genre_five_button = (Button) findViewById(R.id.epg_choice_genre_five_button);
        epg_choice_genre_six_button = (Button) findViewById(R.id.epg_choice_genre_six_button);
        epg_choice_genre_seven_button = (Button) findViewById(R.id.epg_choice_genre_seven_button);
        epg_choice_genre_eight_button = (Button) findViewById(R.id.epg_choice_genre_eight_button);
        epg_choice_genre_nine_button = (Button) findViewById(R.id.epg_choice_genre_nine_button);

        epg_choice_close_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_close_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_two_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_three_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_four_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_five_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_six_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_seven_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_eight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_nine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
