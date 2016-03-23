package com.stvn.nscreen.pvr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 9. 15..
 */

public class PvrSubActivity extends AppCompatActivity {
    private static final String                 tag = PvrSubActivity.class.getSimpleName();
    private static       PvrSubActivity         mInstance;
    private              JYSharedPreferences    mPref;

    private RequestQueue mRequestQueue;

    private              PvrSubListViewAdapter mAdapter;
    private              SwipeMenuListView     mListView;

    private              ImageButton           pvr_sub_backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvr_sub);

        mInstance = this;
        mPref     = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        mAdapter  = new PvrSubListViewAdapter(this, null);

        String jstr = getIntent().getExtras().getString("jstr");
        try {
            JSONArray arr = new JSONArray(jstr);
            for ( int i = 0; i< arr.length(); i++ ) {
                JSONObject jo = (JSONObject)arr.get(i);
                int RecordingType = jo.getInt("RecordingType");
                if ( RecordingType != 1 ) {
                    ListViewDataObject obj = new ListViewDataObject(i, 0, jo.toString());
                    mAdapter.addItem(obj);
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        // 2016-03-23 녹화예약관리 목록 : 방영시간별로 오름차순으로 정렬하기.
        if(mAdapter.getCount() > 1) {
            mAdapter.sortDatas();
        }

        pvr_sub_backBtn = (ImageButton) findViewById(R.id.pvr_sub_backBtn);

        pvr_sub_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView     = (SwipeMenuListView)findViewById(R.id.pvr_sub_listview);
        mListView.setAdapter(mAdapter);
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ListViewDataObject item = (ListViewDataObject) mAdapter.getItem(position);
                String sSeriesId = null;
                String sChannelId = null;
                String sProgramName = null;
                String starttime = null;
                String recordingtype = null;
                try {
                    JSONObject jo = new JSONObject(item.sJson);
                    sChannelId = jo.getString("ChannelId");
                    sProgramName = jo.getString("ProgramName");
                    starttime = jo.getString("RecordStartTime");
                    sSeriesId = jo.getString("SeriesId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String sSeriesId2 = sSeriesId;
                final String sChannelId2 = sChannelId;
                final String starttime2 = starttime;
                final String recordingtype2 = recordingtype;
                final int position2 = position;
                int iMenuType = mAdapter.getItemViewType(position);
                switch (iMenuType) {
                    case 0: {
                        String alertTitle = "녹화예약취소확인";
                        String alertMsg1 = sProgramName;
                        String alertMsg2 = getString(R.string.error_not_paring_compleated6);
                        CMAlertUtil.Alert_series_delete(mInstance, alertTitle, alertMsg1, alertMsg2, false, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String ReserveCancel = "1"; // 시리즈전체취소-1, 단편취소-2
                                requestSetRecordCancelReserve(position2, sChannelId2, starttime2, ReserveCancel, sSeriesId2);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String ReserveCancel = "2";
                                requestSetRecordCancelReserve(position2, sChannelId2, starttime2, ReserveCancel, sSeriesId2);
                            }
                        }, new DialogInterface.OnClickListener() { // 시리즈전체취소-1, 단편취소-2
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                    break;
                }
                return false;
            }
        });
    }

    /**
     * Swipe Menu for ListView
     */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // Create different menus depending on the view type
            switch (menu.getViewType()) {
                case 0: {
                    createMenu0(menu);  // 삭제
                }
                break;
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        private void createMenu0(SwipeMenu menu) { // 녹화예약취소
            SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
            item1.setBackground(new ColorDrawable(Color.rgb(0xC1, 0x4F, 0x28)));
            item1.setWidth(dp2px(90));
            item1.setTitle("녹화예약취소");
            item1.setTitleSize(12);
            item1.setTitleColor(Color.WHITE);
            menu.addMenuItem(item1);
        }
    };

    private void requestSetRecordCancelReserve(int position, String channelId, String starttime, String ReserveCancel, String seriesId) {
        final int position2 = position;
        final String ReserveCancel2 = ReserveCancel;
        if ( mPref.isLogging() ) { Log.d(tag, "requestSetRecordCancelReserve()"); }
        try {
            starttime = URLEncoder.encode(starttime, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String uuid = mPref.getValue(JYSharedPreferences.UUID, "");
        String url  = mPref.getRumpersServerUrl() + "/SetRecordCancelReserve.asp?Version=1&terminalKey=" + terminalKey + "&deviceId=" + uuid + "&channelId="
                + channelId + "&StartTime=" + starttime + "&ReserveCancel=" + ReserveCancel + "&seriesId=" + seriesId;
        // 시리즈전체취소-1, 단편취소-2
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseSetRecordCancelReserve(response);
                if ( "2".equals(ReserveCancel2) ) {
                    //
                }
                mAdapter.remove(position2);
                mAdapter.notifyDataSetChanged();
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