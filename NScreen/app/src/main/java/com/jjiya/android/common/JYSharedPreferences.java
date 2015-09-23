package com.jjiya.android.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;



/**
 * Created by swlim on 2015. 5. 26..
 */
public class JYSharedPreferences {

    static Context mContext;

    private final boolean bLogging = true; // 개발시에는 true 로.
    //private final boolean bLogging = false; // release 시에는 false 로.

    private final boolean bRealServer = false; // 개발서버 접속시에는 false 로.
    //private final boolean bRealServer = true; // 개발서버 접속시에는 true 로.

    private              JYSharedPreferences    mPref;
    private final String PREF_NAME = "com.jjiya.pref";

    public final static String COOKIE       = "COOKIE";
    public final static String UUID         = "UUID";
    public final static String AUTO_LOGIN   = "AUTO_LOGIN";
    public final static String USER_ID      = "USER_ID";
    public final static String USER_PWD     = "USER_PWD";
    public final static String USER_TYPE    = "USER_TYPE";
    public final static String SERVER_CODE  = "SERVER_CODE";  // 기기등록시에 선택한 서버.
    public final static String LOGIN_SERVER = "LOGIN_SERVER"; // 로그인 화면에서 선택한 서버.
    public final static String VoVolunteerActivityIsCompletMode = "VoVolunteerActivityIsCompletMode"; // VoVolunteerActivityIsCompletMode : true, false


    public JYSharedPreferences(Context c) {
        mContext = c;
    }

    public boolean isLogging() {
        return bLogging;
    }

    public boolean isRealServer() {
        return bRealServer;
    }

    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public String getEpgServerUrl() {
        if ( isRealServer() ) {
            return Constants.SERVER_URL_EPG_REAL;
        } else {
            return Constants.SERVER_URL_EPG_DEV;
        }

    }
}
