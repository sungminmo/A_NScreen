package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.Constants;
import com.jjiya.android.common.FourVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;
import com.widevine.sampleplayer.VideoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VodDetailBundleActivity extends Activity {

    private static final String              tag = VodDetailBundleActivity.class.getSimpleName();
    private static VodDetailBundleActivity   mInstance;
    private              JYSharedPreferences mPref;

    // network
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private ImageLoader mImageLoader;

    // UI
    private TextView mTitleTextView;
    private NetworkImageView mMovieImageImageView;
    private TextView mPriceTextView;
    private TextView mPricelistTextView;
    private TextView PricevbatTextview;
    private TextView mTimeTextView;
    private ImageView mMobileImageView;

    private JSONArray mBundleAssetList;

    private NetworkImageView mPoster1;
    private NetworkImageView mPoster2;
    private NetworkImageView mPoster3;
    private NetworkImageView mPoster4;
    private NetworkImageView mPoster5;

    private TextView mTitle1;
    private TextView mTitle2;
    private TextView mTitle3;
    private TextView mTitle4;
    private TextView mTitle5;

    private TextView mPrice1;
    private TextView mPrice2;
    private TextView mPrice3;
    private TextView mPrice4;
    private TextView mPrice5;

    private TextView mAleady;
    private Button okButton;

    // activity
    private String assetId; // intent param
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_detail_bundle);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);

        assetId   = getIntent().getExtras().getString("assetId");
        productId = getIntent().getExtras().getString("productId");

        mTitleTextView        = (TextView)findViewById(R.id.vod_detail_bundle_title);
        mMovieImageImageView  = (NetworkImageView)findViewById(R.id.vod_detail_bundle_imagefilename_imageview);
        mPriceTextView        = (TextView)findViewById(R.id.vod_detail_bundle_price_textview);
        mPricelistTextView    = (TextView)findViewById(R.id.vod_detail_bundle_pricelist_textview);
        PricevbatTextview     = (TextView)findViewById(R.id.vod_detail_bundle_pricevbat_textview);
        mTimeTextView         = (TextView)findViewById(R.id.vod_detail_bundle_time_textview);
        mMobileImageView      = (ImageView)findViewById(R.id.vod_detail_bundle_device_mobile_imageview);

        mPoster1 = (NetworkImageView)findViewById(R.id.poster1_netwokr_imageview);
        mPoster2 = (NetworkImageView)findViewById(R.id.poster2_netwokr_imageview);
        mPoster3 = (NetworkImageView)findViewById(R.id.poster3_netwokr_imageview);
        mPoster4 = (NetworkImageView)findViewById(R.id.poster4_netwokr_imageview);
        mPoster5 = (NetworkImageView)findViewById(R.id.poster5_netwokr_imageview);

        mTitle1 = (TextView)findViewById(R.id.poster1_title_textview);
        mTitle2 = (TextView)findViewById(R.id.poster2_title_textview);
        mTitle3 = (TextView)findViewById(R.id.poster3_title_textview);
        mTitle4 = (TextView)findViewById(R.id.poster4_title_textview);
        mTitle5 = (TextView)findViewById(R.id.poster5_title_textview);

        mPrice1 = (TextView)findViewById(R.id.poster1_price_textview);
        mPrice2 = (TextView)findViewById(R.id.poster2_price_textview);
        mPrice3 = (TextView)findViewById(R.id.poster3_price_textview);
        mPrice4 = (TextView)findViewById(R.id.poster4_price_textview);
        mPrice5 = (TextView)findViewById(R.id.poster5_price_textview);

        mAleady = (TextView)findViewById(R.id.vod_detail_bundle_already_textview);

        ImageButton backButton = (ImageButton)findViewById(R.id.vod_detail_bundle_topmenu_left_imagebutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        okButton = (Button)findViewById(R.id.vod_detail_bundle_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        // 미리보기 버튼
//        mPrePlayButton = (Button)findViewById(R.id.vod_detail_preplay_button);
//        mPrePlayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ( mPref.isPairingCompleted() == false ) {
//                        String alertTitle = "셋탑박스 연동 필요";
//                        String alertMsg1  = mTitle;
//                        String alertMsg2  = getString(R.string.error_not_paring_compleated3);
//                        CMAlertUtil.Alert1(mInstance, alertTitle, alertMsg1, alertMsg2, true, false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }, true);
//                } else {
//                    isPrePlay = true;
//                    requestContentUri();
//                }
//            }
//        });

        requestGetBundleProductInfo();
    }

    /**
     * 화면 전체 새로 고침.
     */
    public void refreshAll(String aid){
        assetId = aid;

        /**
         * VOD 상세정보 요청
         */
        requestGetBundleProductInfo();

    }

    private void requestGetBundleProductInfo() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetBundleProductInfo()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // http://58.141.255.79:8080/HApplicationServer/getBundleProductInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&externalProductId=500217&productProfile=1
        String url = mPref.getWebhasServerUrl() + "/getBundleProductInfo.json?version=1&terminalKey="+terminalKey+"&productId="+productId+"&&productProfile=1";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();

                try {
                    JSONObject jo            = new JSONObject(response);

                    // asset
                    JSONObject bundleProduct = jo.getJSONObject("bundleProduct");

                    String joProductName = bundleProduct.getString("productName"); // 패키지 이름
                    String joImageFileName = bundleProduct.getString("imageFileName"); // 이미지
                    String joPurchasedTime = bundleProduct.getString("purchasedTime"); // 구매시간
                    String joSuggestedPrice = bundleProduct.getString("suggestedPriceTotal"); // 총합
                    String joPrice = bundleProduct.getString("price"); // 가격
                    String joRentalDuration = bundleProduct.getString("rentalDuration"); // 유효기간
                    String joRentalDurationUnit = bundleProduct.getString("rentalDurationUnit"); // 유효기간 단위 0: hour, 1: day, 2: week, 3: month, 4: year
                    Boolean joMobilePublicationRight = bundleProduct.getBoolean("mobilePublicationRight"); // 모바일 유무

                    mTitleTextView.setText(joProductName);
                    mMovieImageImageView.setImageUrl(joImageFileName, mImageLoader);
                    if ( "".equals(joPurchasedTime) ) {
                        mPriceTextView.setVisibility(View.VISIBLE);
                        mPriceTextView.setText(joSuggestedPrice + " 원");
                        mPriceTextView.setPaintFlags(mPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mPricelistTextView.setVisibility(View.VISIBLE);
                        mPricelistTextView.setText(joPrice + " 원");
                        PricevbatTextview.setVisibility(View.VISIBLE);
                        mAleady.setVisibility(View.GONE);
                        okButton.setVisibility(View.VISIBLE);
                    } else if ( !"".equals(joPurchasedTime) ) {
                        mPriceTextView.setText("이미 구매하셨습니다.");
                        String strColor = "#7b5aa3";
                        mPriceTextView.setTextColor(Color.parseColor(strColor));
                        mAleady.setVisibility(View.GONE);
                        okButton.setVisibility(View.VISIBLE);
                        mAleady.setVisibility(View.VISIBLE);
                        okButton.setVisibility(View.GONE);
                    }

                    if ( "0".equals(joRentalDurationUnit) ) {
                        joRentalDurationUnit = "시간";
                    } else if ( "1".equals(joRentalDurationUnit) ) {
                        joRentalDurationUnit = "일";
                    } else if ( "2".equals(joRentalDurationUnit) ) {
                        joRentalDurationUnit = "주";
                    } else if ( "3".equals(joRentalDurationUnit) ) {
                        joRentalDurationUnit = "개월";
                    } else if ( "4".equals(joRentalDurationUnit) ) {
                        joRentalDurationUnit = "년";
                    }

                    mTimeTextView.setText(joRentalDuration + joRentalDurationUnit);

                    if ( joMobilePublicationRight == false ) {
                        mMobileImageView.setVisibility(View.GONE);
                    } else if ( joMobilePublicationRight == true ) {
                        mMobileImageView.setVisibility(View.VISIBLE);
                    }

                    mBundleAssetList         = bundleProduct.getJSONArray("bundleAssetList");


                    for ( int i = 0; i < mBundleAssetList.length(); i++ ) {
                        JSONObject bundle = mBundleAssetList.getJSONObject(i);
                        final String assetId2 = bundle.getString("assetId");
                        String displayName = bundle.getString("displayName");
                        String imageFileName = bundle.getString("imageFileName");
                        long suggestedPrice = bundle.getLong("suggestedPrice");
                        if ( i == 0 ) {
                            mPoster1.setVisibility(View.VISIBLE);
                            mPoster1.setImageUrl(imageFileName, mImageLoader);
                            mTitle1.setText(displayName);
                            mPrice1.setText(UiUtil.toNumFormat((int)suggestedPrice) + " 원");
                            mPoster1.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(VodDetailBundleActivity.this, VodDetailBundle2Activity.class);
                                    intent.putExtra("assetId", assetId2);
                                    startActivity(intent);
                                }
                            });
                        }
                        if ( i == 1 ) {
                            mPoster2.setVisibility(View.VISIBLE);
                            mPoster2.setImageUrl(imageFileName, mImageLoader);
                            mTitle2.setText(displayName);
                            mPrice2.setText(UiUtil.toNumFormat((int)suggestedPrice));
                            mPoster2.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(VodDetailBundleActivity.this, VodDetailBundle2Activity.class);
                                    intent.putExtra("assetId", assetId2);
                                    startActivity(intent);
                                }
                            });
                        }
                        if ( i == 2 ) {
                            mPoster3.setVisibility(View.VISIBLE);
                            mPoster3.setImageUrl(imageFileName, mImageLoader);
                            mTitle3.setText(displayName);
                            mPrice3.setText(UiUtil.toNumFormat((int)suggestedPrice));
                            mPoster3.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(VodDetailBundleActivity.this, VodDetailBundle2Activity.class);
                                    intent.putExtra("assetId", assetId2);
                                    startActivity(intent);
                                }
                            });
                        }
                        if ( i == 3 ) {
                            mPoster4.setVisibility(View.VISIBLE);
                            mPoster4.setImageUrl(imageFileName, mImageLoader);
                            mTitle4.setText(displayName);
                            mPrice4.setText(UiUtil.toNumFormat((int)suggestedPrice));
                            mPoster4.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(VodDetailBundleActivity.this, VodDetailBundle2Activity.class);
                                    intent.putExtra("assetId", assetId2);
                                    startActivity(intent);
                                }
                            });
                        }
                        if ( i == 4 ) {
                            mPoster5.setVisibility(View.VISIBLE);
                            mPoster5.setImageUrl(imageFileName, mImageLoader);
                            mTitle5.setText(displayName);
                            mPrice5.setText(UiUtil.toNumFormat((int)suggestedPrice));
                            mPoster5.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Intent intent = new Intent(VodDetailBundleActivity.this, VodDetailBundle2Activity.class);
                                    intent.putExtra("assetId", assetId2);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
//
//
//                    String runningTimeMinute = String.valueOf((Integer.parseInt(runningTime.substring(0, 2)) * 60) + Integer.parseInt(runningTime.substring(3))) + "분";
//                    runningTime = runningTimeMinute;

                    // 값들 찍어주기. -----------------------------------------------------------------

                    //mMovieImageImageView.setImageUrl(imageFileName, mImageLoader);

                    //mTitleTextView.setText(asset.getString("title"));

//                    if ( "2".equals(publicationRight) ) {
//                        mMobileImageView.setVisibility(View.VISIBLE);
//                    }

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
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    // http://192.168.40.5:8080/HApplicationServer/recommendContentGroupByAssetId.json?
    // version=1&
    // terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&
    // assetId=www.hchoice.co.kr|M4154270LSG347422301&
    // contentGroupProfile=2

}
