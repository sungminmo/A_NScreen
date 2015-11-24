package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private              EditText            mPwd;
    private              TextView            mPaymethod, mPrice, mTime, mInfo, mMinus;
    private              Button              backBtn, purchaseBtn;
    private              FrameLayout         mCouponFrame;
    private              String              assetId, productId, goodId, categoryId, mTitle;
    private              String              viewable, listPrice, sPayMethod, pointBalance, totalMoneyBalance;
    //private              String              pointPrice;  // 복합결제시 사용할 TV포인트 금액.
    private              long                couponPrice; // 복합결제시 사용할 Coupon 포인트 금액.

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

        //couponPrice       = getIntent().getExtras().getString("couponPrice");
        assetId           = getIntent().getExtras().getString("assetId");
        productId         = getIntent().getExtras().getString("productId");
        goodId            = getIntent().getExtras().getString("goodId");
        categoryId        = getIntent().getExtras().getString("categoryId");
        mTitle            = getIntent().getExtras().getString("mTitle");
        viewable          = getIntent().getExtras().getString("viewable");          // 시청기간
        listPrice         = getIntent().getExtras().getString("listPrice");         // 상품의 금액(할인가)
        sPayMethod        = getIntent().getExtras().getString("sPayMethod");        // 0: 일반결제, 1:복합결제(쿠폰(할인권)+일반결제), 2:복합결제(쿠폰+일반결제), 3:쿠폰결제, 4:TV포인트 결제.
        pointBalance      = getIntent().getExtras().getString("pointBalance");      // TV포인트
        totalMoneyBalance = getIntent().getExtras().getString("totalMoneyBalance"); // 금액형 쿠폰의 총 잔액

        mPaymethod   = (TextView)findViewById(R.id.dialog_buy_paymethod);
        mPrice       = (TextView)findViewById(R.id.dialog_buy_price);
        mTime        = (TextView)findViewById(R.id.dialog_buy_time);
        mInfo        = (TextView)findViewById(R.id.dialog_buy_info);
        mPwd         = (EditText)findViewById(R.id.dialog_buy_pwd_edittext);
        backBtn      = (Button) findViewById(R.id.backBtn);
        purchaseBtn  = (Button) findViewById(R.id.purchaseBtn);
        mCouponFrame = (FrameLayout)findViewById(R.id.dailog_buy_cupon_frame);
        mMinus       = (TextView)findViewById(R.id.dailog_buy_minus);

        // pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
        // totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.
        // 0: 일반결제, 1:복합결제(쿠폰(할인권)+일반결제), 2:복합결제(쿠폰+일반결제), 3:쿠폰결제, 4:TV포인트 결제.
        if ( "0".equals(sPayMethod) ) {                 // 일반 ------------------------------------
            mPaymethod.setText("일반결제 [부가세 별도]");
            mPrice.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
        } else if ( "1".equals(sPayMethod) ) {          // 복합 할인 --------------------------------
            mPaymethod.setText("일반결제 [부가세 별도]");
            long total = 0;
            total = Long.valueOf(listPrice) - Long.valueOf(totalMoneyBalance);
            mPrice.setText(UiUtil.toNumFormat((int)total)+"원");
            mCouponFrame.setVisibility(View.VISIBLE);
        } else if ( "2".equals(sPayMethod) ) {          // 복합 ------------------------------------
            mPaymethod.setText("일반결제 [부가세 별도]");
            long total = 0;
            total = Long.valueOf(listPrice) - Long.valueOf(totalMoneyBalance);
            if ( total < 0 ) {
                total = Long.valueOf(listPrice);
            }
            mPrice.setText(UiUtil.toNumFormat((int) total)+"원");
            mCouponFrame.setVisibility(View.VISIBLE);
        } else if ( "3".equals(sPayMethod) ) {          // 쿠폰포인트 -------------------------------
            mPaymethod.setText("쿠폰사용");
            mPrice.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
            mCouponFrame.setVisibility(View.VISIBLE);
            mMinus.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원 차감");
        } else if ( "4".equals(sPayMethod) ) {          // TV포인트 ---------------------------------
            mPaymethod.setText("TV포인트");
            mPrice.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
        }
        mTime.setText(viewable);



        mPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE: {
                        buy();
                    } break;
                }
                return false;
            }
        });

        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void buy(){
        String pwd = mPref.getValue(JYSharedPreferences.PURCHASE_PASSWORD, "");
        if ( ! pwd.equals(mPwd.getText().toString()) ) {
            mInfo.setText("구매 비밀번호가 잘못되었습니다.");
            mPwd.clearAnimation();
            mPwd.requestFocus();
        } else {
            if ( "0".equals(sPayMethod) ) {
                requestPurchaseAssetEx2();          // nomal
            } else if ( "1".equals(sPayMethod)) {

            } else if ( "2".equals(sPayMethod) ) {
                requestPurchaseByComplexMethods();  // complex
            } else if ( "3".equals(sPayMethod) ) {
                requestPurchaseByCoupon();            // coupon
            } else if ( "4".equals(sPayMethod)) {
                requestPurchaseByPoint();           // tv
            }
        }
    }

    // 일반결제
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



    // 쿠폰전용 결제
    private void requestPurchaseByPoint() {
        mProgressDialog	   = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId  = "";
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url         = mPref.getWebhasServerUrl()
                + "/purchaseByPoint.json?version=1&uiComponentDomain=0&uiComponentId=0&domainId=CnM"
                + "&terminalKey="+terminalKey
                + "&assetId="+encAssetId
                + "&productId="+productId
                + "&goodId="+goodId
                + "&price="+listPrice
                + "&categoryId="+categoryId;
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

    // TV포인트 결제
    private void requestPurchaseByCoupon() {
        mProgressDialog	   = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId  = "";
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url         = mPref.getWebhasServerUrl()
                //+ "/purchaseByCoupon.json?version=2&uiComponentDomain=0&uiComponentId=0&domainId=CnM"
                + "/purchaseByCoupon.json?version=1&domainId=CnM"
                + "&terminalKey="+terminalKey
                + "&assetId="+encAssetId
                + "&productId="+productId
                + "&goodId="+goodId
                + "&price="+listPrice
                + "&categoryId="+categoryId;
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

    // TV포인트 결제
    private void requestPurchaseByComplexMethods() {
        mProgressDialog	   = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId  = "";
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // http://58.141.255.79:8080/HApplicationServer/purchaseByComplexMethods.json?version=2&uiComponentDomain=0&uiComponentId=0&domainId=CnM&terminalKey=97CF2C2DC48840F7833EDA283F232357&assetId=www.hchoice.co.kr%7CM4172288LSG357693401&productId=108&goodId=1593110
        // &price=4500
        // &categoryId=304547&
        // pointPrice=0&couponPrice=1275&normalPrice=3225
        // price = pointPrice + couponPrice + normalPrice;
        long normalPrice = Long.valueOf(listPrice) - Long.valueOf(totalMoneyBalance);
        String url         = mPref.getWebhasServerUrl()
                + "/purchaseByComplexMethods.json?version=2&uiComponentDomain=0&uiComponentId=0&domainId=CnM"
                + "&terminalKey="+terminalKey
                + "&assetId="+encAssetId
                + "&productId="+productId
                + "&goodId="+goodId
                + "&categoryId="+categoryId
                + "&price="+listPrice
                + "&pointPrice=0"
                + "&couponPrice="+totalMoneyBalance
                + "&normalPrice="+String.valueOf(normalPrice);
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