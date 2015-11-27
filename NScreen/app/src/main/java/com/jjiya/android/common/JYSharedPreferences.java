package com.jjiya.android.common;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.jjiya.android.common.crypt.AESCrypt;
import com.stvn.nscreen.LoadingActivity;
import com.stvn.nscreen.bean.BookmarkChannelObject;
import com.stvn.nscreen.bean.MainCategoryObject;
import com.stvn.nscreen.bean.WatchTvObject;
import com.stvn.nscreen.bean.WatchVodObject;
import com.stvn.nscreen.bean.WishObject;
import com.stvn.nscreen.vod.VodMainBaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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


    public final static String I_AM_ADULT   = "I_AM_ADULT";
    public final static String APP_VERSION_FOR_SERVER = "APP_VERSION_FOR_SERVER";

    public final static String RUMPERS_TERMINAL_KEY  = "MjAxMS0wNC0xNl8yMTk0NDY4Nl9Dbk1UZXN0QXBwXyAg";   // 고정키값. 모든 앱이 같은 값을 사용 함.
    public final static String RUMPERS_SETOPBOX_KIND = "RUMPERS_SETOPBOX_KIND";

    // Public TerminalKey = 8A5D2E45D3874824FF23EC97F78D358
    // Private terminalKey = C5E6DBF75F13A2C1D5B2EFDB2BC940
    public final static String WEBHAS_PUBLIC_TERMINAL_KEY  = "A9D0D3B07231F38878AB0979D7C315A";
    public final static String WEBHAS_PRIVATE_TERMINAL_KEY = "WEBHAS_PRIVATE_TERMINAL_KEY"; // 폰마다 다른 키값.
    public final static String PURCHASE_PASSWORD           = "PURCHASE_PASSWORD"; // 구매비밀번호.
    public final static String VOD_OTHER_TAB_CATEGORY_ID   = "VOD_OTHER_TAB_CATEGORY_ID";


    public JYSharedPreferences(Context c) {
        mContext = c;
        //RealmConfiguration config = new RealmConfiguration.Builder(mContext).build();
        //Realm.deleteRealm(config);
        mRealm = Realm.getInstance(c);
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
     * 서버로부터 받은 버젼.
     * @param ver
     */
    public void setAppVersionForServer(String ver){
        put(APP_VERSION_FOR_SERVER, ver);
    }

    public String getAppVersionForServer() {
        String ver = getValue(APP_VERSION_FOR_SERVER, "");
        return ver;
    }

    /**
     * 앱으로부터 알아낸 버젼.
     */
    public String getAppVersionForApp() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void makeUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        put(UUID, uuid.toString());
        writePairingInfoToPhone();
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
     * 폰마다 다른 암/복호화 키를 만들기 위한 함수.
     * 안드로이드 아이디를 넘기면, 32자리 문자열을 만든다.
     * @param ANDROID_ID
     * @return
     */
    private String makeKey(String ANDROID_ID){
        String etc = "ilovejiyoonjiwoonyounghee!!!!!!!";
        StringBuffer sb = new StringBuffer();
        sb.append(ANDROID_ID);
        int posi = 0;
        while ( sb.length() < 32 ) {
            sb.append(etc.charAt(posi));
            posi++;
        }
        return sb.toString();
    }

    /**
     * 페어링 정보를 폰에 저장한다.
     * 주의. 만약 본 메소드를 수정해야 한다면, readPairingInfoFromPhone() 메소드와 쌍으로 같이 맞쳐줘야 한다.
     */
    public void writePairingInfoToPhone() {
        try {
            String uuid  = getValue(UUID,"");
            String tk    = getValue(WEBHAS_PRIVATE_TERMINAL_KEY,"");
            String pwd   = getValue(PURCHASE_PASSWORD,"");
            String stb   = getValue(RUMPERS_SETOPBOX_KIND,"");
            String adult = getValue(I_AM_ADULT,"");

            JSONObject root = new JSONObject();
            root.put(UUID, uuid);
            root.put(WEBHAS_PRIVATE_TERMINAL_KEY, tk);
            root.put(PURCHASE_PASSWORD, pwd);
            root.put(RUMPERS_SETOPBOX_KIND, stb);
            root.put(I_AM_ADULT, adult);

            String jstr = root.toString();

            String androidId    = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String key          = makeKey(androidId);
            AESCrypt crypt      = new AESCrypt(key);
            String encryptedStr = crypt.encrypt(jstr);

            File cnmdir = new File(Environment.getExternalStorageDirectory(), "/.cnm"); // /storage/emulated/0
            if ( !cnmdir.exists() ) {
                cnmdir.mkdir();
            }
            File file = new File(cnmdir, ".nscreen");
            if ( !file.exists() ) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false /*append*/));
            writer.write(encryptedStr);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 페어링 정보를 폰에서 읽어 온다.
     */
    public void readPairingInfoFromPhone() {
        try {
            File cnmdir = new File(Environment.getExternalStorageDirectory(), "/.cnm"); // /storage/emulated/0
            File file = new File(cnmdir, ".nscreen");
            FileInputStream fis = new FileInputStream(file);
            int readCount = (int)file.length();
            byte[] buffer = new byte[readCount];
            fis.read(buffer);
            fis.close();

            String readStr      = new String(buffer, "UTF-8");
            String androidId    = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String key          = makeKey(androidId);
            AESCrypt crypt      = new AESCrypt(key);
            String decryptedStr = crypt.decrypt(readStr);

            JSONObject jo = new JSONObject(decryptedStr);
            String uuid   = jo.getString(UUID);
            String tk     = jo.getString(WEBHAS_PRIVATE_TERMINAL_KEY);
            String pwd    = jo.getString(PURCHASE_PASSWORD);
            String stb    = jo.getString(RUMPERS_SETOPBOX_KIND);
            String adult  = jo.getString(I_AM_ADULT);

            put(UUID, uuid);
            put(WEBHAS_PRIVATE_TERMINAL_KEY, tk);
            put(PURCHASE_PASSWORD, pwd);
            put(RUMPERS_SETOPBOX_KIND, stb);
            put(I_AM_ADULT, adult);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 테스트로 사용자 정보를 저장한다.
     */
    public void testWritePairingInfoToPhone() {
        try {
            String uuid  = "868c95ad-6e5d-4266-b11b-d26d90a9e8d1";
            String tk    = "97CF2C2DC48840F7833EDA283F232357";
            String pwd   = "1234";
            String stb   = "PVR";
            String adult = "I_AM_ADULT";

            JSONObject root = new JSONObject();
            root.put(UUID, uuid);
            root.put(WEBHAS_PRIVATE_TERMINAL_KEY, tk);
            root.put(PURCHASE_PASSWORD, pwd);
            root.put(RUMPERS_SETOPBOX_KIND, stb);
            root.put(I_AM_ADULT, adult);

            String jstr = root.toString();

            String androidId    = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String key          = makeKey(androidId);
            AESCrypt crypt      = new AESCrypt(key);
            String encryptedStr = crypt.encrypt(jstr);

            File cnmdir = new File(Environment.getExternalStorageDirectory(), "/.cnm"); // /storage/emulated/0
            if ( !cnmdir.exists() ) {
                cnmdir.mkdir();
            }
            File file = new File(cnmdir, ".nscreen");
            if ( !file.exists() ) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false /*append*/));
            writer.write(encryptedStr);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 페어링 정보 날리기.
     * LoadingActivity의 requestGetAppInitialize()에서 응답중에 SetTopBoxKind 값이 없으면 이넘을 호출해서
     * 페어링 정보를 날려버린다.
     */
    public void removePairingInfo(){
        put(UUID, "");
        put(RUMPERS_SETOPBOX_KIND, ""); // 셋탑종류
        put(JYSharedPreferences.WEBHAS_PRIVATE_TERMINAL_KEY, ""); // 터미널키 저장.
        put(JYSharedPreferences.PURCHASE_PASSWORD, "");     // 구매비번 저장.
        put(I_AM_ADULT,""); // 성인인증
        writePairingInfoToPhone();

        makeUUID();   // 페어링 정보 날린다음 uuid가 없다. 그래서 uuid 새로 만든다.
        writePairingInfoToPhone();
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

    /**
     * 성인인증 받으면 호출해야 되는 메소드
     */
    public void setIAmAdult() {
        put(I_AM_ADULT, I_AM_ADULT);
        writePairingInfoToPhone();
    }

    public void removeIAmAdult() {
        put(I_AM_ADULT, "");
        writePairingInfoToPhone();
    }

    public boolean isAdultVerification() {
        if ( getValue(I_AM_ADULT, "").equals("") ) {
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
                RealmResults<WishObject> results = mRealm.where(WishObject.class).equalTo("sAssetId", assetId).findAll();
                if ( results.size() > 0 ) {
                    // skip
                } else {
                    // 객체를 생성하거나 데이터를 수정하는 작업을 수행한다.
                    WishObject wish = realm.createObject(WishObject.class); // Create a new object
                    wish.setsAssetId(assetId);
                    wish.setsPhoneNumber(jo.getString("phoneNumber"));
                    wish.setsUserId(jo.getString("userId"));
                }
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

    public ArrayList<JSONObject> getAllBookmarkChannelObject() {
        ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
        RealmResults<BookmarkChannelObject> results = mRealm.where(BookmarkChannelObject.class).findAll();
        try {
            for (int i = 0; i < results.size(); i++) {
                BookmarkChannelObject obj = results.get(i);
                JSONObject            jo  = new JSONObject();

                jo.put("sChannelId",     obj.getsChannelId());
                jo.put("sChannelName",    obj.getsChannelName());
                jo.put("sChannelNumber", obj.getsChannelNumber());

                arr.add(jo);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return arr;
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
        newObj1.setsSortNo(obj1.getsSortNo());
        newObj1.setsCategoryId(obj1.getsCategoryId());
        newObj1.setsCategoryType(obj1.getsCategoryType());
        newObj1.setsCategoryTitle(obj1.getsCategoryTitle());
        MainCategoryObject newObj2 = realm.createObject(MainCategoryObject.class); // Create a new object
        newObj2.setsSortNo(obj2.getsSortNo());
        newObj2.setsCategoryId(obj2.getsCategoryId());
        newObj2.setsCategoryType(obj2.getsCategoryType());
        newObj2.setsCategoryTitle(obj2.getsCategoryTitle());
        MainCategoryObject newObj3 = realm.createObject(MainCategoryObject.class); // Create a new object
        newObj3.setsSortNo(obj3.getsSortNo());
        newObj3.setsCategoryId(obj3.getsCategoryId());
        newObj3.setsCategoryType(obj3.getsCategoryType());
        newObj3.setsCategoryTitle(obj3.getsCategoryTitle());
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }

    public MainCategoryObject getMainCategoryObject(int i) {
        String sSortNo = String.valueOf(i);
        RealmResults<MainCategoryObject> results = mRealm.where(MainCategoryObject.class).equalTo("sSortNo", sSortNo).findAll();
        if ( results.size() > 0 ) {
            return results.get(0);
        } else {
            return null;
        }
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

    /***********************************************************************************************
     * VOD시청
     */
    public void addWatchVod(Date watchDate, String assetId, String title) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        RealmResults<WatchVodObject> results = realm.where(WatchVodObject.class).findAll();
        long iSeq = 0;
        if ( results == null ) {
            //
        } else {
            if ( results.size() == 0 ) {
                iSeq = 0;
            } else {
                iSeq = realm.where(WatchVodObject.class).maximumInt("iSeq") + 1;
            }
        }

        WatchVodObject obj = new WatchVodObject();
        obj.setiSeq((int)iSeq);
        obj.setdDate(watchDate);
        obj.setsAssetId(assetId);
        obj.setsTitle(title);
        realm.beginTransaction();
        WatchVodObject obj2 = realm.copyToRealm(obj);
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }

    public void removeWatchVod(int iSeq) {
        // Realm Database **********************************************************************
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(mContext);
        realm.beginTransaction();
        RealmResults<WatchVodObject> results = mRealm.where(WatchVodObject.class).equalTo("iSeq", iSeq).findAll();
        //RealmResults<WatchVodObject> results = mRealm.where(WatchVodObject.class).findAll();
        if ( results.size() > 0 ) {
            WatchVodObject obj = results.get(0);
            obj.removeFromRealm();
        } else {
            //
        }
        realm.commitTransaction();
        // Realm Database **********************************************************************
    }


    public ArrayList<JSONObject> getAllWatchVodObject() {
        ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
        RealmResults<WatchVodObject> results = mRealm.where(WatchVodObject.class).findAll();
        try {
            int loop = results.size()-1;
            SimpleDateFormat sd    = new SimpleDateFormat("MM.dd (E),HH:mm");
            for (int i = 0; i < results.size(); i++) {
                WatchVodObject  obj    = results.get(loop);
                JSONObject       jo    = new JSONObject();

                String           sDate = sd.format(obj.getdDate()).toString();
                jo.put("iSeq", obj.getiSeq());
                jo.put("sDate", sDate);
                jo.put("sAssetId", obj.getsAssetId());
                jo.put("sTitle", obj.getsTitle());
                arr.add(jo);
                loop--;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return arr;
    }
}

