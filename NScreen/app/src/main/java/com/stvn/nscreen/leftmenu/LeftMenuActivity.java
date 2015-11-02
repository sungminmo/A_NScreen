package com.stvn.nscreen.leftmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jjiya.android.common.JYSharedPreferences;
import com.stvn.nscreen.R;
import com.stvn.nscreen.epg.EpgMainActivity;
import com.stvn.nscreen.my.MyMainActivity;
import com.stvn.nscreen.pairing.PairingMainActivity;
import com.stvn.nscreen.pvr.PvrMainActivity;
import com.stvn.nscreen.rmt.RemoteControllerActivity;
import com.stvn.nscreen.setting.CMSettingMainActivity;

/**
 * Created by limdavid on 15. 10. 30..
 */

public class LeftMenuActivity extends Activity {
    private static final String                 tag = LeftMenuActivity.class.getSimpleName();
    private static       LeftMenuActivity       mInstance;
    private              JYSharedPreferences    mPref;

    private              LinearLayout           leftmenu_tv_linearLayout, leftmenu_remote_linearLayout, leftmenu_pvr_linearLayout, leftmenu_my_linearLayout, leftmenu_setting_linearLayout, leftmenu_right;

    private              Button                 leftmenu_pairing_button1, leftmenu_pairing_button2;

    private              ImageButton            imageButton2, leftmenu_whatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_leftmenu);

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.LEFT;

        mInstance = this;
        mPref = new JYSharedPreferences(this);
        if (mPref.isLogging()) {
            Log.d(tag, "onCreate()");
        }



        leftmenu_tv_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_tv_linearLayout);
        leftmenu_remote_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_remote_linearLayout);
        leftmenu_pvr_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_pvr_linearLayout);
        leftmenu_my_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_my_linearLayout);
        leftmenu_setting_linearLayout = (LinearLayout) findViewById(R.id.leftmenu_setting_linearLayout);
        leftmenu_right = (LinearLayout) findViewById(R.id.leftmenu_right);
        leftmenu_pairing_button1 = (Button) findViewById(R.id.leftmenu_pairing_button1);
        leftmenu_pairing_button2 = (Button) findViewById(R.id.leftmenu_pairing_button2);
        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        leftmenu_whatBtn = (ImageButton) findViewById(R.id.leftmenu_whatBtn);

        String pwd = mPref.getValue(JYSharedPreferences.PURCHASE_PASSWORD, "");

        if ( "".equals(pwd) ) {
            leftmenu_pairing_button1.setVisibility(View.VISIBLE);
            leftmenu_pairing_button2.setVisibility(View.GONE);
        } else {
            leftmenu_pairing_button1.setVisibility(View.GONE);
            leftmenu_pairing_button2.setVisibility(View.VISIBLE);
        }

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftmenu_pairing_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, PairingMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        leftmenu_pairing_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, PairingMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        leftmenu_tv_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, EpgMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        leftmenu_remote_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, RemoteControllerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        leftmenu_pvr_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, PvrMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        leftmenu_my_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, MyMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        leftmenu_setting_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, CMSettingMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        leftmenu_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftmenu_whatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, LeftMenuDialogActivity.class);
                startActivity(intent);
            }
        });
    }
}
