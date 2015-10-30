package com.stvn.nscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.pairing.PairingCheckActivity;
import com.stvn.nscreen.pairing.PairingMainActivity;
import com.stvn.nscreen.pairing.PairingSubActivity;
import com.stvn.nscreen.rmt.RemoteControllerActivity;
import com.stvn.nscreen.vod.VodBuyActivity;
import com.stvn.nscreen.vod.VodDetailActivity;

import java.util.UUID;

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

        String sWebhasTerminalKey = mPref.getWebhasTerminalKey();
        Log.d(tag, "sWebhasTerminalKey:"+sWebhasTerminalKey);

        if ( mPref.getValue(JYSharedPreferences.UUID, "").equals("") ) {
            // UUID 없으면 만들기.
            UUID uuid = UUID.randomUUID();
            mPref.put(JYSharedPreferences.UUID, uuid.toString());
        }

        Button Button1, Button8, Button10, Button11, Button12, Button13, Button14, Button15;

        Button1  = (Button) findViewById(R.id.Button1);
        Button8  = (Button) findViewById(R.id.Button8);
        Button10 = (Button) findViewById(R.id.Button10);
        Button11 = (Button) findViewById(R.id.Button11);
        Button12 = (Button) findViewById(R.id.Button12);
        Button13 = (Button) findViewById(R.id.Button13);
        Button14 = (Button) findViewById(R.id.Button14);
        Button15 = (Button) findViewById(R.id.Button15);

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.leftmenu.LeftMenuActivity.class);
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

        Button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodMainActivity.class);
                startActivity(i);
            }
        });

        Button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodDetailActivity.class);
                i.putExtra("assetId","www.hchoice.co.kr|M4154270LSG347422301");
                startActivity(i);
            }
        });

        Button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PairingMainActivity.class);
                startActivity(i);
            }
        });

        Button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PairingSubActivity.class);
                startActivity(i);
            }
        });

        Button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PairingCheckActivity.class);
                startActivity(i);
            }
        });

        Button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, VodBuyActivity.class);
                i.putExtra("assetId","www.hchoice.co.kr|M4154270LSG347422301");
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
