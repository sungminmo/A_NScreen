package com.stvn.nscreen.setting.pay_channel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;
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
import java.util.Map;

/**
 * 설정화면 > 유료채널 안내 관리
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingPayChannelActivity extends CMBaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private CMSettingPayChannelAdapter mAdapter;
    private ArrayList<ListViewDataObject> mPayChannelList;

    // network
    private RequestQueue mRequestQueue;

    private JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pay_channel);
        setActionBarInfo("유료채널 안내", CMActionBar.CMActionBarStyle.BACK);

        mPref = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mPayChannelList = new ArrayList<ListViewDataObject>();
        initializeView();

        requestPayChannelList();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 지역설정 화면 UI 설정
     * */
    private void initializeView() {

        this.mAdapter = new CMSettingPayChannelAdapter(this, mPayChannelList);
        this.mListView = (ListView)findViewById(R.id.setting_pay_channel_listview);
        this.mListView.setOnItemClickListener(this);
        this.mListView.setAdapter(this.mAdapter);
    }

    private void requestPayChannelList() {
        showProgressDialog("", getString(R.string.wait_a_moment));
//        String terminalKey = JYSharedPreferences.RUMPERS_TERMINAL_KEY;
        String url = mPref.getRumpersServerUrl() + "/GetServiceJoyNList.asp";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parsePayChannelList(response);
                mAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                CMLog.e("CMSettingPayChannelActivity", error.getMessage());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                CMLog.d("CMSettingPayChannelActivity", params.toString());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parsePayChannelList(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("Joy_ID")) {
                        sb.append("{\"Joy_ID\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Title")) {
                        sb.append(",\"Joy_Title\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_SubTitle")) {
                        sb.append(",\"Joy_SubTitle\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Content")) {
                        String strContent = xpp.nextText().replaceAll("\"", "'");
                        sb.append(",\"Joy_Content\":\"").append(strContent).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("Joy_Thumbnail_Img")) {
                        sb.append(",\"Joy_Thumbnail_Img\":\"").append(xpp.nextText()).append("\"}");
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, sb.toString());
                        mPayChannelList.add(obj);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListViewDataObject info = (ListViewDataObject)parent.getItemAtPosition(position);
        try {
            JSONObject jsonObj = new JSONObject(info.sJson);

            Intent nextIntent = new Intent(CMSettingPayChannelActivity.this, CMSettingPayChannelDetailActivity.class);
            nextIntent.putExtra("Joy_Title", jsonObj.getString("Joy_Title"));
            nextIntent.putExtra("Joy_ID", jsonObj.getString("Joy_ID"));
            startActivity(nextIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
