package com.jjiya.android.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.stvn.nscreen.bean.WishObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by swlim on 2015. 5. 26..
 */
public class JYSharedPreferences {

    static Context mContext;

    // Realm
    private Realm mRealm;

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

    public final static String RUMPERS_TERMINAL_KEY = "MjAxMS0wNC0xNl8yMTk0NDY4Nl9Dbk1UZXN0QXBwXyAg";   // 고정키값. 모든 앱이 같은 값을 사용 함.
    public final static String RUMPERS_SETOPBOX_KIND = "RUMPERS_SETOPBOX_KIND";

    // Public TerminalKey = 8A5D2E45D3874824FF23EC97F78D358
    // Private terminalKey = C5E6DBF75F13A2C1D5B2EFDB2BC940
    public final static String WEBHAS_PUBLIC_TERMINAL_KEY = "8A5D2E45D3874824FF23EC97F78D358";
    public final static String WEBHAS_PRIVATE_TERMINAL_KEY = "WEBHAS_PRIVATE_TERMINAL_KEY"; // 폰마다 다른 키값.
    public final static String PURCHASE_PASSWORD = "PURCHASE_PASSWORD"; // 구매비밀번호.

    public JYSharedPreferences(Context c) {
        mContext = c;
        mRealm   = Realm.getInstance(c);
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

    /**
     * private키가 있다면 privatekey를 사용. 없다면, publickey 사용.
     * @return
     */
    public String getWebhasTerminalKey() {
        StringBuffer sb = new StringBuffer();
        String sWebhasTerminalKey = getValue(WEBHAS_PRIVATE_TERMINAL_KEY,"");
        if ( "".equals(sWebhasTerminalKey) ) {
            sWebhasTerminalKey = WEBHAS_PUBLIC_TERMINAL_KEY;
        }
        sb.append(sWebhasTerminalKey);
        return sb.toString();
    }

//    private void writeToFile(String data) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }

    public String getAircodeServerUrl() {
        return Constants.SERVER_URL_AIRCODE_REAL;
    }

    public String getWebhasServerUrl() {
        // return Constants.SERVER_URL_CASTIS_VPN;         // 삼성C&M 사무실에서는 캐스트이즈 서버에 이걸로 접속.
        return Constants.SERVER_URL_CASTIS_PUBLIC;         // 삼성C&M을 제외한 장소에서는 캐스트이즈 서버에 이걸로 접속.
    }

    public String getRumpersServerUrl() {
        //return Constants.SERVER_URL_RUMPUS_VPN;       // 삼성C&M 사무실에서는 럼퍼스 서버에 이걸로 접속.
        return Constants.SERVER_URL_RUMPUS_PUBLIC; // 삼성C&M을 제외한 장소에서는 럼퍼스 서버에 이걸로 접속.
    }

    /**
     * 페어링 한적이 있기? 없기?
     * 캐스트이즈 private terminal key 가 있는지 여부로 판단한다.
     * @return  있지. 없지.
     */
    public boolean isPairingCompleted() {
        if ( "".equals(getValue(WEBHAS_PRIVATE_TERMINAL_KEY, "")) ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isWishAsset(String assetId) {
        RealmResults<WishObject> results = mRealm.where(WishObject.class).equalTo("sAssetId", assetId).findAll();
        if ( results.size() > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * getWishList로 전체 찜 목록을 가져왔을때 호출되는 함수.
     * 기존의 Wish들을 삭제하고, 파라메터의 어레이로 다시 채운다.
     * @param arr
     */
    public void setAllWishList(JSONArray arr) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);

        // remove all
        realm.beginTransaction();
        realm.allObjects(WishObject.class).clear();
        for ( int i = 0; i < arr.length(); i++ ) {
            try {
                JSONObject jo      = arr.getJSONObject(i);
                JSONObject asset   = jo.getJSONObject("asset");
                String     assetId = asset.getString("assetId");
                // 객체를 생성하거나 데이터를 수정하는 작업을 수행한다.
                WishObject wish = realm.createObject(WishObject.class); // Create a new object
                wish.setsAssetId(assetId);
                wish.setsPhoneNumber(jo.getString("phoneNumber"));
                wish.setsUserId(jo.getString("userId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 트랜잭션 종료
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }
}
