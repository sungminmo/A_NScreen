package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 10. 26..
 */

public class VodBuyFragment extends Fragment {

    private static final String              tag = VodBuyFragment.class.getSimpleName();
    private static       VodBuyFragment      mInstance;
    private              JYSharedPreferences mPref;

    // network
    private              RequestQueue        mRequestQueue;
    private              ProgressDialog      mProgressDialog;

    // UI
    private              LinearLayout        vod_buy_step2_linearlayout;

    // activity
    private              String               assetId; // intent param
    private              String               isSeriesLink; // 시리즈 여부. ("YES or NO")
    private              long                 pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
    private              long                 totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.

    private              LinearLayout         vod_buy_step1_one_linearlayout, vod_buy_step1_serise_linearlayout, vod_buy_step1_oneproduct_linearlayout, vod_buy_step1_packeage_linearlayout, vod_buy_step1_month_linearlayout, vod_buy_step1_month_all_linearlayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod_buy, container, false);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this.getActivity());
        mRequestQueue = Volley.newRequestQueue(this.getActivity());

        Bundle param = getArguments();
        assetId = param.getString("assetId");
        isSeriesLink = param.getString("isSeriesLink");
        pointBalance      = 0l;
        totalMoneyBalance = 0l;

        vod_buy_step1_one_linearlayout        = (LinearLayout)view.findViewById(R.id.vod_buy_step1_one_linearlayout);
        vod_buy_step1_serise_linearlayout     = (LinearLayout)view.findViewById(R.id.vod_buy_step1_serise_linearlayout);
        vod_buy_step1_oneproduct_linearlayout = (LinearLayout)view.findViewById(R.id.vod_buy_step1_oneproduct_linearlayout);
        vod_buy_step1_packeage_linearlayout   = (LinearLayout)view.findViewById(R.id.vod_buy_step1_packeage_linearlayout);
        vod_buy_step1_month_linearlayout      = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_linearlayout);
        vod_buy_step1_month_all_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_all_linearlayout);

        vod_buy_step2_linearlayout = (LinearLayout)view.findViewById(R.id.vod_buy_step2_linearlayout);

        if ( "YES".equals(isSeriesLink) ) {
            vod_buy_step1_serise_linearlayout.setVisibility(View.VISIBLE);
        }


        requestGetPointBalance(); // 포인트 잔액을 얻어낸다

        return view;
    }
    /***********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_buy);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        assetId           = getIntent().getExtras().getString("assetId");
        pointBalance      = 0l;
        totalMoneyBalance = 0l;


        requestGetPointBalance(); // 포인트 잔액을 얻어낸다
    }
    ***********************************************************************************************/

    /**
     * 5.23.1 GetPointBalance
     * 포인트 잔액을 얻어낸다
     * terminalKey
     * domainId : "CnM"
     */
    private void requestGetPointBalance() {
        mProgressDialog	 = ProgressDialog.show(mInstance.getActivity(),"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        terminalKey = "8A5D2E45D3874824FF23EC97F78D358";
        String url = mPref.getWebhasServerUrl() + "/getPointBalance.json?version=1&terminalKey="+terminalKey+"&domainId=CnM";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mProgressDialog.dismiss();
                // {"resultCode":252,"transactionId":null,"pointBalance":0,"errorString":"Authentication fail, code(2000)","version":"1"}

                requestGetCouponBalance2(); // 사용가능한 쿠폰 정보를 얻어낸다.

                try {
                    JSONObject jo      = new JSONObject(response);
                    int resultCode = jo.getInt("resultCode");
                    pointBalance       = jo.getLong("pointBalance");
                    String errorString = jo.getString("errorString");

                    if ( resultCode != 100 ) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance.getActivity());
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });
                        alert.setMessage(errorString);
                        alert.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    /**
     * 5.23.3 GetCouponBalance2
     * 사용가능한 쿠폰 정보를 얻어낸다.
     * terminalKey
     * domainId : "CnM"
     */
    private void requestGetCouponBalance2() {
        // mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        terminalKey = "8A5D2E45D3874824FF23EC97F78D358";
        String url = mPref.getWebhasServerUrl() + "/getCouponBalance2.json?version=1&terminalKey="+terminalKey+"&domainId=CnM";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                // {"totalMoneyBalance":0,"transactionId":null,"promotionCouponIssued":false,"autopaySubscription":0,"autopayCouponName":"","errorString":"",
                // "discountCouponCount":0,"version":"1","extraInfoList":[],"couponList":[],"autopayCouponPrice":0,"resultCode":100,"autopayCouponBonusRate":0}
                try {
                    JSONObject jo      = new JSONObject(response);
                    int resultCode     = jo.getInt("resultCode");
                    totalMoneyBalance  = jo.getLong("totalMoneyBalance");
                    String errorString = jo.getString("errorString");


                    vod_buy_step1_one_linearlayout        = (LinearLayout)view.findViewById(R.id.vod_buy_step1_one_linearlayout);
                    vod_buy_step1_serise_linearlayout     = (LinearLayout)view.findViewById(R.id.vod_buy_step1_serise_linearlayout);
                    vod_buy_step1_oneproduct_linearlayout = (LinearLayout)view.findViewById(R.id.vod_buy_step1_oneproduct_linearlayout);
                    vod_buy_step1_packeage_linearlayout   = (LinearLayout)view.findViewById(R.id.vod_buy_step1_packeage_linearlayout);
                    vod_buy_step1_month_linearlayout      = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_linearlayout);
                    vod_buy_step1_month_all_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_all_linearlayout);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}