package com.stvn.nscreen.epg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;

import org.w3c.dom.Text;

/**
 * Created by limdavid on 15. 9. 14..
 */

public class EpgChoiceActivity extends Activity {
    private static final String                 tag = EpgChoiceActivity.class.getSimpleName();
    private static       EpgChoiceActivity      mInstance;
    private              JYSharedPreferences    mPref;

    private              ImageButton            epg_choice_close_imageButton;

    private              Button                 epg_choice_genre_all_button, epg_choice_genre_like_button, epg_choice_genre_one_button, epg_choice_genre_two_button, epg_choice_genre_three_button, epg_choice_genre_four_button, epg_choice_genre_five_button, epg_choice_genre_six_button, epg_choice_genre_seven_button, epg_choice_genre_eight_button, epg_choice_genre_nine_button;
    private              String                 sGenreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_epg_choice);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        epg_choice_close_imageButton  = (ImageButton) findViewById(R.id.epg_choice_close_imageButton);

        epg_choice_genre_all_button   = (Button) findViewById(R.id.epg_choice_genre_all_button);
        epg_choice_genre_like_button  = (Button) findViewById(R.id.epg_choice_genre_like_button);
        epg_choice_genre_one_button   = (Button) findViewById(R.id.epg_choice_genre_one_button);
        epg_choice_genre_two_button   = (Button) findViewById(R.id.epg_choice_genre_two_button);
        epg_choice_genre_three_button = (Button) findViewById(R.id.epg_choice_genre_three_button);
        epg_choice_genre_four_button  = (Button) findViewById(R.id.epg_choice_genre_four_button);
        epg_choice_genre_five_button  = (Button) findViewById(R.id.epg_choice_genre_five_button);
        epg_choice_genre_six_button   = (Button) findViewById(R.id.epg_choice_genre_six_button);
        epg_choice_genre_seven_button = (Button) findViewById(R.id.epg_choice_genre_seven_button);
        epg_choice_genre_eight_button = (Button) findViewById(R.id.epg_choice_genre_eight_button);
        epg_choice_genre_nine_button  = (Button) findViewById(R.id.epg_choice_genre_nine_button);

        Intent recvIntent = getIntent();
        if (recvIntent != null) {
            if (recvIntent.hasExtra("GENRE_CODE")) {
                String genreCode = recvIntent.getStringExtra("GENRE_CODE");
                if (TextUtils.isEmpty(genreCode)) {
                    epg_choice_genre_all_button.setSelected(true);
                } else if ("&genreCode=0".equals(genreCode)) {
                    epg_choice_genre_like_button.setSelected(true);
                } else if ("&genreCode=1".equals(genreCode)) {
                    epg_choice_genre_one_button.setSelected(true);
                } else if ("&genreCode=2".equals(genreCode)) {
                    epg_choice_genre_two_button.setSelected(true);
                } else if ("&genreCode=3".equals(genreCode)) {
                    epg_choice_genre_three_button.setSelected(true);
                } else if ("&genreCode=4".equals(genreCode)) {
                    epg_choice_genre_four_button.setSelected(true);
                } else if ("&genreCode=5".equals(genreCode)) {
                    epg_choice_genre_five_button.setSelected(true);
                } else if ("&genreCode=6".equals(genreCode)) {
                    epg_choice_genre_six_button.setSelected(true);
                } else if ("&genreCode=7".equals(genreCode)) {
                    epg_choice_genre_seven_button.setSelected(true);
                } else if ("&genreCode=8".equals(genreCode)) {
                    epg_choice_genre_eight_button.setSelected(true);
                } else if ("&genreCode=9".equals(genreCode)) {
                    epg_choice_genre_nine_button.setSelected(true);
                }
            }
        }

        epg_choice_genre_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "");
                intent.putExtra("sGenreName", "전체채널");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=0");  // 0은 원래 없는 코드. 그래서 0일 경우는 선호라고 로컬디비 사용.
                intent.putExtra("sGenreName", "선호채널");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_close_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        epg_choice_genre_one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=1");
                intent.putExtra("sGenreName", "지상파/지역");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_two_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=2");
                intent.putExtra("sGenreName", "교육/키즈");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_three_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=3");
                intent.putExtra("sGenreName", "음악/오락");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_four_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=4");
                intent.putExtra("sGenreName", "스포츠/Game");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_five_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=5");
                intent.putExtra("sGenreName", "종교/기타");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_six_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=6");
                intent.putExtra("sGenreName", "뉴스/다큐");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_seven_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=7");
                intent.putExtra("sGenreName", "영화");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_eight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=8");
                intent.putExtra("sGenreName", "드라마/여성");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        epg_choice_genre_nine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                intent.putExtra("sGenreCode", "&genreCode=9");
                intent.putExtra("sGenreName", "홈쇼핑");
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}