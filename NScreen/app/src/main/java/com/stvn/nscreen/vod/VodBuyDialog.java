package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import java.net.URLEncoder;
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
    private              TextView            mPaymethod, dialog_buy_price, mTime, mInfo, mMinus;
    private              TextView            dialog_buy_original_price_textview;
    private              TextView            dailog_buy_use_coupon; // 쿠폰사용
    private              Button              backBtn, purchaseBtn;
    private              String              assetId, productId, goodId, categoryId, mTitle;
    private              String              viewable, listPrice, sPayMethod, pointBalance, totalMoneyBalance;
    //private              String              pointPrice;  // 복합결제시 사용할 TV포인트 금액.
    private              long                couponPrice;            // 복합결제시 사용할 Coupon 포인트 금액.
    private              long                lpriceCouponDiscounted; // 할인을 적용할 경우, 할인 적용후 결제한 금액가.
    private              long                ldiscountAmount;        // 할인을 적용할 경우, 할인금액.
    private              String              sdiscountCouponId;      // 할인을 적용할 경우, 사용할 쿠폰의 ID.
    private              String              productType; // RVOD, SVOD,...
    private              String              productName; // RVOD, SVOD,...

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
        lpriceCouponDiscounted = getIntent().getExtras().getLong("lpriceCouponDiscounted"); // 할인을 적용할 경우, 할인 적용후 결제한 금액가.
        ldiscountAmount        = getIntent().getExtras().getLong("ldiscountAmount");        // 할인을 적용할 경우, 할인금액.
        sdiscountCouponId      = getIntent().getExtras().getString("sdiscountCouponId");    // 할인을 적용할 경우, 사용할 쿠폰의 ID.
        productType      = getIntent().getExtras().getString("productType");                // RVOD,SVOD,...
        productName      = getIntent().getExtras().getString("productName");                // RVOD,SVOD,...

        if ( mPref.isLogging() ) {
            Log.d(tag, "assetId:"+assetId);
            Log.d(tag, "productId:"+productId);
            Log.d(tag, "goodId:"+goodId);
            Log.d(tag, "categoryId:"+categoryId);
            Log.d(tag, "mTitle:"+mTitle);
            Log.d(tag, "viewable:"+viewable);
            Log.d(tag, "listPrice:"+listPrice);
            Log.d(tag, "sPayMethod:"+sPayMethod);
            Log.d(tag, "pointBalance:"+pointBalance);
            Log.d(tag, "totalMoneyBalance:"+totalMoneyBalance);
            Log.d(tag, "lpriceCouponDiscounted:"+lpriceCouponDiscounted);
            Log.d(tag, "ldiscountAmount:"+ldiscountAmount);
            Log.d(tag, "sdiscountCouponId:"+sdiscountCouponId);
        }

        mPaymethod                         = (TextView)findViewById(R.id.dialog_buy_paymethod);
        dialog_buy_original_price_textview = (TextView)findViewById(R.id.dialog_buy_original_price_textview);
        dailog_buy_use_coupon              = (TextView)findViewById(R.id.dailog_buy_use_coupon);
        dialog_buy_price                   = (TextView)findViewById(R.id.dialog_buy_price);
        mTime                              = (TextView)findViewById(R.id.dialog_buy_time);
        mInfo                              = (TextView)findViewById(R.id.dialog_buy_info);
        mPwd                               = (EditText)findViewById(R.id.dialog_buy_pwd_edittext);
        backBtn                            = (Button) findViewById(R.id.backBtn);
        purchaseBtn                        = (Button) findViewById(R.id.purchaseBtn);
        mMinus                             = (TextView)findViewById(R.id.dailog_buy_minus);

        dialog_buy_original_price_textview.setPaintFlags(dialog_buy_original_price_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);



        mPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE: {
                        buy();
                    }
                    break;
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

        setUI();
    }

    private void setUI() {
        dialog_buy_original_price_textview.setText(""); // 일반(할인)결제 일때 원래가격.
        dialog_buy_price.setText(""); // 결재금액
        mMinus.setText("");  // 원차감.
        mPaymethod.setText("");  // 쿠폰사용.
        dailog_buy_use_coupon.setText(""); // 쿠폰사용.

        if ( lpriceCouponDiscounted > 0 ) { // 일반결제인데 할인쿠폰 적용해서 할인이 가능하면..
            dialog_buy_original_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice)) + "원");
        }

        mTime.setText(viewable); // 시청기간.

        // pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
        // totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.
        // 0: 일반결제, 1:복합결제(쿠폰(할인권)+일반결제), 2:복합결제(쿠폰+일반결제), 3:쿠폰결제, 4:TV포인트 결제.
        if ( "0".equals(sPayMethod) ) {                 // 일반 ------------------------------------
            Log.d(tag, "일반결제임-----------------------------------------------------------------");
            mPaymethod.setText("일반결제 [부가세 별도]");
            dailog_buy_use_coupon.setText("");
            if ( "SVOD".equals(productType) ) {
                dialog_buy_price.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원/월");
                mTime.setText("해지 시 까지");
            } else {
                dialog_buy_price.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
            }
        } else if ( "1".equals(sPayMethod) ) {          // 일반(할인) --------------------------------
            Log.d(tag, "일반(할인쿠폰)결제임---------------------------------------------------------");
            mPaymethod.setText("일반결제 [부가세 별도]");
            long lDpPrice       = Long.valueOf(listPrice);
            long lMyCouponPoint = Long.valueOf(totalMoneyBalance);
            long lNeedPrice     = lDpPrice - lpriceCouponDiscounted;

            dialog_buy_original_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
            dailog_buy_use_coupon.setText("");
            dialog_buy_price.setText("→ "+UiUtil.toNumFormat((int)lpriceCouponDiscounted)+"원");
        } else if ( "2".equals(sPayMethod) ) {          // 복합 ------------------------------------
            Log.d(tag, "복합결제임------------------------------------------------------------------");
            mPaymethod.setText("일반결제 [부가세 별도]");
            dailog_buy_use_coupon.setText("쿠폰사용");
            long total = 0;
            total = Long.valueOf(listPrice) - Long.valueOf(totalMoneyBalance);
            if ( total < 0 ) {
                total = Long.valueOf(listPrice);
            }
            dialog_buy_price.setText(UiUtil.toNumFormat((int) total)+"원");
        } else if ( "3".equals(sPayMethod) ) {          // 쿠폰포인트 -------------------------------
            Log.d(tag, "쿠폰포인트---결제임---------------------------------------------------------");
            mPaymethod.setText("쿠폰사용");
            dailog_buy_use_coupon.setText("쿠폰사용");
            dialog_buy_price.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
            mMinus.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원 차감");
        } else if ( "4".equals(sPayMethod) ) {          // TV포인트 ---------------------------------
            Log.d(tag, "TV포인트결제임--------------------------------------------------------------");
            mPaymethod.setText("TV포인트");
            dialog_buy_price.setText(UiUtil.toNumFormat(Integer.valueOf(listPrice))+"원");
        }
    }

    private void buy(){
        String pwd = mPref.getValue(JYSharedPreferences.PURCHASE_PASSWORD, "");
        if ( ! pwd.equals(mPwd.getText().toString()) ) {
            mInfo.setText("구매 비밀번호가 잘못되었습니다.");
            mPwd.clearAnimation();
            mPwd.requestFocus();
        } else {
            if ( "0".equals(sPayMethod) ) {
                requestPurchaseAssetEx2();          // 일반결재
            } else if ( "1".equals(sPayMethod)) {
                requestPurchaseAssetEx2();          // 일반결재(할인)
            } else if ( "2".equals(sPayMethod) ) {
                requestPurchaseByComplexMethods();  // complex
            } else if ( "3".equals(sPayMethod) ) {
                requestPurchaseByCoupon();          // coupon
            } else if ( "4".equals(sPayMethod)) {
                requestPurchaseByPoint();           // tv
            }
        }
    }

    // 번들(묶음)
    // requestPurchaseProduct.    ver 2

    // 일반결제
    private void requestPurchaseAssetEx2() {
        mProgressDialog	   = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId  = null;
        try {
            encAssetId = URLEncoder.encode(assetId, "utf-8");
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        String url         = mPref.getWebhasServerUrl() + "/purchaseAssetEx2.json?version=2&terminalKey="+terminalKey
                +"&uiComponentDomain=0"
                +"&uiComponentId=0"
                +"&domainId=CnM"
                +"&productId="+productId
                +"&goodId="+goodId
                +"&assetId="+encAssetId;
        if ( lpriceCouponDiscounted > 0 ) {
            url += "&discountCouponId=" + sdiscountCouponId + "&discountAmount=" + ldiscountAmount +"&price=" + listPrice;
        }
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                int   resultCode  = 0;
                try {
                    JSONObject jo = new JSONObject(response);
                    resultCode    = jo.getInt("resultCode");
                    if ( resultCode == 100 ) {
                        String alertTitle = "구매완료";
                        String alertMsg1  = mTitle;
                        String alertMsg2  = getString(R.string.success_purchase);
                        if ( "SVOD".equals(productType) ) {
                            alertTitle = "가입완료";
                            alertMsg1  = productName;
                            alertMsg2  = getString(R.string.success_purchase_svod);
                        }
                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("jstr", "");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, true);
                    } else {
                        String errorString = jo.getString("errorString");
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        alert.setMessage(errorString);
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

    // TV포인트 결제
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

    // 쿠폰전용 결제
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

    // 복합 결제
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