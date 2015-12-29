package com.stvn.nscreen.rmt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 10. 26..
 */

public class RemoteControllerActivity extends AppCompatActivity{

    private static final String                          tag = RemoteControllerActivity.class.getSimpleName();
    private static       RemoteControllerActivity        mInstance;
    private              JYSharedPreferences             mPref;

    // network
    private              RequestQueue                    mRequestQueue;
    private              ProgressDialog                  mProgressDialog;
    private              Map<String, Object>             mNetworkError;
    private              Map<String, Object>             RemoteChannelControl;

    // STB status
    private              String                          mStbState;             // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel1; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbRecordingchannel2; // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbWatchingchannel;   // GetSetTopStatus API로 가져오는 값.
    private              String                          mStbPipchannel;        // GetSetTopStatus API로 가져오는 값.



    // gui
    private              RemoteControllerListViewAdapter mAdapter;
    private              ListView                        mListView;

    private              ImageButton                     remote_controller_genre_choice_imageButton, remote_controller_backBtn;

    private              TextView                        remote_controller_genre_name, remote_controller_channel_textview;
    private              String                          sChannel, sPower, sVolume, sGenreCode;
    private              Button                          remote_controller_power_button, remote_controller_volume_up_button, remote_controller_volume_down_button;
    private              LinearLayout                    channel1_linearlayout, channel2_linearlayout, channel3_linearlayout, channel4_linearlayout, channel5_linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_controller);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mNetworkError = new HashMap<String, Object>();
        RemoteChannelControl = new HashMap<String, Object>();

        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter = new RemoteControllerListViewAdapter(this, null);

        mListView = (ListView)findViewById(R.id.remote_controller_listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        remote_controller_genre_choice_imageButton = (ImageButton) findViewById(R.id.remote_controller_genre_choice_imageButton);
        remote_controller_backBtn                  = (ImageButton) findViewById(R.id.remote_controller_backBtn);

        remote_controller_genre_name               = (TextView) findViewById(R.id.remote_controller_genre_name);
        remote_controller_channel_textview         = (TextView) findViewById(R.id.remote_controller_channel_textview);

        remote_controller_power_button             = (Button) findViewById(R.id.remote_controller_power_button);
        remote_controller_volume_up_button         = (Button) findViewById(R.id.remote_controller_volume_up_button);
        remote_controller_volume_down_button       = (Button) findViewById(R.id.remote_controller_volume_down_button);

        channel1_linearlayout                      = (LinearLayout) findViewById(R.id.channel1_linearlayout);
        channel2_linearlayout                      = (LinearLayout) findViewById(R.id.channel2_linearlayout);
        channel3_linearlayout                      = (LinearLayout) findViewById(R.id.channel3_linearlayout);
        channel4_linearlayout                      = (LinearLayout) findViewById(R.id.channel4_linearlayout);
        channel5_linearlayout                      = (LinearLayout) findViewById(R.id.channel5_linearlayout);


        try {
            Intent recvIntent = getIntent();
            if (recvIntent != null && recvIntent.hasExtra("sGenreCode")) {
                this.sGenreCode = recvIntent.getStringExtra("sGenreCode");
            } else {
                this.sGenreCode = "";
            }

            sChannel = getIntent().getExtras().getString("Channel");
            remote_controller_channel_textview.setText(sChannel + "번");
            remote_controller_genre_name.setText(getIntent().getExtras().getString("sGenreName"));
        } catch (NullPointerException e) {
            sChannel = "";
            remote_controller_channel_textview.setText("번");
            remote_controller_genre_name.setText("전체채널");
        }

        remote_controller_genre_choice_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RemoteControllerActivity.this, RemoteControllerChoiceActivity.class);
                i.putExtra("Channel", sChannel);
                i.putExtra("StbState", mStbState);
                i.putExtra("GENRE_CODE", sGenreCode);
                startActivity(i);
            }
        });

        remote_controller_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestGetSetTopStatus();
        requestGetChannelList();

        remote_controller_power_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSetRemotePowerControl(sPower);
            }
        });

        remote_controller_volume_down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sVolume = "DN";
                requestSetRemoteVolumeControl(sVolume);
            }
        });

        remote_controller_volume_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sVolume = "UP";
                requestSetRemoteVolumeControl(sVolume);
            }
        });
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mAdapter.getItem(position);
            try {
                JSONObject jo = new JSONObject(dobj.sJson);
                String channelId = jo.getString("channelId");
                String channelNumber = jo.getString("channelNumber");
                requestSetRemoteChannelControl(channelId);

                sChannel = channelNumber;
                remote_controller_channel_textview.setText(sChannel + "번");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // http://58.141.255.80/SMApplicationServer/GetSetTopStatus.asp?deviceId=86713f34-15f4-45ba-b1df-49b32b13d551
    // 7.3.40 GetSetTopStatus
    // 셋탑의 상태 확인용.
    private void requestGetSetTopStatus() {
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetSetTopStatus()"); }
        String uuid        = mPref.getValue(JYSharedPreferences.UUID, "");
        String url         = mPref.getRumpersServerUrl() + "/GetSetTopStatus.asp?deviceId="+uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseGetSetTopStatus(response);

                String resultCode = (String) mNetworkError.get("resultCode");
                if ( Constants.CODE_RUMPUS_OK.equals(resultCode) ) {
                    //
                    mStbState             = (String)mNetworkError.get("state");
                    mStbRecordingchannel1 = (String)mNetworkError.get("recordingchannel1");
                    mStbRecordingchannel2 = (String)mNetworkError.get("recordingchannel2");
                    mStbWatchingchannel   = (String)mNetworkError.get("watchingchannel");
                    mStbPipchannel        = (String)mNetworkError.get("pipchannel");

                    if ( "1".equals(mStbState) ) { // VOD 시청중.
                        channel1_linearlayout.setVisibility(View.GONE);
                        channel2_linearlayout.setVisibility(View.VISIBLE);
//                        String alertTitle = "채널 변경";
//                        String alertMsg1  = getString(R.string.error_not_remote_control_case1);
//                        String alertMsg2  = "";
//                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }, true);
                    } else if ( "2".equals(mStbState) ) { // 독립형.
                        channel1_linearlayout.setVisibility(View.GONE);
                        channel3_linearlayout.setVisibility(View.VISIBLE);
//                        String alertTitle = "채널 변경";
//                        String alertMsg1  = getString(R.string.error_not_remote_control_case2);
//                        String alertMsg2  = "";
//                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }, true);
                    } else if ( "4".equals(mStbState) ) { // 셋탑박스 대기모드.
                        channel1_linearlayout.setVisibility(View.GONE);
                        channel4_linearlayout.setVisibility(View.VISIBLE);
                    } else if ( "6".equals(mStbState) ) { // 개인 미디어 시청중.
                        channel1_linearlayout.setVisibility(View.GONE);
                        channel5_linearlayout.setVisibility(View.VISIBLE);
//                        String alertTitle = "채널 변경";
//                        String alertMsg1  = getString(R.string.error_not_remote_control_case2);
//                        String alertMsg2  = "";
//                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }, true);
                    }

//                } else if ( Constants.CODE_RUMPUS_ERROR_205_Not_Found_authCode.equals(resultCode) ) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
//                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    alert.setMessage(getString(R.string.RUMPUS_ERROR_MSG_Not_Found_authCode));
//                    alert.show();
                } else if ( "241".equals(resultCode) ) { // 페어링 안한 놈은 이값의 응답을 받지만, 정상처리 해줘야 한다.
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
                    String alertTitle = "씨앤앰 모바일 TV";
                    String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "028".equals(resultCode) ) { // 셋탑박스의 전원을 off하면 이값의 응답을 받지만, 정상처리 해줘야 한다.
                    //
                    mStbState             = "";
                    mStbRecordingchannel1 = "";
                    mStbRecordingchannel2 = "";
                    mStbWatchingchannel   = "";
                    mStbPipchannel        = "";
                    mAdapter.setStbState(mStbState, mStbRecordingchannel1, mStbRecordingchannel2, mStbWatchingchannel, mStbPipchannel);
                    String alertTitle = "씨앤앰 모바일 TV";
                    String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else {
                    String errorString = (String)mNetworkError.get("errorString");
                    StringBuilder sb   = new StringBuilder();
                    sb.append("API: GetSetTopStatus\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                    alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage(sb.toString());
                    alert.show();
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
                        mNetworkError.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("errorString", errorString);

                    } else if (xpp.getName().equalsIgnoreCase("state")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("state", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel1")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("recordingchannel1", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("recordingchannel2")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("recordingchannel2", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("watchingchannel")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("watchingchannel", errorString);
                    } else if (xpp.getName().equalsIgnoreCase("pipchannel")) {
                        String errorString = xpp.nextText();
                        mNetworkError.put("pipchannel", errorString);
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


    private void requestSetRemoteChannelControl(String channelId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemoteChannelControl()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/SetRemoteChannelControl.asp?deviceId=" + uuid + "&channelId=" + channelId + "&version=1&terminalKey=" + tk;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRemoteChannelControl(response);
                if ( Constants.CODE_RUMPUS_OK.equals(RemoteChannelControl.get("resultCode")) ) {
                    // ok
                } else if ( "014".equals(RemoteChannelControl.get("resultCode")) ) {        // Hold Mode
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "셋탑박스가 꺼져있습니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);

                } else if ( "020".equals(RemoteChannelControl.get("resultCode")) ) {        // Hold Mode
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "데이터 방송 시청중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);

                } else if ( "021".equals(RemoteChannelControl.get("resultCode")) ) {        // VOD 시청중
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "VOD 시청중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                } else if ( "008".equals(RemoteChannelControl.get("resultCode")) ) {        // 녹화물 재생중
                    String alertTitle = "채널 변경";
                    String alertMessage1 = "녹화물 재생중엔 채널변경이 불가능합니다.";
                    String alertMessage2 = "";
                    CMAlertUtil.Alert(mInstance, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseSetRemoteChannelControl(String response) {
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
                        RemoteChannelControl.put("resultCode", resultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
                        RemoteChannelControl.put("errorString", errorString);
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


    private void requestGetChannelList() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelList()"); }
        try {
            mStbState  = getIntent().getExtras().getString("StbState");
        } catch (NullPointerException e) {
        }
        String url = mPref.getAircodeServerUrl() + "/getChannelList.xml?version=1&areaCode=" + mPref.getValue(CMConstants.USER_REGION_CODE_KEY, "17") + sGenreCode + "&noCache=";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                parseGetChannelList(response);

                String mStbWatchingchannel1 = mAdapter.getChannelNumberWithChannelId(mStbWatchingchannel);

                remote_controller_channel_textview.setText(mStbWatchingchannel1 + "번");

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
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
        int iChannelNumber = 0;
        StringBuilder sb = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("channelId")) {
                        sb.append("{\"channelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelNumber")) {
                        String sChannelNumber = xpp.nextText();
                        iChannelNumber = Integer.valueOf(sChannelNumber);
                        sb.append(",\"channelNumber\":\"").append(sChannelNumber).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelName")) {
                        sb.append(",\"channelName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("channelProgramOnAirTitle")) {
                        sb.append(",\"channelProgramOnAirTitle\":\"").append(xpp.nextText()).append("\"");
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
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), iChannelNumber, sb.toString());
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
    }

    private void requestSetRemotePowerControl(String power) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemotePowerControl()"); }
        String uuid  = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk    = mPref.getWebhasTerminalKey();
        String url   = mPref.getRumpersServerUrl() + "/SetRemotePowerControl.asp?Version=1&deviceId=" + uuid + "&terminalKey=" + tk + "&power=" + power;

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void requestSetRemoteVolumeControl(String volume) {

        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRemoteVolumeControl()"); }
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String tk   = mPref.getWebhasTerminalKey();
        String url  = mPref.getRumpersServerUrl() + "/SetRemoteVolumeControl.asp?deviceId=" + uuid + "&version=1&terminalKey=" + tk + "&volume=" + volume;

        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                if (error instanceof TimeoutError ) {
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
