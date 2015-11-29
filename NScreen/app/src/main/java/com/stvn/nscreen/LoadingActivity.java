package com.stvn.nscreen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.crypt.AESCrypt;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.bean.MainCategoryObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoadingActivity extends AppCompatActivity {

    private static final String              tag = LoadingActivity.class.getSimpleName();
    public  static       LoadingActivity     mInstance;
    private              JYSharedPreferences mPref;

    // network
    private              RequestQueue        mRequestQueue;
    private              Map<String, Object> mGetAppInitialize;
    private              MainCategoryObject  mMainCategoryObject1;
    private              MainCategoryObject  mMainCategoryObject2;
    private              MainCategoryObject  mMainCategoryObject3;

    // ui
    private              ProgressBar         mProgressBar;
    private              TextView            mTextView;
    private              Button              mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        mInstance         = this;
        mPref             = new JYSharedPreferences(this);
        mRequestQueue     = Volley.newRequestQueue(this);
        mGetAppInitialize = new HashMap<String, Object>();
        mMainCategoryObject1 = new MainCategoryObject();
        mMainCategoryObject2 = new MainCategoryObject();
        mMainCategoryObject3 = new MainCategoryObject();

        mProgressBar  = (ProgressBar)findViewById(R.id.loading_progressbar);
        mTextView     = (TextView)findViewById(R.id.loading_textview);
        mButton       = (Button)findViewById(R.id.loading_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setVisibility(View.INVISIBLE);
                mProgressBar.setProgress(0);
                mProgressBar.setProgress(10);
                mTextView.setText("초기화 중입니다.");
                requestGetAppInitialize();
            }
        });

        mProgressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        if ( getIntent().getExtras() != null ) {
            String striSeq = getIntent().getExtras().getString("iSeq");
            int iSeqNoti = Integer.valueOf(striSeq);
            //NotificationManager notifier = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            //notifier.cancel(iSeqNoti);

            //NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            // Create a new intent which will be fired if you click on the notification
            //Intent intent = new Intent(this, ApplicationClass.class);
            // Attach the intent to a pending intent
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, iSeqNoti, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
            notificationManager.cancel(iSeqNoti);
        }

        // 10~20 : file io
        // 21~60 : requestGetAppInitialize
        // 61~100 : requestGetCategoryTreeForVodMain

        mProgressBar.setProgress(10);
        mTextView.setText("초기화 중입니다.");

        // test only
        // mPref.testWritePairingInfoToPhone();

        if ( mPref.getValue(JYSharedPreferences.UUID, "").equals("") ) {
            // 앱을 설치하고 처음 실행했다면, 여디로 들어온다.
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "/.cnm/.nscreen"); // /storage/emulated/0
                if ( file.exists() ) {  // 만약 .nscreen 파일이 있다면, 이전에 사용하다가 앱을 삭제하고 다시 설치한 경우다. 이경우는 자동 재연동 되도록 해준다.
                    mPref.readPairingInfoFromPhone();
                } else {
                    //
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //mPref.writePairingInfoToPhone();
        //mPref.readPairingInfoFromPhone();
        requestGetAppInitialize();
    }

    private void setUIFail(String msg) {
        mProgressBar.setProgress(0);
        mTextView.setText(msg);
        mButton.setVisibility(View.VISIBLE);
    }


    /**
     * // 10~20 : file io
     // 21~60 : requestGetAppInitialize
     // 61~100 : requestGetCategoryTreeForVodMain
     */
    private void requestGetAppInitialize() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetAppInitialize()"); }
        String url  = mPref.getRumpersServerUrl() + "/GetAppInitialize.asp?appType=A&appId="+mPref.getValue(JYSharedPreferences.UUID, "");
        mTextView.setText("AppInitialize...(R)");
        mProgressBar.setProgress(21);
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setProgress(30);
                parseGetAppInitialize(response);
                mProgressBar.setProgress(50);
                if ( Constants.CODE_RUMPUS_OK.equals(mGetAppInitialize.get("resultCode")) ) {
                    // ok
                    String SetTopBoxKind = (String)mGetAppInitialize.get("SetTopBoxKind");
                    if ( "".equals(SetTopBoxKind) ) {
                        // 셋탑박스 종류가 안내려왔으니, 페어링 정보를 초기화(제거)
                        mPref.removePairingInfo();
                    } else {
                        mPref.setSettopBoxKind(SetTopBoxKind);
                    }
                    // 메인 category 저장.
                    mPref.addMainCategory(mMainCategoryObject1, mMainCategoryObject2, mMainCategoryObject3);
                    // 서버로부터 받은 앱의 버젼 저장.
                    String appversion = (String)mGetAppInitialize.get("appversion");
                    mPref.setAppVersionForServer(appversion);

                    requestGetCategoryTreeForVodMain();
                } else {
                    String errorString = (String)mGetAppInitialize.get("errorString");
                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage(errorString);
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    setUIFail(mInstance.getString(R.string.error_network_timeout));
                } else if (error instanceof NoConnectionError) {
                    setUIFail(mInstance.getString(R.string.error_network_noconnectionerror));
                } else if (error instanceof ServerError) {
                    setUIFail(mInstance.getString(R.string.error_network_servererror));
                } else if (error instanceof NetworkError) {
                    setUIFail(mInstance.getString(R.string.error_network_networkerrorr));
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetAppInitialize(String response) {
        response = response.replace("<![CDATA[","");
        response = response.replace("]]>", "");
        XmlPullParserFactory factory = null;
        int iCategoryLoop = 0;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        String resultCode = xpp.nextText();
                        mGetAppInitialize.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mGetAppInitialize.put("errorString", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("appversion")) {
                        String appversion = xpp.nextText();
                        mGetAppInitialize.put("appversion", appversion);
                    } else if (xpp.getName().equalsIgnoreCase("SetTopBoxKind")) {
                        String SetTopBoxKind = xpp.nextText();
                        mGetAppInitialize.put("SetTopBoxKind", SetTopBoxKind);
                    } else if (xpp.getName().equalsIgnoreCase("categoryId")) {
                        String categoryId = xpp.nextText();
                        if ( iCategoryLoop == 0 ) {
                            mMainCategoryObject1.setsSortNo("1");
                            mMainCategoryObject1.setsCategoryId(categoryId);
                        } else if ( iCategoryLoop == 1 ) {
                            mMainCategoryObject2.setsSortNo("2");
                            mMainCategoryObject2.setsCategoryId(categoryId);
                        } else if ( iCategoryLoop == 2 ) {
                            mMainCategoryObject3.setsSortNo("3");
                            mMainCategoryObject3.setsCategoryId(categoryId);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("categorytype")) {
                        String categorytype = xpp.nextText();
                        if ( iCategoryLoop == 0 ) {
                            mMainCategoryObject1.setsCategoryType(categorytype);
                        } else if ( iCategoryLoop == 1 ) {
                            mMainCategoryObject2.setsCategoryType(categorytype);
                        } else if ( iCategoryLoop == 2 ) {
                            mMainCategoryObject3.setsCategoryType(categorytype);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("category_title")) {
                        String category_title = xpp.nextText();
                        if ( iCategoryLoop == 0 ) {
                            mMainCategoryObject1.setsCategoryTitle(category_title); iCategoryLoop++;
                        } else if ( iCategoryLoop == 1 ) {
                            mMainCategoryObject2.setsCategoryTitle(category_title); iCategoryLoop++;
                        } else if ( iCategoryLoop == 2 ) {
                            mMainCategoryObject3.setsCategoryTitle(category_title); iCategoryLoop++;
                        }
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mGetAppInitialize.put("errorString", errorString);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * VOD 메인 탭 5개 중에, 4개 받아오기.
     * http://58.141.255.79:8080/HApplicationServer/getCategoryTree.json?version=1&terminalKey=D049DBBA897611A7F6B6454471B5B6&categoryProfile=1&categoryId=0&depth=2&traverseType=DFS
     * /**
     * // 10~20 : file io
     // 21~60 : requestGetAppInitialize
     // 61~100 : requestGetCategoryTreeForVodMain
     */
    private void requestGetCategoryTreeForVodMain() {
        mProgressBar.setProgress(61);
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetCategoryTreeForVodMain()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = mPref.getWebhasServerUrl() + "/getCategoryTree.json?version=1&terminalKey="+terminalKey+"&&categoryProfile=1&categoryId=0&depth=2&traverseType=DFS";
        mTextView.setText("CategoryTree...(H)");
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setProgress(70);
                try {
                    JSONObject root    = new JSONObject(response);
                    String resultCode  = root.getString("resultCode");
                    if ( Constants.CODE_WEBHAS_OK.equals(resultCode) ) {
                        JSONArray categoryList  = root.getJSONArray("categoryList");
                        for ( int i = 0; i < categoryList.length(); i++ ) {
                            JSONObject jo = (JSONObject)categoryList.get(i);
                            String description = jo.getString("description");
                            if ( "mobileTV_01".equals(description) ) {
                                mPref.put(Constants.CATEGORY_ID_TAB2, jo.getString("categoryId"));
                            } else if ( "mobileTV_02".equals(description) ) {
                                mPref.put(Constants.CATEGORY_ID_TAB3, jo.getString("categoryId"));
                            } else if ( "mobileTV_03".equals(description) ) {
                                mPref.put(Constants.CATEGORY_ID_TAB4, jo.getString("categoryId"));
                            } else if ( "mobileTV_04".equals(description) ) {
                                mPref.put(Constants.CATEGORY_ID_TAB5, jo.getString("categoryId"));
                            }
                        }
                    } else {
                        //
                    }
                    mTextView.setText("초기화를 완료 했습니다.");
                    mProgressBar.setProgress(100);
                    Intent intent = new Intent(mInstance, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
                if (error instanceof TimeoutError) {
                    setUIFail(mInstance.getString(R.string.error_network_timeout));
                } else if (error instanceof NoConnectionError) {
                    setUIFail(mInstance.getString(R.string.error_network_noconnectionerror));
                } else if (error instanceof ServerError) {
                    setUIFail(mInstance.getString(R.string.error_network_servererror));
                } else if (error instanceof NetworkError) {
                    setUIFail(mInstance.getString(R.string.error_network_networkerrorr));
                }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
