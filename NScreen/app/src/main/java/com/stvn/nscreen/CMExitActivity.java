package com.stvn.nscreen;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.stvn.nscreen.common.CMBaseActivity;

/**
 * Created by gim-udam on 16. 1. 11..
 */
public class CMExitActivity extends CMBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        moveTaskToBack(true);
//        finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);

        finishAffinity();
    }
}
