package com.stvn.nscreen.setting.region;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.stvn.nscreen.setting.CMSettingData;
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
 * 설정화면 > 지역설정
 * Created by kimwoodam on 2015. 9. 30..
 */

public class CMSettingRegionActivity extends CMBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView mRegionName;
    private ListView mListView;
    private CMSettingRegionAdapter mAdapter;
    private ArrayList<ListViewDataObject> mChannelAreaList;

    // network
    private RequestQueue mRequestQueue;
    private JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_region);
        setActionBarInfo("지역설정", CMActionBar.CMActionBarStyle.BACK);

        this.mPref = new JYSharedPreferences(this);
        this.mRequestQueue = Volley.newRequestQueue(this);
        this.mChannelAreaList = new ArrayList<ListViewDataObject>();
        initializeView();

        requestGetChannelAreaList();
    }

    @Override
    public void onBackEventPressed() {
        finish();
    }

    /**
     * 지역설정 화면 UI 설정
     * */
    private void initializeView() {

        String areaCode = CMSettingData.getInstance().getUserAreaCode(CMSettingRegionActivity.this);
        String areaName = CMSettingData.getInstance().getUserAreaName(CMSettingRegionActivity.this);

        this.mRegionName = (TextView)findViewById(R.id.setting_region_name);
        this.mRegionName.setText(areaName);
        this.mAdapter = new CMSettingRegionAdapter(this, this.mChannelAreaList);
        this.mAdapter.changeSelectedRegion(areaCode, areaName);

        this.mListView = (ListView)findViewById(R.id.setting_region_listview);
        this.mListView.setOnItemClickListener(this);
        this.mListView.setAdapter(this.mAdapter);

        findViewById(R.id.setting_region_cancel).setOnClickListener(this);
        findViewById(R.id.setting_region_complete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_region_cancel: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.setting_region_complete: {

                String areaCode = this.mAdapter.getRegionCode();
                String areaName = this.mAdapter.getRegionName();

                CMSettingData.getInstance().setUserAreaCode(CMSettingRegionActivity.this, areaCode);
                CMSettingData.getInstance().setUserAreaName(CMSettingRegionActivity.this, areaName);

                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListViewDataObject info = (ListViewDataObject)parent.getItemAtPosition(position);
        try {
            JSONObject jsonObj = new JSONObject(info.sJson);
            String areaCode = jsonObj.getString("areaCode");
            String areaName = jsonObj.getString("areaName");
            this.mRegionName.setText(areaName);
            this.mAdapter.changeSelectedRegion(areaCode, areaName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestGetChannelAreaList() {
        showProgressDialog("", getString(R.string.wait_a_moment));

        String url = mPref.getAircodeServerUrl() + "/getChannelArea.xml?version=1";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseChannelAreaList(response);
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

    private void parseChannelAreaList(String response) {

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
                    if (xpp.getName().equalsIgnoreCase("areaCode")) {
                        sb.append("{\"areaCode\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("areaName")) {
                        sb.append(",\"areaName\":\"").append(xpp.nextText()).append("\"}");
                        ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, sb.toString());
                        mChannelAreaList.add(obj);
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
}