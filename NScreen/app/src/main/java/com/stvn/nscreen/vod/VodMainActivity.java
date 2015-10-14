package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VodMainActivity extends AppCompatActivity {

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

        mAdapter  = new VodMainGridViewAdapter(this, null);
        // for test
//        for ( int i = 0; i < 1000; i++ ) {
//            String sChannel        = String.format("%02d", i);
//            ListViewDataObject obj = new ListViewDataObject(0, 0, "{\"channelNumber\":\""+sChannel+"\",\"title\":\"전국 노래자랑 광진구편 초대가수 임석원 사회 송해\"}");
//            mAdapter.addItem(obj);
//        }

        mGridView = (GridView)findViewById(R.id.vod_main_gridview);
        mGridView.setAdapter(mAdapter);

        loadVpnData_getPopularityChart();
        mAdapter.notifyDataSetChanged();
        //requestGetChannelList();
    }

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
