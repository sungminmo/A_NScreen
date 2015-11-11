package com.jjiya.android.common;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.stvn.nscreen.bean.BookmarkChannelObject;
import com.stvn.nscreen.bean.BaseCategoryObject;
import com.stvn.nscreen.bean.MainCategoryObject;
import com.stvn.nscreen.bean.WatchTvObject;
import com.stvn.nscreen.bean.WishObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
    public final static String VoVolunteerActivityIsCompletMode = "VoVolunteerActivityIsCompletMode"; // VoVolunteerActivityIsCompletMode : true, false


    public final static String RUMPERS_TERMINAL_KEY  = "MjAxMS0wNC0xNl8yMTk0NDY4Nl9Dbk1UZXN0QXBwXyAg";   // 고정키값. 모든 앱이 같은 값을 사용 함.
    public final static String RUMPERS_SETOPBOX_KIND = "RUMPERS_SETOPBOX_KIND";

    // Public TerminalKey = 8A5D2E45D3874824FF23EC97F78D358
    // Private terminalKey = C5E6DBF75F13A2C1D5B2EFDB2BC940
    public final static String WEBHAS_PUBLIC_TERMINAL_KEY  = "8A5D2E45D3874824FF23EC97F78D358";
    public final static String WEBHAS_PRIVATE_TERMINAL_KEY = "WEBHAS_PRIVATE_TERMINAL_KEY"; // 폰마다 다른 키값.
    public final static String PURCHASE_PASSWORD = "PURCHASE_PASSWORD"; // 구매비밀번호.

    public JYSharedPreferences(Context c) {
        mContext = c;

        //RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
        //Realm.deleteRealm(config);
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

    /**
     * 페어링완료후나 스플래쉬에서 받은 셋탑박스 종류를 저장한다.
     * @param SetTopBoxKind
     */
    public void setSettopBoxKind(String SetTopBoxKind) {
        put(RUMPERS_SETOPBOX_KIND, SetTopBoxKind);
    }

    /**
     * 셋탑박스 정보를 꺼내간다.
     * @return
     */
    public String getSettopBoxKind() {
        String rtn = getValue(RUMPERS_SETOPBOX_KIND, "");
        return rtn;
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

    /**
     * Local Alarm Noti
     */

    /**
     *
     * @param programId "S321387639"
     * @param seriesId ""
     * @param programTitle "국악무대"
     * @param programBroadcastingStartTime 2015-11-10 16:02:00"
     *
     * mPref.addWatchTvAlarm("S321387639", "", "국악무대1", "2015-11-10 18:50:00");
     * mPref.addWatchTvAlarm("S321387640", "", "국악무대2", "2015-11-10 18:51:00");
     * mPref.addWatchTvAlarm("S321387641", "", "국악무대3", "2015-11-10 18:52:00");
     * mPref.addWatchTvAlarm("S321387642", "", "국악무대4", "2015-11-10 18:53:00");
     * mPref.addWatchTvAlarm("S321387643", "", "국악무대5", "2015-11-10 18:54:00");
     *
     */
    public void addWatchTvReserveAlarm(String programId, String seriesId, String programTitle, String programBroadcastingStartTime) {

        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);

        // remove all
        realm.beginTransaction();
        WatchTvObject obj = realm.createObject(WatchTvObject.class); // Create a new object
        long iSeq = realm.where(WatchTvObject.class).maximumInt("iSeq") + 1;
        obj.setiSeq((int)iSeq);
        obj.setsScheduleSeq("");
        obj.setsBroadcastingDate("");
        obj.setsProgramId(programId);
        obj.setsSeriesId(seriesId);
        obj.setsProgramTitle(programTitle);
        obj.setsProgramContent("");
        obj.setsProgramBroadcastingStartTime(programBroadcastingStartTime);
        obj.setsProgramBroadcastingEndTime("");
        obj.setsProgramHD("");
        obj.setsProgramGrade("");
        obj.setsProgramPVR("");
        // 트랜잭션 종료
        realm.commitTransaction();
        // Realm Database **********************************************************************


        Date startDate = null;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = formatter.parse(programBroadcastingStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(startDate);


        Intent intent = new Intent(mContext, WatchTvAlarmBroadcastReceiver.class);
        intent.putExtra("programId",                    programId);
        intent.putExtra("seriesId",                     seriesId);
        intent.putExtra("programTitle",                 programTitle);
        intent.putExtra("programBroadcastingStartTime", programBroadcastingStartTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int)iSeq, intent, PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    public void removeWatchTvReserveAlarm(String programId) {
        int iSeq = 0;
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        RealmResults<WatchTvObject> results = mRealm.where(WatchTvObject.class).equalTo("sProgramId", programId).findAll();
        if ( results.size() > 0 ) {
            WatchTvObject obj = results.get(0);
            iSeq = obj.getiSeq();
            obj.removeFromRealm();
        } else {
            //
        }
        realm.commitTransaction();

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, WatchTvAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, iSeq, intent, PendingIntent.FLAG_NO_CREATE);
        if ( pendingIntent != null ) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    public boolean isWatchTvReserveWithProgramId(String programId) {
        RealmResults<WatchTvObject> results = mRealm.where(WatchTvObject.class).equalTo("sProgramId", programId).findAll();
        if ( results.size() > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 선호채널
     */
    public void addBookmarkChannel(String channelId, String channelNumber, String channelName) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        BookmarkChannelObject bm = realm.createObject(BookmarkChannelObject.class); // Create a new object
        bm.setsChannelId(channelId);
        bm.setsChannelNumber(channelNumber);
        bm.setsChannelName(channelName);
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }

    public void removeBookmarkChannelWithChannelId(String channelId) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        RealmResults<BookmarkChannelObject> results = mRealm.where(BookmarkChannelObject.class).equalTo("sChannelId", channelId).findAll();
        if ( results.size() > 0 ) {
            BookmarkChannelObject obj = results.get(0);
            obj.removeFromRealm();
        } else {
            //
        }
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }

    public boolean isBookmarkChannelWithChannelId(String channelId) {
        RealmResults<BookmarkChannelObject> results = mRealm.where(BookmarkChannelObject.class).equalTo("sChannelId", channelId).findAll();
        if ( results.size() > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 카테고리(메인)
     */
    public void addMainCategory(MainCategoryObject obj1, MainCategoryObject obj2, MainCategoryObject obj3) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        realm.allObjects(MainCategoryObject.class).clear();
        MainCategoryObject newObj1 = realm.createObject(MainCategoryObject.class); // Create a new object
        newObj1.setsCategoryId(obj1.getsCategoryId());
        newObj1.setsCategoryType(obj1.getsCategoryType());
        newObj1.setsCategoryTitle(obj1.getsCategoryTitle());
        MainCategoryObject newObj2 = realm.createObject(MainCategoryObject.class); // Create a new object
        newObj2.setsCategoryId(obj2.getsCategoryId());
        newObj2.setsCategoryType(obj2.getsCategoryType());
        newObj2.setsCategoryTitle(obj2.getsCategoryTitle());
        MainCategoryObject newObj3 = realm.createObject(MainCategoryObject.class); // Create a new object
        newObj3.setsCategoryId(obj3.getsCategoryId());
        newObj3.setsCategoryType(obj3.getsCategoryType());
        newObj3.setsCategoryTitle(obj3.getsCategoryTitle());
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }

    public void removeCategory(String channelId) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        RealmResults<BookmarkChannelObject> results = mRealm.where(BookmarkChannelObject.class).equalTo("sChannelId", channelId).findAll();
        if ( results.size() > 0 ) {
            BookmarkChannelObject obj = results.get(0);
            obj.removeFromRealm();
        } else {
            //
        }
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }
}

