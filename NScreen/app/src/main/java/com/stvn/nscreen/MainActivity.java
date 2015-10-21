package com.stvn.nscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.vod.VodDetailActivity;

public class MainActivity extends AppCompatActivity {

    private static final String              tag = VodDetailActivity.class.getSimpleName();
    private static       MainActivity        mInstance;
    private              JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);

        /**
         * 테스트 터미널키 저장하기.
         */
        mPref.put(JYSharedPreferences.TERMINAL_KEY, "9CED3A20FB6A4D7FF35D1AC965F988D2");


        Button Button1, Button2, Button3, Button4, Button5, Button6, Button7, Button8;

        Button1 = (Button) findViewById(R.id.Button1);
        Button2 = (Button) findViewById(R.id.Button2);
        Button3 = (Button) findViewById(R.id.Button3);
        Button4 = (Button) findViewById(R.id.Button4);
        Button5 = (Button) findViewById(R.id.Button5);
        Button6 = (Button) findViewById(R.id.Button6);
        Button7 = (Button) findViewById(R.id.Button7);
        Button8 = (Button) findViewById(R.id.Button8);

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.epg.EpgChoiceActivity.class);
                startActivity(i);
            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.epg.EpgMainActivity.class);
                startActivity(i);
            }
        });

        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.epg.EpgSubActivity.class);
                startActivity(i);
            }
        });

        Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MyMainActivity.class);
                startActivity(i);
            }
        });

        Button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MySubActivity.class);
                startActivity(i);
            }
        });

        Button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrMainActivity.class);
                startActivity(i);
            }
        });

        Button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrSubActivity.class);
                startActivity(i);
            }
        });

        Button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.search.SearchMainActivity.class);
                startActivity(i);
            }
        });


        ((Button) findViewById(R.id.Button9)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.setting.CMSettingMainActivity.class);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.Button10)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodMainActivity.class);
                startActivity(i);
            }
        });

    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
