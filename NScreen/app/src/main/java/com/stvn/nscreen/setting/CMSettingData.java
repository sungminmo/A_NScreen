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

    /**
     * 사용자 설정 지역 코드 정보 반환
     * */
    public String getUserAreaCode(Context context) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        return preferences.getValue(CMConstants.USER_REGION_CODE_KEY, "");
    }

    /**
     * 사용자 설정 지역 코드 정보 저장
     * */
    public void setUserAreaCode(Context context, String areaCode) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        preferences.put(CMConstants.USER_REGION_CODE_KEY, areaCode);
    }

    public String getUserAreaName(Context context) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        return preferences.getValue(CMConstants.USER_REGION_NAME_KEY, "");
    }
    /**
     * 사용자 설정 지역 코드 정보 저장
     * */
    public void setUserAreaName(Context context, String areaCode) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        preferences.put(CMConstants.USER_REGION_NAME_KEY, areaCode);
    }

    /**
     * 구매인증 비밀번호 반환
     * */
    public String getPurchaseAuthPassword(Context context) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        return preferences.getValue(JYSharedPreferences.PURCHASE_PASSWORD, "");
    }

    /**
     * 구매인증 비밀번호 설정
     * */
    public void setPurchaseAuthPassword(Context context, String purchasePwd) {
        JYSharedPreferences preferences = new JYSharedPreferences(context);
        preferences.put(JYSharedPreferences.PURCHASE_PASSWORD, purchasePwd);
        preferences.writePairingInfoToPhone();
    }

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
