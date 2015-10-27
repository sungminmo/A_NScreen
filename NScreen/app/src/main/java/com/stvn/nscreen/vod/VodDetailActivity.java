package com.stvn.nscreen.vod;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.FourVodPosterPagerAdapter;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.jjiya.android.http.BitmapLruCache;
import com.jjiya.android.http.JYStringRequest;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.CMActionBar;
import com.stvn.nscreen.common.CMBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VodDetailActivity extends CMBaseActivity {

    private static final String              tag = VodDetailActivity.class.getSimpleName();
    private static       VodDetailActivity   mInstance;
    private              JYSharedPreferences mPref;

    // network
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private String sJson;
    private ImageLoader mImageLoader;

    // UI
    private TextView mTitleTextView;
    private ImageView mRatingImageView;
    private ImageView mReviewStar1ImageView;
    private ImageView mReviewStar2ImageView;
    private ImageView mReviewStar3ImageView;
    private ImageView mReviewStar4ImageView;
    private ImageView mReviewStar5ImageView;
    private ImageView mHdSdImageView;
    private TextView mPriceTextView;
    private TextView mGenreTextView;
    private TextView mDirectorTextView;
    private TextView mStarringTextView;
    private TextView mViewableTextView;
    private LinearLayout mSeriesLinearLayout;
    private TextView mSynopsisTextView;
    private LinearLayout mPurchaseLinearLayout;
    private LinearLayout mPlayLinearLayout;
    private LinearLayout mTvOnlyLiearLayout;
    private ViewPager mViewPager;

    // activity
    private String assetId; // intent param
    private List<JSONObject> relationVods;
    private FourVodPosterPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_detail);

        mInstance     = this;
        mPref         = new JYSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        mImageLoader = new ImageLoader(mRequestQueue, imageCache);

        //sJson   = getIntent().getExtras().getString("sJson");
        assetId   = getIntent().getExtras().getString("assetId");
