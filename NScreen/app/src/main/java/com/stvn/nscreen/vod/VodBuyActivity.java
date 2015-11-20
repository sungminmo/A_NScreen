package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.stvn.nscreen.util.CMAlertUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limdavid on 15. 10. 26..
 */

public class VodBuyActivity extends Activity {

    private static final String              tag = VodBuyActivity.class.getSimpleName();
    private static       VodBuyActivity      mInstance;
    private              JYSharedPreferences mPref;

    // network
    private              RequestQueue        mRequestQueue;
    private              ProgressDialog      mProgressDialog;

    // UI
    private              LinearLayout        vod_buy_step2_linearlayout;
    // activity
    private              JSONArray            couponList;
    // for 결재
    private              JSONArray            productList;
    private              JSONArray            discountCouponMasterIdList;
    private              String               productType; // RVOD, ....
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

    private              LinearLayout         vod_buy_step1_serise_linearlayout;
    private              TextView             vod_buy_step1_serise_textview;
    private              LinearLayout         vod_buy_step1_packeage_linearlayout, vod_buy_step1_month_all_linearlayout;

    // 단일회차
    private              LinearLayout         vod_buy_step1_one_serise_linearlayout;
    private              TextView             vod_buy_step1_one_serise_price_textview;
    // 단일상품
    private              LinearLayout         vod_buy_step1_one_product_linearlayout;
    private              TextView             vod_buy_step1_one_product_price_textview;



    private              LinearLayout[]       monthLinearlayout;  // 월정액
    private              TextView[]           monthTypeTextview;  // 월정액
    private              TextView[]           monthPriceTextview; // 월정액

    private              TextView             vod_buy_step2_dis_price_textview; // 일반결제금액(쿠폰있는경우)
    private              TextView             vod_buy_step2_coupon_point_textview; // 쿠폰 포인트
    private              TextView             vod_buy_step2_coupon_can_textview;     // [잔액부족-복합결제 가능]
    private              TextView             vod_buy_step2_tv_point_textview;     // TV 포인트
    private              TextView             vod_buy_step2_tv_can_textview;     // TV 포인트 결제 가능/불가능
    private              TextView             vod_buy_step2_tv_can2_textview;     // 보유 포인트 차감결제/TV 포인트 회원가입 필요
    private              boolean              isJoinedTvPointMembership;           // TV포인트 가입 했음? default true, 했음.


    private              LinearLayout         vod_buy_step2_normal_linearlayout, vod_buy_step2_normal_dis_linearlayout, vod_buy_step2_coupon_linearlayout, vod_buy_step2_point_linearlayout, vod_buy_step2_linearlayout2;

