package com.stvn.nscreen.setting;

import android.content.Context;

import com.jjiya.android.common.CMConstants;
import com.jjiya.android.common.JYSharedPreferences;

/**
 * Created by kimwoodam on 2015. 10. 9..
 */
public class CMSettingData {
    private final static CMSettingData mInstance = new CMSettingData();
    private CMSettingData() {
    }

    public static CMSettingData getInstance() {
        return mInstance;
    }

//    public static final String ADULT_AUTH_CHECK_KEY = "ADULT_AUTH_CHECK"; // 성인인증 여부
//    public static final String ADULT_SEARCH_RESTRICTION_KEY = "ADULT_AUTH_CHECK"; // 성인검색 제한 설정
    /**
     * 성인인증 여부 반환
     * */
    public boolean isAdultAuth(Context context) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        return preferences.getValue(CMConstants.ADULT_AUTH_CHECK_KEY, false);
    }

    /**
     * 성인인증 여부 설정
     * */
    public void setAdultAuth(Context context, boolean isAuth) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        preferences.put(CMConstants.ADULT_AUTH_CHECK_KEY, isAuth);
    }

    /**
     * 성인검색 제한 설정 여부 반환 (default:true)
     * */
    public boolean getAdultSearchRestriction(Context context) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        return preferences.getValue(CMConstants.ADULT_SEARCH_RESTRICTION_KEY, true);
    }

    /**
     * 성인검색 제한 여부 설정
     * */
    public void setAdultSearchRestriction(Context context, boolean isOn) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        preferences.put(CMConstants.ADULT_SEARCH_RESTRICTION_KEY, isOn);
    }

}
