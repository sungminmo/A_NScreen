package com.stvn.nscreen.rmt;

import android.app.Activity;
import android.content.Intent;
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

public class RemoteControllerChoiceActivity extends Activity {
    private static final String                 tag = RemoteControllerChoiceActivity.class.getSimpleName();
    private static RemoteControllerChoiceActivity mInstance;
    private              JYSharedPreferences    mPref;

    private              ImageButton            remote_controller_choice_close_imageButton;

    private              String                 sChannel;

    private              Button                 remote_controller_choice_genre_all_button, remote_controller_choice_genre_like_button, remote_controller_choice_genre_one_button, remote_controller_choice_genre_two_button, remote_controller_choice_genre_three_button, remote_controller_choice_genre_four_button, remote_controller_choice_genre_five_button, remote_controller_choice_genre_six_button, remote_controller_choice_genre_seven_button, remote_controller_choice_genre_eight_button, remote_controller_choice_genre_nine_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_remote_controller_choice);

        sChannel  = getIntent().getExtras().getString("Channel");

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        remote_controller_choice_close_imageButton  = (ImageButton) findViewById(R.id.remote_controller_choice_close_imageButton);

        remote_controller_choice_genre_all_button   = (Button) findViewById(R.id.remote_controller_choice_genre_all_button);
        remote_controller_choice_genre_like_button  = (Button) findViewById(R.id.remote_controller_choice_genre_like_button);
        remote_controller_choice_genre_one_button   = (Button) findViewById(R.id.remote_controller_choice_genre_one_button);
        remote_controller_choice_genre_two_button   = (Button) findViewById(R.id.remote_controller_choice_genre_two_button);
        remote_controller_choice_genre_three_button = (Button) findViewById(R.id.remote_controller_choice_genre_three_button);
        remote_controller_choice_genre_four_button  = (Button) findViewById(R.id.remote_controller_choice_genre_four_button);
        remote_controller_choice_genre_five_button  = (Button) findViewById(R.id.remote_controller_choice_genre_five_button);
        remote_controller_choice_genre_six_button   = (Button) findViewById(R.id.remote_controller_choice_genre_six_button);
        remote_controller_choice_genre_seven_button = (Button) findViewById(R.id.remote_controller_choice_genre_seven_button);
        remote_controller_choice_genre_eight_button = (Button) findViewById(R.id.remote_controller_choice_genre_eight_button);
        remote_controller_choice_genre_nine_button  = (Button) findViewById(R.id.remote_controller_choice_genre_nine_button);

        remote_controller_choice_genre_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "");
                intent.putExtra("sGenreName", "전체채널");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        remote_controller_choice_close_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        remote_controller_choice_genre_one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=1");
                intent.putExtra("sGenreName", "지상파/지역");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_two_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=2");
                intent.putExtra("sGenreName", "교육/키즈");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_three_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=3");
                intent.putExtra("sGenreName", "음악/오락");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_four_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=4");
                intent.putExtra("sGenreName", "스포츠/Game");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_five_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=5");
                intent.putExtra("sGenreName", "종교/기타");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_six_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=6");
                intent.putExtra("sGenreName", "뉴스/다큐");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_seven_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=7");
                intent.putExtra("sGenreName", "영화");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_eight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=8");
                intent.putExtra("sGenreName", "드라마/여성");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        remote_controller_choice_genre_nine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=9");
                intent.putExtra("sGenreName", "홈쇼핑");
                intent.putExtra("Channel", sChannel);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