    // 사용가능한 쿠폰이 있는지 찾아서 리턴한다.
    private String getUsableCouponId(){
        String couponId = null;
        try {
            StringBuffer masters = new StringBuffer();
            for (int i = 0; i < discountCouponMasterIdList.length(); i++) {
                String id = (String)discountCouponMasterIdList.get(i);
                masters.append("[").append(id).append("]");
            }
            for (int i = 0; i < couponList.length(); i++) {
                JSONObject jo = (JSONObject)couponList.get(i);
                String id = jo.getString("discountCouponMasterId");
                StringBuffer key = new StringBuffer().append("[").append(id).append("]");
                if ( masters.toString().contains(key) ) {
                    return id;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return couponId;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case 3000: {    // 묶음 할인상품 구매.
                if ( resultCode == RESULT_OK ) {
                    vod_buy_step1_one_product_linearlayout.setSelected(false);
                    vod_buy_step1_packeage_linearlayout.setSelected(true);
                } else if ( resultCode == RESULT_CANCELED ) {
                    vod_buy_step1_one_product_linearlayout.setSelected(true);
                    vod_buy_step1_packeage_linearlayout.setSelected(false);
                }
            } break;
        }
    }

    private void setUI() {
        vod_buy_title_textview.setText(mTitle);
        // 금액 표시 부분.

        // step1 -----------------------------------------------------------------------------------
        // RVOD (단일상품 또는 단일회차 버튼 표시 해야 한다. isSeriesLink
        try {
            int iLoopOfSVOD = 0;
            for ( int i = 0; i< productList.length(); i++ ) {
                JSONObject jo = (JSONObject)productList.get(i);
                int price = jo.getInt("price"); // 정가
                int listPrice = jo.getInt("listPrice"); //할인적용가
                String productType = jo.getString("productType");
                final String productId2   = jo.getString("productId");
                Log.d(tag, "productType: "+productType);
                if ( "RVOD".equals(productType) ) { //
                    if ( "YES".equals(isSeriesLink) ) { // 시리즈이면 "단일 회차 구매" 표시
                        vod_buy_step1_one_serise_linearlayout.setVisibility(View.VISIBLE);
                        vod_buy_step1_one_serise_price_textview.setText(UiUtil.toNumFormat(listPrice) + "원/월 [부가세 별도]");

                    } else {                            // 시리즈이면 "단일상품 구매" 표시
                        vod_buy_step1_one_product_linearlayout.setVisibility(View.VISIBLE);  // 단일 상품 구매
                        vod_buy_step1_one_product_price_textview.setText(UiUtil.toNumFormat(listPrice)+"원/월 [부가세 별도]");
                        vod_buy_step1_one_product_linearlayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vod_buy_step1_one_product_linearlayout.setSelected(true);
                                vod_buy_step1_packeage_linearlayout.setSelected(false);
                            }
                        });
                    }
                }
                if ( "Package".equals(productType) ) {
                    vod_buy_step1_serise_linearlayout.setVisibility(View.VISIBLE);
                    vod_buy_step1_serise_textview.setText(UiUtil.toNumFormat(listPrice)+"원/월 [부가세 별도]");
                }
                if ( "Bundle".equals(productType) ) { // 묶음 할인상품 구매
                    vod_buy_step1_packeage_linearlayout.setVisibility(View.VISIBLE);
                    vod_buy_step1_packeage_linearlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mInstance, VodDetailBundleActivity.class);
                            intent.putExtra("assetId",   assetId);
                            intent.putExtra("productId", productId2);
                            startActivityForResult(intent, 3000);
                        }
                    });
                }
                if ( "SVOD".equals(productType) ) {
                    String productName = jo.getString("productName");
                    String[] productNames = productName.split(":");
                    monthLinearlayout[iLoopOfSVOD].setVisibility(View.VISIBLE);
                    monthTypeTextview[iLoopOfSVOD].setText(productNames[0]);
                    monthPriceTextview[iLoopOfSVOD].setText(UiUtil.toNumFormat(price)+"원/월 [부가세 별도]");      // 원/월 [부가세 별도]
                    iLoopOfSVOD++;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        // step2 -----------------------------------------------------------------------------------
        // vod_buy_step2_normal_linearlayout        // 일반결제 (비쿠폰구매)
        // vod_buy_step2_normal_dis_linearlayout    // 일반결제 (쿠폰구매)

//        totalMoneyBalance  = jo.getLong("totalMoneyBalance");
//        String errorString = jo.getString("errorString");
//        couponList         = jo.getJSONArray("couponList");
//        private              long                 pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
//        private              long                 totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.

        vod_buy_step2_normal_linearlayout.setVisibility(View.VISIBLE);
        vod_buy_step2_normal_price.setText(UiUtil.toNumFormat(Integer.valueOf(sListPrice)) + "원 [부가세 별도]");
        vod_buy_step2_dis_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(sListPrice)) + "원 [부가세 별도]");
        if ( couponList.length() == 0 ) {   // 쿠폰이 없으면 무조건 일반결제 (비쿠폰구매)를 보이자.
            vod_buy_step2_normal_linearlayout.setSelected(true);
        } else {
            // 쿠폰이 있다면, 적용가능한 쿠폰이 있는지 비교해야 한다.
            String couponId = getUsableCouponId();
            if ( couponId != null ) {
                vod_buy_step2_normal_linearlayout.setVisibility(View.GONE);
                vod_buy_step2_normal_dis_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step2_normal_dis_linearlayout.setSelected(true);
            }
        }
        vod_buy_step2_coupon_point_textview.setText("잔액: "+UiUtil.toNumFormat((int) totalMoneyBalance) + "원");
        vod_buy_step2_tv_point_textview.setText("잔액: "+UiUtil.toNumFormat((int) pointBalance) + "원");
        //private              TextView             vod_buy_step2_coupon_can_textview;     // [쿠폰잔액부족-복합결제 가능]
        //private              TextView             vod_buy_step2_tv_can_textview;     // TV 포인트 결제 가능/불가능
        long lsListPrice        = Long.valueOf(sListPrice);
        long ltotalMoneyBalance = Long.valueOf(totalMoneyBalance);
        long lpointBalance      = Long.valueOf(pointBalance);
        // 쿠폰 포인트
        if ( ltotalMoneyBalance <= 0 ) {
            vod_buy_step2_coupon_linearlayout.setEnabled(false);
            vod_buy_step2_coupon_can_textview.setText("[잔액부족]");
        } else if ( ltotalMoneyBalance >= lsListPrice ) {
            vod_buy_step2_coupon_can_textview.setText("[쿠폰 결제 가능]");
        } else {
            vod_buy_step2_coupon_can_textview.setText("[잔액부족-복합결제가능]");
        }
        // TV 포인트
        if ( lpointBalance >= lsListPrice ) {
            vod_buy_step2_tv_can_textview.setText("TV 포인트 결제가능");
            vod_buy_step2_tv_can2_textview.setText("보유 포인트 차감결제");
        } else {    // TV포인트는 부족하면 무조건 딤처리. (복합결제 안됨)
            vod_buy_step2_point_linearlayout.setEnabled(false);
            vod_buy_step2_tv_can_textview.setText("TV에서 충전 하실 수 있습니다.");
            vod_buy_step2_tv_can_textview.setText("TV 포인트 충전 필요");
        }
        if ( isJoinedTvPointMembership == false ) {
            vod_buy_step2_point_linearlayout.setEnabled(false);
            vod_buy_step2_tv_can_textview.setText("TV 포인트 회원가입 필요");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_buy);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        try {
            String productListStr = getIntent().getExtras().getString("productList");
            String discountCouponMasterIdListStr = getIntent().getExtras().getString("discountCouponMasterIdList");
            productList = new JSONArray(productListStr);
            discountCouponMasterIdList = new JSONArray(discountCouponMasterIdListStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        productType   = getIntent().getExtras().getString("productType");
        assetId       = getIntent().getExtras().getString("assetId");
        isSeriesLink  = getIntent().getExtras().getString("isSeriesLink");
        mTitle        = getIntent().getExtras().getString("mTitle");
        sListPrice    = getIntent().getExtras().getString("sListPrice");
        sPrice        = getIntent().getExtras().getString("sPrice");
        productId     = getIntent().getExtras().getString("productId");
        goodId        = getIntent().getExtras().getString("goodId");
        pointBalance      = 0l;
        totalMoneyBalance = 0l;
        isJoinedTvPointMembership = true;

        vod_buy_step1_one_serise_linearlayout    = (LinearLayout)findViewById(R.id.vod_buy_step1_one_serise_linearlayout);  // 단일 회차 구매
        vod_buy_step1_one_serise_price_textview  = (TextView)findViewById(R.id.vod_buy_step1_one_serise_price_textview);  // 단일 회차 구매
        vod_buy_step1_serise_linearlayout        = (LinearLayout)findViewById(R.id.vod_buy_step1_serise_linearlayout);      // 시리즈 전체회차 구매
        vod_buy_step1_serise_textview            = (TextView)findViewById(R.id.vod_buy_step1_serise_textview);              // 시리즈 전체회차 구매 가격
        vod_buy_step1_one_product_linearlayout   = (LinearLayout)findViewById(R.id.vod_buy_step1_one_product_linearlayout); // 단일상품 구매
        vod_buy_step1_one_product_price_textview = (TextView)findViewById(R.id.vod_buy_step1_one_product_price_textview); // 단일상품 구매
        vod_buy_step1_packeage_linearlayout      = (LinearLayout)findViewById(R.id.vod_buy_step1_packeage_linearlayout);    // 묶음 할인상품 구매

        LinearLayout[] monthLayout = {
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout1),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout2),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout3),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout4),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout5)
        };
        monthLinearlayout = monthLayout;
        TextView[] monthTypeText = {
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview1),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview2),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview3),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview4),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview5)
        };
        monthTypeTextview = monthTypeText;
        TextView[] monthPriceText = {
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview1),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview2),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview3),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview4),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview5)
        };
        monthPriceTextview = monthPriceText;

        vod_buy_step1_month_all_linearlayout  = (LinearLayout)findViewById(R.id.vod_buy_step1_month_all_linearlayout);  // 통합 월정액

        vod_buy_step2_normal_linearlayout     = (LinearLayout)findViewById(R.id.vod_buy_step2_normal_linearlayout);     // 일반결제 (할인권없을때구매)
        vod_buy_step2_normal_dis_linearlayout = (LinearLayout)findViewById(R.id.vod_buy_step2_normal_dis_linearlayout); // 일반결제 (할인권있을때구매)
        vod_buy_step2_coupon_linearlayout     = (LinearLayout)findViewById(R.id.vod_buy_step2_coupon_linearlayout);     // 쿠폰결제
        vod_buy_step2_point_linearlayout      = (LinearLayout)findViewById(R.id.vod_buy_step2_point_linearlayout);      // TV포인트
        vod_buy_step2_linearlayout            = (LinearLayout)findViewById(R.id.vod_buy_step2_linearlayout);            // STEP2. 결제방법선택
        vod_buy_step2_linearlayout2           = (LinearLayout)findViewById(R.id.vod_buy_step2_linearlayout2);           // 월정액 안내
        vod_buy_title_textview                = (TextView)findViewById(R.id.vod_buy_title_textview);
        vod_buy_step2_normal_price            = (TextView)findViewById(R.id.vod_buy_step2_normal_price);                // Step.2 일반결제 (쿠폰을 못사용하는 경우)
        vod_buy_step2_dis_price_textview      = (TextView)findViewById(R.id.vod_buy_step2_dis_price_textview);          // Step.2 일반결제 (쿠폰 사용할수있는 경우)
        vod_buy_step2_coupon_point_textview   = (TextView)findViewById(R.id.vod_buy_step2_coupon_point_textview);       // Step.2 쿠폰포인트
        vod_buy_step2_coupon_can_textview     = (TextView)findViewById(R.id.vod_buy_step2_coupon_can_textview);         // Step.2 [쿠폰잔액부족-복합결제 가능]
        vod_buy_step2_tv_point_textview       = (TextView)findViewById(R.id.vod_buy_step2_tv_point_textview);           // Step.2 TV포인트
        vod_buy_step2_tv_can_textview         = (TextView)findViewById(R.id.vod_buy_step2_tv_can_textview);             // Step.2 TV 포인트 결제 가능/불가능
        vod_buy_step2_tv_can2_textview        = (TextView)findViewById(R.id.vod_buy_step2_tv_can2_textview);             // Step.2 보유 포인트 차감결제/TV 포인트 회원가입 필요