//        try {
//            JSONObject jo = new JSONObject(sJson);
//            assetId = jo.getString("assetId");
//            // (HD)막돼먹은 영애씨 시즌14 02회(08/11
//            // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
//            assetId = "www.hchoice.co.kr|M4132449LFO281926301";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        relationVods = new ArrayList<JSONObject>();
        mPagerAdapter = new FourVodPosterPagerAdapter(this);
        mPagerAdapter.setImageLoader(mImageLoader);

        setActionBarStyle(CMActionBar.CMActionBarStyle.BACK);
        setActionBarTitle(getString(R.string.title_activity_vod_detail));

        mTitleTextView        = (TextView)findViewById(R.id.vod_detail_title);
        mRatingImageView      = (ImageView)findViewById(R.id.vod_detail_rating_imageview);
        mReviewStar1ImageView = (ImageView)findViewById(R.id.vod_detail_review1);
        mReviewStar2ImageView = (ImageView)findViewById(R.id.vod_detail_review2);
        mReviewStar3ImageView = (ImageView)findViewById(R.id.vod_detail_review3);
        mReviewStar4ImageView = (ImageView)findViewById(R.id.vod_detail_review4);
        mReviewStar5ImageView = (ImageView)findViewById(R.id.vod_detail_review5);
        mHdSdImageView        = (ImageView)findViewById(R.id.vod_detail_hd);
        mPriceTextView        = (TextView)findViewById(R.id.vod_detail_price_textview);
        mGenreTextView        = (TextView)findViewById(R.id.vod_detail_genre_textview);
        mDirectorTextView     = (TextView)findViewById(R.id.vod_detail_director_textview);
        mStarringTextView     = (TextView)findViewById(R.id.vod_detail_starring_textview);
        mViewableTextView     = (TextView)findViewById(R.id.vod_detail_viewable_textview);
        mSeriesLinearLayout   = (LinearLayout)findViewById(R.id.vod_detail_series_linearlayout);
        mSynopsisTextView     = (TextView)findViewById(R.id.vod_detail_synopsis_textview);
        mPurchaseLinearLayout = (LinearLayout)findViewById(R.id.vod_detail_purchase_linearlayout);
        mPlayLinearLayout     = (LinearLayout)findViewById(R.id.vod_detail_play_linearlayout);
        mTvOnlyLiearLayout    = (LinearLayout)findViewById(R.id.vod_detail_tvonly_linearlayout);
        mViewPager            = (ViewPager)findViewById(R.id.vod_detail_related_viewpager);



        // (HD)막돼먹은 영애씨 시즌14 02회(08/11
        // http://192.168.40.5:8080/HApplicationServer/getAssetInfo.xml?version=1&terminalKey=9CED3A20FB6A4D7FF35D1AC965F988D2&assetProfile=9&assetId=www.hchoice.co.kr%7CM4132449LFO281926301&transactionId=200
        /**
         * VOD 상세정보 요청
         */
        requestGetAssetInfo();

        /**
         * 화면 하단의 연관 VOD 요청
         */
        requestRecommendContentGroupByAssetId();
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
                    JSONObject asset         = jo.getJSONObject("asset");
                    String rating            = asset.getString("rating");
                    String reviewRatingCount = asset.getString("reviewRatingCount");
                    String reviewRatingTotal = asset.getString("reviewRatingTotal");
                    boolean HDContent        = asset.getBoolean("HDContent");
                    String genre             = asset.getString("genre");
                    String runningTime       = asset.getString("runningTime");
                    String director          = asset.getString("director");
                    String starring          = asset.getString("starring");
                    String synopsis          = asset.getString("synopsis");
                    boolean seriesLink       = asset.getBoolean("seriesLink");

                    // productList
                    JSONArray productList    = asset.getJSONArray("productList");
                    JSONObject product       = productList.getJSONObject(0);
                    String price             = product.getString("price");
                    String purchasedId       = product.getString("purchasedId");

                    // LinearLayout 감추기/보이기 -----------------------------------------------------
                    if ( seriesLink == true ) {      // 시리즈 보여라
                        mSeriesLinearLayout.setVisibility(View.VISIBLE);
                    } else {                         // 시리즈 감춰라.
                        mSeriesLinearLayout.setVisibility(View.GONE);
                    }
                    if ( "0".equals(purchasedId) ) { // 구매하기 보여랴
                        mPurchaseLinearLayout.setVisibility(View.VISIBLE);
                        mPlayLinearLayout.setVisibility(View.GONE);
                    } else {                         // 구매했다. 감쳐라.
                        mPurchaseLinearLayout.setVisibility(View.GONE);
                        mPlayLinearLayout.setVisibility(View.VISIBLE);
                    }
                    // @// TODO: 2015. 10. 21. 지금은 값이 안내려옴. 시청기기 서버작업 되면 수정해야 됨.
                    if ( true ) {                    // TV에서봐 보여랴
                        mTvOnlyLiearLayout.setVisibility(View.VISIBLE);
                    } else {                         // TV에서봐 감쳐라.
                        mTvOnlyLiearLayout.setVisibility(View.VISIBLE);
                    }

                    // 값들 찍어주기. -----------------------------------------------------------------
                    mTitleTextView.setText(asset.getString("title"));
                    if ( "00".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_all);
                    } else if ( "15".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_15);
                    } else if ( "19".equals(rating) ) {
                        mRatingImageView.setImageResource(R.mipmap.btn_age_19);
                    } else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(mInstance);
                        ad.setTitle("알림").setMessage("모르는 rating 케이스:"+rating);
                        AlertDialog alert = ad.create();
                        alert.show();
                    }
                    Long lreviewRatingCount = Long.parseLong(reviewRatingCount);
                    Long lreviewRatingTotal = Long.parseLong(reviewRatingTotal);
                    Long reviewRating = 0l;
                    if ( lreviewRatingTotal > 0 ) { reviewRating = lreviewRatingCount/lreviewRatingTotal*100l; }
                    UiUtil.setStarRating(reviewRating, mReviewStar1ImageView, mReviewStar2ImageView, mReviewStar3ImageView, mReviewStar4ImageView, mReviewStar5ImageView);
                    if ( HDContent == true ) {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_hd);
                    } else {
                        mHdSdImageView.setImageResource(R.mipmap.btn_size_sd);
                    }
                    mPriceTextView.setText(UiUtil.stringParserCommafy(price) + "원 [부가세 별도]");
                    mGenreTextView.setText(genre+" / "+runningTime);
                    mDirectorTextView.setText(director);
                    mStarringTextView.setText(starring);
                    mSynopsisTextView.setText(synopsis);


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

    /**
     * 연관 VOD
     */
    private void requestRecommendContentGroupByAssetId() {
        //mProgressDialog	 = ProgressDialog.show(mInstance,"",getString(R.string.wait_a_moment));
        if ( mPref.isLogging() ) { Log.d(tag, "requestRecommendContentGroupByAssetId()"); }
        String terminalKey = mPref.getWebhasTerminalKey();
        String encAssetId = null;
        try {
            encAssetId  = URLDecoder.decode(assetId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = mPref.getWebhasServerUrl() + "/recommendContentGroupByAssetId.json?version=1&terminalKey="+terminalKey+"&&assetId="+encAssetId+"&contentGroupProfile=2";
        JYStringRequest request = new JYStringRequest(mPref, Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(tag, response);
                //mProgressDialog.dismiss();
                try {
                    JSONObject jo            = new JSONObject(response);

                    // contentGroupList
                    JSONArray contentGroupLists = jo.getJSONArray("contentGroupList");
                    for ( int i = 0; i < contentGroupLists.length(); i++ ) {
                        if ( i > 19 ) {
                            break;
                        }
                        JSONObject content = contentGroupLists.getJSONObject(i);
                        relationVods.add(content);
                        mPagerAdapter.addVod(content);
                    }
                    mViewPager.setAdapter(mPagerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mProgressDialog.dismiss();
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
