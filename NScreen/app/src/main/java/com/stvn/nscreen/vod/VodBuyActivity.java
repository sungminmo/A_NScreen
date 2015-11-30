package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    private              JSONArray            couponList;           // GetCouponBalance2로 받아오는 보유쿠폰목록
    // for 결재
    private              int                  iSeletedProductList;
    private              int                  iSeletedPayMethod;
    private              ArrayList<LinearLayout> step1Buttons;
    private              ArrayList<LinearLayout> step2Buttons;
    private              JSONArray            productList;
    private              JSONArray            discountCouponMasterIdList;
    private              String               productType; // RVOD, ....
    private              String               productId;
    private              String               goodId;
    private              String               assetId; // intent param
    private              String               categoryId; // intent param
    private              String               viewable; // intent param
    private              String               isSeriesLink; // 시리즈 여부. ("YES or NO")
    private              String               mTitle; // asset title
    private              String               sListPrice; // 정가
    private              String               sPrice; // 할인적용가
    private              long                 lpriceCouponDiscounted; // 할인을 적용할 경우, 할인 적용후 결제한 금액가.
    private              String               sdiscountCouponId;      // 할인을 적용할 경우, 할인 적용할 쿠폰 아이디.
    private              long                 ldiscountAmount;        // 할인을 적용할 경우, 할인금액.
    private              TextView             vod_buy_title_textview, vod_buy_step1_one_price, vod_buy_step2_normal_price;
    private              long                 pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
    private              long                 totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.

    private              LinearLayout         vod_buy_step1_serise_linearlayout;
    private              TextView             vod_buy_step1_serise_textview;
    private              LinearLayout         vod_buy_step1_packeage_linearlayout, vod_buy_step1_month_all_linearlayout;
    private              TextView             vod_buy_step1_packeage_textview; // 묶음 할인상품 구매 text

    // 단일회차
    private              LinearLayout         vod_buy_step1_one_serise_linearlayout;
    private              TextView             vod_buy_step1_one_serise_price_textview;
    // 단일상품
    private              LinearLayout         vod_buy_step1_one_product_linearlayout;
    private              TextView             vod_buy_step1_one_product_price_textview;



    private              LinearLayout[]       monthLinearlayout;  // 월정액
    private              TextView[]           monthTypeTextview;  // 월정액
    private              TextView[]           monthPriceTextview; // 월정액

    private              TextView             vod_buy_step2_original_price_textview;  // 원래금액(쿠폰있는경우)
    private              TextView             vod_buy_step2_dis_price_textview;      // 할인된결제금액(쿠폰있는경우)
    private              TextView             vod_buy_step2_coupon_point_textview;   // 쿠폰 포인트
    private              TextView             vod_buy_step2_coupon_can_textview;     // [잔액부족-복합결제 가능]
    // TV ppoint
    private              TextView             vod_buy_step2_tv_point_title_textview; // TV 포인트
    private              TextView             vod_buy_step2_tv_point_textview;       // TV 잔액:원
    private              TextView             vod_buy_step2_tv_can_textview;         // TV 포인트 결제 가능/불가능
    private              TextView             vod_buy_step2_tv_can2_textview;        // 보유 포인트 차감결제/TV 포인트 회원가입 필요
    private              boolean              isJoinedTvPointMembership;             // TV포인트 가입 했음? default true, 했음.


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
            for (int i = 0; i < couponList.length(); i++) {         // GetCouponBalance2로 받아오는 보유쿠폰목록
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

    // 사용가능한 쿠폰의 금액을 알아낸다.
    private long getDiscountAmountBydiscountCouponMasterId(String discountCouponMasterId){
        long discountAmount = 0l;
        try {
            for (int i = 0; i < couponList.length(); i++) {
                JSONObject jo = (JSONObject) couponList.get(i);
                if ( discountCouponMasterId.equals(jo.getString("discountCouponMasterId")) ) {
                    discountAmount = jo.getLong("discountAmount");
                    break;
                }
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return discountAmount;
    }

    // 사용가능한 쿠폰의 ID를 알아낸다.
    private String getDiscountCouponId(String discountCouponMasterId){
        String couponId = "";
        try {
            for (int i = 0; i < couponList.length(); i++) {
                JSONObject jo = (JSONObject) couponList.get(i);
                if ( discountCouponMasterId.equals(jo.getString("discountCouponMasterId")) ) {
                    couponId = jo.getString("couponId");
                    break;
                }
            }
        } catch ( JSONException e ) {
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
                    vod_buy_step2_normal_price.setText(UiUtil.toNumFormat(Integer.valueOf(sListPrice)) + "원 [부가세 별도]");
                    vod_buy_step2_dis_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(sListPrice)) + "원 [부가세 별도]");
                }
            } break;
            case 4000: {    // 결제 다이얼로그.
                if ( resultCode == RESULT_OK ) {
                    Intent newIntent = new Intent();
                    if ( intent.getExtras().get("purchasedProductType") != null ) {
                        String purchasedProductType = intent.getExtras().getString("purchasedProductType");
                        String productId = null;
                        try {
                            JSONObject jo = (JSONObject)productList.get(iSeletedProductList);
                            productId     = jo.getString("productId");
                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
                        newIntent.putExtra("purchasedProductType", purchasedProductType); // 묶음구매했다고 알려줘라.
                        newIntent.putExtra("productId", productId); // 묶음구매했다고 알려줘라.
                    }
                    setResult(RESULT_OK, newIntent);
                    finish();
                } else if ( resultCode == RESULT_CANCELED ) {

                }
            } break;
        }
    }

    private void addButton(LinearLayout button){
        step1Buttons.add(button);
    }
    private void addButton2(LinearLayout button){
        step2Buttons.add(button);
    }
    private void setSeletedButton(LinearLayout ll) {
        try {
            for ( int i = 0; i < step1Buttons.size(); i++ ) {
                LinearLayout loop = step1Buttons.get(i);
                if ( loop.equals(ll) ) {
                    iSeletedProductList = i;
                    loop.setSelected(true);
                } else {
                    loop.setSelected(false);
                }
            }
            // TV포인트는 복합결제 불가능하므로, 가능 금액인지 비교해서 모자르면 딤처리 해야 한다.
            JSONObject jo = (JSONObject)productList.get(iSeletedProductList);
            long lListPrice = jo.getLong("listPrice");
            // pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
            // totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.
            if ( pointBalance > lListPrice ) {
                vod_buy_step2_point_linearlayout.setEnabled(true);

//                vod_buy_step2_tv_point_title_textview.setTextColor(getResources().getColor(R.color.color7b5aa3));
//                vod_buy_step2_tv_point_textview.setTextColor(getResources().getColor(R.color.black));
//                vod_buy_step2_tv_can2_textview.setTextColor(getResources().getColor(R.color.black));
//                vod_buy_step2_tv_can_textview.setTextColor(getResources().getColor(R.color.black));
            } else { // TV Point Dim
                vod_buy_step2_point_linearlayout.setEnabled(false);
                vod_buy_step2_tv_can2_textview.setText("TV 포인트 충전 필요");
                vod_buy_step2_tv_can_textview.setText("TV에서 충전 하실 수 있습니다.");

//                vod_buy_step2_tv_point_title_textview.setTextColor(getResources().getColor(R.color.colore5e5e5));
//                vod_buy_step2_tv_point_textview.setTextColor(getResources().getColor(R.color.colore5e5e5));
//                vod_buy_step2_tv_can2_textview.setTextColor(getResources().getColor(R.color.colore5e5e5));
//                vod_buy_step2_tv_can_textview.setTextColor(getResources().getColor(R.color.colore5e5e5));
            }
            // Step.2 는 무조건 디폴트로 1번 일반결제를 선택해준다.
            vod_buy_step2_normal_linearlayout.setSelected(true);
            vod_buy_step2_normal_dis_linearlayout.setSelected(true);
            vod_buy_step2_coupon_linearlayout.setSelected(false);
            vod_buy_step2_point_linearlayout.setSelected(false);
            iSeletedPayMethod = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setSeletedButton2(LinearLayout ll) {
        for ( int i = 0; i < step2Buttons.size(); i++ ) {
            LinearLayout loop = step2Buttons.get(i);
            if ( loop.equals(ll) ) {
                iSeletedPayMethod = i;
                loop.setSelected(true);
            } else {
                loop.setSelected(false);
            }
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
                final int  iLoopOfSVODFinal = iLoopOfSVOD;
                JSONObject jo               = (JSONObject)productList.get(i);
                if ( mPref.isLogging() ) {
                    Log.d(tag, "productList"+i+":" + jo.toString());
                }
                int        price            = jo.getInt("price"); // 정가
                final int        listPrice        = jo.getInt("listPrice"); //할인적용가
                String     productType      = jo.getString("productType");
                final String productId2     = jo.getString("productId");
                Log.d(tag, "setUI ---------------------------------------------------------------");
                Log.d(tag, i+": price(정가): "+price+", listPrice(할인적용가): "+listPrice+", productType: "+productType+", productId: "+productId2);
                if ( "RVOD".equals(productType) ) { //
                    if ( "YES".equals(isSeriesLink) ) { // 시리즈이면 "단일 회차 구매" 표시
                        vod_buy_step1_one_serise_linearlayout.setVisibility(View.VISIBLE);
                        addButton(vod_buy_step1_one_serise_linearlayout);
                        vod_buy_step1_one_serise_price_textview.setText(UiUtil.toNumFormat(listPrice) + "원 [부가세 별도]");
                    } else {                            // 시리즈이면 "단일상품 구매" 표시
                        vod_buy_step1_one_product_linearlayout.setVisibility(View.VISIBLE);  // 단일 상품 구매
                        addButton(vod_buy_step1_one_product_linearlayout);
                        vod_buy_step1_one_product_price_textview.setText(UiUtil.toNumFormat(listPrice)+"원 [부가세 별도]");
                        vod_buy_step1_one_product_linearlayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSeletedButton(vod_buy_step1_one_product_linearlayout);
                            }
                        });
                    }
                    vod_buy_step1_one_serise_linearlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSeletedButton(vod_buy_step1_one_serise_linearlayout);
                            // RVOD를 선택하면, STEP2를 보이고, 안내는 가린다.
                            vod_buy_step2_linearlayout.setVisibility(View.VISIBLE); // 스텝2 보여라.
                            vod_buy_step2_linearlayout2.setVisibility(View.GONE);   // 안내 감쳐라.
                        }
                    });
                    vod_buy_step1_one_serise_linearlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSeletedButton(vod_buy_step1_one_serise_linearlayout);
                            // RVOD를 선택하면, STEP2를 보이고, 안내는 가린다.
                            vod_buy_step2_linearlayout.setVisibility(View.VISIBLE); // 스텝2 보여라.
                            vod_buy_step2_linearlayout2.setVisibility(View.GONE);   // 안내 감쳐라.
                        }
                    });
                }
                if ( "Package".equals(productType) ) {  // 시리즈 전체회차 구매
                    vod_buy_step1_serise_linearlayout.setVisibility(View.VISIBLE);
                    addButton(vod_buy_step1_serise_linearlayout);
                    vod_buy_step1_serise_textview.setText(UiUtil.toNumFormat(listPrice)+"원 [부가세 별도]");
                    vod_buy_step1_serise_linearlayout.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            setSeletedButton(vod_buy_step1_serise_linearlayout);
                        }
                    });
                }
                if ( "Bundle".equals(productType) ) { // 묶음 할인상품 구매
                    vod_buy_step1_packeage_linearlayout.setVisibility(View.VISIBLE);
                    vod_buy_step1_packeage_textview.setText(UiUtil.toNumFormat(listPrice)+"원 [부가세 별도]"); // 묶음 할인상품 구매 text
                    addButton(vod_buy_step1_packeage_linearlayout);
                    vod_buy_step1_packeage_linearlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSeletedButton(vod_buy_step1_packeage_linearlayout);
                            String thisPrice = String.valueOf(listPrice);
                            vod_buy_step2_normal_price.setText(UiUtil.toNumFormat(Integer.valueOf(thisPrice)) + "원 [부가세 별도]");
                            vod_buy_step2_dis_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(thisPrice)) + "원 [부가세 별도]");
                            Intent intent = new Intent(mInstance, VodDetailBundleActivity.class);
                            intent.putExtra("assetId",   assetId);
                            intent.putExtra("productId", productId2);
                            startActivityForResult(intent, 3000);
                        }
                    });
                }
                if ( "SVOD".equals(productType) ) {
                    String productName = jo.getString("productName");
                    Log.d(tag, iLoopOfSVOD + ", productName: " + productName);
                    String[] productNames = productName.split(":");
                    monthLinearlayout[iLoopOfSVOD].setVisibility(View.VISIBLE);
                    addButton(monthLinearlayout[iLoopOfSVOD]);
                    monthTypeTextview[iLoopOfSVOD].setText(productNames[0]);
                    monthPriceTextview[iLoopOfSVOD].setText(UiUtil.toNumFormat(price)+"원/월 [부가세 별도]");      // 원/월 [부가세 별도]

                    monthLinearlayout[iLoopOfSVOD].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSeletedButton(monthLinearlayout[iLoopOfSVODFinal]);
                            // SVOD를 선택하면, STEP2는 통채로 가리고, 안내를 보여준다.
                            vod_buy_step2_linearlayout.setVisibility(View.GONE);     // 스텝2 감쳐라
                            vod_buy_step2_linearlayout2.setVisibility(View.VISIBLE); // 안내 보여라
                        }
                    });
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
        if ( couponList.length() == 0 ) {                           // 보유쿠폰이 없으면 무조건 일반결제 (비쿠폰구매)를 보이자.
            vod_buy_step2_normal_linearlayout.setVisibility(View.VISIBLE);
            vod_buy_step2_normal_dis_linearlayout.setVisibility(View.GONE);
            vod_buy_step2_normal_linearlayout.setSelected(true);
            vod_buy_step2_normal_dis_linearlayout.setSelected(false);
        } else {                                                    // 보유쿠폰이 있다면, 그중에 적용가능한 쿠폰이 있는지 비교해야 한다.
            vod_buy_step2_normal_linearlayout.setVisibility(View.GONE);
            String couponId = getUsableCouponId();
            if ( couponId == null ) {
                vod_buy_step2_normal_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step2_normal_dis_linearlayout.setVisibility(View.GONE);
                vod_buy_step2_normal_linearlayout.setSelected(true);
                vod_buy_step2_normal_dis_linearlayout.setSelected(false);
            } else {
                long discount     = getDiscountAmountBydiscountCouponMasterId(couponId);
                ldiscountAmount   = discount;
                sdiscountCouponId = getDiscountCouponId(couponId);
                lpriceCouponDiscounted  = Integer.valueOf(sListPrice) - (int)discount;
                vod_buy_step2_original_price_textview.setText(UiUtil.toNumFormat(Integer.valueOf(sListPrice)) + "원");
                vod_buy_step2_original_price_textview.setPaintFlags(vod_buy_step2_original_price_textview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                vod_buy_step2_dis_price_textview.setText(" → " + UiUtil.toNumFormat((int)lpriceCouponDiscounted) + " 원 [부가세 별도]");
                vod_buy_step2_normal_linearlayout.setVisibility(View.GONE);
                vod_buy_step2_normal_dis_linearlayout.setVisibility(View.VISIBLE);
                vod_buy_step2_normal_linearlayout.setSelected(false);
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
        if (isJoinedTvPointMembership == false ) {
            vod_buy_step2_point_linearlayout.setEnabled(false);
            vod_buy_step2_tv_can_textview.setText("TV 포인트 회원가입 필요");
        }

        // 값들 잘 찍었으면... Step.1 첫번째 항목을 디폴트로 선택해준다.
        LinearLayout defaultButton = (LinearLayout)step1Buttons.get(0);
        setSeletedButton(defaultButton);
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
        productType       = getIntent().getExtras().getString("productType");
        assetId           = getIntent().getExtras().getString("assetId");
        viewable          = getIntent().getExtras().getString("viewable");
        isSeriesLink      = getIntent().getExtras().getString("isSeriesLink");
        mTitle            = getIntent().getExtras().getString("mTitle");
        sListPrice        = getIntent().getExtras().getString("sListPrice");
        sPrice            = getIntent().getExtras().getString("sPrice");
        productId         = getIntent().getExtras().getString("productId");
        goodId            = getIntent().getExtras().getString("goodId");
        categoryId        = getIntent().getExtras().getString("categoryId");
        pointBalance              = 0l;
        totalMoneyBalance         = 0l;
        isJoinedTvPointMembership = true;
        iSeletedProductList       = 0;
        iSeletedPayMethod         = 0;
        step1Buttons              = new ArrayList<LinearLayout>();
        step2Buttons              = new ArrayList<LinearLayout>();

        vod_buy_step1_one_serise_linearlayout    = (LinearLayout)findViewById(R.id.vod_buy_step1_one_serise_linearlayout);  // 단일 회차 구매
        vod_buy_step1_one_serise_price_textview  = (TextView)findViewById(R.id.vod_buy_step1_one_serise_price_textview);    // 단일 회차 구매
        vod_buy_step1_serise_linearlayout        = (LinearLayout)findViewById(R.id.vod_buy_step1_serise_linearlayout);      // 시리즈 전체회차 구매
        vod_buy_step1_serise_textview            = (TextView)findViewById(R.id.vod_buy_step1_serise_textview);              // 시리즈 전체회차 구매 가격
        vod_buy_step1_one_product_linearlayout   = (LinearLayout)findViewById(R.id.vod_buy_step1_one_product_linearlayout); // 단일상품 구매
        vod_buy_step1_one_product_price_textview = (TextView)findViewById(R.id.vod_buy_step1_one_product_price_textview);   // 단일상품 구매
        vod_buy_step1_packeage_linearlayout      = (LinearLayout)findViewById(R.id.vod_buy_step1_packeage_linearlayout);    // 묶음 할인상품 구매
        vod_buy_step1_packeage_textview          = (TextView)findViewById(R.id.vod_buy_step1_packeage_textview);            // 묶음 할인상품 구매 텍스트

        // 월정액 뷰
        LinearLayout[] monthLayout = {
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout1), (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout2),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout3), (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout4),
                (LinearLayout) findViewById(R.id.vod_buy_step1_month_linearlayout5)
        };
        monthLinearlayout = monthLayout;

        // 월정액 타입 텍스트
        TextView[] monthTypeText = {
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview1), (TextView) findViewById(R.id.vod_buy_step1_month_type_textview2),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview3), (TextView) findViewById(R.id.vod_buy_step1_month_type_textview4),
                (TextView) findViewById(R.id.vod_buy_step1_month_type_textview5)
        };
        monthTypeTextview = monthTypeText;

        // 월정액 금액 텍스트
        TextView[] monthPriceText = {
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview1), (TextView)findViewById(R.id.vod_buy_step1_month_price_textview2),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview3), (TextView)findViewById(R.id.vod_buy_step1_month_price_textview4),
                (TextView)findViewById(R.id.vod_buy_step1_month_price_textview5)
        };
        monthPriceTextview = monthPriceText;

        vod_buy_step1_month_all_linearlayout  = (LinearLayout)findViewById(R.id.vod_buy_step1_month_all_linearlayout);  // 통합 월정액

        vod_buy_step2_normal_linearlayout     = (LinearLayout)findViewById(R.id.vod_buy_step2_normal_linearlayout);     // 일반결제 (할인권없을때구매)
        vod_buy_step2_normal_dis_linearlayout = (LinearLayout)findViewById(R.id.vod_buy_step2_normal_dis_linearlayout); // 일반결제 (할인권있을때구매)
        vod_buy_step2_coupon_linearlayout     = (LinearLayout)findViewById(R.id.vod_buy_step2_coupon_linearlayout);     // 쿠폰결제
        vod_buy_step2_point_linearlayout      = (LinearLayout)findViewById(R.id.vod_buy_step2_point_linearlayout);      // TV포인트

        addButton2(vod_buy_step2_normal_linearlayout);
        addButton2(vod_buy_step2_normal_dis_linearlayout);
        addButton2(vod_buy_step2_coupon_linearlayout);
        addButton2(vod_buy_step2_point_linearlayout);

        vod_buy_step2_normal_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSeletedButton2(vod_buy_step2_normal_linearlayout);
                iSeletedPayMethod = 0;
            }
        });
        vod_buy_step2_normal_dis_linearlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setSeletedButton2(vod_buy_step2_normal_dis_linearlayout);
                iSeletedPayMethod = 1;
            }
        });
        vod_buy_step2_coupon_linearlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setSeletedButton2(vod_buy_step2_coupon_linearlayout);
                iSeletedPayMethod = 2;
            }
        });
        vod_buy_step2_point_linearlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setSeletedButton2(vod_buy_step2_point_linearlayout);
                iSeletedPayMethod = 3;
            }
        });

        vod_buy_step2_linearlayout            = (LinearLayout)findViewById(R.id.vod_buy_step2_linearlayout);            // STEP2. 결제방법선택
        vod_buy_step2_linearlayout2           = (LinearLayout)findViewById(R.id.vod_buy_step2_linearlayout2);           // 월정액 안내
        vod_buy_title_textview                = (TextView)findViewById(R.id.vod_buy_title_textview);
        vod_buy_step2_normal_price            = (TextView)findViewById(R.id.vod_buy_step2_normal_price);                // Step.2 일반결제 (쿠폰을 못사용하는 경우)
        vod_buy_step2_original_price_textview = (TextView)findViewById(R.id.vod_buy_step2_original_price_textview);          // Step.2 일반결제 (쿠폰 사용할수있는 경우)
        vod_buy_step2_dis_price_textview      = (TextView)findViewById(R.id.vod_buy_step2_dis_price_textview);          // Step.2 일반결제 (쿠폰 사용할수있는 경우)
        vod_buy_step2_coupon_point_textview   = (TextView)findViewById(R.id.vod_buy_step2_coupon_point_textview);       // Step.2 쿠폰포인트
        vod_buy_step2_coupon_can_textview     = (TextView)findViewById(R.id.vod_buy_step2_coupon_can_textview);         // Step.2 [쿠폰잔액부족-복합결제 가능]

        vod_buy_step2_tv_point_title_textview = (TextView)findViewById(R.id.vod_buy_step2_tv_point_title_textview);
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
                // iSeletedProductList
                // iSeletedPayMethod 0: 일반결제, 1:복합결제(쿠폰(할인권)+일반결제), 2:복합결제(쿠폰+일반결제), 3:쿠폰결제, 4:TV포인트 결제.

                try {
                    Intent intent = new Intent(mInstance, VodBuyDialog.class);

                    // pointBalance; // TV포인트. getPointBalance 통해서 받아옴.
                    // totalMoneyBalance; // 금액형 쿠폰의 총 잔액. getCouponBalance2 통해서 받아옴.
                    // 상품의 금액(할인가) 알아내기.
                    JSONObject jo = (JSONObject)productList.get(iSeletedProductList);
                    String listPrice = jo.getString("listPrice");
                    String selectedProductType = jo.getString("productType");
                    String selectedProductName = jo.getString("productName");
                    String selectedProductId   = jo.getString("productId");
                    String selectedGoodId      = jo.getString("goodId");

                    // 0000-00-30 00:00:00
                    // 0000-00-00 24:00:00
                    String           viewablePeriod = jo.getString("viewablePeriod");
                    Calendar         cal            = Calendar.getInstance();
                    Locale           currentLocale  = new Locale("KOREAN", "KOREA");
                    SimpleDateFormat formatter      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale);
                    Date             dayofday       = formatter.parse(viewablePeriod);
                    cal.setTime(dayofday);
                    int selectedViewablePeriod      = cal.get(Calendar.DATE);
                    viewable                        = selectedViewablePeriod + "일";

                    // Step.2의 결제방식을 알아내기.
                    String sPayMethod = "";
                    // 일반이 숨김상태이고 할인이 선택된 상태라면, 할인을 선택한거다.
                    if ( vod_buy_step2_normal_linearlayout.getVisibility() == View.GONE && vod_buy_step2_normal_dis_linearlayout.isSelected() ) {
                        iSeletedPayMethod = 1;
                    }

                    if ( iSeletedPayMethod == 0 ) {        // 일반
                        sPayMethod = "0";
                    } else if ( iSeletedPayMethod == 1 ) { // 일반(할인)
                        sPayMethod = "1";
                        intent.putExtra("lpriceCouponDiscounted", lpriceCouponDiscounted);                 // 할인을 적용할 경우, 할인 적용후 결제한 금액가.
                        intent.putExtra("sdiscountCouponId",      sdiscountCouponId);                      // 할인을 적용할 경우, 사용할 쿠폰의 ID.
                        intent.putExtra("ldiscountAmount",        ldiscountAmount);                        // 할인을 적용할 경우, 할인 금액.
                    } else if ( iSeletedPayMethod == 2 ) { // 쿠폰
                        long lListPrice = jo.getLong("listPrice");
                        if ( totalMoneyBalance >= lListPrice ) { // 쿠폰결제
                            sPayMethod = "3";
                        } else {
                            sPayMethod = "2";
                        }
                    } else if ( iSeletedPayMethod == 3 ) { // TV포인트
                        sPayMethod = "4";
                    }
                    //
                    intent.putExtra("assetId",           assetId);
                    intent.putExtra("productId",         selectedProductId);
                    intent.putExtra("productType",       selectedProductType);
                    intent.putExtra("productName",       selectedProductName);
                    intent.putExtra("goodId",            selectedGoodId);
                    intent.putExtra("categoryId",        categoryId);
                    intent.putExtra("mTitle",            mTitle);
                    intent.putExtra("viewable",          viewable);
                    intent.putExtra("listPrice",         String.valueOf(listPrice));              // 상품의 금액(할인가)
                    intent.putExtra("sPayMethod",        sPayMethod);                             // 0: 일반결제, 1:복합결제(쿠폰(할인권)+일반결제), 2:복합결제(쿠폰+일반결제), 3:쿠폰결제, 4:TV포인트 결제.
                    intent.putExtra("pointBalance",      String.valueOf(pointBalance));           // TV포인트
                    intent.putExtra("totalMoneyBalance", String.valueOf(totalMoneyBalance));      // 금액형 쿠폰의 총 잔액

                    startActivityForResult(intent, 4000);
                } catch ( JSONException e ) {
                    e.printStackTrace();
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }

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
                        isJoinedTvPointMembership = false;      // TV포인트 미가입자.
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

                requestGetCouponBalance2();     // 사용가능한 쿠폰 정보를 얻어낸다.
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
     * 사용가능한 쿠폰 정보를 얻어낸다. 내가 소유한 쿠폰 목록 포함.
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