//        vod_buy_step1_one_linearlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        Button purchaseButton = (Button)findViewById(R.id.vod_buy_ok_button);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, VodBuyDialog.class);
                intent.putExtra("productId", productId);
                intent.putExtra("goodId", goodId);
                intent.putExtra("mTitle", mTitle);
                startActivity(intent);
            }
        });

        Button cancleButton   = (Button)findViewById(R.id.vod_buy_cancle_button);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("jstr", "");
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ((ImageButton)findViewById(R.id.vod_buy_back_imagebutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("jstr", "");
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        requestGetPointBalance(); // 포인트 잔액을 얻어낸다
    }



    /**
     * 5.23.1 GetPointBalance
     * 포인트 잔액을 얻어낸다
     * terminalKey
     * domainId : "CnM"
     */
    private void requestGetPointBalance() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        String terminalKey = mPref.getWebhasTerminalKey();
        String url = mPref.getWebhasServerUrl() + "/getPointBalance.json?version=1&terminalKey="+terminalKey+"&domainId=CnM";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mProgressDialog.dismiss();
                // {"resultCode":252,"transactionId":null,"pointBalance":0,"errorString":"Authentication fail, code(2000)","version":"1"}
                try {
                    JSONObject jo      = new JSONObject(response);
                    int resultCode     = jo.getInt("resultCode");
                    pointBalance       = jo.getLong("pointBalance");
                    String errorString = jo.getString("errorString");


                    if ( resultCode == 252 ) {
                        isJoinedTvPointMembership = false;
                    } else if ( resultCode != 100 ) {
                        StringBuilder sb   = new StringBuilder();
                        sb.append("API: getPointBalance\nresultCode: ").append(resultCode).append("\nerrorString: ").append(errorString);
                        AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                        alert.setPositiveButton("알림", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage(sb.toString());
                        alert.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                requestGetCouponBalance2(); // 사용가능한 쿠폰 정보를 얻어낸다.
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
                    couponList         = jo.getJSONArray("couponList");
//                    if ( couponList == null ) {
//                        couponList = new JSONArray();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUI();
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
     * http://192.168.40.5:8080/HApplicationServer/
     * purchaseAssetEx2.xml?
     * version=2&
     * terminalKey=
     * transactionId=603&
     * productId=108
     * &goodId=1544796&
     * uiComponentDomain=0&
     * uiComponentId=0
     */

}