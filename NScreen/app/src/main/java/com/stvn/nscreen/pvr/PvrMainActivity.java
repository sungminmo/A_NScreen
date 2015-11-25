package com.stvn.nscreen.pvr;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.epg.EpgSubActivity;
import com.stvn.nscreen.pairing.PairingMainActivity;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by limdavid on 15. 9. 14..
 */

public class PvrMainActivity extends AppCompatActivity {
    private static final String                 tag = PvrMainActivity.class.getSimpleName();
    private static       PvrMainActivity        mInstance;
    private              JYSharedPreferences    mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;
    private              Map<String, Object>    mNetworkError;

    private              ArrayList<JSONObject>  mReservs;  // 전체 얘약 목록
    private              PvrMainListViewAdapter mAdapter;  // 시리즈 중복은 제외한 예약 목록
    private              SwipeMenuListView      mListView;

    private              ImageButton            pvr_main_backBtn;
    private              Button                 button1, button2;
    private              TextView               textView1, textView2;

    private String getSeriesJson(String str){
        JSONArray arr = new JSONArray();
        try {
            for (int i = 0; i < mReservs.size(); i++) {
                JSONObject jo = mReservs.get(i);
                if (str.equals(jo.getString("SeriesId"))) {
                    arr.put(jo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvr_main);

        pvr_main_backBtn = (ImageButton) findViewById(R.id.pvr_main_backBtn);

        pvr_main_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mNetworkError = new HashMap<String, Object>();
        mReservs      = new ArrayList<JSONObject>();
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter      = new PvrMainListViewAdapter(this, null);

        button1       = (Button) findViewById(R.id.button1);
        button2       = (Button) findViewById(R.id.button2);

        textView1     = (TextView) findViewById(R.id.pvr_main_count_textview1);
        textView2     = (TextView) findViewById(R.id.pvr_main_count_textview2);

        mListView     = (SwipeMenuListView)findViewById(R.id.pvr_main_listview);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(creator);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ListViewDataObject item = (ListViewDataObject) mAdapter.getItem(position);
                String sRecordId     = null;
                String sSeriesId     = null;
                String sChannelId    = null;
                String sProgramName  = null;
                String starttime     = null;
                String recordingtype = null;
                try {
                    JSONObject jo = new JSONObject (item.sJson);
                    if ( jo.isNull("RecordId") ) {
                        sRecordId = "";
                    } else {
                        sRecordId = jo.getString("RecordId");  // error RecordId 없음.
                    }
                    sSeriesId = jo.getString("SeriesId");
                    sChannelId = jo.getString("ChannelId");
                    sProgramName = jo.getString("ProgramName");
                    starttime  = jo.getString("RecordStartTime");
                    recordingtype = jo.getString("RecordingType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String sChannelId2 = sChannelId;
                final String starttime2 = starttime;
                final String recordingtype2 = recordingtype;
                final String sRecordId2 = sRecordId;
                final String sSeriesId2 = sSeriesId;
                int iMenuType = mAdapter.getItemViewType(position);
                switch (iMenuType) {
                    case 0: {
                        requestSetRecordStop(sChannelId);
                    }
                    break;
                    case 1: {
                        if ( "NULL".equals(sSeriesId) ) { // 단편
                            String ReserveCancel = "2";
                            requestSetRecordCancelReserve(sChannelId, starttime, ReserveCancel);
                        } else if ( !"NULL".equals(sSeriesId) ) { // 시리즈
                            String alertTitle = "녹화예약취소확인";
                            String alertMsg1 = sProgramName;
                            String alertMsg2 = getString(R.string.error_not_paring_compleated6);
                            CMAlertUtil.Alert_series_delete(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String ReserveCancel = "2";
                                    requestSetRecordCancelReserve(sChannelId2, starttime2, ReserveCancel);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String ReserveCancel = "1";
                                    requestSetRecordCancelReserve(sChannelId2, starttime2, ReserveCancel);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                        }
                    }
                    break;
                    case 2: {
                        if ( "NULL".equals(sSeriesId) ) { // 단편
                            String ReserveCancel = "2";
                            requestSetRecordDele(sChannelId, starttime, sRecordId);
                        } else if ( !"NULL".equals(sSeriesId) ) { // 시리즈
                            String alertTitle = "녹화예약취소확인";
                            String alertMsg1 = sProgramName;
                            String alertMsg2 = getString(R.string.error_not_paring_compleated6);
                            CMAlertUtil.Alert_series_delete(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String ReserveCancel = "1";
                                    requestSetRecordSeriesDele(sChannelId2, starttime2, sRecordId2, sSeriesId2);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String ReserveCancel = "2";
                                    requestSetRecordDele(sChannelId2, starttime2, sRecordId2);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                    }
                    break;
                }
                return false;
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                button1.setSelected(true);
                button2.setSelected(false);
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.GONE);
                requestGetRecordReservelist();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                button1.setSelected(false);
                button2.setSelected(true);
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.VISIBLE);
                requestGetRecordlist();
            }
        });

        button1.setSelected(true);
        requestGetRecordReservelist();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(tag, "mItemClickListener() " + position);
            ListViewDataObject dobj = (ListViewDataObject) mAdapter.getItem(position);

            try {
                JSONObject jo              = new JSONObject(dobj.sJson);
                String     sSeriesId  = jo.getString("SeriesId");

                if ( !"NULL".equals(sSeriesId) ) {
                    Intent intent = new Intent(mInstance, PvrSubActivity.class);
                    intent.putExtra("SeriesId", sSeriesId);
                    String jstr = getSeriesJson(sSeriesId);
                    intent.putExtra("jstr", jstr);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Swipe Menu for ListView
     */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // Create different menus depending on the view type
            switch (menu.getViewType()) {
                case -1: {
                    // nothing
                } break;
                case 0: {
                    createMenu0(menu);  // 녹화중지
                } break;
                case 1: {
                    createMenu1(menu);  // 녹화예약취소
                } break;
                case 2: {
                    createMenu2(menu); // 녹화물삭제
                } break;
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        private void createMenu0(SwipeMenu menu) { // 녹화중지
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xEA, 0x55, 0x55)));
            item1.setWidth(dp2px(90));
            item1.setTitle("녹화중지");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }

        private void createMenu1(SwipeMenu menu) { // 녹화예약취소
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
            item1.setWidth(dp2px(90));
            item1.setTitle("녹화예약취소");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }

        private void createMenu2(SwipeMenu menu) { // 녹화물삭제
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xEA, 0x55, 0x55)));
            item1.setWidth(dp2px(90));
            item1.setTitle("삭제");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }
    };

