package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import org.json.JSONArray;
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

public class VodMainActivity extends CMBaseActivity {

    private static final String                 tag = VodMainActivity.class.getSimpleName();
    private static       VodMainActivity        mInstance;
    private              JYSharedPreferences    mPref;

    // network
    private              RequestQueue           mRequestQueue;
    private              ProgressDialog         mProgressDialog;

    // gui
    private              VodMainGridViewAdapter mAdapter;
    private              GridView               mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_main);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        if ( mPref.isLogging() ) { Log.d(tag, "onCreate()"); }

        setActionBarStyle(CMActionBar.CMActionBarStyle.MAIN);
        setActionBarTitle(getString(R.string.title_activity_main));

        mAdapter  = new VodMainGridViewAdapter(this, null);

        mGridView = (GridView)findViewById(R.id.vod_main_gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(assetItemClickListener);

        requestGetPopularityChart(); //loadVpnData_getPopularityChart();

        ((LinearLayout)findViewById(R.id.vod_main_pop20_more_linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VodMainActivity.this, com.stvn.nscreen.vod.VodCategoryMainActivity.class);
                startActivity(i);
            }
        });

    }

    // vod_main_pop20_more_linearlayout

    private AdapterView.OnItemClickListener assetItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListViewDataObject item = (ListViewDataObject)mAdapter.getItem(position);
            Intent intent = new Intent(VodMainActivity.this, com.stvn.nscreen.vod.VodDetailActivity.class);
            intent.putExtra("sJson", item.sJson);
            startActivity(intent);
        }
    };

    private void loadVpnData_getPopularityChart() {

        String response = getResources().getString(R.string.get_populaity_chart);

        try {
            JSONObject jo  = new JSONObject(response);
            JSONObject weeklyChart = jo.getJSONObject("weeklyChart");
            JSONArray  popularityList = weeklyChart.getJSONArray("popularityList");
            for ( int i = 0; i < popularityList.length(); i++ ) {
                JSONObject popularity = (JSONObject)popularityList.get(i);
                // {\"assetId\":\"www.hchoice.co.kr|M4154270LSG347422301\",\"categoryId\":713230,\"comparision\":\"0\",\"hitCount\":838,\"hot\":false,\"isNew\":true,\"new\":true,\"ranking\":1,\"title\":\"(HD)극장동시-베테랑\"}
                /*
                String assetId     = popularity.getString("popularity");
                String categoryId  = popularity.getString("categoryId");
                String comparision = popularity.getString("comparision");
                String hitCount    = popularity.getString("hitCount");
                String hot         = popularity.getString("hot");
                String isNew       = popularity.getString("isNew");
                String isNewOri    = popularity.getString("new");
                String ranking     = popularity.getString("ranking");
                String title       = popularity.getString("title");
                */
                ListViewDataObject obj = new ListViewDataObject(mAdapter.getCount(), 0, popularity.toString());
                mAdapter.addItem(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * http://192.168.40.5:8080/HApplicationServer/getPopularityChart.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&categoryId=713230&requestItems=weekly
     *
     * */
    private void requestGetPopularityChart() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String url = mPref.getVodServerUrl() + "/getPopularityChart.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&categoryId=713230&requestItems=weekly";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                parseGetPopularityChart(response);
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

    /**
     *
     * @param response
     */
    private void parseGetPopularityChart(String response) {
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
                    if (xpp.getName().equalsIgnoreCase("assetId")) {
                        sb.append("{\"assetId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("categoryId")) {
                        sb.append(",\"categoryId\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("comparision")) {
                        sb.append(",\"comparision\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("hitCount")) {
                        sb.append(",\"hitCount\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("hot")) {
                        sb.append(",\"hot\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("isNew")) {
                        sb.append(",\"isNew\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("new")) {
                        sb.append(",\"new\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("ranking")) {
                        sb.append(",\"ranking\":\"").append(xpp.nextText()).append("\"");
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        sb.append(",\"title\":\"").append(xpp.nextText()).append("\"}");
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

    private void requestGetChannelList() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetChannelList()"); }
        String url = mPref.getEpgServerUrl() + "/getChannelList.xml?version=1&areaCode=0";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //parseGetChannelList(response);
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
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

}
