package com.stvn.nscreen.vod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.FourVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VodDetailBundle2Activity extends Activity {

    private static final String              tag = VodDetailBundle2Activity.class.getSimpleName();
    private static       VodDetailBundle2Activity mInstance;
    private              JYSharedPreferences mPref;
    
    // network
    private              RequestQueue mRequestQueue;
    private              ProgressDialog mProgressDialog;
    private              String sJson;
    private              ImageLoader mImageLoader;

    // UI
    private              TextView mTitleTextView;
    private              ImageView mRatingImageView;
    private              ImageView mReviewStar1ImageView;
    private              ImageView mReviewStar2ImageView;
    private              ImageView mReviewStar3ImageView;
    private              ImageView mReviewStar4ImageView;
    private              ImageView mReviewStar5ImageView;
    private              ImageView mPromotionSticker;
    private              ImageView mHdSdImageView;
    private              NetworkImageView mMovieImageImageView;
    private              TextView mPriceTextView;
    private              TextView mGenreTextView;
    private              TextView mDirectorTextView;
    private              TextView mStarringTextView;
    private              TextView mViewableTextView;
    private              ImageView mMobileImageView;
    private              TextView mSynopsisTextView;
    private              String viewable;

    // activity
    private              String assetId; // intent param
    private              List<JSONObject> relationVods;
    private              List<JSONObject> series;
    private              FourVodPosterPagerAdapter mPagerAdapter;
    private              String mTitle;
    private              String sListPrice;
    private              String sPrice;

    // for 결재
    private              JSONArray productList;

    private              String productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_detail_bundle2);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader  = new ImageLoader(mRequestQueue, imageCache);

        //sJson   = getIntent().getExtras().getString("sJson");
        assetId   = getIntent().getExtras().getString("assetId");

        relationVods = new ArrayList<JSONObject>();
        series = new ArrayList<JSONObject>();

        mTitleTextView        = (TextView)findViewById(R.id.vod_detail_title);
        mRatingImageView      = (ImageView)findViewById(R.id.vod_detail_rating_imageview);
        mReviewStar1ImageView = (ImageView)findViewById(R.id.vod_detail_review1);
        mReviewStar2ImageView = (ImageView)findViewById(R.id.vod_detail_review2);
        mReviewStar3ImageView = (ImageView)findViewById(R.id.vod_detail_review3);
        mReviewStar4ImageView = (ImageView)findViewById(R.id.vod_detail_review4);
        mReviewStar5ImageView = (ImageView)findViewById(R.id.vod_detail_review5);
        mHdSdImageView        = (ImageView)findViewById(R.id.vod_detail_hd);
        mPromotionSticker     = (ImageView)findViewById(R.id.imageView14);
        mMovieImageImageView  = (NetworkImageView)findViewById(R.id.vod_detail_imagefilename_imageview);
        mPriceTextView        = (TextView)findViewById(R.id.vod_detail_price_textview);
        mGenreTextView        = (TextView)findViewById(R.id.vod_detail_genre_textview);
        mDirectorTextView     = (TextView)findViewById(R.id.vod_detail_director_textview);
        mStarringTextView     = (TextView)findViewById(R.id.vod_detail_starring_textview);
        mViewableTextView     = (TextView)findViewById(R.id.vod_detail_viewable_textview);
        mSynopsisTextView     = (TextView)findViewById(R.id.vod_detail_synopsis_textview);
        mMobileImageView      = (ImageView)findViewById(R.id.vod_detail_device_mobile_imageview);

        ImageButton backButton = (ImageButton)findViewById(R.id.vod_detail_topmenu_left_imagebutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // (HD)막돼먹은 영애씨 시즌14 02회(08/11
        // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
        /**
         * VOD 상세정보 요청
         */
        requestGetAssetInfo();
    }

    private void requestGetAssetInfo() {
        mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestGetAssetInfo()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/getAssetInfo.json?version=1&terminalKey="+terminalKey+"&assetProfile=9&assetId="+encAssetId+"&transactionId=200";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                mProgressDialog.dismiss();

                try {
                    JSONObject jo            = new JSONObject(response);

                    // asset
                    JSONObject asset            = jo.getJSONObject("asset");
                    String categoryId           = asset.getString("categoryId");
                    String imageFileName        = asset.getString("imageFileName");
                    String rating               = asset.getString("rating");
                    String reviewRatingCount    = asset.getString("reviewRatingCount");
                    String reviewRatingTotal    = asset.getString("reviewRatingTotal");
                    boolean HDContent           = asset.getBoolean("HDContent");
                    String genre                = asset.getString("genre");
                    String runningTime          = asset.getString("runningTime");
                    String director             = asset.getString("director");
                    String starring             = asset.getString("starring");
                    String synopsis             = asset.getString("synopsis");
                    String promotionSticker     = asset.getString("promotionSticker");
                    String title                = asset.getString("title");
                    String publicationRight     = asset.getString("publicationRight"); // 1: TV ONLY, 2 MOBILE

                    productList                 = asset.getJSONArray("productList");
                    JSONObject product          = (JSONObject)productList.get(0);
                    productType                 = product.getString("productType");
                    Integer viewablePeriodState = product.getInt("viewablePeriodState");
                    String viewablePeriod       = product.getString("viewablePeriod");

                    String isNew                = ""; // 0:없음, 1:있음.
                    Object isNewObj             = asset.get("isNew");
                    if ( isNewObj != null ) { isNew = asset.getString("isNew"); }
                    String assetNew             = "0"; // 0:없음, 1:new일부만, 2:new단체
                    if ( ! asset.isNull("assetNew") ) {
                        assetNew = asset.getString("assetNew");
                    }
                    String assetHot             = "0"; // 0:없음, 1:new일부만, 2:new단체
                    if ( ! asset.isNull("assetHot") ) {
                        assetHot = asset.getString("assetHot");
                    }
                    String hot                  = ""; // 0:없음, 1:있음.
                    if ( ! asset.isNull("hot") ) {
                        hot = asset.getString("hot");
                    }

                    String runningTimeMinute    = String.valueOf((Integer.parseInt(runningTime.substring(0, 2)) * 60) + Integer.parseInt(runningTime.substring(3))) + "분";
                    runningTime = runningTimeMinute;

                    String price                = product.getString("price");
                    String listPrice            = product.getString("listPrice");
                    String purchasedId          = product.getString("purchasedId");
                    String purchasedTime        = product.getString("purchasedTime");
                    // 값들 찍어주기. -----------------------------------------------------------------

                    mMovieImageImageView.setImageUrl(imageFileName, mImageLoader);

                    UiUtil.setPromotionSticker(promotionSticker, isNew, hot, assetNew, assetHot, mPromotionSticker);

                    if( viewablePeriodState == 1 ) {
                        mViewableTextView.setText("무제한시청");
                    } else {
                        viewable = String.valueOf((Integer.parseInt(viewablePeriod.substring(0, 4)) * 365) + (Integer.parseInt(viewablePeriod.substring(5, 7)) * 30 ) + Integer.parseInt(viewablePeriod.substring(8, 10))) + "일";
                        viewablePeriod = viewable;
                        mViewableTextView.setText(viewablePeriod);
                    }

                    mTitleTextView.setText(asset.getString("title"));
                    mTitle = title;
                    if ( "00".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_all);
                    } else if ( "07".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_7);
                    } else if ( "12".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_12);
                    } else if ( "15".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_15);
                    } else if ( "19".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_19);
                    }
                    Float lreviewRatingCount = Float.parseFloat(reviewRatingCount);
                    Float lreviewRatingTotal = Float.parseFloat(reviewRatingTotal);
                    Float reviewRating = 0f;
                    if ( lreviewRatingTotal > 0 ) { reviewRating = lreviewRatingTotal / lreviewRatingCount;
                    }
                    UiUtil.setStarRating(reviewRating, mReviewStar1ImageView, mReviewStar2ImageView, mReviewStar3ImageView, mReviewStar4ImageView, mReviewStar5ImageView);

                    if ( HDContent == true ) {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_hd);
                    } else {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_sd);
                    }
                    sPrice = price;
                    sListPrice = listPrice;
                    if ( "".equals(purchasedTime) ) {
                        mPriceTextView.setText(UiUtil.stringParserCommafy(price) + "원 [부가세 별도]");
                    } else {
                        mPriceTextView.setText("이미 구매하셨습니다.");
                        String strColor = "#7b5aa3";
                        mPriceTextView.setTextColor(Color.parseColor(strColor));
                    }
                    // 에외처리. productType 이 FOD(무료시청)일 경우는 구매하지 않았더라도, 시청하기 보여라.
                    if ( "FOD".equals(productType) || "0".equals(price) ) {
                        mPriceTextView.setText("무료시청");
                    }
                    mGenreTextView.setText(genre+" / "+runningTime);
                    mDirectorTextView.setText(director);
                    mStarringTextView.setText(starring);
                    mSynopsisTextView.setText(synopsis);
                    if ( "2".equals(publicationRight) ) {
                        mMobileImageView.setVisibility(View.VISIBLE);
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
                params.put("areaCode", String.valueOf(0));
                if ( mPref.isLogging() ) { Log.d(tag, "getParams()" + params.toString()); }
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
