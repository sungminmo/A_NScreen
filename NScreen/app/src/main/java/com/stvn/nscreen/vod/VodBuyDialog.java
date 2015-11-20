package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 11. 9..
 */

public class VodBuyDialog extends Activity {
    private static final String              tag = VodBuyDialog.class.getSimpleName();
    private static       VodBuyDialog        mInstance;
    private              JYSharedPreferences mPref;

    private              RequestQueue        mRequestQueue;
    private              ProgressDialog      mProgressDialog;
    private              Button              backBtn, purchaseBtn;
    private              String              productId, goodId, mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_vod_buy_dialog);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        if (mPref.isLogging()) { Log.d(tag, "onCreate()"); }

        productId     = getIntent().getExtras().getString("productId");
        goodId        = getIntent().getExtras().getString("goodId");
        mTitle        = getIntent().getExtras().getString("mTitle");

        backBtn       = (Button) findViewById(R.id.backBtn);
        purchaseBtn   = (Button) findViewById(R.id.purchaseBtn);

        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPurchaseAssetEx2();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestPurchaseAssetEx2() {
        mProgressDialog	   = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String url         = mPref.getWebhasServerUrl() + "/purchaseAssetEx2.json?version=2&terminalKey="+terminalKey
                +"&productId="+productId +"&goodId="+goodId+"&uiComponentDomain=0&uiComponentId=0";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                int   resultCode  = 0;
                final String jstr = "";
                // {"resultCode":100,"discountCouponPaymentList":[],"transactionId":null,"errorString":"","version":"2","enrolledEventIdList":[]}
                try {
                    JSONObject jo = new JSONObject(response);
                    resultCode    = jo.getInt("resultCode");

                    // jstr = jo.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String alertTitle = "구매완료";
                String alertMsg1  = mTitle;
                String alertMsg2  = getString(R.string.success_purchase);
                CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("jstr", jstr);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, true);
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