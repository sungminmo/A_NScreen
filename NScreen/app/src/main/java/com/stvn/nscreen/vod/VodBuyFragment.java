package com.stvn.nscreen.vod;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.BufferUnderflowException;
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
    // for 결재
    private              String               productId;
    private              String               goodId;
    private              String               assetId; // intent param
    private              String               isSeriesLink; // 시리즈 여부. ("YES or NO")
    private              String               mTitle; // asset title
    private              String               sListPrice; // 정가
    private              String               sPrice; // 할인적용가
    private              TextView             vod_buy_title_textview, vod_buy_step1_one_price, vod_buy_step2_normal_price;
    private              long                 pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
    private              long                 totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.

    private              LinearLayout         vod_buy_step1_one_linearlayout, vod_buy_step1_serise_linearlayout, vod_buy_step1_oneproduct_linearlayout, vod_buy_step1_packeage_linearlayout, vod_buy_step1_month_linearlayout, vod_buy_step1_month_all_linearlayout;
    private              LinearLayout         vod_buy_step2_normal_linearlayout, vod_buy_step2_normal_dis_linearlayout, vod_buy_step2_coupon_linearlayout, vod_buy_step2_point_linearlayout;




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
        mTitle = param.getString("mTitle");
        sListPrice = param.getString("sListPrice");
        sPrice = param.getString("sPrice");
        productId = param.getString("productId");
        goodId = param.getString("goodId");
        pointBalance      = 0l;
        totalMoneyBalance = 0l;

        vod_buy_step1_one_linearlayout        = (LinearLayout)view.findViewById(R.id.vod_buy_step1_one_linearlayout);
        vod_buy_step1_serise_linearlayout     = (LinearLayout)view.findViewById(R.id.vod_buy_step1_serise_linearlayout);
        vod_buy_step1_oneproduct_linearlayout = (LinearLayout)view.findViewById(R.id.vod_buy_step1_oneproduct_linearlayout);
        vod_buy_step1_packeage_linearlayout   = (LinearLayout)view.findViewById(R.id.vod_buy_step1_packeage_linearlayout);
        vod_buy_step1_month_linearlayout      = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_linearlayout);
        vod_buy_step1_month_all_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step1_month_all_linearlayout);
        vod_buy_step2_normal_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step2_normal_linearlayout);
        vod_buy_step2_normal_dis_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step2_normal_dis_linearlayout);
        vod_buy_step2_coupon_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step2_coupon_linearlayout);
        vod_buy_step2_point_linearlayout  = (LinearLayout)view.findViewById(R.id.vod_buy_step2_point_linearlayout);
        vod_buy_step2_linearlayout = (LinearLayout)view.findViewById(R.id.vod_buy_step2_linearlayout);
        vod_buy_title_textview = (TextView)view.findViewById(R.id.vod_buy_title_textview);
        vod_buy_step1_one_price = (TextView)view.findViewById(R.id.vod_buy_step1_one_price);
        vod_buy_step2_normal_price = (TextView)view.findViewById(R.id.vod_buy_step2_normal_price);

        vod_buy_title_textview.setText(mTitle);
        if ( sListPrice.equals(sPrice) ) {
            vod_buy_step1_one_price.setText(UiUtil.toNumFormat(Integer.parseInt(sListPrice)) + "원/월 [부가세 별도]");
            vod_buy_step2_normal_price.setText(UiUtil.toNumFormat(Integer.parseInt(sListPrice)) + "원/월 [부가세 별도]");
        } else {
            vod_buy_step1_one_price.setText(UiUtil.toNumFormat(Integer.parseInt(sPrice)) + "원/월 [부가세 별도]");
            vod_buy_step2_normal_price.setText(UiUtil.toNumFormat(Integer.parseInt(sPrice)) + "원/월 [부가세 별도]");
        }
        if ( "YES".equals(isSeriesLink) ) {
            vod_buy_step1_one_linearlayout.setVisibility(View.VISIBLE);
            vod_buy_step1_serise_linearlayout.setVisibility(View.VISIBLE);
        } else {
            vod_buy_step1_one_linearlayout.setVisibility(View.VISIBLE);
            vod_buy_step1_month_linearlayout.setVisibility(View.VISIBLE);
            vod_buy_step1_month_all_linearlayout.setVisibility(View.VISIBLE);
        }

        vod_buy_step1_one_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(true);
                vod_buy_step1_serise_linearlayout.setSelected(false);
                vod_buy_step1_oneproduct_linearlayout.setSelected(false);
                vod_buy_step1_packeage_linearlayout.setSelected(false);
                vod_buy_step1_month_linearlayout.setSelected(false);
                vod_buy_step1_month_all_linearlayout.setSelected(false);
            }
        });

        vod_buy_step1_serise_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(false);
                vod_buy_step1_serise_linearlayout.setSelected(true);
                vod_buy_step1_oneproduct_linearlayout.setSelected(false);
                vod_buy_step1_packeage_linearlayout.setSelected(false);
                vod_buy_step1_month_linearlayout.setSelected(false);
                vod_buy_step1_month_all_linearlayout.setSelected(false);
            }
        });

        vod_buy_step1_oneproduct_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(false);
                vod_buy_step1_serise_linearlayout.setSelected(false);
                vod_buy_step1_oneproduct_linearlayout.setSelected(true);
                vod_buy_step1_packeage_linearlayout.setSelected(false);
                vod_buy_step1_month_linearlayout.setSelected(false);
                vod_buy_step1_month_all_linearlayout.setSelected(false);
            }
        });

        vod_buy_step1_packeage_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(false);
                vod_buy_step1_serise_linearlayout.setSelected(false);
                vod_buy_step1_oneproduct_linearlayout.setSelected(false);
                vod_buy_step1_packeage_linearlayout.setSelected(true);
                vod_buy_step1_month_linearlayout.setSelected(false);
                vod_buy_step1_month_all_linearlayout.setSelected(false);
            }
        });

        vod_buy_step1_month_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(false);
                vod_buy_step1_serise_linearlayout.setSelected(false);
                vod_buy_step1_oneproduct_linearlayout.setSelected(false);
                vod_buy_step1_packeage_linearlayout.setSelected(false);
                vod_buy_step1_month_linearlayout.setSelected(true);
                vod_buy_step1_month_all_linearlayout.setSelected(false);
            }
        });

        vod_buy_step1_month_all_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step1_one_linearlayout.setSelected(false);
                vod_buy_step1_serise_linearlayout.setSelected(false);
                vod_buy_step1_oneproduct_linearlayout.setSelected(false);
                vod_buy_step1_packeage_linearlayout.setSelected(false);
                vod_buy_step1_month_linearlayout.setSelected(false);
                vod_buy_step1_month_all_linearlayout.setSelected(true);
            }
        });

        vod_buy_step2_normal_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_normal_linearlayout.setSelected(true);
                vod_buy_step2_normal_dis_linearlayout.setSelected(false);
                vod_buy_step2_coupon_linearlayout.setSelected(false);
                vod_buy_step2_point_linearlayout.setSelected(false);
            }
        });

        vod_buy_step2_normal_dis_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_normal_linearlayout.setSelected(false);
                vod_buy_step2_normal_dis_linearlayout.setSelected(true);
                vod_buy_step2_coupon_linearlayout.setSelected(false);
                vod_buy_step2_point_linearlayout.setSelected(false);
            }
        });

        vod_buy_step2_coupon_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_normal_linearlayout.setSelected(false);
                vod_buy_step2_normal_dis_linearlayout.setSelected(false);
                vod_buy_step2_coupon_linearlayout.setSelected(true);
                vod_buy_step2_point_linearlayout.setSelected(false);
            }
        });

        vod_buy_step2_point_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod_buy_step2_normal_linearlayout.setSelected(false);
                vod_buy_step2_normal_dis_linearlayout.setSelected(false);
                vod_buy_step2_coupon_linearlayout.setSelected(false);
                vod_buy_step2_point_linearlayout.setSelected(true);
            }
        });

        Button purchaseButton = (Button)view.findViewById(R.id.vod_buy_ok_button);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(mInstance.getActivity());
                ad.setTitle("알림")
                        .setMessage("구매비밀번호는 작업중 입니다. 확인을 누르시면 결제 됩니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPurchaseAssetEx2();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = ad.create();
                alert.show();
            }
        });
        Button cancleButton   = (Button)view.findViewById(R.id.vod_buy_cancle_button);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack("VodBuyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

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
        String url = mPref.getWebhasServerUrl() + "/getCouponBalance2.json?version=1&terminalKey=" + terminalKey + "&domainId=CnM";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                // {"totalMoneyBalance":0,"transactionId":null,"promotionCouponIssued":false,"autopaySubscription":0,"autopayCouponName":"","errorString":"",
                // "discountCouponCount":0,"version":"1","extraInfoList":[],"couponList":[],"autopayCouponPrice":0,"resultCode":100,"autopayCouponBonusRate":0}
                try {
                    JSONObject jo = new JSONObject(response);
                    int resultCode = jo.getInt("resultCode");
                    totalMoneyBalance = jo.getLong("totalMoneyBalance");
                    String errorString = jo.getString("errorString");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (mPref.isLogging()) {
                    VolleyLog.d(tag, "onErrorResponse(): " + error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("version", String.valueOf(1));
                if (mPref.isLogging()) {
                    Log.d(tag, "getParams()" + params.toString());
                }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    /**
     * http://192.168.40.5:8080/HApplicationServer/
     * purchaseAssetEx2.xml?
     * version=2&
     * terminalKey=8A5D2E45D3874824FF23EC97F78D358&
     * transactionId=603&
     * productId=108
     * &goodId=1544796&
     * uiComponentDomain=0&
     * uiComponentId=0
     */
    private void requestPurchaseAssetEx2() {
        // mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        terminalKey = "8A5D2E45D3874824FF23EC97F78D358";
        String url = mPref.getWebhasServerUrl() + "/purchaseAssetEx2.json?version=2&terminalKey="+terminalKey
                +"&productId="+productId +"&goodId="+goodId+"&uiComponentDomain=0&uiComponentId=0";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jo      = new JSONObject(response);
                    int resultCode     = jo.getInt("resultCode");

                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack("VodBuyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

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