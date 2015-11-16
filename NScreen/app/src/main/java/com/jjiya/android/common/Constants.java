package com.jjiya.android.common;

/**
 * Created by swlim on 2015. 5. 26..
 */
public class Constants {

    public static final String SERVER_URL_AIRCODE_REAL = "http://58.141.255.69:8080/nscreen";

    // 럼퍼스(PVR,검색)
    public static final String SERVER_URL_RUMPUS_PUBLIC  = "http://58.141.255.80/smapplicationserver";
    public static final String SERVER_URL_RUMPUS_VPN     = "http://192.168.44.10/smapplicationserver";

    // (VOD)
    public static final String SERVER_URL_CASTIS_PUBLIC  = "http://58.141.255.79:8080/HApplicationServer";
    public static final String SERVER_URL_CASTIS_VPN     = "http://192.168.40.5:8080/HApplicationServer";


    public static final String CODE_WEBHAS_OK = "100";
    public static final String CODE_RUMPUS_OK = "100";

    public static final String CODE_RUMPUS_ERROR_205_Not_Found          = "205";    // 예약녹화물이 없을때 내려오는 응답코드.
    public static final String CODE_RUMPUS_ERROR_205_Not_Found_authCode = "205";    // 셋탑의 인증번호를 폰에서 틀리게 올렸을대 내려오는 응답코드.

    // 영화 27282, 애니 27281, tv다시보기 27279, 성인 20912
//    public static final String CATEGORY_ID_MOVIE = "27282";
//    public static final String CATEGORY_ID_ANI   = "27281";
//    public static final String CATEGORY_ID_TV    = "27279";
//    public static final String CATEGORY_ID_ADULT = "20912";
//    public static final String CATEGORY_ID_MOVIE = "1579721"; // 11/12일 전달받은 모바일전용카테고리
//    public static final String CATEGORY_ID_ANI   = "1579722"; // 11/12일 전달받은 모바일전용카테고리
//    public static final String CATEGORY_ID_TV    = "1579723"; // 11/12일 전달받은 모바일전용카테고리
//    public static final String CATEGORY_ID_ADULT = "1579724"; // 11/12일 전달받은 모바일전용카테고리
    public static final String CATEGORY_ID_TAB2 = "CATEGORY_ID_TAB2";
    public static final String CATEGORY_ID_TAB3 = "CATEGORY_ID_TAB3";
    public static final String CATEGORY_ID_TAB4 = "CATEGORY_ID_TAB4";
    public static final String CATEGORY_ID_TAB5 = "CATEGORY_ID_TAB5";
}
