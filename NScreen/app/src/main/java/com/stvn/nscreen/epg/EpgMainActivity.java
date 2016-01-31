package com.stvn.nscreen.epg;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.jjiya.android.common.CMConstants;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.bean.BookmarkChannelObject;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMLog;


import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpgMainActivity extends AppCompatActivity {

    private static final String                 tag = EpgMainActivity.class.getSimpleName();
    private static       EpgMainActivity        mInstance;
    private              JYSharedPreferences    mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;

    private              ArrayList<JSONObject>  mBookmarkChannels;
    private              String                 sGenreCode; // "&genreCode=0";  // 0은 원래 없는 코드. 그래서 0일 경우는 선호라고 로컬디비 사용.
    private              String                 sGenreName;

    // gui
    private              EpgMainListViewAdapter mAdapter;
    private              ListView               mListView;

    private              Map<String, Object>    mStbStateMap;
    private              String                 mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                 mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.

    private              ImageButton            epg_main_genre_choice_imageButton, epg_main_backBtn;

    private              TextView epg_main_genre_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg_main);

        if ( getIntent().getExtras() == null ) {
            sGenreName = "전체채널";
            sGenreCode = "";
        } else {
            sGenreName = getIntent().getExtras().getString("sGenreName");
            sGenreCode = getIntent().getExtras().getString("sGenreCode");
        }


        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mStbStateMap  = new HashMap<String, Object>();
        mBookmarkChannels = new ArrayList<JSONObject>();

        if ( mPref.isLogging() ) { Log.d(tag, "onCreate()"); }

        epg_main_genre_name = (TextView) findViewById(R.id.epg_main_genre_name);
        epg_main_genre_name.setText(sGenreName);

        mAdapter      = new EpgMainListViewAdapter(this, null);
        mAdapter.setGenreCode(this.sGenreCode);

        mListView     = (ListView)findViewById(R.id.epg_main_listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);


        epg_main_genre_choice_imageButton = (ImageButton) findViewById(R.id.epg_main_genre_choice_imageButton);
        epg_main_backBtn                  = (ImageButton) findViewById(R.id.epg_main_backBtn);



        epg_main_genre_choice_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EpgMainActivity.this, EpgChoiceActivity.class);
                intent.putExtra("GENRE_CODE", sGenreCode);
                startActivity(intent);
            }
        });

        epg_main_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        requestGetChannelList();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        reloadAll();
    }

    private void reloadAll() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        mStbStateMap.clear();
        mStbState = "";             // GetSetTopStatus API로 가져오는 값.
        mStbRecordingchannel1 = ""; // GetSetTopStatus API로 가져오는 값.
        mStbRecordingchannel2 = ""; // GetSetTopStatus API로 가져오는 값.
        mStbWatchingchannel = "";   // GetSetTopStatus API로 가져오는 값.
        mStbPipchannel = "";        // GetSetTopStatus API로 가져오는 값.



        requestGetSetTopStatus();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mAdapter.getItem(position);

            try {
                JSONObject jo              = new JSONObject(dobj.sJson);
                String     sChannelNumber  = jo.getString("channelNumber");
                String     sChannelId      = jo.getString("channelId");
                String     sChannelName    = jo.getString("channelName");
                String     sChannelLogoImg = jo.getString("channelLogoImg");

                Intent     intent          = new Intent(mInstance, EpgSubActivity.class);
                intent.putExtra("channelNumber", sChannelNumber);
                intent.putExtra("channelName", sChannelName);
                intent.putExtra("channelLogoImg", sChannelLogoImg);
                intent.putExtra("channelId", sChannelId);

                startActivityForResult(intent, 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private void requestGetChannelList() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelList()"); }
        // mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));

        // -----------------------------------------------------------------------------------------
        // 선호채널의 경우는 내부 디비 처리. sGenreCode; // "&genreCode=0"
//        if ( "&genreCode=0".equals(sGenreCode) ) {
//            mBookmarkChannels.clear();
//            mBookmarkChannels = mPref.getAllBookmarkChannelObject();
//            for ( int i = 0; i< mBookmarkChannels.size(); i++ ) {
//                JSONObject jo = mBookmarkChannels.get(i);
//                ListViewDataObject obj = new ListViewDataObject(i, i, jo.toString());
//                mAdapter.addItem(obj);
//            }
//            mAdapter.notifyDataSetChanged();
//            return;
//        }

        // -----------------------------------------------------------------------------------------
        // 선호채널일 경우 아니면 전체채널 조회를 하여 필터링한다
        String sGenreCode2 = sGenreCode;
        if ( "&genreCode=0".equals(sGenreCode) ) {
            sGenreCode2 = "";
        }
        String url = mPref.getAircodeServerUrl() + "/getChannelList.xml?version=1&areaCode=" + mPref.getValue(CMConstants.USER_REGION_CODE_KEY, "17") + sGenreCode2 + "&noCache=";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CMLog.d("채널 목록 수신");
                parseGetChannelList(response);
                mAdapter.notifyDataSetChanged();
                CMLog.d("채널 목록 리스트 갱신");

                // 구글셋탑이 아닌경우에만 셋탑 상태에 대하여 조회 한다.
                if ( "HD".equals(mPref.getSettopBoxKind()) || "PVR".equals(mPref.getSettopBoxKind()) ) {
                    requestGetSetTopStatus();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mProgressDialog.dismiss();
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

    private void parseGetChannelList(String response) {
        StringBuilder sb = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            String channelId = "";
            String channelNumber = "";
            String channelName = "";
            String channelProgramOnAirTitle = "";
            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("channelId")) {
                        channelId = xpp.nextText();
                        sb.append("{\"channelId\":\"").append(channelId).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelNumber")) {
                        channelNumber = xpp.nextText();
                        sb.append(",\"channelNumber\":\"").append(channelNumber).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelName")) {
                        channelName = xpp.nextText();
                        sb.append(",\"channelName\":\"").append(channelName).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirTitle")) {
                        channelProgramOnAirTitle = xpp.nextText();
                        sb.append(",\"channelProgramOnAirTitle\":\"").append(channelProgramOnAirTitle).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelInfo")) {
                        sb.append(",\"channelInfo\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelOnAirHD")) {
                        sb.append(",\"channelOnAirHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelLogoImg")) {
                        sb.append(",\"channelLogoImg\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirID")) {
                        sb.append(",\"channelProgramOnAirID\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirStartTime")) {
                        sb.append(",\"channelProgramOnAirStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirEndTime")) {
                        sb.append(",\"channelProgramOnAirEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramGrade")) {
                        sb.append(",\"channelProgramGrade\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelView")) {
                        sb.append(",\"channelView\":\"").append(xpp.nextText()).append("\"}");
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, sb.toString());
                        mAdapter.addItem(obj);
                        sb.setLength(0);
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

        if ( "&genreCode=0".equals(sGenreCode) ) {
            ArrayList<String> bookmarks = new ArrayList<String>();
            for ( int i = 0; i < mAdapter.getCount(); i++ ) {
                ListViewDataObject obj = (ListViewDataObject)mAdapter.getItem(i);
                try {
                    JSONObject jo = new JSONObject(obj.sJson);
                    String channelId                = jo.getString("channelId");
                    String channelNumber            = jo.getString("channelNumber");
                    String channelName              = jo.getString("channelName");
                    String channelProgramOnAirTitle = jo.getString("channelProgramOnAirTitle");
                    //Log.d(tag, "channelId: "+channelId+", channelNumber:"+channelNumber+", channelName:"+channelName+ ", channelProgramOnAirTitle:"+channelProgramOnAirTitle+", bookmark:"+mPref.isBookmarkChannelWithChannelId(channelId));
                    if ( mPref.isBookmarkChannelWithChannelNumber(channelNumber) == true ) {
                        bookmarks.add(obj.sJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.mDatas.clear();
            for ( int i = 0; i < bookmarks.size(); i++ ) {
                //Log.d(tag, "bookmarks :"+bookmarks.get(i));
                ListViewDataObject obj = new ListViewDataObject(i, i, bookmarks.get(i));
                mAdapter.addItem(obj);
            }

        }
    }

    // http://58.141.255.80/SMApplicationServer/GetSetTopStatus.asp?deviceId=86713f34-15f4-45ba-b1df-49b32b13d551
    // 7.3.40 GetSetTopStatus
    // 셋탑의 상태 확인용.
    private void requestGetSetTopStatus() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetSetTopStatus()"); }
        String uuid        = mPref.getValue(JYSharedPreferences.UUID, "");
        String url         = mPref.getRumpersServerUrl() + "/GetSetTopStatus.asp?deviceId="+uuid;
        CMLog.d("셋탑 상태 요청");
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CMLog.d("셋탑 상태 수신");
                parseGetSetTopStatus(response);

                String resultCode = (String) mStbStateMap.get("resultCode");

                if (UiUtil.checkSTBStateCode(resultCode, EpgMainActivity.this)) {
                    mStbState             = (String) mStbStateMap.get("state");
                    mStbRecordingchannel1 = (String) mStbStateMap.get("recordingchannel1");
                    mStbRecordingchannel2 = (String) mStbStateMap.get("recordingchannel2");
                    mStbWatchingchannel   = (String) mStbStateMap.get("watchingchannel");
                    mStbPipchannel        = (String) mStbStateMap.get("pipchannel");
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                } else {
                    if ( "241".equals(resultCode) ) { // 페어링 안한 놈은 이값의 응답을 받지만, 정상처리 해줘야 한다.
                        //
                        mStbState             = "";
                        mStbRecordingchannel1 = "";
                        mStbRecordingchannel2 = "";
                        mStbWatchingchannel   = "";
                        mStbPipchannel        = "";
                        mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    } else if ( "206".equals(resultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                        //
                        mStbState             = "";
                        mStbRecordingchannel1 = "";
                        mStbRecordingchannel2 = "";
                        mStbWatchingchannel   = "";
                        mStbPipchannel        = "";
                        mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    } else if ( "028".equals(resultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                        //
                        mStbState             = "";
                        mStbRecordingchannel1 = "";
                        mStbRecordingchannel2 = "";
                        mStbWatchingchannel   = "";
                        mStbPipchannel        = "";
                        mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    } else if ( "SMART".equals(mPref.getSettopBoxKind()) ) { // SMART
                        //
                        mStbState             = "";
                        mStbRecordingchannel1 = "";
                        mStbRecordingchannel2 = "";
                        mStbWatchingchannel   = "";
                        mStbPipchannel        = "";
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_noconnectionerror), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_servererror), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mInstance, mInstance.getString(R.string.error_network_networkerrorr), Toast.LENGTH_LONG).show();
                }
                if ( mPref.isLogging() ) { VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage()); }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseGetSetTopStatus(String response) {
        XmlPullParserFactory factory = null;
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
                        mStbStateMap.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("errorString", errorString);

                    } else if (xpp.getName().equalsIgnoreCase("state")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("state", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel1")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("recordingchannel1", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel2")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("recordingchannel2", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("watchingchannel")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("watchingchannel", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("pipchannel")) {
                        String errorString = xpp.nextText();
                        mStbStateMap.put("pipchannel", errorString);
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
}
