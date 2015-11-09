package com.jjiya.android.common;

/**
 * Created by kimwoodam on 2015. 9. 17..
 * 앱내 사용 될 상수에 대하여 정의한다.
 */
public class CMConstants {
    public static final String CM_SCHEMA = "http://schemas.android.com/apk/res/stvn.nscreen";
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static final String CM_CUSTOM_SCHEME = "cnmapp";

    public static final String CM_ADULT_AUTH_HOST = "adult_auth";
    public static final String CM_ADULT_AUTH_PARAM = "result";
    public static final String ADULT_AUTH_URL = "http://58.141.255.80/CheckPlusSafe_ASP/checkplus_main.asp";



    // 환경설정 내 Preference 사용 키
    public static final String USER_REGION_CODE_KEY = "USER_REGION_CODE";
    public static final String USER_REGION_NAME_KEY = "USER_REGION_NAME";
    public static final String PURCHASE_AUTH_PASSWORD_KEY = "PURCHASE_AUTH_PASSWORD";
    public static final String ADULT_AUTH_CHECK_KEY = "ADULT_AUTH_CHECK"; // 성인인증 여부
    public static final String ADULT_SEARCH_RESTRICTION_KEY = "ADULT_SEARCH_RESTRICTION"; // 성인검색 제한 설정
}