    private void requestGetRecordReservelist() {
        mProgressDialog	        = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetRecordReservelist()"); }
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String          url     = mPref.getRumpersServerUrl() + "/getRecordReservelist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                //String sResultCode = parseGetRecordReservelist(request.getUtf8Response()); // 파싱 결과를 리턴 받는다.
                String sResultCode = parseGetRecordReservelist(response); // 파싱 결과를 리턴 받는다.
                if ( Constants.CODE_RUMPUS_ERROR_205_Not_Found.equals(sResultCode) ) {
                    // 녹화물 없음이므로 정상.
                    textView1.setText("총 0개의 녹화예약 콘텐츠가 있습니다.");
                    mAdapter.setTabNumber(1);
                    mAdapter.notifyDataSetChanged();
                } else if ( ! Constants.CODE_RUMPUS_OK.equals(sResultCode) ) {
                    String msg = "getRecordReservelist("+sResultCode+":"+mNetworkError.get("errorString")+")";
                    AlertDialog.Builder ad = new AlertDialog.Builder(mInstance);
                    ad.setTitle("알림").setMessage(msg).setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = ad.create();
                    alert.show();
                } else {
                    textView1.setText("총 " + mAdapter.getCount() + "개의 녹화예약 콘텐츠가 있습니다.");
                    mAdapter.setTabNumber(1);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
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
                params.put("version", String.valueOf(1));
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private String parseGetRecordReservelist(String response) {
        String               sResultCode = "0";   // 응답받은 resultCode
        StringBuilder        sb          = new StringBuilder();
        StringBuilder        sb2         = new StringBuilder(); // for array
        XmlPullParserFactory factory     = null;
        List<String>         strings     = new ArrayList<String>();

        response = response.replace("<![CDATA[","");
        response = response.replace("]]>","");
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), "UTF-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("resultCode")) {
                        sResultCode = xpp.nextText(); mNetworkError.put("resultCode",sResultCode);
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        mNetworkError.put("errorString",xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("Reserve_Item")) {
                        //
                    } else if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        // array start -------------------------------------------------------------
                        sb2.append("{\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("OverlapId")) {
                        sb2.append(",\"OverlapId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("SeriesId")) {
                        sb2.append(",\"SeriesId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ScheduleId")) {
                        sb2.append(",\"ScheduleId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelId")) {
                        sb2.append(",\"ChannelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelNo")) {
                        sb2.append(",\"ChannelNo\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelName")) {
                        sb2.append(",\"ChannelName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Program_Grade")) {
                        sb2.append(",\"Program_Grade\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb2.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb2.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");  // <![CDATA[ ]]>  한글 깨짐.
                    } else if (xpp.getName().equalsIgnoreCase("RecordStartTime")) {
                        sb2.append(",\"RecordStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordEndTime")) {
                        sb2.append(",\"RecordEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordHD")) {
                        sb2.append(",\"RecordHD\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordPaytype")) {
                        sb2.append(",\"RecordPaytype\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordStatus")) {
                        // array end -----------------------------------------------------------
                        sb2.append(",\"RecordStatus\":\"").append(xpp.nextText()).append("\"}");
                        strings.add(sb2.toString());
                        sb2.setLength(0);
                    //} else if ( bStartedArr == true && xpp.getName().equalsIgnoreCase("Reserve_Item")) {

                    } else if (xpp.getName().equalsIgnoreCase("response")) {
                        //
                    }
                }
                eventType = xpp.next();
            }
            sb.append("}");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( strings.size() > 0 ) {
            try {
                for (int i = 0; i < strings.size(); i++) {
                    String str = strings.get(i);
                    JSONObject jo = new JSONObject(str);
                    mReservs.add(jo);  // 모든 예약 목록 저장.
                    String RecordingType = jo.getString("RecordingType");
                    String SeriesId = jo.getString("SeriesId");
                    if ("0".equals(RecordingType) && (!"NULL".equals(SeriesId))) {
                        // nothing
                    } else {
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, str);
                        mAdapter.addItem(obj);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return sResultCode;
    }

    /**
     * 녹화물 리스트 요청
     */
    private void requestGetRecordlist() {
        mProgressDialog	        = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetRecordlist()"); }
        String          uuid    = mPref.getValue(JYSharedPreferences.UUID, "");
        String          tk      = mPref.getWebhasTerminalKey();
        String          url     = mPref.getRumpersServerUrl() + "/getrecordlist.asp?Version=1&terminalKey=" + tk + "&deviceId=" + uuid;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();
                parseGetrecordlist(response);
                textView2.setText("총 " + mAdapter.getCount() + "개의 녹화 콘텐츠가 있습니다.");
                mAdapter.setTabNumber(2);
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

    private void parseGetrecordlist(String response) {
        StringBuilder        sb      = new StringBuilder();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new ByteArrayInputStream(response.getBytes("utf-8")), "utf-8");

            int eventType = xpp.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("RecordId")) {
                        sb.append("{\"RecordId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordingType")) {
                        sb.append(",\"RecordingType\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("SeriesId")) {
                        sb.append(",\"SeriesId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ChannelId")) {
                        sb.append(",\"ChannelId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Channel_logo_img")) {
                        sb.append(",\"Channel_logo_img\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ProgramName")) {
                        sb.append(",\"ProgramName\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordStartTime")) {
                        sb.append(",\"RecordStartTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordEndTime")) {
                        sb.append(",\"RecordEndTime\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("RecordHD")) {
                        sb.append(",\"RecordHD\":\"").append(xpp.nextText()).append("\"}");
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
    }

    private void requestSetRecordStop(String channelId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordStop()"); }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordStop.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId=" + channelId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordStop(response);
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

    private void parseSetRecordStop(String response) {
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
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
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
     * 녹화물 삭제
     * @param channelId
     * @param starttime
     * @param sRecordId
     */
    private void requestSetRecordDele(String channelId, String starttime, String sRecordId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordDele()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordDele.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&deleteType=0&RecordId=" + sRecordId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordDele(response);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                textView2.setText("목록 재 요청 중입니다.");
                requestGetRecordlist();
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

    private void parseSetRecordDele(String response) {
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
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
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

    private void requestSetRecordSeriesDele(String channelId, String starttime, String sRecordId, String sSeriesId) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordSeriesDele()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordSeriesDele.asp?version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&deleteType=0&RecordId=" + sRecordId + "SeriesId=" + sSeriesId;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordSeriesDele(response);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                textView2.setText("목록 재 요청 중입니다.");
                requestGetRecordlist();
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

    private void parseSetRecordSeriesDele(String response) {
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
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
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

    private void requestSetRecordCancelReserve(String channelId, String starttime, String recordingtype) {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordCancelReserve()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8"); // error
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordCancelReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&ReserveCancel=" + recordingtype;
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseSetRecordCancelReserve(response);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                requestGetRecordReservelist();
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

    private void parseSetRecordCancelReserve(String response) {
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
                    } else if (xpp.getName().equalsIgnoreCase("errorString")) {
                        String errorString = xpp.nextText();
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
